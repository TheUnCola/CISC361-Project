import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/*
 * Matt Leung
 * Steven Sell
 * 
 * CISC 361: Operating Systems Project 1
 */

public class proj {
	
	public static ArrayList<String> lines;
	
	public proj(String[] args){
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
	        System.out.println("Unable to open file '" + fileName + "'");                
	    }
	    catch(IOException ex) {
	        System.out.println("Error reading file '" + fileName + "'");
	    }
	}
}
