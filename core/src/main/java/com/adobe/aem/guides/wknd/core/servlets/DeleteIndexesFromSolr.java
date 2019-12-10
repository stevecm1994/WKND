package com.adobe.aem.guides.wknd.core.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.Servlet;
import org.osgi.framework.Constants;
import org.apache.sling.api.servlets.HttpConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import com.adobe.aem.guides.wknd.core.SolrServerConfiguration;
 
 
/**
 * 
 * @author kavarana 
 * This servlet deletes all the indexes from the configured Solr server
 *
 */
@Component(service=Servlet.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=Solr Delete Index",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.paths="+ "/bin/solr/delete/all/indexes"
           })        
public class DeleteIndexesFromSolr extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(DeleteIndexesFromSolr.class);
    @Reference
    SolrServerConfiguration solrConfigurationService;
     
    @Override
    protected void doPost(final SlingHttpServletRequest reqest,
            final SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        final String protocol = solrConfigurationService.getSolrProtocol();
        final String serverName = solrConfigurationService.getSolrServerName();
        final String serverPort = solrConfigurationService.getSolrServerPort();
        final String coreName = solrConfigurationService.getSolrCoreName();
        String URL = protocol + "://" + serverName + ":" + serverPort
                + "/solr/" + coreName;
        HttpSolrClient server = new HttpSolrClient(URL);
        try {
            server.deleteByQuery("*:*");
            server.commit();
            server.close();
            response.getWriter().write("Deleted all the indexes from solr server ");
        } catch (SolrServerException e) {
            LOG.error("Exception due to", e);
        }
         
         
    }
}
