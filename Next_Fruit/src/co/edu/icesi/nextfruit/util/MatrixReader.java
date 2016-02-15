package co.edu.icesi.nextfruit.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class MatrixReader {
	
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
