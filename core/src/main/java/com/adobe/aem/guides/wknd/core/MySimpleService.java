package com.adobe.aem.guides.wknd.core;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name="AEM Solr Search - Solr Configuration Service" ,description="Service Configuration")
public @interface MySimpleService {

	@AttributeDefinition(name="protocol",defaultValue="http",description="Configuration value")
	String protocolValue();
    @AttributeDefinition(name="Solr Server Name",defaultValue="localhost",description="Server name or IP address")
	String serverName();
    @AttributeDefinition(name="Solr Server Port",defaultValue="8983",description="Server port") 
	String serverPort();
    @AttributeDefinition(name="Solr Core Name",defaultValue="collection",description="Core name in solr server")
	String serverCollection();
    @AttributeDefinition(name="Content Page Path",defaultValue="/content/we-retail",description="Content page path from where solr has to index the pages")
	String serverPath();
}
