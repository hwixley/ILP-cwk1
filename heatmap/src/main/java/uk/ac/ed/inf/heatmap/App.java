package uk.ac.ed.inf.heatmap;

import java.io.*;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	String predFilePath = args[0];
        
        File predFile = new File(predFilePath);
        BufferedReader br = new BufferedReader(new FileReader(predFile));

        String predictions = br.readLine();
        System.out.println(predictions);
    }
}
