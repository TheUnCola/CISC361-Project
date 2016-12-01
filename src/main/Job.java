package main;

public class Job {

	private int priority;
	private int timeArrive;
	private int process;
	private int jobNum;
	private int mem;
	private int rT;
	private int numDev;
	
	public Job(int priority, int timeArrive, int process, int jobNum, int mem, int rT, int numDev) {
		this.priority = priority;
		this.timeArrive = timeArrive;
		this.process = process;
		this.jobNum = jobNum;
		this.mem = mem;
		this.rT = rT;
		this.numDev = numDev;
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

	public void setNumDev(int numDev) {
		this.numDev = numDev;
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

}
