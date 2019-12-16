package com.adobe.aem.guides.wknd.core.listeners;


import java.util.HashMap;


import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import org.apache.sling.event.jobs.JobManager;

@Component(immediate = true,
service=EventHandler.class,
property= {
		EventConstants.EVENT_TOPIC + "=" + ReplicationAction.EVENT_TOPIC
}
)
public class ReplicationListener implements EventHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	//dictates to the JobManager which JobConsumer will be instantiated in line 42
	private static final String TOPIC = "com/adobe/wknd/core/replicationjob";
	private static final String TOPIC2 = "com/adobe/wknd/core/replicationjob2";
	
	@Reference
	private JobManager jobManager;
	
	
	
	@Override
	public void handleEvent(Event event) {
		// Records all replication actions like activating deactivating dispacher flush..
		ReplicationAction action = ReplicationAction.fromEvent(event);
		
		if (action.getType().equals(ReplicationActionType.ACTIVATE)) {
			if (action.getPath() != null)
			{
				try {
					// Create a properties map that contains things we want to pass through the job
					HashMap<String, Object> jobprops = new HashMap<String, Object>();
					jobprops.put("PAGE_PATH", action.getPath());
					logger.info("Event Handler called for replicating " + action.getPath());
					// Add the job
					jobManager.addJob(TOPIC2, jobprops);
					logger.info("=============Topic: '"+TOPIC+"' with payload: '"+action.getPath()+"' was added to the Job Manager");

				} catch (Exception e) {
					logger.error("============= ERROR CREATING JOB : NO PAYLOAD WAS DEFINED");
					e.printStackTrace();
				}
			}
		}
		
	}

}
