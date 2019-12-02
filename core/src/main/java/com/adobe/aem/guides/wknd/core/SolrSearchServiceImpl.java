package com.adobe.aem.guides.wknd.core;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
 
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
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import com.adobe.aem.core.SolrSearchService;
import com.adobe.aem.core.SolrServerConfiguration;
import com.adobe.aem.core.utils.SolrUtils;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.search.result.SearchResult;

@Component
public class SolrSearchServiceImpl implements SolrSearchService {
	
	private static final Logger LOG = LoggerFactory.getLogger(SolrSearchServiceImpl.class);
	
	@Reference
	private QueryBuilder queryBuilder;
	
	@Reference
	private SlingRepository repository;
	
	@Reference
    SolrServerConfiguration solrConfigurationService;
	
	@Reference
	ResourceResolver resourceResolver;

	@Override
	public JSONArray crawlContent(String resourcePath, String resourceType) {
		
		Map <String,String> params = new HashMap<String,String>();
		params.put("path", resourcePath);
		params.put("type", resourceType);
		params.put("p.offset", "0");
		params.put("p.limit", "1000");
		
		Session session = null;
		
		try {
			session = repository.loginAdministrative(null);
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
		return null;
	}

	@Override
	public JSONObject createPageMetadataObject(Resource pageContent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean indexPageToSolr(JSONObject indexPageData, HttpSolrClient server)
			throws JSONException, SolrServerException, IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean indexPagesToSolr(JSONArray indexPageData, HttpSolrClient server)
			throws JSONException, SolrServerException, IOException {
		// TODO Auto-generated method stub
		return false;
	}

	
}
