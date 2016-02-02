package histogram;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import co.edu.icesi.frutificator.util.Statistics;

public class Histogram {

	private Mat mat;
	private int[] histogram;
	private int[][] rgbHistogram;

	public Histogram(String imagePath) {
		mat = Imgcodecs.imread(imagePath);
		histogram = new int[768];
		rgbHistogram = new int[3][768];
		for (int i = 0; i < mat.height(); i++) {
			for (int j = 0; j < mat.width(); j++) {
				double d[] = mat.get(i, j);
				int pos = (int) (d[0]+d[1]+d[2]);
				addToHistogram(pos, histogram);
				addToHistogram((int)d[2], rgbHistogram[0]);
				addToHistogram((int)d[1], rgbHistogram[1]);
				addToHistogram((int)d[0], rgbHistogram[2]);
			}
		}
		
		// Fix RGB
		for (int i = 255; i >= 0; i--) {
			int red = rgbHistogram[0][i]/3;
			int green = rgbHistogram[1][i]/3;
			int blue = rgbHistogram[2][i]/3;
			rgbHistogram[0][i*3] = red;
			rgbHistogram[0][i*3+1] = red;
			rgbHistogram[0][i*3+2] = red;
			rgbHistogram[1][i*3] = green;
			rgbHistogram[1][i*3+1] = green;
			rgbHistogram[1][i*3+2] = green;
			rgbHistogram[2][i*3] =blue;
			rgbHistogram[2][i*3+1] =blue;
			rgbHistogram[2][i*3+2] =blue;
		}
	}

	private void addToHistogram(int pos, int[] histogram) {
		histogram[pos] += 3;
		if(pos > 1) {
			histogram[pos-2] ++;
			if(pos > 0)
				histogram[pos-1] += 2;
		}
		if(pos < 767) {
			histogram[pos+1] += 2;
			if(pos < 766)
				histogram[pos+2] ++;
		}
	}

	public void removeGrayRegion(int start, int end, double[] fill) {
		for (int i = 0; i < mat.height(); i++) {
			for (int j = 0; j < mat.width(); j++) {
				double d[] = mat.get(i, j);
				int pos = (int) (d[0]+d[1]+d[2]);
				if(pos > start && pos < end) {
					mat.put(i, j, fill);
				}
			}
		}
	}

	public void smoothHistogram() {
		int smooth[] = new int[histogram.length];
		smooth[0] = histogram[0];
		smooth[histogram.length-1] = histogram[histogram.length-1];
		for (int i = 1; i < histogram.length-1; i++) {
			smooth[i] = (histogram[i-1]+histogram[i+1])/2;
		}
		histogram = smooth;
	}

	public void fillHistogram() {
		int smooth[] = new int[histogram.length];
		smooth[0] = histogram[0];
		smooth[histogram.length-1] = histogram[histogram.length-1];
		for (int i = 1; i < histogram.length-1; i++) {
			smooth[i] = Math.max(histogram[i-1], histogram[i+1]);
		}
		histogram = smooth;
	}

	public void smoothFillHistogram() {
		int smooth[] = new int[histogram.length];
		smooth[0] = histogram[0];
		smooth[histogram.length-1] = histogram[histogram.length-1];
		for (int i = 1; i < histogram.length-1; i++) {
			smooth[i] = (histogram[i-1]+histogram[i+1]+Math.max(histogram[i-1], histogram[i+1])*2)/4;
		}
		histogram = smooth;
	}

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

	public Mat getImage() {
		return mat;
	}

	public int getMaxHeight() {
		int max = 0;
		for (int i = 0; i < histogram.length; i++) {
			if(histogram[i] > max)
				max = histogram[i];
		}
		return max;
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

}
