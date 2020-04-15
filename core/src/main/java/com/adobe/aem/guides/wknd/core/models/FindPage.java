package com.adobe.aem.guides.wknd.core.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.models.annotations.Model;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables=Resource.class)
public class FindPage {

	@Inject@Named("path")
	private String componentPath;
	
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	private ResourceResolver resourceResolver;
	
	private Session session;
	
	PageManager pageManager ;
	
	Map predicateMap = new HashMap();
	QueryBuilder queryBuilder;
	Map pages = new HashMap();

	
	
	@PostConstruct
	protected void init() {
		
		 final Logger logger = LoggerFactory.getLogger(getClass());
		 logger.info(componentPath);
		
	
		try {
			resourceResolver = resourceResolverFactory.getResourceResolver(null);
		} catch (LoginException e) {
			
			e.printStackTrace();
		}
		
		session= resourceResolver.adaptTo(Session.class);
		pageManager = resourceResolver.adaptTo(PageManager.class);
		
		predicateMap.put("path","/content");
		predicateMap.put("property", "sling:resourceType");
    	predicateMap.put("property.value",componentPath);
    	predicateMap.put("p.limit", "-1");
    	
    	Query query = queryBuilder.createQuery(PredicateGroup.create(predicateMap), session);
    	
    	SearchResult result = query.getResult();
    	
    	Iterator<Resource> resources = result.getResources();
  	    while (resources.hasNext()) {
  	       
  		  Page page = pageManager.getContainingPage(resources.next());
  		  pages.put("1", page);
  		  
  	    }
  	    logger.info(pages.toString());		
	}
	
	public Map getPages() {
        return pages;
    }	
}
