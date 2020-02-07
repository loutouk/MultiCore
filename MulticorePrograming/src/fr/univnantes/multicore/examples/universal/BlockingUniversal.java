package fr.univnantes.multicore.examples.universal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fr.univnantes.multicore.examples.universal.Universal.*;

/**
 * This class shows a starvation-free universal construction that uses locks
 * @author Matthieu Perrin
 * @param <S> Type of the states of the simulated object
 */
public class BlockingUniversal<S extends State<S>> implements Universal<S> {

	private final S state;
	private final Lock lock;

	/**
	 * Constructor
	 * @param initialState the state of the implemented object at the beginning of the execution
	 * @param fair if true, the construction is starvation-free ; otherwise, it is deadlock-free
	 */
	public BlockingUniversal(S initialState, boolean fair) {
		state = initialState;
		lock = new ReentrantLock(fair);
	}

	/**
	 * Constructor for a deadlock-free lock
	 * @param initialState the state of the implemented object at the beginning of the execution
	 */
	public BlockingUniversal(S initialState) {
		this(initialState, false);
	}

	/**
	 * Performs the given operation in a linearizable manner
	 * @param operation the operation that must be performed
	 * @return a result of the operation, so that it is linearizable
	 */
	public <R> R invoke(Operation<S, R> operation) {
		// Protect the execution of the operation by a lock
		lock.lock();
		R result = operation.invoke(state);
		lock.unlock();
		return result;
	}

}
