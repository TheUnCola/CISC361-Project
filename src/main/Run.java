package main;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;

/*
 * Matt Leung
 * Steven Sell
 * 
 * CISC 361: Operating Systems Project 1
 */

public class Run {
	
	public static ArrayList<String> lines;
	
	// Hold Queues
	Queue<Job> hSJF; //Highest Priority
	Queue<Job> hFIFO;
	
	// Ready Queue
	Queue<Job> rQueue;
	
	// Wait Queue
	Queue<Job> wQueue;
	
	// Complete Queue
	Queue<Job> cQueue;
	
	public static void main(String[] args) {
		//Step 1: Read in file
		// The name of the file to open.
	    String fileName = "textfile.txt";
	    String line = null;
	    try {
	        FileReader fileReader = 
	            new FileReader(fileName);
	        BufferedReader bufferedReader = 
	            new BufferedReader(fileReader);
	        while((line = bufferedReader.readLine()) != null) {
	            lines.add(line);
	        }
	        bufferedReader.close();         
	    }
	    catch(FileNotFoundException ex) {
	    	ex.printStackTrace();
	        System.out.println("Unable to open file '" + fileName + "'");                
	    }
	    catch(IOException ex) {
	    	ex.printStackTrace();
	        System.out.println("Error reading file '" + fileName + "'");
	    }
	    
	    //Step 2: Configure System (First Line of Input)
	    String[] systemConfig = lines.get(0).split(" ");
	    int currentTime = Integer.parseInt(systemConfig[1]);
	    //Can input be in a different order ???
	    int totMem = Integer.parseInt(systemConfig[2].substring(systemConfig[2].indexOf("=")+1));
	    int numDev = Integer.parseInt(systemConfig[3].substring(systemConfig[3].indexOf("=")+1));
	    int qTime = Integer.parseInt(systemConfig[4].substring(systemConfig[4].indexOf("=")+1));
	    
	    Sys sys = new Sys(totMem, numDev, numDev, totMem, qTime, currentTime);
	    
	    //Step 3: Loop over each line remaining
	    //Loop on time and each line
	    for(int i = 1; i < lines.size()-1; i++) {
	    	String[] strPart = lines.get(i).split(" ");
	    	for(int j = 0; j < strPart.length-1; j++) {
	    		if(j == 0) {
	    			switch(strPart[j]) {
	    			case "A": 
	    				break;
	    			case "Q":
	    				break;
	    			case "L":
	    				break;
	    			case "D":
	    				break;
	    			default:
	    				System.out.println("Error");
	    				break;
	    			}
	    		}
	    	}
	    }
	}
}
