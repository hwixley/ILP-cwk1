package uk.ac.ed.inf.heatmap;

import java.io.*;
import java.util.ArrayList;
import java.lang.*;
import com.mapbox.geojson.*;

public class App 
{
    public static void main( String[] args ) throws Exception
    {	
    	//Retrieve predictions.txt as command line argument
    	String predFilePath = args[0];
        
    	//Read the predictions.txt file using BufferedReader
        File predFile = new File(System.getProperty("user.dir") + "/" + predFilePath);
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
        
        //public static Polygon poly = new Polygon();
        
        //Define colourGrid (heatmap): this will store the respective rgb-string values in each cell
        ArrayList<ArrayList<String>> colourGrid = new ArrayList<ArrayList<String>>();
        
        //Iterate through our nested 'predictions' ArrayList so we can classify each of the predictions
        for (int lineNum = 0; lineNum < predictions.size(); lineNum++) {
        	//Define colourLine: represents the row of cells at index 'lineNum' in the heatmap
        	ArrayList<String> colourLine = new ArrayList<String>();
        	colourLine.clear();
        	
        	for (int cellNum = 0; cellNum < predictions.get(lineNum).size(); cellNum++) {
        		Integer prediction = predictions.get(lineNum).get(cellNum);
        		
        		String colour = "";
        		
        		//Classify the given 'prediction' by returning it's appropriate rgb-string
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
        		//Add the classified prediction into the 'colourLine' ArrayList
        		colourLine.add(cellNum, colour);
        	}
        	//Add the row of classified predictions ('colourLine') into the 'colourGrid' ArrayList
        	colourGrid.add(lineNum, (ArrayList<String>) colourLine.clone());
        }
        
        //Define the borders of our confinement area
        double maxLat = 55.946233; 
        double minLat = 55.942617;
        double maxLng = -3.184319;
        double minLng = -3.192473;
        //Calculate the size of a cell for a 10x10 grid within our confinement area
        double cellLength = Math.abs((maxLng - minLng) / 10); // = 8.154 x 10^-4
        double cellHeight = Math.abs((maxLat - minLat) / 10); // = 3.616 x 10^-4
    }
}
