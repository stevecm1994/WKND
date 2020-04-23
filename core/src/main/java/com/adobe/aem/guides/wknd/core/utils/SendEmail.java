package com.adobe.aem.guides.wknd.core.utils;

import org.apache.commons.collections4.Get;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component(immediate = true,service = SendEmail.class)
public class SendEmail {

	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Reference
	private MessageGatewayService messageGatewayService;
	
public void sendEmail(String message) {
		
		
	    try {
	    	MessageGateway<Email> messageGateway; 		
			Email email = new SimpleEmail();
			String emailToRecipients = "steve.mathews@publicissapient.com";		
			email.addTo(emailToRecipients);
			email.setSubject("AEM Custom Step");
		    email.setFrom("stevecm1994@gmail.com"); 
			email.setMsg(message);
			messageGateway = messageGatewayService.getGateway(Email.class);
			messageGateway.send((Email) email);			
			logger.info("Email Successfully Sent");
			
		} catch (EmailException e) {
			
			e.printStackTrace();
		}
	}
}
