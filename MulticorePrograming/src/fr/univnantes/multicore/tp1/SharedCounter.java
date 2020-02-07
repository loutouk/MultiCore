package fr.univnantes.multicore.tp1;

import fr.univnantes.multicore.Argument;

public class SharedCounter {

	private static int value = 0;

	// TODO : invert comments on next two lines
	// synchronized static void Increment() {
	static void increment() {
		++value;
	}

	// TODO : invert comments on next two lines
	// synchronized static void criticalSectionDec() {
	static void decrement() {
		--value;
	}	
	
	public static void main( String args[] ) throws InterruptedException {
		// Get the number of iterations in the loops executed by the threads
		int iterations = Argument.get(args, "Number of loop iterations?");

		// Create two threads
		Thread threads [] = new Thread[2];
		for(int i = 0; i < threads.length; i++) {
			// Create the runnable task (see below for a brief note on Lambda expressions)
			Runnable runnable = () -> { 
				for(int j = 0; j < iterations; j++) {
					increment();
					decrement();
				}
			};
			threads[i] = new Thread(runnable); // Create the thread
		}

		for(Thread t : threads) t.start();		// Start all threads
		for(Thread t : threads) t.join(); 		// Wait until all threads have terminated

		// TODO: Predict the expected output
		// Explain the observation
		System.out.println("Value: " + value);
	}

}

/*
 * A brief note on Lambda expressions:
 * 
 * The instruction:
 * 
 * Runnable runnable = () -> {...};
 * 
 * is interpreted as the anonymous class:
 * 
 * Runnable runnable = new Runnable () {
 *   public void run() { 
 *     ...
 *   }
 * };
 * 
 * that is itself equivalent to:
 * 
 * Runnable runnable = new MyRunnable();
 * 
 * class MyRunnable implements Runnable {
 *   public void run() { 
 *     ...
 *   }
 * }
 * 
 * @see https://www.jmdoudoux.fr/java/dej/chap-lambdas.htm for more explanations on lambda expressions
 */