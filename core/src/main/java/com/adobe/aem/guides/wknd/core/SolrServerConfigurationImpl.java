package com.adobe.aem.guides.wknd.core;

import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
 
import com.adobe.aem.guides.wknd.core.SolrServerConfiguration ; 
import com.adobe.aem.guides.wknd.core.MySimpleService; 
  
  
@Component(service = SolrServerConfiguration.class,configurationPolicy=ConfigurationPolicy.REQUIRE)	
@Designate(ocd = MySimpleService.class)
public class SolrServerConfigurationImpl implements SolrServerConfiguration {
      
    // to use the OSGi annotations
    // use version 3.2.0 of maven-bundle-plugin
  
    private MySimpleService config;
     
    private String solrProtocol;
     
    private String solrServerName ;
 
    private String solrServerPort ;
 
    private String solrCoreName ;
     
    private String contentPagePath ;
     
      
 
    @Activate
    public void activate(MySimpleService config) {
        this.config = config;
         
         
        //Populate the solrProtocol data member
        this.solrProtocol = config.protocolValue();
        this.solrServerName = config.serverName();
        this.solrServerPort = config.serverPort(); 
        this.solrCoreName = config.serverCollection(); 
        this.contentPagePath = config.serverPath(); 
         
    }
  
          
    public String getSolrProtocol() {
        return this.solrProtocol;
    }
     
    public String getSolrServerName() {
        return this.solrServerName;
    }
 
    public String getSolrServerPort() {
        return this.solrServerPort;
    }
 
    public String getSolrCoreName() {
        return this.solrCoreName;
    }
     
    public String getContentPagePath() {
        return this.contentPagePath;
    }
  
}
