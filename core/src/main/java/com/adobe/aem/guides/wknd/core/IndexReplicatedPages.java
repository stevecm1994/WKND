package com.adobe.aem.guides.wknd.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.guides.wknd.core.servlets.IndexContentToSolr;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
@Component(
	       service = JobConsumer.class,
	       immediate = true,
	       property= {
	    		   JobConsumer.PROPERTY_TOPICS + "=com/adobe/wknd/core/replicationjob2"
	       })
public class IndexReplicatedPages implements JobConsumer {

	 private static final Logger LOG = LoggerFactory
	            .getLogger(IndexReplicatedPages.class);
	
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
    @Reference
    SolrServerConfiguration solrConfigurationService;
 
    @Reference
    SolrSearchService solrSearchService;
	
	
	@Override
	public JobResult process(final Job job) {
		
		final String pagePath = job.getProperty("PAGE_PATH").toString();
		ResourceResolver resourceResolver = null;
		boolean flag = false;
		try { 
		      Map<String, Object> serviceParams = new HashMap<String, Object>();
			  serviceParams.put(ResourceResolverFactory.SUBSERVICE, null);
			  resourceResolver = resourceResolverFactory.getServiceResourceResolver(serviceParams);
			} catch (LoginException e) {
				e.printStackTrace();
			}
		LOG.info(pagePath);
		Resource pageResource = resourceResolver.getResource(pagePath+"/jcr:content");
		JSONObject resourceJson = solrSearchService.createPageMetadataObject(pageResource);
		try {
			 flag = solrSearchService.indexPageToSolr(resourceJson, createServer());
		} catch (Exception e) {
			 LOG.error("Exception due to", e);
			 flag=false;
		}
		
		if(flag) {
			LOG.info("Inexing page Success");
			return JobConsumer.JobResult.OK;
		}
		else {
			LOG.info("Inexing page falied");
			return JobConsumer.JobResult.FAILED;
			
		}
	}
     
    public HttpSolrClient createServer() {
        final String protocol = solrConfigurationService.getSolrProtocol();
        final String serverName = solrConfigurationService.getSolrServerName();
        final String serverPort = solrConfigurationService.getSolrServerPort();
        final String coreName = solrConfigurationService.getSolrCoreName();
        String URL = protocol + "://" + serverName + ":" + serverPort
                + "/solr/" + coreName;
        LOG.info(URL);
        HttpSolrClient server = new HttpSolrClient(URL);
        return server;	
	}
	
	
	
	
}	