package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileInput {

	private ArrayList<String> lines;

	public FileInput() {
		lines = new ArrayList<String>();
		
		// The name of the file to open.
		String fileName = "input.txt";
		String line;
		try {
			FileReader fileReader = new FileReader("input.txt");
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
	}
	
	public ArrayList<String> getLines() {
		return lines;
	}

}
