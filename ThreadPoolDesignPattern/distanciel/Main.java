package fr.univnantes.multicore.distanciel;

import java.awt.Color;

public class Main {

	public static void main(String[] args) {

		/**
		 * TODO: The server has to be replaced by a one using a thread pool
		 */
		Server server = new NaiveServer();

		Client client = new Client(server);
		
		/**
		 * TODO: Tweak the following settings to find a difficulty that 
		 * makes the computation more interesting to observe on your machine
		 * (between 10 and 30 seconds)
		 */

		/*
		 * Choose the part of the complex plan that will be displayed on the screen
		 *   - x_min,
		 *   - y_min,
		 *   - x_max,
		 *   - y_max.
		 */
		//client.setCoordinates(-2.25, -1, 0.75, 1);  // Full Mandelbrot set
		// client.setCoordinates(-1.5, -0.1, -1.2, 0.1);  // Biggest circle
		 client.setCoordinates(-1.5, -0.01, -1.47, 0.01);  // Replicate on the left
		// client.setCoordinates(-0.65, -0.72, -0.32, -0.5);  // Side of the cardioid

		/*
		 * Number of pixels in the window :
		 *   - number of blocks in a line, 
		 *   - number of blocks in a row,
		 *   - number of pixels horizontally in a block,
		 *   - number of pixels vertically in a block.
		 */
		client.setResolution(30, 20, 50, 50);
		
		/*
		 * Maximal number of iterations before we consider it will never diverge
		 */
		client.setThreshold(100000);
		
		/*
		 * The general color of the final picture
		 */
		client.setColor(new ColorMap(new Color(240,160,80), 50));
		
		// The NaiveServer class is so bad it has to cheat
		if(server.getClass() == NaiveServer.class){
			client.setResolution(18, 12, 40, 40);
			client.setThreshold(1000);
		}
		
		client.start();
	}
}
