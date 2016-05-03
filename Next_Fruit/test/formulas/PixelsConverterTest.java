package formulas;

import org.opencv.core.Point;

import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;

public class PixelsConverterTest {

	public static void main(String[] args) {

		double pxPerCm = 2;
		CameraCalibration calibration = new CameraCalibration(null, pxPerCm, null, 0, 0, 0, "");

		Point[] box = new Point[] {
				new Point(0, 2),
				new Point(4, 6),
				new Point(8, 6),
				new Point(12, 2),
				new Point(4, 2),
				new Point(2, 0),
		};

		PolygonWrapper px = new PolygonWrapper(box, false, null);
		PolygonWrapper cm = new PolygonWrapper(box, false, calibration);

		System.out.println("In px: area="+px.getArea()+" perimeter="+px.getPerimeter());
		System.out.println("In cm: area="+cm.getArea()+" perimeter="+cm.getPerimeter());

	}

}
