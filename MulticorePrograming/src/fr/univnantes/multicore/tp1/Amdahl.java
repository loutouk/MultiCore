package fr.univnantes.multicore.tp1;

public class Amdahl {

	// TODO: add or remove the synchronized keyword
	//private static synchronized void doSomeWork(int j) { 
	private static void doSomeWork(int j) {
		try {
			Thread.sleep(100);
			System.out.println(Thread.currentThread().getName() + " performed its operation number " + j);
		} catch (InterruptedException e) { }
	}


	public static void main( String args[] ) throws InterruptedException {

		long startTime = System.currentTimeMillis();

		// Start ten threads
		Thread threads [] = new Thread[10];
		for(int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(() -> {
				for(int j = 0; j < 10; j++) 
					doSomeWork(j);
			}, "Thread " + i);
			threads[i].start();
		}

		// Wait until all threads have terminated
		for(Thread t : threads)	t.join();


		// TODO: Predict the expected output
		//   - How long would the execution take if 100% of the program could be parallelized?
		//   - How long would the execution take if 0% of the program could be parallelized?
		//   - How long does it take with/without the synchronized keyword?
		//   - What proportion of the program can be parallelized with/without the synchronized keyword?
		//   - Explain why
		System.out.println(System.currentTimeMillis() - startTime + " milliseconds");
	}

}