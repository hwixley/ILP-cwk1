package uk.ac.ed.inf.heatmap;

import java.io.*;
import java.util.ArrayList;

public class App 
{
    public static void main( String[] args ) throws Exception
    {	
    	//Retrieve predictions.txt as command line argument
    	String predFilePath = args[0];
        
    	//Read the predictions.txt file using BufferedReader
        File predFile = new File(predFilePath);
        BufferedReader br = new BufferedReader(new FileReader(predFile));
        
        //Create ArrayLists to store the predictions.txt data
        ArrayList<Integer> predictions = new ArrayList<Integer>();
        ArrayList<Integer> fileLine = new ArrayList<Integer>();
        
        //Iterate through the lines of the predictions.txt file and store them as Integers in the 'predictions' ArrayList
        String line;
        while ((line = br.readLine()) != null) {
        	System.out.println(line);
        }
    }
}
