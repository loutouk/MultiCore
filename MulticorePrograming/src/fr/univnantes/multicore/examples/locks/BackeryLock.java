package fr.univnantes.multicore.examples.locks;

import fr.univnantes.multicore.examples.snapshot.*;

/**
 * A starvation-free lock by Lamport, that only uses 
 * @author Matthieu Perrin
 */
public class BackeryLock {

	private static class Cell {
		volatile int clock = 0;
		volatile boolean hello = false;
		final long threadId = Thread.currentThread().getId();
	}

	// Can be replaced by an atomicIntegerArray of size the number of threads
	private final SnapshotObject<Cell> array = new CollectObject<Cell>();
	
	public void lock() {
		Cell own = array.read();
		own.hello = true;							// Say hello
		for(Cell other : array.snapshot()) {		// Take a ticket greater that all other tickets
			if(other.clock > own.clock) own.clock = other.clock;
		}
		own.clock++;
	    while(blocked());                         // Wait for your turn
	}
	
	private boolean blocked() {
		Cell own = array.read();
		for(Cell other : array.snapshot()) {
			// Should other go before own?
			if (other.hello) {
				// Lexicographic order on (clock, threadId)
				if (other.clock < own.clock) return true;
				if (other.clock == own.clock && other.threadId < own.threadId) return true;
			}
		}
		return false;
	}

	public void unlock() {
		// Say goodbye
		array.read().hello = false;
	}	
}
