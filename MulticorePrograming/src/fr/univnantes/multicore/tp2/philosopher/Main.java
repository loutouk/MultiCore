package fr.univnantes.multicore.tp2.philosopher;

public class Main {

	public static void main(String args[]) throws InterruptedException {
		int n = 5;
		DinnerTable table = new DinnerTable(n);

		// Start the threads
		Philosopher philosophers[] = new Philosopher[n];
		Thread threads[] = new Thread[n];
		for (int i = 0; i < n; i++) {
			// TODO : Replace starving philosopher by smart ones !
			philosophers[i] = new StarvingPhilosopher(table, i);
			//philosophers[i] = new SmartPhilosopher(table, i);
			threads[i] = new Thread(philosophers[i]);
			threads[i].start();
		}

		// Run the simulation for 5s
		Thread.sleep(5000);
		for (Thread t : threads) t.interrupt();
		for (Thread t : threads) t.join();

		// Write progress per thread
		System.out.println();
		for(Philosopher p : philosophers)
			System.out.println(p.toString() + " ate " + p.timesEaten() +  " times");

	}

}
