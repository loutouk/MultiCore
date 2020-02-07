package fr.univnantes.multicore.tp2.unisex;

import java.util.Random;

public class Main {

	public static void main(String argv[]) throws InterruptedException {

		// Parameters
		int n = 10;
		int nbBathroom = 3;

		// TODO : Replace the basic bathroom by a deadlock-free (then starvation-free) one.
		//Bathroom bathroom = new NonParallelBathroom(nbBathroom);
		//Bathroom bathroom = new ParallelBathroom(nbBathroom);
		Bathroom bathroom = new FairParallelBathroom(nbBathroom);
		//////////////////////////////////////////////////////////////////////////////////

		// Start the threads
		Random rand = new Random();
		Person persons[] = new Person[n];
		Thread threads[] = new Thread[n];
		for (int i = 0; i < n; i++) {
			persons[i] = new Person(i, rand.nextBoolean(), bathroom);
			threads[i] = new Thread(persons[i]);
			threads[i].start();
		}

		// Run the simulation for 5s
		Thread.sleep(5000);
		for (Thread t : threads) t.interrupt();
		for (Thread t : threads) t.join();

		// Write progress per thread
		System.out.println();
		for (Person p : persons) 
			System.out.println(p.toString() + " entered the bathroom " + p.numberEntries() +  " times");
	}

}
