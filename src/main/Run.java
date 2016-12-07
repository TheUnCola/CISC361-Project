package main;

import org.apache.commons.lang3.StringUtils;

/*
 * Matt Leung
 * Steven Sell
 * 
 * CISC 361: Operating Systems Project 1
 */

public class Run {

	public static void main(String[] args) {
		// Step 1: Read in file
		FileInput fi = new FileInput();

		// Step 2: Configure System (First Line of Input)
		String[] systemConfig = fi.getLines().get(0).split(" ");
		int currentTime = Integer.parseInt(systemConfig[1]);
		int totMem = Integer.parseInt(systemConfig[2].substring(systemConfig[2].indexOf("=") + 1));
		int numDev = Integer.parseInt(systemConfig[3].substring(systemConfig[3].indexOf("=") + 1));
		int qTime = Integer.parseInt(systemConfig[4].substring(systemConfig[4].indexOf("=") + 1));

		Sys sys = new Sys(totMem, numDev, qTime, currentTime);
		System.out.println("System created with Mem=" + totMem + ", Dev=" + numDev + ", QTime=" + qTime
				+ ", CurrentTime=" + currentTime);

		System.out.println("+------+-------+-----+-----+----+--------+------+------+----+");
		System.out.format("| %4s | %5s | %1s | %3s | %2s | %6s | %4s | %4s | %2s | \n", "Job", "Time", "Dev", "Mem",
				"rT", StringUtils.center("Pos", 6), "aMem", "aDev", "Qt");
		System.out.println("+------+-------+-----+-----+----+--------+------+------+----+");

		// Step 3: Loop until all jobs have completed
		int currentLine = 0;
		Job j = new Job();
		while (!sys.isComplete()/*
								 * && sys.getCurrTime() <
								 * j.getNextJobTime(currentLine+1, fi)
								 */) {

			// If time == arrival of next line pause and initialize job
			if (j.getNextJobTime(currentLine + 1, fi) == sys.getCurrTime()) {
				currentLine++;
				j.initNextJob(sys, fi, currentLine);
			}

			// Execute
			if (!sys.getAllJobs().isEmpty())
				j.runJob(sys);

			/*try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
			sys.incCurrTime();

			if (j.getNextJobType(currentLine + 1, fi).equals("D")
					&& j.getNextJobTime(currentLine + 1, fi) <= sys.getCurrTime() && sys.gethSJF().isEmpty()
					&& sys.gethFIFO().isEmpty() && sys.getwQueue().isEmpty() && sys.getrQueue().isEmpty()) {
				System.out.println(sys.getCurrTime());
				sys.setComplete(true);
			}
		}
		System.out.println("System has completed all jobs.");
	}
}
