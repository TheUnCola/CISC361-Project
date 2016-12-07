package main;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class Sys {
	private int totMem;
	private int numDev;
	private int aDev;
	private int aMem;
	private int qTime;
	private int currTime;
	private int qCount;
	private boolean complete;
	
	// Hold Queues
	private ArrayDeque<Job> hSJF; // Highest Priority
	private ArrayDeque<Job> hFIFO;

	// Ready Queue
	private ArrayDeque<Job> rQueue;

	// Wait Queue
	private ArrayDeque<Job> wQueue;

	// Complete Queue
	private ArrayDeque<Job> cQueue;
	
	// Total Job List (used for easily referencing jobs)
	private ArrayList<Job> allJobs;

	public Sys(int totMem, int numDev, int qTime, int currTime) {
		this.totMem = totMem;
		this.numDev = numDev;
		this.aDev = numDev;
		this.aMem = totMem;
		this.qTime = qTime;
		this.currTime = currTime;
		this.qCount = 0;
		this.complete = false;
		hSJF = new ArrayDeque<Job>();
		hFIFO = new ArrayDeque<Job>();
		rQueue = new ArrayDeque<Job>();
		wQueue = new ArrayDeque<Job>();
		cQueue = new ArrayDeque<Job>();
		allJobs = new ArrayList<Job>();
	}

	// No setters for 3 of these since they're initialized and shouldn't be changed
	public int getTotMem() {
		return totMem;
	}

	public int getNumDev() {
		return numDev;
	}

	public int getqTime() {
		return qTime;
	}

	public int getaDev() {
		return aDev;
	}

	public void incaDev(int aDev) {
		this.aDev += aDev;
	}
	
	public void decaDev(int aDev) {
		this.aDev -= aDev;
	}

	public int getaMem() {
		return aMem;
	}

	public void incaMem(int aMem) {
		this.aMem += aMem;
	}
	
	public void decaMem(int aMem) {
		this.aMem -= aMem;
	}

	public int getCurrTime() {
		return currTime;
	}

	public void incCurrTime() {
		this.currTime++;
	}
	
	public ArrayDeque<Job> gethSJF() {
		return hSJF;
	}

	public ArrayDeque<Job> gethFIFO() {
		return hFIFO;
	}

	public ArrayDeque<Job> getrQueue() {
		return rQueue;
	}

	public ArrayDeque<Job> getwQueue() {
		return wQueue;
	}

	public ArrayDeque<Job> getcQueue() {
		return cQueue;
	}

	public ArrayList<Job> getAllJobs() {
		return allJobs;
	}
	
	public int getqCount() {
		return this.qCount;
	}
	
	public void incqCount() {
		if(qCount <= qTime)
			this.qCount++;
		else
			System.out.println("qCount Error!");
	}
	
	public void resetqCount() {
		this.qCount = 0;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}
}
