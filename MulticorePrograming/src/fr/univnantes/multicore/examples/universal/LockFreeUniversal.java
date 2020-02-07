package fr.univnantes.multicore.examples.universal;
import java.util.concurrent.atomic.AtomicReference;

import fr.univnantes.multicore.examples.universal.Universal.*;


/**
 * This class shows a lock-free universal construction based on the compareAndSet operation
 * @author Matthieu Perrin
 * @param <S> Type of the states of the simulated object
 */
public class LockFreeUniversal<S extends State<S>> implements Universal<S> {

	private final AtomicReference<S> state;

	public LockFreeUniversal(S initialState) {
		state = new AtomicReference<S>(initialState);
	}

	/**
	 * Performs the given operation in a linearizable manner
	 * @param operation the operation that must be performed
	 * @return a result of the operation, so that it is linearizable
	 */
	public <R> R invoke(Operation<S, R> operation) {
		S previous, next;
		R result;
		do {
			// Copy the current state and execute the operation
			previous = state.get();
			next = previous.copy();  // This copy is what makes this construction inefficient for large data structures
			result = operation.invoke(next);
			// Attempt a compareAndSet
			// if succeeds, terminates
			// otherwise, retry
		} while(!this.state.compareAndSet(previous, next));
		return result;
	}

}
