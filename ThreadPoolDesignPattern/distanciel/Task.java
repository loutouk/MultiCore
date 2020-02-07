package fr.univnantes.multicore.distanciel;

import java.util.concurrent.Callable;

/**
 * All the data needed to build a new block
 */
public class Task implements Callable<Block> {

	public final double x_min;
	public final double y_min;
	public final double x_max;
	public final double y_max;

	public final int x_def;
	public final int y_def;
	public final int x_pix;
	public final int y_pix;

	public final int threshold;

	/**
	 * Builds and stores all the data needed to create a new block
	 * @param x_min minimal abscissa of the block
	 * @param y_min minimal ordinate of the block
	 * @param x_max maximal abscissa of the block
	 * @param y_max maximal ordinate of the block
	 * @param x_def number of pixels in a block horizontally
	 * @param y_def number of pixels in a block vertically
	 * @param threshold Maximal number of iterations before we consider a pixel is in the set
	 */
	public Task(double x_min, double y_min, double x_max, double y_max, int x_def, int y_def, int x_pix, int y_pix, int threshold){
		this.x_min = x_min;
		this.y_min = y_min;
		this.x_max = x_max;
		this.y_max = y_max;

		this.x_def = x_def;
		this.y_def = y_def;
		this.x_pix = x_pix;
		this.y_pix = y_pix;
		
		this.threshold = threshold;
	}

	/**
	 * returns the expected priority level of the task
	 * this is done by evaluating the angles of the block with a very low threshold
	 * @return true if the block should be computed in priority
	 */
	public boolean hasPriority() {
		return !(-1==mandelbrot(x_min, y_min, 10)
				|| -1==mandelbrot(x_max, y_min, 10)
				|| -1==mandelbrot(x_min, y_max, 10)
				|| -1==mandelbrot(x_max, y_max, 10));
	}

	/**
	 * Do the actual work
	 * Choose carefully the thread that executes this method
	 */
	@Override
	public Block call() {
		Block block = new Block(x_def, y_def, x_pix, y_pix);
		double stepX = (x_max - x_min)/x_def;
		double stepY = (y_max - y_min)/y_def;
		for(int x = 0; x < x_def; ++x){
			for(int y = 0; y < y_def; ++y){
				int i = mandelbrot(x_min + stepX * x, y_min + stepY * y, threshold);
				block.setPixel(x, y, i);
			}
		}
		return block;
	}

	/**
	 * Do the math
	 * @param xc real part of a complex number
	 * @param yc imaginary part of a complex number
	 * @param threshold Maximal number of iterations before we consider a point is in the set
	 * @return -1 if xc + i * yc is probably in the set, or an integer between 0 and threshold indicating the time it takes to ensure it is not
	 */
	private int mandelbrot(double xc, double yc, int threshold){
		if(isMainCardioid(xc, yc) || isMainCircle(xc, yc)) return -1;
		double x = xc, y = yc;
		for(int k = 0; k < threshold; k++) {
			double xPrime = x * x - y * y + xc;
			y = 2 * x * y + yc;
			x = xPrime;
			if(x * x + y * y >= 4) return k;
		}
		return -1;
	}

	private boolean isMainCardioid(double x, double y){
		double p2 = (x - .25)*(x - .25) + y*y;
		double p = Math.sqrt(p2);
		return x < p - 2*p*p + .25;
	}
	
	private boolean isMainCircle(double x, double y){
		return (x+1)*(x+1) +y*y < .25*.25;
	}
}
