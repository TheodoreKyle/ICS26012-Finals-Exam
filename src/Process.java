import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

public class Process {
	
		String pid;
		int arrivalTime, burstTime, remainingBurstTime;
		int completionTime, turnAroundTime, waitingTime;
		int priority;
		
		
		Process(String pid, int prio, int aT, int bT) {
			this.pid = pid;
			this.arrivalTime = aT;
			this.burstTime = bT;
			this.remainingBurstTime = bT;
			this.completionTime = 0;
			this.turnAroundTime = 0;
			this.waitingTime = 0;
			this.priority = prio;
		}
		
		String getPid() {
			return pid;
		}
		
		int getArrivalTime() {
			return arrivalTime;
		}
		
		int getBurstTime() {
			return burstTime;
		}
		
		int getRemainingBurstTime() {
			return remainingBurstTime;
		}
		
		int getCompletionTime() {
			return completionTime;
		}
		
		int getTurnAroundTime() {
			return turnAroundTime;
		}
		
		int getWaitingTime() {
			return waitingTime;
		}
		
		int getPriority() {
			return priority;
		}
		
		void setBurstTime(int burstTime) {
			this.burstTime = burstTime;
		}
		
		void setRemainingBurstTime(int remainingburstTime) {
			this.remainingBurstTime = remainingburstTime;
		}
		
		void setCompletionTime(int completionTime) {
			this.completionTime = completionTime;
		}
		
		void setTurnAroundTime(int turnAroundTime) {
			this.turnAroundTime = turnAroundTime;
		}
		
		void setWaitingTime(int waitingTime) {
			this.waitingTime = waitingTime;
		}
		
		public static void sortByArrivalTime(Queue <Process> processes) {
			
			List<Process> sortedProcesses = new ArrayList<>(processes);
			sortedProcesses.sort(Comparator.comparingInt(Process::getArrivalTime));
			
			processes.clear();
			processes.addAll(sortedProcesses);
		}
		
	}

