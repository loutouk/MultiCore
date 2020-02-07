package fr.univnantes.multicore.examples.universal;

import java.util.concurrent.ThreadLocalRandom;

import fr.univnantes.multicore.examples.universal.Universal.State;

/**
 * An example of an object implemented using a universal construction
 * A wait-free, linearizable counter
 * @author Matthieu Perrin
 */
public class Example {

	// Class representing the internal state of the object: an pair of integers
	protected static class Cell implements State<Cell> {
		public int inCircle = 0;
		public int outCircle = 0;
		public Cell (int in, int out) {
			inCircle = in;
			outCircle = out;
		}
		@Override
		public Cell copy() {
			return new Cell(inCircle, outCircle);
		}
	}

	
	public static void main (String[] args) throws InterruptedException {

		/*int nbThreads = 100;
		int nbIterations = 10000;
		
		Universal<Cell> universal = new WaitFreeUniversal<Cell>(new Cell(0,0));
		
		for (int i=0; i < nbThreads; i++){
			new Thread(() -> {
				for (int j=0; j < nbIterations; j++){

					System.out.println(universal.invoke(cell -> {

						double x = ThreadLocalRandom.current().nextDouble();
						double y = ThreadLocalRandom.current().nextDouble();

						if(x*x+y*y > 1)
							cell.outCircle++;
						else
							cell.inCircle++;

						return 4.0 * cell.inCircle / (cell.inCircle+cell.outCircle);
					}));
				}
			}).start();
		}*/
	}


}
