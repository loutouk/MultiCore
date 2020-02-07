package fr.univnantes.multicore.tp1;

/**
 * 
 * @author Matthieu Perrin, inspired from https://github.com/thibaultdelor/InvalidCodeBlog
 */
public class Volatile {

	// TODO: comment/uncomment the two following lines:
	private static int shared = 0;
	//private static volatile int shared = 0;

	public static void main(String[] args) throws InterruptedException {

		/*
		 * Reader thread
		 */
		new Thread(() -> {
			// Write each i as soon as it sees it written to shared
			for(int i = 1; i <= 5; ++i) {
				while (shared < i); // wait until value >= i
				System.out.println("Read shared: " + i);
			}
		}).start();

		Thread.sleep(250);

		/*
		 * Writer thread
		 */
		new Thread(() -> {
			// Every 1/2 second, write the next integer value to shared
			try {
				for(int i = 1; i <= 5; ++i) {
					System.out.println("Write shared to " + i);
					shared = i;
					Thread.sleep(500);
				}
			} catch (InterruptedException e) {}
		}).start();
	}
}
