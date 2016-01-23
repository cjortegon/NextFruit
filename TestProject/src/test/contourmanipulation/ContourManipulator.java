package test.contourmanipulation;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;



/**
 * 
 * @author JuanD
 *
 *	Por defecto estan comentareados las llamadas a los metodos drawContour, drawCorners y
 *	saveImage. Para guardar imagenes del proceso como trazabilidad visual, descomentarear
 *	estos llamados y escoger un directorio para guardar las imagenes.
 *
 */
public class ContourManipulator {

	private Mat image;

	public ContourManipulator(String path){
		image = Imgcodecs.imread(path);
	}

	public Mat getImage() {
		return image;
	}

	/**
	 * Metodo que dada una imagen, encuentra el contorno rectangular maximo y lo recorta,
	 * endereza y centra en la nueva imagen que retorna.
	 * @param imagenACentrar
	 * @return
	 */
	public void centrarImagen(){
		List<MatOfPoint> contornos = findContours();	
		RectContour contornoRectangularMayor = findMaxRectangleContour(contornos);
		image = findWarpPerspective(contornoRectangularMayor);
	}

	/**
	 * Metodo que retorna una lista con objetos MatOfPoints que representan los contornos
	 * encontrados en la imagen.
	 * @param image
	 * @return
	 */
	private List<MatOfPoint> findContours(){
		//
		//	Considerar que los valores siguientes sean parametrizables.
		//
		int lowTreshold = 60;
		int ratio = 2;
		//
		//	Considerar que los valores anteriores sean parametrizables.
		//
		Mat imgGrises = new Mat();
		Imgproc.cvtColor(image, imgGrises, Imgproc.COLOR_BGR2GRAY);
		Imgproc.Canny(imgGrises, imgGrises, lowTreshold, lowTreshold*ratio);
		//saveImage("CannyImage.jpg", imgGrises);
		List<MatOfPoint> contornos = new ArrayList<MatOfPoint>();	
		Mat hierarchy = new Mat();
		Imgproc.findContours(imgGrises, contornos, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		//Pintar contornos
		drawContour(contornos, image, hierarchy);
		return contornos;
	}

	/**
	 * Metodo que encuentra el contorno maximo(y las esquinas de este) de una imagen,
	 * y retorna un objeto de tipo RectContour con dicha informacion.
	 * @param contornos
	 * @param image
	 * @return
	 */
	private RectContour findMaxRectangleContour(List<MatOfPoint> contornos){
		double maxArea = -1;
		int IMaxArea = -1;
		MatOfPoint contornoRetorno = null; 
		MatOfPoint2f curvaAprox = new MatOfPoint2f();
		MatOfPoint2f curvaMax = new MatOfPoint2f();
		List<MatOfPoint> contornosMayores = new ArrayList<MatOfPoint>();

		for(int i = 0; i < contornos.size(); i++){
			MatOfPoint contornoTemporal = contornos.get(i);
			double areaContorno = Imgproc.contourArea(contornoTemporal);

			if(areaContorno > maxArea){
				int sizeContorno = (int)contornoTemporal.total();
				Imgproc.approxPolyDP(new MatOfPoint2f(contornoTemporal.toArray()), curvaAprox, sizeContorno*0.05, true);
				maxArea = areaContorno;   	

				if(curvaAprox.total() == 4){
					curvaMax = curvaAprox;
					maxArea = areaContorno;
					IMaxArea = i;
					contornosMayores.add(contornoTemporal);
					contornoRetorno = contornoTemporal;
				}

			}

		}

		//
		//	Aqui se hallan los puntos en las esquinas del contorno
		//

		//	Imagen con el rectangulo mas largo encontrado
		Mat m = new Mat(image.size(), CvType.CV_8U);
		Imgproc.cvtColor(m, m, Imgproc.COLOR_BayerBG2BGR);

		//Esto pinta el contorno
		//Imgproc.drawContours(img, contornos, IMaxArea, new Scalar(0, 0, 0), 1);
		//saveImage("MaxContour.jpg", img);

		double temporal[] = curvaMax.get(0,0);
		Point p1 = new Point(temporal[0], temporal[1]);

		temporal = curvaMax.get(1, 0);
		Point p2 = new Point(temporal[0], temporal[1]);

		temporal = curvaMax.get(2, 0);
		Point p3 = new Point(temporal[0], temporal[1]);

		temporal = curvaMax.get(3, 0);
		Point p4 = new Point(temporal[0], temporal[1]);

		ArrayList<Point> esquinas = new ArrayList<Point>();
		esquinas.add(p1);
		esquinas.add(p2);
		esquinas.add(p3);
		esquinas.add(p4);

		//pinto las esquinas
		//drawCorners(esquinas, img);

		RectContour contornoMaximo = new RectContour(contornoRetorno, esquinas);
		return contornoMaximo;
	}

	/**
	 * Metodo que recorta, endereza y centra un contorno de interes, dada una imagen.
	 * @param image
	 * @param rectangulo
	 * @return
	 */
	private Mat findWarpPerspective(RectContour rectangulo){
		MatOfPoint contornoRetorno = rectangulo.getContorno();
		ArrayList<Point> puntosFuente = rectangulo.getEsquinas();
		Point p1 = puntosFuente.get(0);
		Point p2 = puntosFuente.get(1);
		Point p3 = puntosFuente.get(2);
		Point p4 = puntosFuente.get(3);
		ArrayList<Point> fuente = new ArrayList<Point>();
		fuente.add(p2);
		fuente.add(p3);
		fuente.add(p4);
		fuente.add(p1);

		//Aqui valido la rotacion(en grados) del elemento para decidir hacia que lado se debe
		//girar para que quede centrado correctamente.
		//Aclaracion -> angulo positivo implica que el elemento esta inclinado hacia la derecha.
		//			 -> angulo negativo implica que el elemento esta inclinado hacia la izquierda.
		MatOfPoint2f tamanioMinImg = new MatOfPoint2f(contornoRetorno.toArray());
		RotatedRect areaMinima = Imgproc.minAreaRect(tamanioMinImg);
		double anguloInclinacion = areaMinima.angle;
		if(areaMinima.size.width < areaMinima.size.height+1){
			anguloInclinacion = anguloInclinacion+90;
		}

		//Mat que representa los puntos de la imagen img
		Mat inicioM = Converters.vector_Point2f_to_Mat(fuente);

		double anchoInferior = Math.sqrt(((Math.pow((p3.x - p4.x), 2))+(Math.pow((p3.y - p4.y),2))));
		double anchoSuperior = Math.sqrt(((Math.pow((p2.x - p1.x), 2))+(Math.pow((p2.y - p1.y),2))));
		double anchoMaximo = Math.max(anchoInferior, anchoSuperior);

		double alturaDerecha = Math.sqrt((Math.pow((p2.x - p3.x), 2))+(Math.pow((p2.y - p3.y), 2)));
		double alturaIzquierda = Math.sqrt((Math.pow((p1.x - p4.x), 2))+(Math.pow((p1.y - p4.y), 2)));
		double alturaMaxima = Math.max(alturaDerecha, alturaIzquierda);

		Mat retorno = new Mat();

		//Inclinado a la derecha
		if(anguloInclinacion > 0){
			List<Point> destino = new ArrayList<Point>();
			//En caso de que se vea una parte exterior a la superficie de interes,
			//restarle 1(o alguna peque�a cantidad) a alturaMaxima y a anchoMaximo.
			destino.add(new Point(0,alturaMaxima));
			destino.add(new Point(anchoMaximo,alturaMaxima));
			destino.add(new Point(anchoMaximo,0));
			destino.add(new Point(0,0));
			Mat finM = Converters.vector_Point2f_to_Mat(destino);

			Mat perspectiveTransform = Imgproc.getPerspectiveTransform(inicioM, finM);
			Imgproc.warpPerspective(image, retorno, perspectiveTransform, new Size(anchoMaximo, alturaMaxima), Imgproc.INTER_CUBIC);
		}
		//Inclinado a la izquierda o sin inclinacion
		else{
			List<Point> destino = new ArrayList<Point>();
			destino.add(new Point(0,0));
			destino.add(new Point(0,alturaMaxima-1));
			destino.add(new Point(anchoMaximo-1,alturaMaxima-1));
			destino.add(new Point(anchoMaximo-1,0));
			Mat finM = Converters.vector_Point2f_to_Mat(destino);

			Mat perspectiveTransform = Imgproc.getPerspectiveTransform(inicioM, finM);
			Imgproc.warpPerspective(image, retorno, perspectiveTransform, new Size(anchoMaximo, alturaMaxima), Imgproc.INTER_CUBIC);	
		}
		//Guarda la imagen
		//saveImage("TransformedImage.jpg", retorno);
		return retorno;
	}

	/**
	 * Metodo que guarda una imagen en disco usando la variable PATH, mas el nombre recibido
	 * por parametro.
	 * @param directorio
	 * @param img
	 */
	//	private void saveImage(String directorio, Mat img){
	//		Imgcodecs.imwrite(PATH+directorio, img);
	//	};

	/**
	 * Metodo que dibuja los contornos de la imagen recibida en verde.
	 * @param contornos
	 * @param img
	 * @param hierarchy
	 */
	private void drawContour(List<MatOfPoint> contornos, Mat img, Mat hierarchy){
		MatOfPoint2f fuente = new MatOfPoint2f();
		MatOfPoint2f destino = new MatOfPoint2f();
		Scalar color =  new Scalar(0, 250, 0);

		for (int i = 0; i < contornos.size(); i++) {
			contornos.get(i).convertTo(fuente, CvType.CV_32FC2);
			Imgproc.approxPolyDP(fuente, destino, 0.01*Imgproc.arcLength(fuente, true), true);
			destino.convertTo(contornos.get(i),  CvType.CV_32S);
			//Imgproc.drawContours(img, contornos, i, color, 2, 8, hierarchy, 0, new Point());
		}
		//		saveImage("F:\\ContourImage.jpg", img);
	}

	/**
	 * Metodo que pinta cuatro puntos dados de una imagen, que representan las esquinas
	 * o puntos de interes.
	 * @param esquinas
	 * @param img
	 */
	private void drawCorners(ArrayList<Point> esquinas, Mat img){
		Point p1 = esquinas.get(0);
		Point p2 = esquinas.get(1);
		Point p3 = esquinas.get(2);
		Point p4 = esquinas.get(3);

		//p1 -> rojo
		Imgproc.circle(img, new Point(p1.x, p1.y), 20, new Scalar(255, 0, 0), 5); 
		//p2 -> verde
		Imgproc.circle(img, new Point(p2.x, p2.y), 20, new Scalar(0, 255, 0), 5); 
		//p3 -> azul
		Imgproc.circle(img, new Point(p3.x, p3.y), 20, new Scalar(0, 0, 255), 5);
		//p4 -> Amarillo
		Imgproc.circle(img, new Point(p4.x, p4.y), 20, new Scalar(0, 255, 255), 5);

		//saveImage("Corners.jpg", img);
	}

}
