package org.flowable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
//import org.flowable.engine.test.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Task;
//import org.flowable.task.api.Task;

public class HolidayRequest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
	      .setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")
	      .setJdbcUsername("sa")
	      .setJdbcPassword("")
	      .setJdbcDriver("org.h2.Driver")
	      .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

	    ProcessEngine processEngine = cfg.buildProcessEngine();
		
	    
	    
	    RepositoryService repositoryService = processEngine.getRepositoryService();
	    Deployment deployment = repositoryService.createDeployment()
	      .addClasspathResource("holiday-request.bpmn20.xml")
	      .deploy();
	    
	    
	    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
	    		  .deploymentId(deployment.getId())
	    		  .singleResult();
	    		System.out.println("Found process definition : " + processDefinition.getName());

	    		Scanner scanner= new Scanner(System.in);

	    		System.out.println("Who are you?");
	    		String employee = scanner.nextLine();

	    		System.out.println("How many holidays do you want to request?");
	    		Integer nrOfHolidays = Integer.valueOf(scanner.nextLine());

	    		System.out.println("Why do you need them?");
	    		String description = scanner.nextLine();
	    		
	    		
	    		RuntimeService runtimeService = processEngine.getRuntimeService();

	    		Map<String, Object> variables = new HashMap<String, Object>();
	    		variables.put("employee", employee);
	    		variables.put("nrOfHolidays", nrOfHolidays);
	    		variables.put("description", description);
	    		ProcessInstance processInstance =
	    		  runtimeService.startProcessInstanceByKey("holidayRequest", variables);

	    	/*	int num=(Integer) variables.get("nrOfHolidays");
	    		boolean left;
	    		
	    	try {
	    		
	    		if(num>5)
	    		{
	    			left=false;
	    			System.out.println("You have not "+num+"leaves available,so Try Again");
	    		}
	    		else
	    		{
	    			left=true;
	    			
	    			//variables = new HashMap<String, Object>();
		    		variables.put("left",left);
		    		
	    		}

				
			} catch (Exception e) {

				e.printStackTrace();
				System.out.println("You dont have enough alloted holidays for this request..");
				System.out.println("press enter to continue");
				scanner.nextLine();
				// TODO: handle exception
			}	    		
*/
	    		
	    		
	    	TaskService taskService = processEngine.getTaskService();
	    	List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
	    	System.out.println("You have " + tasks.size() + " tasks:");
	    	for (int i=0; i<tasks.size(); i++) {
	    	  System.out.println((i+1) + ") " + tasks.get(i).getName());
	    	}
	    		
	    		
	    	System.out.println("Which task would you like to complete?");
	    	int taskIndex = Integer.valueOf(scanner.nextLine());
	    	Task task = tasks.get(taskIndex - 1);
	    	Map<String, Object> processVariables = taskService.getVariables(task.getId());
	    	System.out.println(processVariables.get("employee") + " wants " +
	    	    processVariables.get("nrOfHolidays") + " of holidays. Do you approve this?");


	    		
	    			boolean approved = scanner.nextLine().toLowerCase().equals("y");
	    			if(approved==true)
	    			{
	    		variables = new HashMap<String, Object>();
	    		variables.put("approved", approved);
	    		taskService.complete(task.getId(), variables);
	    		System.out.println("Request has been approved for : "+employee);
	    		
	    			}
	    			else
	    		     {
	    			    System.out.println("rejection Email has been sent to "+employee);
	    		     }
	    		
	    		HistoryService historyService = processEngine.getHistoryService();
	    		
	    		List<HistoricActivityInstance> activities =
	    		  historyService.createHistoricActivityInstanceQuery()
	    		   .processInstanceId(processInstance.getId())
	    		   .finished()
	    		   .orderByHistoricActivityInstanceEndTime().asc()
	    		   .list();

	    		for (HistoricActivityInstance activity : activities) 
	    		{
	    		  System.out.println(activity.getActivityId() + " took "
	    		    + activity.getDurationInMillis() + " milliseconds");
	    		}
	    		
	    		
	    		
	}

}
