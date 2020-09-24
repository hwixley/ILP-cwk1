package uk.ac.ed.inf.heatmap;

import java.io.*;
import java.util.ArrayList;

public class App 
{
    public static void main( String[] args ) throws Exception
    {	
    	//Retrieve predictions.txt as command line argument
    	String predFilePath = "/home/hwixley/Documents/Year3/ILP/cwk1/heatmap/src/main/java/uk/ac/ed/inf/heatmap/predictions.txt"; //args[0];
        
    	//Read the predictions.txt file using BufferedReader
        File predFile = new File(predFilePath);
        BufferedReader br = new BufferedReader(new FileReader(predFile));
        
        //Create ArrayLists to store the predictions.txt data
        ArrayList<ArrayList<Integer>> predictions = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> fileLine = new ArrayList<Integer>();
        
        //Iterate through the lines of the predictions.txt file and store them as Integers in the 'predictions' ArrayList
        String line;
        while ((line = br.readLine()) != null) {
        	fileLine.clear();
        	
        	Boolean lineEnd = false;
        	Integer commaIndex;
        	Integer startIndex = 0;
        	String restOfLine = line;
        	String subString;
        	
        	//Iterates through the predictions on each line and stores them in 'fileLine' which is then added to 'predictions'
        	while (!lineEnd) {
        		startIndex = 0;
        		commaIndex = restOfLine.indexOf(",", startIndex);
        		
        		//Checks if there are no more commas found (meaning the last entry on the given parsed file line)
        		if (commaIndex == -1) {
        			subString = restOfLine;
            		fileLine.add(Integer.parseInt(subString));
        			lineEnd = true;
        			break;
        		} else {
            		subString = restOfLine.substring(startIndex, commaIndex);
            		fileLine.add(Integer.parseInt(subString));
            		startIndex = commaIndex + 2;
            		restOfLine = restOfLine.substring(startIndex, restOfLine.length());
        		}
        	}
        	//All predictions for the given line ('fileLine') have been parsed as Integers and added to 'predictions'
        	predictions.add((ArrayList<Integer>) fileLine.clone());
        }
        
        
        ArrayList<ArrayList<String>> colourGrid = new ArrayList<ArrayList<String>>();
        for (int lineNum = 0; lineNum < predictions.size(); lineNum++) {
        	ArrayList<String> colourLine = new ArrayList<String>();
        	colourLine.clear();
        	
        	for (int cellNum = 0; cellNum < predictions.get(lineNum).size(); cellNum++) {
        		Integer prediction = predictions.get(lineNum).get(cellNum);
        		
        		String colour = "";
        		
        		if (prediction < 32) {
        			colour = "#00f00";
        		} else if (prediction < 64) {
        			colour = "#40ff00";
        		} else if (prediction < 96) {
        			colour = "#80ff00";
        		} else if (prediction < 128) {
        			colour = "#c0ff00";
        		} else if (prediction < 160) {
        			colour = "#ffc000";
        		} else if (prediction < 192) {
        			colour = "#ff8000";
        		} else if (prediction < 224) {
        			colour = "#ff4000";
        		} else if (prediction < 256) {
        			colour = "#ff0000";
        		}
        		colourLine.add(cellNum, colour);
        	}
        	colourGrid.add(lineNum, (ArrayList<String>) colourLine.clone());
        }
        
        System.out.println(colourGrid);
        
        
    }
}
