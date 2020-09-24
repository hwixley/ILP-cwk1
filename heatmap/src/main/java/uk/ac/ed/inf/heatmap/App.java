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
        
        //Define an object to store the relevant data for a given cell in the 10x10 grid
        class GridCell {
        	String rgbString;
        	BoundingBox cellArea;
        }
        
        //Define the borders of our confinement area
        double maxLat = 55.946233; 
        double minLat = 55.942617;
        double maxLng = -3.184319;
        double minLng = -3.192473;
        //Calculate the dimensions of a cell for a 10x10 grid within our confinement area
        double cellLength = Math.abs((maxLng - minLng) / 10); // = 8.154 x 10^-4
        double cellHeight = Math.abs((maxLat - minLat) / 10); // = 3.616 x 10^-4
        
        String geojsonText = "{\"type\"\t: \"FeatureCollection\",\n\t\"features\"\t: [";
        
        //Define the overall confinement area as 'mapArea'
        BoundingBox mapArea = BoundingBox.fromLngLats(minLng, minLat, maxLng, maxLat);
         
        //Define heatmap: this will store the respective rgb-string and BoundingBox values in each cell
        ArrayList<ArrayList<GridCell>> heatmap = new ArrayList<ArrayList<GridCell>>();
        
        //Iterate through our nested 'predictions' ArrayList so we can classify the predictions and calculate cell bounds
        for (int row = 0; row < predictions.size(); row++) {
        	//Define heatmapRow: represents the row of cells at index 'lineNum' in the 'heatmap'
        	ArrayList<GridCell> heatmapRow = new ArrayList<GridCell>();
        	heatmapRow.clear();
        	
        	for (int col = 0; col < predictions.get(row).size(); col++) {
        		Integer prediction = predictions.get(row).get(col);
        		
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
        		
        		//Calculate the confinement areas of each cell in the grid, within the overall confinement area ('mapArea')
        		BoundingBox cellBounds = BoundingBox.fromLngLats(minLng + col*cellLength, maxLat - ((row+1)*cellHeight), minLng + ((col+1)*cellLength), maxLat + row*cellHeight);
        		
        		//Define the GridCell object and assign it
        		GridCell cell = new GridCell();
        		cell.rgbString = colour;
        		cell.cellArea = cellBounds;

        		//Add the classified prediction into the 'heatmapRow' ArrayList
        		heatmapRow.add(col, cell);
        		
        		String cellJson = "\n\t{\"type\"\t\t: \"Feature\",\n\t\t\t\"geometry\"\t: {\"type\" : \"Polygon\",\n\t\t\t\t\"coordinates\" : [[";
        		cellJson += Double.toString(maxLat - ((row+1)*cellHeight)) + "," + Double.toString(maxLng + (col*cellLength)) + "],[";
        		cellJson += Double.toString(maxLat - ((row+1)*cellHeight)) + "," + Double.toString(maxLng + ((col+1)*cellLength)) + "],[";
        		cellJson += Double.toString(maxLat - (row*cellHeight)) + "," + Double.toString(maxLng + ((col+1)*cellLength)) + "],[";
        		cellJson += Double.toString(maxLat - (row*cellHeight)) + "," + Double.toString(maxLng + (col*cellHeight)) + "]]},\n";
        		
        		if ((row == predictions.size()-1) && (col == predictions.size()-1)) {
        			cellJson += "\t\t\t\t\t\"properties\"\t: {\"fill-opacity\" : 0.75, \"rgb-string\" : \"" + colour  + "\", \"fill\" : \"" + colour + "\"}}";
        		} else {
        			cellJson += "\t\t\t\t\t\"properties\"\t: {\"fill-opacity\" : 0.75, \"rgb-string\" : \"" + colour  + "\", \"fill\" : \"" + colour + "\"}},";
        		}
        		
        		
        		
        		
        		geojsonText += cellJson;
        	}
        	//Add the row of classified predictions ('heatmapRow') into the 'heatmap' ArrayList
        	heatmap.add(row, (ArrayList<GridCell>) heatmapRow.clone());
        }
        geojsonText += "\n\t]\n}";
        //System.out.println(heatmap);
        System.out.println(geojsonText);
        
        try {
        	FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/" + "heatmap.geojson");
        	writer.write(geojsonText);
        	writer.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
}
