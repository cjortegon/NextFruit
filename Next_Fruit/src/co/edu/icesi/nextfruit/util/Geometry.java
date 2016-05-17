package co.edu.icesi.nextfruit.util;

/**
 * This class helps in geometry calculations.
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

	/**
	 * Finds the angle between 2 given points.
	 * @param xHead x component from the head of the line.
	 * @param yHead y component from the head of the line.
	 * @param xTail x component from the tail of the line.
	 * @param yTail y component from the tail of the line.
	 * @return
	 */
	public static double getAngleOfLineBetweenTwoPoints(double xHead, double yHead, double xTail, double yTail) {
		double xDiff = xHead - xTail;
		double yDiff = yHead - yTail;
		return Math.atan2(yDiff, xDiff);
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

	/**
	 * Finds the distance between 2 points
	 * @param fromX x from the first point.
	 * @param fromY y from the first point.
	 * @param toX x from the second point.
	 * @param toY y from the second point.
	 * @return
	 */
	public static double distance(double fromX, double fromY, double toX, double toY) {
		return Math.sqrt(Math.pow(fromX - toX, 2) + Math.pow(fromY - toY, 2));
	}

	/**
	 * @param angle given angle in radians.
	 * @return The angle in the range from 0 to 2*pi.
	 */
	public static double checkAngle(double angle) {
		if(angle < 0)
			angle += (Math.PI*2);
		else if(angle > (Math.PI*2))
			angle -= (Math.PI*2);
		return angle;
	}

}
