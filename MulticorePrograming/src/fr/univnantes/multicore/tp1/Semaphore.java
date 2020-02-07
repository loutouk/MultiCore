package fr.univnantes.multicore.tp1;

import fr.univnantes.multicore.Argument;

//inspired from https://www.mkyong.com/java/java-thread-mutex-and-semaphore-example/
public class Semaphore {
	
	private int counter;

	public Semaphore( int counter ) { 
		this.counter = counter; 
	}

	public synchronized void acquire() throws InterruptedException {
		while( counter <= 0 ) { wait(); } // When no tokens are free, the thread is put on hold.
		--counter; // Token consumption
	}

	synchronized public void release() {
		++counter; // The thread releases his token
		notify();
	}
	

	public static void main(String[] args) {
		// Create a semaphore
		Semaphore semaphore = new Semaphore(Argument.get(args, "number of tokens?"));

		// Start 6 threads
		for(int i = 0; i < 6; i++){
			String name = "Thread " + i;
			new Thread(() -> {
				try {
					
					System.out.println( name + " acquiring token..." );
					semaphore.acquire();
					System.out.println( name + " got the token!" );

					for( int j = 0 ; j < 5 ; ++j ) {
						System.out.println(name + " is performing operation " + j);
						Thread.sleep( 1000 ); // sleep 1 second
					}
					
					System.out.println( name + " releasing token..." );
					semaphore.release();

				}
				catch( InterruptedException e ) { }
			}).start();
		}
	}

}
