package fr.univnantes.multicore.examples;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import fr.univnantes.multicore.Argument;

public class ProducerConsumer {

	public static void main(String[] args) {

		// The queue is used to transfer information between the two threads
		BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();

		// Consumer thread
		new Thread(() -> {
			try {
				int value = 1;
				do {
					value = queue.take();
					System.out.println(value);
				} while(value != 1);
			} catch (InterruptedException e) { }
		}).start();

		// Producer thread
		new Thread(() -> {
			try {
				int value = Argument.get(args);
				while(value != 1) {
					queue.put(value);
					if(value%2 == 0)
						value = value/2;
					else
						value = value*3+1;
					Thread.sleep(500);
				}
				queue.put(value);
			} catch (InterruptedException e) { }
		}).start();

	}
}