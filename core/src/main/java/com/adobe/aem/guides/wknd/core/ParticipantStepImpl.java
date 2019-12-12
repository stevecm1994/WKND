package com.adobe.aem.guides.wknd.core;

import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.HistoryItem;
import com.day.cq.workflow.exec.ParticipantStepChooser;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.Workflow;
import com.day.cq.workflow.metadata.MetaDataMap;



@Component(immediate = true,
           service = ParticipantStepChooser.class,
		   property = {   "chooser.label=Sample Workflow Participant Chooser",
						Constants.SERVICE_DESCRIPTION + "=Sample Implementation of dynamic participant chooser.",
						Constants.SERVICE_VENDOR+ "=Adobe"
		})
public class ParticipantStepImpl implements ParticipantStepChooser {

	private static final Logger logger = LoggerFactory.getLogger(ParticipantStepImpl.class);
	
	@Override
	public String getParticipant(WorkItem workItem, WorkflowSession wfSession, MetaDataMap metaDataMap) throws WorkflowException {
		logger.info("################ Inside the SampleProcessStepChooserImpl GetParticipant ##########################");
	    String participant = "admin";
	    Workflow wf = workItem.getWorkflow();
	    String payload = workItem.getWorkflowData().getPayload().toString();
	    logger.info(payload);
	    String path = getPath(payload);
	    List<HistoryItem> wfHistory = wfSession.getHistory(wf);
	    if (!wfHistory.isEmpty()) {
	      participant = chooseParticipant(path);
	    } else {
	      participant = chooseParticipant(path);
	    }
	    logger.info("####### Participant : " + participant + " ##############");
	    return participant;
	}
	
	public String getPath(String payload) {
		
		String path;
		if(payload!=null) {
			
			path=payload.substring(1,24);
			logger.info(path);
			return path;
		}
		else return "";		
		
		
		
	}
	
	public String chooseParticipant(String path) {
		
		if(path.equals("content/we-retail/us/en"))
			return "we-us-english";
		else if(path.equals("content/we-retail/us/es"))
			return "we-us-spanish";
		else if(path.equals("content/we-retail/ca/en"))
			return "we-ca-english";
		else if(path.equals("content/we-retail/ca/fr"))
			return "we-ca-french";
		else return "admin";
	}

}
