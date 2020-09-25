package uk.ac.ed.inf.heatmap;

import java.io.*;
import java.util.ArrayList;
import java.lang.*;
import com.mapbox.geojson.*;

public class App 
{
    public static void main( String[] args ) throws Exception
    {	
    	//Retrieve 'predictions.txt' as command line argument
    	String predFilePath = args[0];
        
    	//Read the 'predictions.txt' file using BufferedReader
        File predFile = new File(System.getProperty("user.dir") + "/" + predFilePath);
        BufferedReader br = new BufferedReader(new FileReader(predFile));
        
        //Create ArrayLists to store the 'predictions.txt' data
        ArrayList<ArrayList<Integer>> predictions = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> fileLine = new ArrayList<Integer>();
        
        //Iterate through the lines of the 'predictions.txt' file and store them as Integers in the 'predictions' ArrayList
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
        			
        		//Else we continue iterating through the predictions on the given line
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
        
        //Define the borders of our confinement area
        double maxLat = 55.946233; 
        double minLat = 55.942617;
        double maxLng = -3.184319;
        double minLng = -3.192473;
        //Calculate the dimensions of a rectangular cell in a 10x10 grid within our confinement area
        double cellLength = Math.abs((maxLng - minLng) / 10); // = 8.154 x 10^-4
        double cellHeight = Math.abs((maxLat - minLat) / 10); // = 3.616 x 10^-4
        
        //Initialize our Geo-JSON file in the format of a FeatureCollection with Polygon features
        String geojsonText = "{\"type\"\t: \"FeatureCollection\",\n\t\"features\"\t: [";
        
        //Iterate through our 'predictions' ArrayList and append a Geo-JSON Polygon to 'geojsonText' for each prediction
        for (int row = 0; row < predictions.size(); row++) {   	
        	for (int col = 0; col < predictions.get(row).size(); col++) {
        		Integer prediction = predictions.get(row).get(col);
        		
        		String colour = "";
        		
        		//Classify the given 'prediction' by returning it's appropriate rgb-string
        		if (prediction < 32) {
        			colour = "#00ff00";
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
        		
        		//Initialization text of a Geo-JSON Polygon feature
        		String cellJson = "\n\t{\"type\"\t\t: \"Feature\",\n\t\t\t\"geometry\"\t: {\"type\" : \"Polygon\",\n\t\t\t\t\"coordinates\" : [[[";
        		
        		//Add the coordinates of the polygon to represent the given cell:
        		//South-West polygon corner
        		cellJson += Double.toString(minLng + (col*cellLength)) + ", " + Double.toString(maxLat - ((row+1)*cellHeight))  + "],[";
        		//South-East polygon corner
        		cellJson += Double.toString(minLng + ((col+1)*cellLength)) + ", " + Double.toString(maxLat - ((row+1)*cellHeight)) + "],[";
        		//North-East polygon corner
        		cellJson += Double.toString(minLng + ((col+1)*cellLength)) + ", " + Double.toString(maxLat - (row*cellHeight)) + "],[";
        		//North-West polygon corner
        		cellJson += Double.toString(minLng + (col*cellLength)) + ", " + Double.toString(maxLat - (row*cellHeight)) + "],[";
        		//South-West polygon corner (repeated in order to create a closed loop)
        		cellJson += Double.toString(minLng + (col*cellLength)) + ", " + Double.toString(maxLat - ((row+1)*cellHeight)) + "]]]},\n";
        		
        		//Add the relevant properties (fill-opacity, rgb-string, fill) to the given Geo-JSON Polygon feature
        		if ((row == predictions.size()-1) && (col == predictions.size()-1)) {
        			cellJson += "\t\t\t\"properties\"\t: {\"fill-opacity\" : 0.75, \"rgb-string\" : \"" + colour  + "\", \"fill\" : \"" + colour + "\"}}";
        		} else {
        			cellJson += "\t\t\t\"properties\"\t: {\"fill-opacity\" : 0.75, \"rgb-string\" : \"" + colour  + "\", \"fill\" : \"" + colour + "\"}},";
        		}
        		//Add this Polygon feature to the FeatureCollection in our Geo-JSON file
        		geojsonText += cellJson;
        	}
        }
        //Add the closing brackets for our FeatureCollection in our Geo-JSON file
        geojsonText += "\n\t]\n}";
        
        //Try write the 'geojsonText' to a file ('heatmap.geojson')
        try {
        	FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/heatmap.geojson");
        	writer.write(geojsonText);
        	writer.close();
        	//Success writing to file 'heatmap.geojson'
        	System.out.println("The air quality predictions from the input file (\"" + predFilePath + "\") have been formatted into a Geo-JSON map.\nGeo-JSON file path:\t" + System.getProperty("user.dir") + "/heatmap.geojson");
        	
        } catch (IOException e) {
        	//Failure writing to file 'heatmap.geojson'
        	e.printStackTrace();
        }
    }
}
