package main;

import java.util.Collection;

public class Device {

	Collection<Job> arrJobs;
	private int devNum;
	
	public Device(int devNum) {
		this.devNum = devNum;
	}

	public int getDevNum() {
		return this.devNum;
	}
}
