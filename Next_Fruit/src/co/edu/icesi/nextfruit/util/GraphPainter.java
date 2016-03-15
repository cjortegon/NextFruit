package co.edu.icesi.nextfruit.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Set;

import co.edu.icesi.nextfruit.modules.model.CameraCalibration;

public class GraphPainter {

	public static void paintXYrepresentation(Set<Integer> colors, Dimension windowSize, CameraCalibration calibration, Graphics g) {
		for (Integer color : colors) {
			double[] bgr = ImageUtility.rgb2bgr(color);
			double[] rgb = new double[] {bgr[2], bgr[1], bgr[0]};
			double[] xyz = ColorConverter.rgb2xyz(rgb);
			double[] xyY = ColorConverter.XYZ2xyY(xyz, calibration.getWhiteX());
			//			System.out.println("("+((int)xyY[0])+","+((int)xyY[1])+","+((int)xyY[2])+")");
			g.setColor(new Color(color));
			g.drawRect((int)(xyY[0]*windowSize.getWidth()), (int)((1-xyY[1])*windowSize.getHeight()), 1, 1);
		}
	}

}
