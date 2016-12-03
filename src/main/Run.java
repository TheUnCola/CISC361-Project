package main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/*
 * Matt Leung
 * Steven Sell
 * 
 * CISC 361: Operating Systems Project 1
 */

public class Run {

	static ArrayList<String> lines = new ArrayList<String>();

	public static void main(String[] args) {
		// Step 1: Read in file
		// The name of the file to open.
		String fileName = "input.txt";
		String line;
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fileReader);
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			br.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Error reading file '" + fileName + "'");
		}

		// Step 2: Configure System (First Line of Input)
		String[] systemConfig = lines.get(0).split(" ");
		int currentTime = Integer.parseInt(systemConfig[1]);
		int totMem = Integer.parseInt(systemConfig[2].substring(systemConfig[2].indexOf("=") + 1));
		int numDev = Integer.parseInt(systemConfig[3].substring(systemConfig[3].indexOf("=") + 1));
		int qTime = Integer.parseInt(systemConfig[4].substring(systemConfig[4].indexOf("=") + 1));

		Sys sys = new Sys(totMem, numDev, qTime, currentTime);
		System.out.println("System created with Mem=" + totMem + ", Dev=" + numDev + ", QTime=" + qTime + ", CurrentTime=" + currentTime);

		// Step 3: Loop over each line remaining
		// Loop on time and each line
		int quantumCount = 0;
		for (int i = 1; i < lines.size() - 1; i++) {
			while (currentTime < 20) {
				String[] strPart = lines.get(i).split(" ");
				//Line can begin running at its specified arrival time
				if(strPart[1].equals(currentTime)) {
					int tempCurrentTime;
					int tempJobNum;
					int tempMem;
					int tempDev;
					int tempRT;
					int tempPriority;
					switch (strPart[0]) {
					//New job comes in
					case "A":
						tempCurrentTime = Integer.parseInt(strPart[1]);
						tempJobNum = Integer.parseInt(strPart[2].substring(strPart[2].indexOf("=") + 1));
						tempMem = Integer.parseInt(strPart[3].substring(strPart[3].indexOf("=") + 1));
						tempDev = Integer.parseInt(strPart[4].substring(strPart[4].indexOf("=") + 1));
						tempRT = Integer.parseInt(strPart[5].substring(strPart[5].indexOf("=") + 1));
						tempPriority = Integer.parseInt(strPart[6].substring(strPart[6].indexOf("=") + 1));
	
						//Create Job (and add to total list)
						Job tempJob = new Job(tempCurrentTime, tempJobNum, tempMem, tempDev, tempRT, tempPriority);
						sys.getAllJobs().add(tempJob);
						System.out.println("Job " + tempJob.getJobNum() + " created");
						
						//Add to hold queue (we're adding everything then taking first elements to run)
						if(tempJob.getPriority() == 1) sys.gethSJF().add(tempJob);
						else sys.gethFIFO().add(tempJob);
						break;
						
					//Request for devices
					case "Q":
						tempJobNum = Integer.parseInt(strPart[2].substring(strPart[2].indexOf("=") + 1));
						tempDev = Integer.parseInt(strPart[3].substring(strPart[3].indexOf("=") + 1));
					
						if(sys.gethSJF().contains(sys.getAllJobs().get(tempJobNum-1)) || sys.gethFIFO().contains(sys.getAllJobs().get(tempJobNum-1))) {
							//Increase required # of devices for that job
							sys.getAllJobs().get(tempJobNum-1).incNumDev(numDev);
						} else if(sys.getrQueue().contains(sys.getAllJobs().get(tempJobNum-1))) {
							//Remove devices from system if available or put job in wait queue
							if(sys.getaDev() >= tempDev) {
								sys.decaDev(tempDev);
								sys.getAllJobs().get(tempJobNum-1).incNumDev(tempDev);
							}
						}
						break;
					//Release of devices
					case "L":
						tempJobNum = Integer.parseInt(strPart[2].substring(strPart[2].indexOf("=") + 1));
						tempDev = Integer.parseInt(strPart[3].substring(strPart[3].indexOf("=") + 1));
						
						if(sys.gethSJF().contains(sys.getAllJobs().get(tempJobNum-1)) || sys.gethFIFO().contains(sys.getAllJobs().get(tempJobNum-1))) {
							//Decrease required # of devices for that job (if not yet running)
							sys.getAllJobs().get(tempJobNum-1).decNumDev(numDev);
						} else if(sys.getrQueue().contains(sys.getAllJobs().get(tempJobNum-1))) {
							//Add back devices to system and decrease from job
							sys.incaDev(tempDev);
							sys.getAllJobs().get(tempJobNum-1).decNumDev(tempDev);
						}
						break;
					case "D":
						break;
					default:
						System.out.println("Error.");
						break;
					}
					
					//Check Hold Queues and add to Ready Queues if possible
					if(!sys.gethSJF().isEmpty() && sys.gethSJF().peek().getMem() <= sys.getaMem() && sys.gethSJF().peek().getNumDev() <= sys.getaDev()) {
						//Add first position of Hold Queue to ready queue
						sys.getrQueue().add(sys.gethSJF().peek());
						//Decrease available memory and devices
						sys.decaDev(sys.gethSJF().peek().getNumDev());
						sys.decaMem(sys.gethSJF().peek().getMem());
						//Remove from Hold Queue
						sys.gethSJF().remove();
					} else if(sys.gethFIFO().isEmpty() && sys.gethFIFO().peek().getMem() <= sys.getaMem() && sys.gethFIFO().peek().getNumDev() <= sys.getaDev()) {
						//Add first position of Hold Queue to ready queue
						sys.getrQueue().add(sys.gethFIFO().peek());
						//Decrease available memory and devices
						sys.decaDev(sys.gethFIFO().peek().getNumDev());
						sys.decaMem(sys.gethFIFO().peek().getMem());
						//Remove from Hold Queue
						sys.gethFIFO().remove();
					}
					
					//---------- Ready, Wait, Run, Complete Queues below ----------
					
					Job runningJob = sys.getrQueue().peek();
					if(quantumCount < 4) {
						if(runningJob.getrT() > 0) {
							//Set job to running status
							runningJob.setRunning(true);
							System.out.println("Job " + runningJob.getJobNum() + " is running.");
							System.out.println("rT decreased " + runningJob.getrT() + " -> " + (runningJob.getrT()-1));
							//Decrease rT of job
							runningJob.decrT();
							quantumCount++;
						}
					}
					
					runningJob.setRunning(false);
					
					//Rotate jobs (Round Robin)
					if(quantumCount == 4) {
						sys.getrQueue().add(runningJob); //Add to back
						sys.getrQueue().remove(); //Remove from front
						quantumCount = 0;
					}
					
					//Job is finished
					if(runningJob.getrT() == 0) {
						System.out.println("Job " + runningJob.getJobNum() + " has completed.");
						
						//Add to Complete Queue
						sys.getcQueue().add(runningJob);
						//Increase system dev and mem
						sys.incaDev(runningJob.getNumDev());
						sys.incaMem(runningJob.getMem());
						
						//Remove from ready queue
						sys.getrQueue().remove();
					}
					
					
					//
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				currentTime++;
				System.out.println("System Mem=" + totMem + ", Dev=" + numDev + ", QTime=" + qTime + ", CurrentTime=" + currentTime);
			}
		}
	}
}
