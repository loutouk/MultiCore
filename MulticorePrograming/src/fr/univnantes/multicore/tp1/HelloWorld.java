package fr.univnantes.multicore.tp1;

public class HelloWorld {
	
	/*
	 * TODO: Use the debugger to display the following outputs.
	 * 
	 * Output 1:
	 * Hello
	 * Multithreaded
	 * World
	 * !
	 *
	 * Output 2:
	 * World
	 * Multithreaded
	 * Hello
	 * !
	 * 
	 * Is it possible to obtain the exclamation mark on the first line?
	 */
	
	public static void main( String args[] ) throws InterruptedException {

		String messages[] = { "Hello", "Multithreaded", "World" };
		Runnable runnables[] = new Example[messages.length];
		Thread threads[] = new Thread[messages.length];

		for( int i = 0 ; i < messages.length ; ++i ) {
			runnables[i] = new Example( messages[i] );
			threads[i] = new Thread( runnables[i] );
			threads[i].start();
		}

		for( Thread t : threads ) t.join();

		System.out.println( "!" );
	}

}


class Example implements Runnable {

	private String message;

	Example( String message ) {
		this.message = message;
	}

	public void run() {
		System.out.println( message );
	}

}
