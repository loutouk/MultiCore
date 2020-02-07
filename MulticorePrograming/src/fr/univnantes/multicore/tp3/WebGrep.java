package fr.univnantes.multicore.tp3;

import java.io.IOException;
import java.util.LinkedList;

public class WebGrep {

	private final static LinkedList<String> explored = new LinkedList<String>();

	/*
	 *  TODO : the search must be parallelized between the given number of threads
	 */
	private static void explore(String address) {
		try {
			/*
			 *  Check that the page was not already explored and adds it
			 * TODO : the check and insertion must be atomic. Explain why. How to do it?
			 */
			if(!explored.contains(address)) {
				explored.add(address);
				// Parse the page to find matches and hypertext links
				ParsedPage page = Tools.parsePage(address);
				if(!page.matches().isEmpty()) {
					/* 
					 * TODO: Tools.print(page) is not thread safe...
					 */
					Tools.print(page);
					// Recursively explore other pages
					for(String href : page.hrefs()) explore(href);
				}
			}
		} catch (IOException e) {/*We could retry later...*/}
	}


	public static void main(String[] args) throws InterruptedException, IOException {
		// Initialize the program using the options given in argument
		if(args.length == 0) Tools.initialize("-celt --threads=1000 Nantes https://fr.wikipedia.org/wiki/Nantes");
		else Tools.initialize(args);

		// TODO Just do it!
		System.err.println("You must search parallelize the application between " + Tools.numberThreads() + " threads!\n");

		// Get the starting URL given in argument
		for(String address : Tools.startingURL()) 
			explore(address);
	}
}
