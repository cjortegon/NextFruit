package co.edu.icesi.nextfruit.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * This static class reads values from a csv file and load a double matrix with those values.
 * @author JuanD
 *
 */
public class MatrixReader {
	
	/**
	 * Load a common matrix from the csv files.
	 * @param path, the path of the csv file containing the matrix to use.
	 * @return Matrix of type double containing the loaded data.
	 * @throws IOException
	 */
	public static double[][] read(String path) throws IOException{
		double[][] matrixM = new double[3][3];
		String[] numbers = new String[3];
		String line = "";
		
		FileReader fR = new FileReader(path);
		BufferedReader bR = new BufferedReader(fR);
		
		for(int i = 0; (line=bR.readLine()) != null; i++){	
			numbers = line.split(";");
			
			for(int j = 0; j < numbers.length; j++){
				matrixM[i][j] = Double.parseDouble(numbers[j].toString());
			}
		}

		return matrixM;
	}
	
}
