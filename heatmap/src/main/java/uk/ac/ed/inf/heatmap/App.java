package uk.ac.ed.inf.heatmap;

import java.io.*;

public class App 
{
    public static void main( String[] args ) throws Exception
    {	
    	//Retrieve predictions.txt as command line argument
    	String predFilePath = args[0];
        
        File predFile = new File(predFilePath);
        BufferedReader br = new BufferedReader(new FileReader(predFile));
        
        //Read predictions.txt file and output data into 'predictions' String
        String predictions = br.readLine();
        System.out.println(predictions);
    }
}
