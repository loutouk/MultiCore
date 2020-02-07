package fr.univnantes.multicore.distanciel;

import java.awt.Color;

/**
 * Class in charge of computing and storing a square image of a part of the Mandelbrot set
 */
public class ColorMap {

	private final Color colorIn;
	private final Color colorOut;
	private final int darkness;
	
	/**
	 * Creates a new ColorMap
	 * @param colorOut general color of points outside of the set
	 * @param darkness the lower, the brighter ; default is 50 
	 * @param colorIn color of point inside the set ; default is black
	 */
	public ColorMap(Color colorOut, int darkness, Color colorIn){
		if(darkness <= 0) this.darkness = 1;
		else this.darkness = darkness;
		this.colorOut = colorOut;
		this.colorIn = colorIn;
	}

	/**
	 * Creates a new ColorMap
	 * @param colorOut general color of points outside of the set
	 * @param darkness the lower, the brighter ; default is 50 
	 */
	public ColorMap(Color colorOut, int darkness){
		this(colorOut, darkness, Color.BLACK);
	}

	/**
	 * Creates a new ColorMap
	 * @param colorOut general color of points outside of the set
	 */
	public ColorMap(Color colorOut){
		this(colorOut, 50, Color.BLACK);
	}

	/**
	 * Associates a color to an integer
	 * @param i value that was returned by Mandelbrot
	 * @return the color in which the data will be drawn on the screen
	 */
	public Color get(int i){
		if(i<0) return colorIn;
		int r = (int) Math.min(255, colorOut.getRed()   * i / darkness);
		int g = (int) Math.min(255, colorOut.getGreen() * i / darkness);
		int b = (int) Math.min(255, colorOut.getBlue()  * i / darkness);
		return new Color(r,g,b);
	}
}
