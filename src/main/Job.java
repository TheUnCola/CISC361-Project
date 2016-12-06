package main;

public class Job {

	private int priority;
	private int timeArrive;
	private int process;
	private int jobNum;
	private int mem;
	private int rT;
	private int numDev;
	private boolean isRunning;
	
	public Job(int timeArrive, int jobNum, int mem, int numDev, int rT, int priority){
		this.priority = priority;
		this.timeArrive = timeArrive;
		this.jobNum = jobNum;
		this.mem = mem;
		this.rT = rT;
		this.numDev = numDev;
		this.isRunning = false;
	}
	
	public Job() {
		//Empty Constructor
	}
	
	public int getNextJobTime(int nextLine, FileInput fi) {
		String[] strPart = fi.getLines().get(nextLine).split(" ");
		return Integer.parseInt(strPart[1]);
	}
	
	public void runJob(Sys sys) {
		// Check Hold Queues and add to Ready Queues if possible
		if (!sys.gethSJF().isEmpty()) {
			if(sys.gethSJF().getFirst().getMem() <= sys.getaMem() && sys.gethSJF().getFirst().getNumDev() <= sys.getaDev()) {
				// Add first position of Hold Queue to ready queue
				sys.getrQueue().addLast(sys.gethSJF().getFirst());
				System.out.println("Adding Job " + sys.gethSJF().getFirst().getJobNum() + " to rQueue");
				// Decrease available memory and devices
				sys.decaDev(sys.gethSJF().getFirst().getNumDev());
				sys.decaMem(sys.gethSJF().getFirst().getMem());
				// Remove from Hold Queue
				sys.gethSJF().removeFirst();
			}
		} else if (!sys.gethFIFO().isEmpty()) {
			if(sys.gethFIFO().getFirst().getMem() <= sys.getaMem() && sys.gethFIFO().getFirst().getNumDev() <= sys.getaDev()) {
				// Add first position of Hold Queue to ready queue
				sys.getrQueue().addLast(sys.gethFIFO().getFirst());
				System.out.println("Adding Job " + sys.gethFIFO().getFirst().getJobNum() + " to rQueue");
				// Decrease available memory and devices
				sys.decaDev(sys.gethFIFO().getFirst().getNumDev());
				sys.decaMem(sys.gethFIFO().getFirst().getMem());
				// Remove from Hold Queue
				sys.gethFIFO().removeFirst();
			}
		}

		Job runningJob = sys.getrQueue().getFirst();
		if (sys.getqCount() > 4) {
			// Rotate jobs (Round Robin)
			if (runningJob.getrT() > 0) {
				sys.getrQueue().addLast(runningJob); // Add to back
				sys.getrQueue().removeFirst(); // Remove from front
				sys.resetqCount();
			}
		}
		runningJob = sys.getrQueue().getFirst();
		
		if (runningJob.getrT() > 0) {
			// Set job to running status
			runningJob.setRunning(true);
			System.out.println("Job " + runningJob.getJobNum() + " is running.");
			System.out.println("rT decreased " + runningJob.getrT() + " -> " + (runningJob.getrT() - 1) + ", qTime=" + (sys.getqCount()+1));
			// Decrease rT of job
			runningJob.decrT();
			sys.incqCount();
		}
		runningJob.setRunning(false);

		// Job is finished
		if (runningJob.getrT() == 0) {
			System.out.println("Job " + runningJob.getJobNum() + " has completed.");
			// Add to Complete Queue
			sys.getcQueue().add(runningJob);
			// Increase system dev and mem
			sys.incaDev(runningJob.getNumDev());
			sys.incaMem(runningJob.getMem());
			// Remove from ready queue
			sys.getrQueue().removeFirst();
		}
	}
	
	public void initNextJob(Sys sys, FileInput fi, int currentLine) {
		String[] strPart = fi.getLines().get(currentLine).split(" ");
		int tempCurrentTime;
		int tempJobNum;
		int tempMem;
		int tempDev;
		int tempRT;
		int tempPriority;
		
		switch (strPart[0]) {
		// New job comes in
		case "A":
			tempCurrentTime = Integer.parseInt(strPart[1]);
			tempJobNum = Integer.parseInt(strPart[2].substring(strPart[2].indexOf("=") + 1));
			tempMem = Integer.parseInt(strPart[3].substring(strPart[3].indexOf("=") + 1));
			tempDev = Integer.parseInt(strPart[4].substring(strPart[4].indexOf("=") + 1));
			tempRT = Integer.parseInt(strPart[5].substring(strPart[5].indexOf("=") + 1));
			tempPriority = Integer.parseInt(strPart[6].substring(strPart[6].indexOf("=") + 1));

			// Create Job (and add to total list)
			Job tempJob = new Job(tempCurrentTime, tempJobNum, tempMem, tempDev, tempRT, tempPriority);
			sys.getAllJobs().add(tempJob);
			System.out.println("Job " + tempJob.getJobNum() + " created");

			// Add to hold queue
			if (tempJob.getPriority() == 1)
				sys.gethSJF().add(tempJob);
			else
				sys.gethFIFO().add(tempJob);
			break;

		// Request for devices
		case "Q":
			tempJobNum = Integer.parseInt(strPart[2].substring(strPart[2].indexOf("=") + 1));
			tempDev = Integer.parseInt(strPart[3].substring(strPart[3].indexOf("=") + 1));

			if (sys.gethSJF().contains(sys.getAllJobs().get(tempJobNum - 1))
					|| sys.gethFIFO().contains(sys.getAllJobs().get(tempJobNum - 1))) {
				// Increase required # of devices for that job
				sys.getAllJobs().get(tempJobNum - 1).incNumDev(tempDev);
			} else if (sys.getrQueue().contains(sys.getAllJobs().get(tempJobNum - 1))) {
				// Remove devices from system if available or put job in wait queue
				if (sys.getaDev() >= tempDev) {
					sys.decaDev(tempDev);
					sys.getAllJobs().get(tempJobNum - 1).incNumDev(tempDev);
				} //else ...
			}
			break;
		// Release of devices
		case "L":
			tempJobNum = Integer.parseInt(strPart[2].substring(strPart[2].indexOf("=") + 1));
			tempDev = Integer.parseInt(strPart[3].substring(strPart[3].indexOf("=") + 1));

			if (sys.gethSJF().contains(sys.getAllJobs().get(tempJobNum - 1))
					|| sys.gethFIFO().contains(sys.getAllJobs().get(tempJobNum - 1))) {
				// Decrease required # of devices for that job (if not yet running)
				sys.getAllJobs().get(tempJobNum - 1).decNumDev(tempDev);
			} else if (sys.getrQueue().contains(sys.getAllJobs().get(tempJobNum - 1))) {
				// Add back devices to system and decrease from job
				sys.incaDev(tempDev);
				sys.getAllJobs().get(tempJobNum - 1).decNumDev(tempDev);
			}
			break;
		case "D":
			break;
		default:
			System.out.println("Error.");
			break;
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public int getProcess() {
		return process;
	}

	public void setProcess(int process) {
		this.process = process;
	}

	public int getNumDev() {
		return numDev;
	}

	public void incNumDev(int numDev) {
		this.numDev += numDev;
	}
	
	public void decNumDev(int numDev) {
		this.numDev -= numDev;
	}

	public int getPriority() {
		return priority;
	}

	public int getTimeArrive() {
		return timeArrive;
	}

	public int getJobNum() {
		return jobNum;
	}

	public int getMem() {
		return mem;
	}

	public int getrT() {
		return rT;
	}
	
	public void decrT() {
		this.rT--;
	}

}
