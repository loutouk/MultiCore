package fr.univnantes.multicore.distanciel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * 
 * @author Matthieu Perrin
 */
public class Client extends JPanel implements ActionListener {

	private final Server server;

	private int x_block_size = 50;
	private int y_block_size = 50;
	private int x_nb_blocks = 12;
	private int y_nb_blocks = 8;

	private int threshold = 1000;

	private double x_min = -2.25;
	private double y_min = -1;
	private double x_max = 0.75;
	private double y_max = 1;

	private Future<Block> blocks[];
	private long beginningTime;
	private boolean started = false;
	private boolean done = false;
	private ColorMap colorMap = new ColorMap(Color.white);

	private static final long serialVersionUID = 1L;
	private final Timer timer = new Timer(100, this);


	/**
	 * Creates the client
	 * @param server reference to the server that manages the computation of the blocks
	 */
	public Client(Server server){
		this.server = server;
	}

	/**
	 * Sets the size of the frame
	 * Cannot be called after start()
	 * The size of the frame is determined by the size of a block and the number of blocks, horizontally and vertically
	 * @param x_nb_blocks  number of blocks in a line
	 * @param y_nb_blocks  number of blocks in a column
	 * @param x_block_size number of pixel horizontally in a block
	 * @param y_block_size number of pixel vertically in a block
	 */
	public void setResolution(int x_nb_blocks, int y_nb_blocks, int x_block_size, int y_block_size) {
		if(started)	throw new RuntimeException("Already started");
		this.x_nb_blocks  = x_nb_blocks;
		this.y_nb_blocks  = y_nb_blocks;
		this.x_block_size = x_block_size;
		this.y_block_size = y_block_size;
	}

	/**
	 * Sets the threshold used in the computation of the Mandelbrot function
	 * Cannot be called after start()
	 * @param threshold Maximal number of iterations before we consider a pixel is in the set
	 */
	public void setThreshold(int threshold) {
		if(started)	throw new RuntimeException("Already started");
		this.threshold = threshold;
	}

	/**
	 * Sets the part of the complex plan that is represented on the image
	 * Cannot be called after start()
	 * @param x_min minimal abscissa of the drawn part of the plan
	 * @param y_min minimal ordinate of the drawn part of the plan
	 * @param x_max maximal abscissa of the drawn part of the plan
	 * @param y_max maximal ordinate of the drawn part of the plan
	 */
	public void setCoordinates(double x_min, double y_min, double x_max, double y_max) {
		if(started)	throw new RuntimeException("Already started");
		this.x_min = x_min;
		this.y_min = y_min;
		this.x_max = x_max;
		this.y_max = y_max;
	}

	/**
	 * Sets the map that assigns colors to pixel given the result of the Mandelbrot function
	 * @param colorMap transforms integers into colors
	 */
	public void setColor(ColorMap colorMap) {
		this.colorMap = colorMap;
	}

	/**
	 * Starts the computation of the image and shows the window frame
	 */
	@SuppressWarnings("unchecked")
	public void start() {
		started = true;
		timer.start();
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		beginningTime = System.currentTimeMillis();
		blocks = (Future<Block>[]) new Future[x_nb_blocks*y_nb_blocks];
		double xsize = (x_max - x_min)/(x_nb_blocks-1);
		double ysize = (y_max - y_min)/(y_nb_blocks-1);
		for(int x = 0; x < x_nb_blocks; x++){
			for(int y = 0; y < y_nb_blocks; y++){
				Task bs = new Task(
						x_min + x * xsize,
						y_min + y * ysize,
						x_min + (x+1) * xsize, 
						y_min + (y+1) * ysize, 
						x_block_size,
						y_block_size,
						x*x_block_size, 
						y*y_block_size, 
						threshold);
				blocks[x * y_nb_blocks + y] = server.getBlock(bs);
			}
		}
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			frame.setSize(new Dimension(x_nb_blocks*x_block_size, y_nb_blocks*y_block_size));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setTitle("The Mandelbrot Set");
			frame.getContentPane().add(this);
			frame.setVisible(true);
		});
	}

	/**
	 * The panel is drawn every 100ms.
	 * Should only be called by the system.
	 */
	@Override
	public void actionPerformed(ActionEvent ev){
		if(ev.getSource()==timer){
			repaint();
		}
	}

	/**
	 * Called when the window needs to be refreshed
	 * Should only be called by the system, use repaint() instead
	 */
	@Override
	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		try {
			int nbDone = 0;
			for(int i = 0; i < blocks.length; i++){
				/**
				 * TODO (just to attract your attention...)
				 * blocks are drawn only when after they have been computed
				 */
				if (blocks[i].isDone()){
					blocks[i].get().draw(g, colorMap);
					nbDone++;
				}
			}
			if(!done && nbDone == blocks.length){
				done = true;
				System.out.println("Computation terminated within " + (double)(System.currentTimeMillis() - beginningTime) / 1000 + " s.");
			}
		} catch (Exception e) {
			throw new Error("Future.get is not supposed to block when isDone returned true");
		}
	}

}
