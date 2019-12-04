package com.adobe.aem.guides.wknd.core;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.xml.ws.ServiceMode;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
 
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import com.adobe.aem.guides.wknd.core.SolrSearchService;
import com.adobe.aem.guides.wknd.core.SolrServerConfiguration;
import com.adobe.aem.guides.wknd.core.utils.SolrUtils;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.search.result.SearchResult;

@Component(service = SolrSearchService.class)
public class SolrSearchServiceImpl implements SolrSearchService {
	
	private static final Logger LOG = LoggerFactory.getLogger(SolrSearchServiceImpl.class);
	
	@Reference
	private QueryBuilder queryBuilder;
	
	@Reference
	private SlingRepository repository;
	
	
	
	@Override
	public JSONArray crawlContent(String resourcePath, String resourceType) {
		
		Map <String,String> params = new HashMap<String,String>();
		params.put("path", resourcePath);
		params.put("type", resourceType);
		params.put("p.offset", "0");
		params.put("p.limit", "1000");
		
		Session session = null;
		
		try {
			session = repository.loginService(null, repository.getDefaultWorkspace());
			Query query = queryBuilder.createQuery(PredicateGroup.create(params), session);
			
			SearchResult searchResults = query.getResult();
            LOG.info("Found '{}' matches for query",
                    searchResults.getTotalMatches());
            if(resourceType.equalsIgnoreCase("cq:PageContent")){
            	return createPageMetadataArray(searchResults);
            }
			
			
		}  catch (RepositoryException e) {
			LOG.error("Exception due to", e);
		}finally {
            if(session != null && session.isLive()) {
 
                session.logout();
 
            }
        }		
		return null;
	}
	
	
	@Override
	public JSONArray createPageMetadataArray(SearchResult results) throws RepositoryException {
		// TODO Auto-generated method stub
		JSONArray solrDocs = new JSONArray();
        for (Hit hit : results.getHits()) {
            Resource pageContent = hit.getResource();
            ValueMap properties = pageContent.adaptTo(ValueMap.class);
            String isPageIndexable = properties.get("notsolrindexable",
                    String.class);
            if (null != isPageIndexable && isPageIndexable.equals("true"))
                continue;
            JSONObject propertiesMap = createPageMetadataObject(pageContent);
            solrDocs.put(propertiesMap);
        }
 
        return solrDocs;
	}

	
	
	
	/**
     * This method creates JSONObject which has all the page metadata which is used to index in Solr server
     * @param It takes resource of type cq:PageContent to extract the page metadata
     * @return Json object with page's metadata
     */
	@Override
	public JSONObject createPageMetadataObject(Resource pageContent) {
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
        propertiesMap.put("id", pageContent.getParent().getPath());
        propertiesMap.put("url", pageContent.getParent().getPath() + ".html");
        ValueMap properties = pageContent.adaptTo(ValueMap.class);
        String pageTitle = properties.get("jcr:title", String.class);
        if (StringUtils.isEmpty(pageTitle)) {
            pageTitle = pageContent.getParent().getName();
        }
        propertiesMap.put("title", pageTitle);
        propertiesMap.put("description", SolrUtils.checkNull(properties.get(
                "jcr:description", String.class)));
        propertiesMap.put("publishDate", SolrUtils.checkNull(properties.get(
                "publishdate", String.class)));
        propertiesMap.put("body","");
        propertiesMap.put("lastModified", SolrUtils.solrDate(properties.get(
                "cq:lastModified", Calendar.class)));
        propertiesMap.put("contentType", "page");
        propertiesMap.put("tags", SolrUtils.getPageTags(pageContent));
        return new JSONObject(propertiesMap);
	}

	@Override
	public boolean indexPageToSolr(JSONObject indexPageData, HttpSolrClient server)
			throws JSONException, SolrServerException, IOException {
		if (null != indexPageData) {
            SolrInputDocument doc = createPageSolrDoc(indexPageData);
            server.add(doc);
            server.commit();
            return true;
        }
 
        return false;
	}

	@Override
	public boolean indexPagesToSolr(JSONArray indexPageData, HttpSolrClient server)
			throws JSONException, SolrServerException, IOException {
		if (null != indexPageData) {
			 
            for (int i = 0; i < indexPageData.length(); i++) {
                JSONObject pageJsonObject = indexPageData.getJSONObject(i);
                SolrInputDocument doc = createPageSolrDoc(pageJsonObject);
                server.add(doc);
            }
            server.commit();
            return true;
        }
 
        return false;
	}
	
	 private SolrInputDocument createPageSolrDoc(JSONObject pageJsonObject) throws JSONException {
         
	        SolrInputDocument doc = new SolrInputDocument();
	        doc.addField("id", pageJsonObject.get("id"));
	        doc.addField("title", pageJsonObject.get("title"));
	        doc.addField("body", pageJsonObject.get("body"));
	        doc.addField("url", pageJsonObject.get("url"));
	        doc.addField("description", pageJsonObject.get("description"));
	        doc.addField("lastModified", pageJsonObject.get("lastModified"));
	        doc.addField("contentType", pageJsonObject.get("contentType"));
	        doc.addField("tags", pageJsonObject.get("tags"));
	        return doc;
	 
	    }

	
}
