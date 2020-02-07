package fr.univnantes.multicore.examples.snapshot;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ExamplePi {
	
	private static class Cell {
		final int inCircle;
		final int outCircle;
		Cell(int in, int out) {
			this.inCircle = in;
			this.outCircle = out;
		}
	}
	
	public static void main (String[] args) throws InterruptedException {
		int nbThreads = 100;
		int nbIterations = 10000;

		SnapshotObject<Cell> array = new WaitFreeSnapshotObject<Cell>();

		for (int i=0; i < nbThreads; i++){
			new Thread(() -> {
				array.update(new Cell(0,0));
				for (int j=0; j < nbIterations; j++){

					Cell cell = array.read();

					double x = ThreadLocalRandom.current().nextDouble();
					double y = ThreadLocalRandom.current().nextDouble();
					
					if(x*x+y*y > 1)
						array.update(new Cell(cell.inCircle, cell.outCircle+1));
					else
						array.update(new Cell(cell.inCircle+1, cell.outCircle));
					
					int in = 0;
					int out = 0;
					List<Cell> cells = array.snapshot();
					for(Cell c : cells) {
						in += c.inCircle;
						out += c.outCircle;
					}
					System.out.println(4.0 * in / (in+out));
					
				}
			}).start();
		}
	}
}
