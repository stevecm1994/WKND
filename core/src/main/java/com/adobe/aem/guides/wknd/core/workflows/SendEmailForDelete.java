package com.adobe.aem.guides.wknd.core.workflows;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;


@Component(
		   property = {
				   "process.label = Send Email for Delete",
					Constants.SERVICE_DESCRIPTION + "=A sample workflow process to Send email for Deletion.",
					Constants.SERVICE_VENDOR+ "=Adobe"
				         
				   
		   })
public class SendEmailForDelete implements WorkflowProcess {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Reference
	private MessageGatewayService messageGatewayService;

	@Override
	public void execute(WorkItem item, WorkflowSession session, MetaDataMap arg2) 
			throws WorkflowException {
		
		
		try {
			
			MessageGateway<Email> messageGateway; 
			
			Email email = new SimpleEmail();
			WorkflowData workflowData = item.getWorkflowData();
			String deletedPath = workflowData.getPayload().toString();
			
			String emailToRecipients = "steve.mathews@publicissapient.com";
			
			email.addTo(emailToRecipients);
			email.setSubject("AEM Custom Step");
		    email.setFrom("stevecm1994@gmail.com"); 
		    email.setMsg("This message is to inform you that the CQ content at path "+ deletedPath + " has been deleted");
		    
		    //Here messageGateway object initialised using messageGatewayService injected using @refernce
		    //Inject a MessageGateway Service and send the message
		    messageGateway = messageGatewayService.getGateway(Email.class);
		  
		    // Check the logs to see that messageGateway is not null
		    messageGateway.send((Email) email);
			
		} catch (Exception e) {
			e.printStackTrace()  ; 
		}
		

	}

}
