package main;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class Sys {
	private int totMem;
	private int numDev;
	private int aDev;
	private int aMem;
	private int qTime;
	private int currTime;
	
	// Hold Queues
	private Queue<Job> hSJF = new ArrayDeque<Job>(); // Highest Priority
	private Queue<Job> hFIFO = new ArrayDeque<Job>();

	// Ready Queue
	private Queue<Job> rQueue = new ArrayDeque<Job>();

	// Wait Queue
	private Queue<Job> wQueue = new ArrayDeque<Job>();

	// Complete Queue
	private Queue<Job> cQueue = new ArrayDeque<Job>();
	
	// Total Job List (used for easily referencing jobs)
	private ArrayList<Job> allJobs = new ArrayList<Job>();
	
	public Queue<Job> gethSJF() {
		return hSJF;
	}

	public Queue<Job> gethFIFO() {
		return hFIFO;
	}

	public Queue<Job> getrQueue() {
		return rQueue;
	}

	public Queue<Job> getwQueue() {
		return wQueue;
	}

	public Queue<Job> getcQueue() {
		return cQueue;
	}

	public ArrayList<Job> getAllJobs() {
		return allJobs;
	}

	public Sys(int totMem, int numDev, int qTime, int currTime) {
		this.totMem = totMem;
		this.numDev = numDev;
		this.aDev = numDev;
		this.aMem = totMem;
		this.qTime = qTime;
		this.currTime = currTime;
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

	public void setCurrTime(int currTime) {
		this.currTime = currTime;
	}
}
