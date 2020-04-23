package com.adobe.aem.guides.wknd.core.listeners;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.guides.wknd.core.utils.SendEmail;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component(immediate = true,service=EventListener.class)
public class ImpNodeDeletionListener implements EventListener{

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Reference
	private SlingRepository repository;
	
	@Reference
	private MessageGatewayService messageGatewayService;
	
	@Reference
	private SendEmail emailUtil;
	
	private Session session;
	private ObservationManager observationManager;
	
	protected void activate(ComponentContext context) throws Exception {
		session = repository.loginService(null,repository.getDefaultWorkspace());
		observationManager = session.getWorkspace().getObservationManager();
		
		observationManager.addEventListener(this, Event.NODE_REMOVED | Event.PROPERTY_CHANGED | Event.NODE_MOVED, "/content/wknd/imp", true, null, 
				new String[]{"cq:PageContent","nt:unstructured","nt:folder","cq:Page"} , true);
		logger.info("*************added JCR event listener");
	}
	
	protected void deactivate(ComponentContext componentContext) {
		try {
			if (observationManager != null) {
				observationManager.removeEventListener(this);
				logger.info("*************removed JCR event listener");
			}
		}
		catch (RepositoryException re) {
			logger.error("*************error removing the JCR event listener ", re);
		}
		finally {
			if (session != null) {
				session.logout();
				session = null;
			}
		}
	}	

	@Override
	public void onEvent(EventIterator it) {
		
		
		while (it.hasNext()) {
			Event event = it.nextEvent();
			
			try {
				String eventPath = event.getPath();
				String eventUser = event.getUserID().toString();
				logger.info("Event Path :" +eventPath+ "Event user :" + eventUser + "userID : "+ event.getUserData());
				String message = "This message is to inform you that Restricted CQ content at path "+ eventPath + " has been deleted by :" + eventUser ;
				emailUtil.sendEmail(message);				
			}
			catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			
			
			
		}
		
	}
	
	
}
