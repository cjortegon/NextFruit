package co.edu.icesi.frutificator.util;

import java.util.ArrayList;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;



/**
 * 
 * @author JuanD
 *
 */
public class RectContour {

	
	MatOfPoint contorno;
	//Arreglo donde:
	//P1: esquina inferior izquieda, P2: esquina superior izquierda,
	//P3: esquina superior derecha,  P4: esquina inferior derecha.
	ArrayList<Point> esquinas;
	
	
	
	public RectContour(MatOfPoint contorno, ArrayList<Point> esquinas){
		this.contorno = contorno;
		this.esquinas = esquinas;
	}
	
	
	public MatOfPoint getContorno() {
		return contorno;
	}

	public void setContorno(MatOfPoint contorno) {
		this.contorno = contorno;
	}

	public ArrayList<Point> getEsquinas() {
		return esquinas;
	}

	public void setEsquinas(ArrayList<Point> esquinas) {
		this.esquinas = esquinas;
	}
	
	
}
