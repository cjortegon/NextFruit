/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.icesi.frutificator.util;

/**
 *
 * @author camilo
 */
public class Geometry {

	final static double EARTH_RADIUS = 6378160; // Radio de la tierra en metros

    public static double distanceBetweenPlaces(double lat1, double lon1, double lat2, double lon2) {
        double dlon = Math.toRadians(lon2 - lon1);
        double dlat = Math.toRadians(lat2 - lat1);

        double a = (Math.sin(dlat / 2) * Math.sin(dlat / 2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * (Math.sin(dlon / 2) * Math.sin(dlon / 2));
        double angle = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return angle * EARTH_RADIUS;
    }
	
	public static double getAngleOfLineBetweenTwoPoints(double xHead, double yHead, double xTail, double yTail) {

//		double xDiff = xTail - xHead;
//		double yDiff = yTail - yHead;
		double xDiff = xHead - xTail;
		double yDiff = yHead - yTail;
		return Math.atan2(yDiff, xDiff);
//		return Math.toDegrees(Math.atan2(yDiff, xDiff)) * Math.PI / 180;
		
//		if(xHead == xTail) {
//			if(yHead > yTail)
//				return Math.PI/2;
//			else
//				return (Math.PI*3)/2;
//		} else if(yHead == yTail) {
//			if(xHead > xTail)
//				return 0;
//			else
//				return Math.PI;
//		} else if(xHead > xTail) { // Derecha
//			if(yHead > yTail) // Cuadrante 1
//			return Math.atan((yHead-yTail)/(xHead-xTail));
//			else // Cuadrante 4
//				return Math.PI*2 - Math.atan((yTail-yHead)/(xHead-xTail));
//		} else { // Izquierda
//			if(yHead > yTail) // Cuadrante 2
//				return Math.PI - Math.atan((yHead-yTail)/(xTail-xHead));
//			else // Cuadrante 3
//				return Math.PI + Math.atan((yTail-yHead)/(xTail-xHead));
//		}
	}

	public static int quadrantAngle(double angle) {
		angle = checkAngle(angle);
		if(angle < Math.PI/2)
			return 1;
		else if(angle < Math.PI)
			return 2;
		else if(angle < Math.PI*(3/4))
			return 3;
		else
			return 4;
	}

	public static double distance(double fromX, double fromY, double toX, double toY) {
		return Math.sqrt(Math.pow(fromX - toX, 2) + Math.pow(fromY - toY, 2));
	}

	public static double checkAngle(double angle) {
		if(angle < 0)
			angle += (Math.PI*2);
		else if(angle > (Math.PI*2))
			angle -= (Math.PI*2);
		return angle;
	}

}
