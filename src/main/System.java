package main;

public class System {
	private int totMem;
	private int numDev;
	private int aDev;
	private int aMem;
	private int qTime;
	private int currTime;
	
	public System(int totMem, int numDev, int aDev, int aMem, int qTime, int currTime) {
		this.totMem = totMem;
		this.numDev = numDev;
		this.aDev = aDev;
		this.aMem = aMem;
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

	public void setaDev(int aDev) {
		this.aDev = aDev;
	}

	public int getaMem() {
		return aMem;
	}

	public void setaMem(int aMem) {
		this.aMem = aMem;
	}

	public int getCurrTime() {
		return currTime;
	}

	public void setCurrTime(int currTime) {
		this.currTime = currTime;
	}
}
