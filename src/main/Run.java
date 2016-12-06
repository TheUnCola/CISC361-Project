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

		// Step 3: Loop until all jobs have completed
		int currentLine = 0;
		Job j = new Job();boolean firstJob = false;
		while (sys.getCurrTime() < 100) {
			
			//If time == arrival of next line pause and initialize job
			if(j.getNextJobTime(currentLine+1, fi) == sys.getCurrTime()) {
				currentLine++;
				j.initNextJob(sys, fi, currentLine);
				firstJob = true;
			}
			
			//Execute
			if(firstJob)
				j.runJob(sys);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			sys.incCurrTime();
			System.out.println("System Mem=" + sys.getaMem() + ", Dev=" + sys.getaDev() + ", QTime=" + sys.getqTime()
					+ ", CurrentTime=" + sys.getCurrTime());
		}
	}
	
	
	
	
}
