package co.edu.icesi.nextfruit.modules.computervision;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

import co.edu.icesi.nextfruit.modules.model.ColorDistribution;
import co.edu.icesi.nextfruit.modules.model.PolygonWrapper;
import co.edu.icesi.nextfruit.util.ColorConverter;
import co.edu.icesi.nextfruit.util.CumulativeStatistics;
import co.edu.icesi.nextfruit.util.Statistics;

/**
 * This class has utility methods to process an image according to histogram based techniques.
 * @author cjortegon
 */
public class Histogram {

	private Mat mat;
	private int[] histogram;
	private int[][] rgbHistogram;
	private double[] ranges;

	public Histogram(String imagePath) {
		mat = Imgcodecs.imread(imagePath);
	}

	public Histogram(Mat mat) {
		this.mat = mat;
	}

	public void convertToCromaticScale() {
		for (int i = 0; i < mat.height(); i++) {
			for (int j = 0; j < mat.width(); j++) {
				double d[] = mat.get(i, j);
				double sum = (d[0] + d[1] + d[2]) / 255;
				if(sum > 0) {
					d[0] /= sum;
					d[1] /= sum;
					d[2] /= sum;
					mat.put(i, j, d);
				}
			}
		}
	}

	/**
	 * Applies a filter finding the white profile of the picture
	 */
	public void applyWhitePatch() {
		double Rmax = 0, Gmax = 0, Bmax = 0;
		for (int i = 0; i < mat.height(); i++) {
			for (int j = 0; j < mat.width(); j++) {
				double d[] = mat.get(i, j);
				if(d[2] > Rmax)
					Rmax = d[2];
				if(d[1] > Gmax)
					Gmax = d[1];
				if(d[0] > Bmax)
					Bmax = d[0];
			}
		}
		Rmax = 255/Rmax;
		Gmax = 255/Gmax;
		Bmax = 255/Bmax;
		for (int i = 0; i < mat.height(); i++) {
			for (int j = 0; j < mat.width(); j++) {
				double d[] = mat.get(i, j);
				d[0] *= Bmax;
				d[1] *= Gmax;
				d[2] *= Rmax;
				mat.put(i, j, d);
			}
		}
	}

	/**
	 * Calculates statistics from the image in the 3 different channels (R,G,B)
	 * @return a matrix with the statistics from the 3 channels where the first row contains the mean values and the second row contains the standard deviation.
	 */
	public double[][] obtainThreshold() {
		if(rgbHistogram == null)
			generateRGBHistogram(false);
		CumulativeStatistics r = new CumulativeStatistics();
		CumulativeStatistics g = new CumulativeStatistics();
		CumulativeStatistics b = new CumulativeStatistics();
		for (int i = 0; i < 256; i++) {
			if(rgbHistogram[0][i] > 0)
				r.addValue(i, rgbHistogram[0][i]);
			if(rgbHistogram[1][i] > 0)
				g.addValue(i, rgbHistogram[1][i]);
			if(rgbHistogram[2][i] > 0)
				b.addValue(i, rgbHistogram[2][i]);
		}
		return new double[][] {{r.getMean(), g.getMean(), b.getMean()},
			{r.getStandardDeviation()*6, g.getStandardDeviation()*6, b.getStandardDeviation()*6}};
	}

	/**
	 * Starts a luminant histogram empty.
	 * @param size of the histogram.
	 */
	public void generateEmptyLuminanceHistogram(int size) {
		histogram = new int[size];
	}

	/**
	 * Accumulates the histogram in the given number of groups.
	 * @param numberOfRanges to divide the histogram.
	 */
	public void generateRangesFromStatistics(int numberOfRanges) {
		this.ranges = new double[numberOfRanges];
		long sum = 0;
		for (int i = 0; i < histogram.length; i++) {
			sum += histogram[i];
			ranges[(int) ((i*numberOfRanges)/((double)histogram.length))] += histogram[i];
		}
		for (int i = 0; i < ranges.length; i++) {
			ranges[i] /= sum;
		}
	}

	public void increaseLuminancePosition(double value, boolean useAutofill) {
		if(useAutofill)
			addToHistogram((int)((histogram.length-1)*value), histogram);
		else
			histogram[(int)((histogram.length-1)*value)] ++;
	}

	public int generateAllHistograms(boolean useAutoFill, PolygonWrapper polygon) {
		return generateHistogram(useAutoFill, true, true, polygon);
	}

	public int generateGrayscaleHistogram(boolean useAutoFill, PolygonWrapper polygon) {
		return generateHistogram(useAutoFill, true, false, polygon);
	}

	public int generateRGBHistogram(boolean useAutoFill, PolygonWrapper polygon) {
		return generateHistogram(useAutoFill, false, true, polygon);
	}

	public int generateAllHistograms(boolean useAutoFill) {
		return generateHistogram(useAutoFill, true, true, null);
	}

	public int generateGrayscaleHistogram(boolean useAutoFill) {
		return generateHistogram(useAutoFill, true, false, null);
	}

	public int generateRGBHistogram(boolean useAutoFill) {
		return generateHistogram(useAutoFill, false, true, null);
	}

	private int generateHistogram(boolean useAutoFill, boolean gray, boolean rgb, PolygonWrapper polygon) {
		if(gray)
			histogram = new int[768];
		if(rgb)
			rgbHistogram = new int[3][256];
		int count = 0;
		int top = polygon == null ? 0 : (int) Math.ceil(polygon.getTop());
		int bottom = polygon == null ? mat.height() : (int) Math.floor(polygon.getBottom()) - 1;
		int left = polygon == null ? 0 : (int) Math.ceil(polygon.getLeft());
		int right = polygon == null ? mat.width() : (int) Math.floor(polygon.getRight()) - 1;
		for (int i = top; i < bottom; i++) {
			for (int j = left; j < right; j++) {
				if(polygon == null || polygon.contains(new Point(j, i))) {
					count ++;
					double d[] = mat.get(i, j);
					int pos = (int) (d[0]+d[1]+d[2]);
					if(useAutoFill) {
						if(gray)
							addToHistogram(pos, histogram);
						if(rgb) {
							addToHistogram((int)d[2], rgbHistogram[0]);
							addToHistogram((int)d[1], rgbHistogram[1]);
							addToHistogram((int)d[0], rgbHistogram[2]);
						}
					} else {
						if(gray)
							histogram[pos] ++;
						if(rgb) {
							rgbHistogram[0][(int)d[2]] ++;
							rgbHistogram[1][(int)d[1]] ++;
							rgbHistogram[2][(int)d[0]] ++;
						}
					}
				}
			}
		}
		return count;
	}

	private void addToHistogram(int pos, int[] histogram) {
		histogram[pos] += 3;
		if(pos > 1) {
			histogram[pos-2] ++;
			if(pos > 0)
				histogram[pos-1] += 2;
		}
		if(pos < histogram.length-1) {
			histogram[pos+1] += 2;
			if(pos < histogram.length-2)
				histogram[pos+2] ++;
		}
	}

	public void removeGrayRegion(int start, int end, double[] fill) {
		for (int i = 0; i < mat.height(); i++) {
			for (int j = 0; j < mat.width(); j++) {
				double d[] = mat.get(i, j);
				int pos = (int) (d[0]+d[1]+d[2]);
				if(pos >= start && pos <= end) {
					mat.put(i, j, fill);
				}
			}
		}
	}

	/**
	 * Removes some colors from an image using a matching color profile.
	 * @param rgb The mean value to be evaluated.
	 * @param range The sensibility from the real color and the mean value evaluated.
	 * @param matchColor The color to replace all the matches. May be null and matches wont be replaced.
	 * @param notMatchColor The color to replace the colors that didn't match. May be null and those out of the range wont be replaced.
	 */
	public void filterFigureByColorProfile(double[] rgb, double[] range, double[] matchColor, double[] notMatchColor) {
		if(matchColor != null || notMatchColor != null) {
			range[0] /= 2;
			range[1] /= 2;
			range[2] /= 2;
			int minR = (int) (rgb[0] - range[0]), maxR = (int) (rgb[0] + range[0]);
			int minG = (int) (rgb[1] - range[1]), maxG = (int) (rgb[1] + range[1]);
			int minB = (int) (rgb[2] - range[2]), maxB = (int) (rgb[2] + range[2]);
			for (int i = 0; i < mat.height(); i++) {
				for (int j = 0; j < mat.width(); j++) {
					double[] d = mat.get(i, j);
					boolean match = (d[0] > minB && d[0] < maxB &&
							d[1] > minG && d[1] < maxG &&
							d[2] > minR && d[2] < maxR
							);
					if(match) {
						if(matchColor != null) {
							mat.put(i, j, matchColor);
						}
					} else {
						if(notMatchColor != null) {
							mat.put(i, j, notMatchColor);
						}
					}
				}
			}
		}
	}

	/**
	 * Removes everything from the image that is outside from the given polygon.
	 * @param polygon Polygon to filter the image.
	 * @param insideColor The replacing color inside the polygon. May be null and the inside region will be kept as original.
	 * @param outsideColor The replacing color outside the polygon. May be null and the outside region will be kept as original.
	 */
	public void filterFigureWithPolygon(PolygonWrapper polygon, double[] insideColor, double[] outsideColor) {
		if(insideColor == null && outsideColor == null)
			return;
		for (int i = 0; i < mat.height(); i++) {
			for (int j = 0; j < mat.width(); j++) {
				if(polygon.contains(new Point(j, i))) {
					if(insideColor != null) {
						mat.put(i, j, insideColor);
					}
				} else {
					if(outsideColor != null) {
						mat.put(i, j, outsideColor);
					}
				}
			}
		}
	}

	/**
	 * Removes some colors from an image using a matching gray profile.
	 * @param gray The mean value to be evaluated.
	 * @param range The sensibility from the real color and the mean value evaluated.
	 * @param matchColor The color to replace all the matches. May be null and matches wont be replaced.
	 * @param notMatchColor The color to replace the colors that didn't match. May be null and those out of the range wont be replaced.
	 */
	public void filterFigureByGrayProfile(int gray, int range, double[] matchColor, double[] notMatchColor) {
		if(matchColor != null || notMatchColor != null) {
			int max = gray + range;
			int min = gray - range;
			for (int i = 0; i < mat.height(); i++) {
				for (int j = 0; j < mat.width(); j++) {
					double[] d = mat.get(i, j);
					boolean match = (d[0] > min && d[0] < max);
					if(match) {
						if(matchColor != null) {
							mat.put(i, j, matchColor);
						}
					} else {
						if(notMatchColor != null) {
							mat.put(i, j, notMatchColor);
						}
					}
				}
			}
		}
	}

	/**
	 * Removes the peaks from the histogram to make it more natural.
	 */
	public void smoothHistogram() {
		int smooth[] = new int[histogram.length];
		smooth[0] = histogram[0];
		smooth[histogram.length-1] = histogram[histogram.length-1];
		for (int i = 1; i < histogram.length-1; i++) {
			smooth[i] = (histogram[i-1]+histogram[i+1])/2;
		}
		histogram = smooth;
	}

	/**
	 * Fills the empty spaces in the histogram making an average between the neighbors.
	 */
	public void fillHistogram() {
		int smooth[] = new int[histogram.length];
		smooth[0] = histogram[0];
		smooth[histogram.length-1] = histogram[histogram.length-1];
		for (int i = 1; i < histogram.length-1; i++) {
			smooth[i] = Math.max(histogram[i-1], histogram[i+1]);
		}
		histogram = smooth;
	}

	/**
	 * Makes the histogram to be softer.
	 */
	public void smoothFillHistogram() {
		int smooth[] = new int[histogram.length];
		smooth[0] = histogram[0];
		smooth[histogram.length-1] = histogram[histogram.length-1];
		for (int i = 1; i < histogram.length-1; i++) {
			smooth[i] = (histogram[i-1]+histogram[i+1]+Math.max(histogram[i-1], histogram[i+1])*2)/4;
		}
		histogram = smooth;
	}

	/**
	 * Uses statistical methods to make the histogram to look smooth.
	 */
	public void statisticalSmothHistogram() {
		int smooth[] = new int[histogram.length];
		smooth[0] = histogram[0];
		smooth[1] = histogram[1];
		smooth[histogram.length-2] = histogram[histogram.length-2];
		smooth[histogram.length-1] = histogram[histogram.length-1];
		for (int i = 2; i < histogram.length-2; i++) {
			Statistics stat = new Statistics();
			stat.addValue(histogram[i-2]);
			stat.addValue(histogram[i-1]);
			stat.addValue(histogram[i]);
			stat.addValue(histogram[i+1]);
			stat.addValue(histogram[i+2]);
			double mean = stat.getMean();
			if(Math.abs(mean - histogram[i]) < stat.getStandardDeviation()/2)
				smooth[i] = histogram[i];
			else
				smooth[i] = (int) mean;
		}
		histogram = smooth;
	}

	/**
	 * Calculates the statistics for all the present colors in the histogram.
	 * @param polygon region to filter the statistical analysis. May be null.
	 * @return a Collection of ColorDistribution containing the repetitions of each color.
	 */
	public Collection<ColorDistribution> getStatisticalColors(PolygonWrapper polygon) {
		HashMap<Integer, ColorDistribution> map = new HashMap<>();
		if(polygon != null) {
			Iterator<Point> iterator = polygon.getIterator();
			while(iterator.hasNext()) {
				Point p = iterator.next();
				double[] color = mat.get((int)p.y, (int)p.x);
				int c = ColorConverter.bgr2rgb(color);
				ColorDistribution colorDistribution = map.get(c);
				if(colorDistribution == null) {
					colorDistribution = new ColorDistribution(c);
					map.put(c, colorDistribution);
				} else {
					colorDistribution.repeat();
				}
			}
		} else {
			for (int i = 0; i < mat.rows(); i++) {
				for (int j = 0; j < mat.cols(); j++) {
					double[] color = mat.get(i, j);
					int c = ColorConverter.bgr2rgb(color);
					ColorDistribution colorDistribution = map.get(c);
					if(colorDistribution == null) {
						colorDistribution = new ColorDistribution(c);
						map.put(c, colorDistribution);
					} else {
						colorDistribution.repeat();
					}
				}
			}
		}
		return map.values();
	}

	/**
	 * Calculates the maximum value from the gray scale histogram.
	 * @return
	 */
	public int getMaxHeight() {
		int max = 0;
		for (int i = 0; i < histogram.length; i++) {
			if(histogram[i] > max)
				max = histogram[i];
		}
		return max;
	}

	/**
	 * Saves the image in the selected location.
	 * @param filename
	 */
	public void save(String filename) {
		Imgcodecs.imwrite(filename, mat);
	}

	// ******************** GETTERS *********************

	public Mat getImage() {
		return mat;
	}

	/**
	 * <pre> generateRangesFromStatistics must be called first.
	 * @return Returns the generated ranges in the method generateRangesFromStatistics(int).
	 */
	public double[] getRanges() {
		return ranges;
	}

	public int[] getHistogram() {
		return histogram;
	}

	public int[] getRedHistogram() {
		return rgbHistogram[0];
	}

	public int[] getGreenHistogram() {
		return rgbHistogram[1];
	}

	public int[] getBlueHistogram() {
		return rgbHistogram[2];
	}

	// ******************** GETTERS *********************

}
