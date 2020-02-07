package fr.univnantes.multicore.distanciel;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 * Class in charge of computing and storing a square image of a part of the Mandelbrot set
 */
public class Block {

	public final int pixel [][];
	public final int x_pix;
	public final int y_pix;

	/**
	 * Creates a new block
	 * @param width Number of pixels in a line of the block
	 * @param height Number of pixels in a column of the block
	 * @param x_pix Abscissa of the top left pixel of the block
	 * @param y_pix Ordinate of the top left pixel of the block
	 */
	public Block(int width, int height, int x_pix, int y_pix){
		this.pixel = new int[width][height];
		this.x_pix = x_pix;
		this.y_pix = y_pix;
	}

	/**
	 * Draws the square block on a given panel
	 * @param g2 object that describes the panel on which to draw
	 * @param colorMap object that describes the color to give to each pixel
	 */
	public void draw(Graphics2D g2, ColorMap colorMap){
		for(int i = 0; i < pixel.length; i++){
			for(int j = 0; j < pixel[0].length; j++){
				Shape point = new Rectangle(x_pix+i, y_pix+j, 1, 1);
				g2.setPaint(colorMap.get(pixel[i][j]));
				g2.fill(point);
			}
		}
	}

	public void setPixel(int x, int y, int i) {
		pixel[x][y] = i;
	}
}
