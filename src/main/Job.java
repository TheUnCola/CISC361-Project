package main;

import java.util.ArrayDeque;

import org.apache.commons.lang3.StringUtils;

public class Job {

	private int priority;
	private int timeArrive;
	private int process;
	private int jobNum;
	private int mem;
	private int rT;
	private int numDev;
	private int timeComplete;
	private boolean isRunning;

	public Job(int timeArrive, int jobNum, int mem, int numDev, int rT, int priority) {
		this.priority = priority;
		this.timeArrive = timeArrive;
		this.jobNum = jobNum;
		this.mem = mem;
		this.rT = rT;
		this.numDev = numDev;
		this.isRunning = false;
		this.setTimeComplete(0);
	}

	public Job() {
		// Empty Constructor
	}

	public int getNextJobTime(int nextLine, FileInput fi) {
		String[] strPart = fi.getLines().get(nextLine).split(" ");
		return Integer.parseInt(strPart[1]);
	}
	
	public String getNextJobType(int nextLine, FileInput fi) {
		String[] strPart = fi.getLines().get(nextLine).split(" ");
		return strPart[0];
	}

	public void runJob(Sys sys) {
		//req while running
			//wQ if not enough aDev
			//interrupt Qt, push to end rQ
			//if not running, drop req
		
		boolean iterateQueue = true;
		//We want to iterate through the queues because more than 1 job can be moved at once
		while(iterateQueue) {
			// Check Wait Queue and remove entry if enough aDev
			if (!sys.getwQueue().isEmpty()) {
				if(sys.getwQueue().getFirst().getNumDev() <= sys.getaDev()) {
					sys.getrQueue().addLast(sys.getwQueue().getFirst());
					sys.decaDev(sys.getwQueue().getFirst().getNumDev());
					printOutput("M" + sys.getwQueue().getFirst().getJobNum(), sys.getCurrTime()+"", sys.getwQueue().getFirst().getNumDev(), sys.getwQueue().getFirst().getMem(),
							sys.getwQueue().getFirst().getrT(), "rQ(" + (sys.getrQueue().size() - 1) + ")", sys.getaMem(), sys.getaDev(), "-", sys); 
					sys.getwQueue().removeFirst();
				} else {
					iterateQueue = false;
				}
			}
			// Check Hold Queues and add to Ready Queues if possible
			else if (!sys.gethSJF().isEmpty()) {
				if (sys.gethSJF().getFirst().getMem() <= sys.getaMem()
						&& sys.gethSJF().getFirst().getNumDev() <= sys.getaDev()) {
					// Add first position of Hold Queue to ready queue
					sys.getrQueue().addLast(sys.gethSJF().getFirst());
					// Decrease available memory and devices
					sys.decaDev(sys.gethSJF().getFirst().getNumDev());
					sys.decaMem(sys.gethSJF().getFirst().getMem());
					printOutput("M" + sys.gethSJF().getFirst().getJobNum(), sys.getCurrTime()+"",
							sys.gethSJF().getFirst().getNumDev(), sys.gethSJF().getFirst().getMem(),
							sys.gethSJF().getFirst().getrT(), "rQ(" + (sys.getrQueue().size() - 1) + ")", sys.getaMem(),
							sys.getaDev(), "-", sys);
					// Remove from Hold Queue
					sys.gethSJF().removeFirst();
				} else {
					iterateQueue = false;
				}
			} else if (!sys.gethFIFO().isEmpty()) {
				if (sys.gethFIFO().getFirst().getMem() <= sys.getaMem()
						&& sys.gethFIFO().getFirst().getNumDev() <= sys.getaDev()) {
					// Add first position of Hold Queue to ready queue
					sys.getrQueue().addLast(sys.gethFIFO().getFirst());
					// Decrease available memory and devices
					sys.decaDev(sys.gethFIFO().getFirst().getNumDev());
					sys.decaMem(sys.gethFIFO().getFirst().getMem());
					printOutput("M" + sys.gethFIFO().getFirst().getJobNum(), sys.getCurrTime()+"",
							sys.gethFIFO().getFirst().getNumDev(), sys.gethFIFO().getFirst().getMem(),
							sys.gethFIFO().getFirst().getrT(), "rQ(" + (sys.getrQueue().size() - 1) + ")", sys.getaMem(),
							sys.getaDev(), "-", sys);
					// Remove from Hold Queue
					sys.gethFIFO().removeFirst();
				} else {
					iterateQueue = false;
				}
			} else {
				iterateQueue = false;
			}
		}

		//If nothing in Ready Queue, continue on loop until something is or done
		if(sys.getrQueue().isEmpty()) return;
		
		//---------------- DOUBLE CHECK THIS AREA OF CODE (IF'S OR ELSE IF'S) ----------------//
		Job runningJob = sys.getrQueue().getFirst();
		if (sys.getqCount() >= sys.getqTime()) {
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
			// Decrease rT of job
			runningJob.decrT();
			sys.incqCount();
			printOutput("J" + runningJob.getJobNum(), sys.getCurrTime() + "-" + (sys.getCurrTime() + 1),
					runningJob.getNumDev(), runningJob.getMem(), runningJob.getrT(), "R", sys.getaMem(), sys.getaDev(),
					""+sys.getqCount(), sys);
		}
		runningJob.setRunning(false);

		// Job is finished
		if (runningJob.getrT() == 0) {
			// Add to Complete Queue
			sys.getcQueue().add(runningJob);
			// Increase system dev and mem
			sys.incaDev(runningJob.getNumDev());
			sys.incaMem(runningJob.getMem());
			// Reset Quantum
			sys.resetqCount();
			// Remove from ready queue
			sys.getrQueue().removeFirst();
			runningJob.setTimeComplete(sys.getCurrTime()+1);
			printOutput("C" + runningJob.getJobNum(), (sys.getCurrTime()+1)+"", runningJob.getNumDev(), runningJob.getMem(), runningJob.getrT(), "cQ(" + (sys.getcQueue().size() - 1) + ")", sys.getaMem(), sys.getaDev(), "-", sys);
		}
		// --------------------------- END CHECK --------------------------- //
	}

	public void initNextJob(Sys sys, FileInput fi, int currentLine) {
		String[] strPart = fi.getLines().get(currentLine).split(" ");
		int tempCurrentTime;
		int tempJobNum;
		int tempMem;
		int tempDev;
		int tempRT;
		int tempPriority;
		String tempPos = "";

		switch (strPart[0]) {
		// New job comes in
		case "A":
			tempCurrentTime = Integer.parseInt(strPart[1]);
			tempJobNum = Integer.parseInt(strPart[2].substring(strPart[2].indexOf("=") + 1));
			tempMem = Integer.parseInt(strPart[3].substring(strPart[3].indexOf("=") + 1));
			tempDev = Integer.parseInt(strPart[4].substring(strPart[4].indexOf("=") + 1));
			tempRT = Integer.parseInt(strPart[5].substring(strPart[5].indexOf("=") + 1));
			tempPriority = Integer.parseInt(strPart[6].substring(strPart[6].indexOf("=") + 1));
			
			// If job takes more mem/dev than the total system has, reject it
			if(tempMem > sys.getTotMem() || tempDev > sys.getNumDev()) {
				System.out.println("Job " + tempJobNum + " too large so it is rejected.");
				break;
			}
			
			// Create Job (and add to total list)
			Job tempJob = new Job(tempCurrentTime, tempJobNum, tempMem, tempDev, tempRT, tempPriority);
			sys.getAllJobs().add(tempJob);

			// Add to hold queue
			if (tempJob.getPriority() == 1) {
				sys.gethSJF().add(tempJob);
				printOutput("I" + tempJob.getJobNum(), Integer.toString(sys.getCurrTime()), tempJob.getNumDev(),
						tempJob.getMem(), tempJob.getrT(), "hQ1(" + (sys.gethSJF().size() - 1) + ")", sys.getaMem(),
						sys.getaDev(), "-", sys);
			} else {
				sys.gethFIFO().add(tempJob);
				printOutput("I" + tempJob.getJobNum(), Integer.toString(sys.getCurrTime()), tempJob.getNumDev(),
						tempJob.getMem(), tempJob.getrT(), "hQ2(" + (sys.gethFIFO().size() - 1) + ")", sys.getaMem(),
						sys.getaDev(), "-", sys);
			}
			break;

		// Request for devices
		case "Q":
			tempJobNum = Integer.parseInt(strPart[2].substring(strPart[2].indexOf("=") + 1));
			tempDev = Integer.parseInt(strPart[3].substring(strPart[3].indexOf("=") + 1));
			if(sys.getAllJobs().get(tempJobNum - 1).getNumDev() + tempDev > sys.getNumDev()) {
				// Reject request since there aren't enough total devices
				printOutput("R" + tempJobNum, sys.getCurrTime()+"", tempDev,
						sys.getAllJobs().get(tempJobNum - 1).getMem(), sys.getAllJobs().get(tempJobNum - 1).getrT(),
						"-", sys.getaMem(), sys.getaDev(), "-", sys);
				System.out.println("There aren't enough total devices on the system to support this request.");
			} else if ((!sys.getrQueue().isEmpty() && !sys.getrQueue().getFirst().equals(sys.getAllJobs().get(tempJobNum - 1))) || sys.gethSJF().contains(sys.getAllJobs().get(tempJobNum - 1))
					|| sys.gethFIFO().contains(sys.getAllJobs().get(tempJobNum - 1))) {
				// Reject request since job isn't running
				printOutput("R" + tempJobNum, sys.getCurrTime()+"", tempDev,
						sys.getAllJobs().get(tempJobNum - 1).getMem(), sys.getAllJobs().get(tempJobNum - 1).getrT(),
						"-", sys.getaMem(), sys.getaDev(), "-", sys);
				System.out.println("The job isn't running so this request is rejected.");
			} else if (sys.getrQueue().getFirst().equals(sys.getAllJobs().get(tempJobNum - 1))) {
				// Remove devices from system if available or put job in wait queue
				if (tempDev <= sys.getaDev()) {
					sys.decaDev(tempDev);
					sys.getAllJobs().get(tempJobNum - 1).incNumDev(tempDev);
					// Moving to end of Ready Queue and reseting Quantum
					sys.getrQueue().addLast(sys.getAllJobs().get(tempJobNum - 1)); // Add to back
					sys.getrQueue().removeFirst(); // Remove from front
					sys.resetqCount();
					printOutput("R" + tempJobNum, sys.getCurrTime() + "", tempDev,
							sys.getAllJobs().get(tempJobNum - 1).getMem(), sys.getAllJobs().get(tempJobNum - 1).getrT(),
							"rQ("+(sys.getrQueue().size()-1)+")", sys.getaMem(), sys.getaDev(), "-", sys);
				} else if(sys.getAllJobs().get(tempJobNum - 1).getNumDev() + tempDev <= sys.getNumDev()) {
					// Add back mem/dev before increasing job amount
					sys.incaDev(sys.getAllJobs().get(tempJobNum - 1).getNumDev());
					sys.incaMem(sys.getAllJobs().get(tempJobNum - 1).getMem());
					// Add to Waiting Queue and remove from Ready Queue if not enough aDev
					sys.getAllJobs().get(tempJobNum - 1).incNumDev(tempDev);
					sys.getwQueue().addLast(sys.getAllJobs().get(tempJobNum - 1));
					sys.getrQueue().remove(sys.getAllJobs().get(tempJobNum - 1));
					//Reset Quantum so next Job can start back at 0
					sys.resetqCount();
					printOutput("R" + tempJobNum, sys.getCurrTime() + "", tempDev,
							sys.getAllJobs().get(tempJobNum - 1).getMem(), sys.getAllJobs().get(tempJobNum - 1).getrT(),
							"wQ(" + (sys.getwQueue().size() - 1) + ")", sys.getaMem(), sys.getaDev(), "-", sys);
				}
				//Double check in this part for avoiding errors
			}
			break;
		// Release of devices
		case "L":
			tempJobNum = Integer.parseInt(strPart[2].substring(strPart[2].indexOf("=") + 1));
			tempDev = Integer.parseInt(strPart[3].substring(strPart[3].indexOf("=") + 1));

			// Decrease required # of devices for that job (if not yet running)
			if (sys.gethSJF().contains(sys.getAllJobs().get(tempJobNum - 1))
					|| sys.gethFIFO().contains(sys.getAllJobs().get(tempJobNum - 1))) {
				sys.getAllJobs().get(tempJobNum - 1).decNumDev(tempDev);
				// Find where job is and what position for printing
				if (sys.gethSJF().contains(sys.getAllJobs().get(tempJobNum - 1))) {
					int i = 0;
					for (Job j : sys.gethSJF()) {
						if (j.equals(sys.getAllJobs().get(tempJobNum - 1)))
							tempPos = "hQ1(" + i + ")";
						i++;
					}
				} else {
					int i = 0;
					for (Job j : sys.gethFIFO()) {
						if (j.equals(sys.getAllJobs().get(tempJobNum - 1)))
							tempPos = "hQ2(" + i + ")";
						i++;
					}
				}
				printOutput("L" + tempJobNum, sys.getCurrTime() + "", tempDev,
						sys.getAllJobs().get(tempJobNum - 1).getMem(), sys.getAllJobs().get(tempJobNum - 1).getrT(),
						tempPos, sys.getaMem(), sys.getaDev(), "-", sys);
			} else if (sys.getrQueue().contains(sys.getAllJobs().get(tempJobNum - 1))) {
				// Add back devices to system and decrease from job (if running)
				sys.incaDev(tempDev);
				sys.getAllJobs().get(tempJobNum - 1).decNumDev(tempDev);
				int i = 0;
				for (Job j : sys.getrQueue()) {
					if (j.equals(sys.getAllJobs().get(tempJobNum - 1)))
						tempPos = "rQ(" + i + ")";
					i++;
				}
				printOutput("L" + tempJobNum, sys.getCurrTime() + "", tempDev,
						sys.getAllJobs().get(tempJobNum - 1).getMem(), sys.getAllJobs().get(tempJobNum - 1).getrT(),
						tempPos, sys.getaMem(), sys.getaDev(), "-", sys);
			}
			break;
		case "D":
			//End Execution at time 9999 regardless of Job states
			if(sys.getCurrTime() == 9999) {
				System.out.println("\nSystem has reached its end state");
				System.out.println("+------+--------+----+-----+-----+");
				System.out.format("| %4s | %6s | %2s | %3s | %3s | \n", "Job", StringUtils.center("Pos", 6), "rT",
						"TT", "WTT");
				System.out.println("+------+--------+----+-----+-----+");
				for(Job j : sys.getAllJobs()) {
					if(findJob(j,sys).equals("rQ(0)"))
						tempPos = "R";
					else
						tempPos = findJob(j,sys);
					int tempWTT = (j.getTimeComplete()-j.getTimeArrive());
					if(j.getrT() > 0)
						tempWTT = (j.getTimeComplete()-j.getTimeArrive())/j.getrT();
					System.out.format("| %4s | %6s | %2s | %3s | %3s | \n", "J"+j.getJobNum(),tempPos,j.getrT(),j.getTimeComplete()-j.getTimeArrive(),tempWTT);
					System.out.println("+------+--------+----+-----+-----+");
				}
				sys.setComplete(true);
			} else {
				//Prints Jobs, Pos (rQ(0) is running), rT, and all Queues' contents
				System.out.println("\nSYSTEM OUTPUT BEGIN");
				System.out.println("+------+--------+----+");
				System.out.format("| %4s | %6s | %2s | \n", StringUtils.center("Job",4), StringUtils.center("Pos", 6), "rT");
				System.out.println("+------+--------+----+");
				for(Job j : sys.getAllJobs()) {
					if(findJob(j,sys).equals("rQ(0)"))
						tempPos = "R";
					else
						tempPos = findJob(j,sys);
					System.out.format("| %4s | %6s | %2s | \n", StringUtils.center("J"+j.getJobNum(),4),StringUtils.center(tempPos,6),j.getrT());
					System.out.println("+------+--------+----+");
				}
				System.out.println("hQ1:" + printQueue(sys.gethSJF()) + " hQ2:" + printQueue(sys.gethFIFO()) + " rQ:" + printQueue(sys.getrQueue()) + " wQ:" + printQueue(sys.getwQueue()) + " cQ:" + printQueue(sys.getcQueue()));
				System.out.println("END SYSTEM OUTPUT \n");
				//Print header for table again
				System.out.println("+------+-------+-----+-----+----+--------+------+------+----+");
				System.out.format("| %4s | %5s | %1s | %3s | %2s | %6s | %4s | %4s | %2s | \n", "Job", "Time", "Dev", "Mem",
						"rT", StringUtils.center("Pos", 6), "aMem", "aDev", "Qt");
				System.out.println("+------+-------+-----+-----+----+--------+------+------+----+");
			}
			break;
		default:
			System.out.println("Unrecognizable Input Code.");
			break;
		}
	}

	public void printOutput(String job, String time, int dev, int mem, int rT, String pos, int aMem, int aDev, String Qt, Sys sys) {
		System.out.format("| %4s | %5s | %3s | %2s | %2s | %6s | %4s | %4s | %2s | ",
				StringUtils.center(job, 4), StringUtils.center(time,5), StringUtils.center(Integer.toString(dev),3), StringUtils.center(Integer.toString(mem),3), rT, StringUtils.center(pos,6), aMem, StringUtils.center(Integer.toString(aDev),3), Qt);
		System.out.println("hQ1:" + printQueue(sys.gethSJF()) + " hQ2:" + printQueue(sys.gethFIFO()) + " rQ:" + printQueue(sys.getrQueue()) + " wQ:" + printQueue(sys.getwQueue()) + " cQ:" + printQueue(sys.getcQueue()));
		System.out.println("+------+-------+-----+-----+----+--------+------+------+----+");
	}
	
	public String printQueue(ArrayDeque<Job> q) {
		String tempString = "";
		for(Job j : q) {
			tempString = tempString.concat("J" + j.jobNum + ",");
		}
		return tempString;
	}
	
	public String findJob(Job j, Sys sys) {
		if(sys.gethSJF().contains(j)) {
			int x = 0;
			for(Job job : sys.gethSJF()) {
				if(job.equals(j)) return "hQ1(" + x + ")";
				x++;
			}
		} else if(sys.gethFIFO().contains(j)) {
			int x = 0;
			for(Job job : sys.gethFIFO()) {
				if(job.equals(j)) return "hQ2(" + x + ")";
				x++;
			}
		} else if(sys.getrQueue().contains(j)) {
			int x = 0;
			for(Job job : sys.getrQueue()) {
				if(job.equals(j)) return "rQ(" + x + ")";
				x++;
			}
		} else if(sys.getwQueue().contains(j)) {
			int x = 0;
			for(Job job : sys.getwQueue()) {
				if(job.equals(j)) return "wQ(" + x + ")";
				x++;
			}
		} else if(sys.getcQueue().contains(j)) {
			int x = 0;
			for(Job job : sys.getcQueue()) {
				if(job.equals(j)) return "cQ(" + x + ")";
				x++;
			}
		} else {
			System.out.println("Job could not be found.");
			
		}
		return "";
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

	public int getTimeComplete() {
		return timeComplete;
	}

	public void setTimeComplete(int timeComplete) {
		this.timeComplete = timeComplete;
	}
}
