import java.util.*;

public class algoMLQ {
	
	public static void MLQ(Queue<Process> processes, int highQuantum, int mediumQuantum) {
		
		if (highQuantum <= 0 || mediumQuantum <= 0) {
		    throw new IllegalArgumentException("Time quantum must be greater than 0.");
		}
		
		Process.sortByArrivalTime(processes);
		
		Queue<Process> pendingQueue = new LinkedList<>(processes);
		Queue<Process> highPriority = new LinkedList<>();
		Queue<Process> mediumPriority = new LinkedList<>();
		Queue<Process> lowPriority = new LinkedList<>();
		Queue<Process> finishedQueue = new LinkedList<>();
		
		
		int currentTime = 0;
		double totalComp = 0;
		double totalTAT = 0;
		double totalWT = 0;
		int numOfEl = processes.size();
		
		while(!pendingQueue.isEmpty() || !highPriority.isEmpty() || !mediumPriority.isEmpty() || !lowPriority.isEmpty()) {
				
			updateReadyQueues(pendingQueue,highPriority,mediumPriority,lowPriority,currentTime);
			
				
				if (highPriority.isEmpty() && mediumPriority.isEmpty() && lowPriority.isEmpty()) {
					currentTime = pendingQueue.peek().getArrivalTime();
					continue;
				}
				
				
				if (!highPriority.isEmpty()) {
					
					Process current = highPriority.remove();
					int executionTime = Math.min(highQuantum, current.getRemainingBurstTime());
					currentTime += executionTime;
					current.setRemainingBurstTime(current.getRemainingBurstTime()-executionTime);
					
					updateReadyQueues(pendingQueue,highPriority,mediumPriority,lowPriority,currentTime);
					
					
					if(current.getRemainingBurstTime() > 0)
						highPriority.add(current);
					else {
						current.setCompletionTime(currentTime);
						current.setTurnAroundTime(current.getCompletionTime()-current.getArrivalTime());
						current.setWaitingTime(current.getTurnAroundTime()-current.getBurstTime());
						
						totalComp += current.getCompletionTime();
						totalTAT += current.getTurnAroundTime();
			            totalWT += current.getWaitingTime();

			            finishedQueue.add(current);
					}
				}
				
				
				
				else if (!mediumPriority.isEmpty()) {
					Process current = mediumPriority.remove();
					int executionTime = Math.min(mediumQuantum, current.getRemainingBurstTime());
					currentTime += executionTime;
					current.setRemainingBurstTime(current.getRemainingBurstTime()-executionTime);
					
					updateReadyQueues(pendingQueue,highPriority,mediumPriority,lowPriority,currentTime);
					
					
					if(current.getRemainingBurstTime() > 0)
						mediumPriority.add(current);
					else {
						current.setCompletionTime(currentTime);
						current.setTurnAroundTime(current.getCompletionTime()-current.getArrivalTime());
						current.setWaitingTime(current.getTurnAroundTime()-current.getBurstTime());
						
						totalComp += current.getCompletionTime();
						totalTAT += current.getTurnAroundTime();
			            totalWT += current.getWaitingTime();

			            finishedQueue.add(current);
					}
				}
				
				
				else if (!lowPriority.isEmpty()){
					
					Process current = lowPriority.remove();
					currentTime += current.getRemainingBurstTime();
					current.setRemainingBurstTime(0);
					current.setCompletionTime(currentTime);
					current.setTurnAroundTime(current.getCompletionTime()-current.getArrivalTime());
					current.setWaitingTime(current.getTurnAroundTime()-current.getBurstTime());
					
					totalComp += current.getCompletionTime();
					totalTAT += current.getTurnAroundTime();
		            totalWT += current.getWaitingTime();

		            finishedQueue.add(current);
					
				}
				
				
		}
	}
	
	static void updateReadyQueues(Queue<Process> pendingQueue, Queue<Process> highPriority, 
			Queue<Process> mediumPriority, Queue<Process> lowPriority, int currentTime) {
		
		while(!pendingQueue.isEmpty() && pendingQueue.peek().getArrivalTime() <= currentTime) {
			int priority = pendingQueue.peek().getPriority();
			Process current = pendingQueue.remove();
			
			switch (priority) {
			
			case 1:
				highPriority.add(current);
				break;
				
			case 2:
				mediumPriority.add(current);
				break;
				
			case 3:
				lowPriority.add(current);
				break;
				
			default:
			    throw new IllegalArgumentException("Invalid priority: " + priority);
			}
			
		}
		
		
	}
		
}
 