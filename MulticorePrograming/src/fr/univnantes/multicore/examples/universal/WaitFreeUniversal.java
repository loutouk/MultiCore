package fr.univnantes.multicore.examples.universal;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import fr.univnantes.multicore.examples.snapshot.*;
import fr.univnantes.multicore.examples.universal.Universal.*;

/**
 * This class shows a wait-free universal construction based on consensus
 * @author Matthieu Perrin
 * @param <S> Type of the states of the simulated object
 */
public class WaitFreeUniversal<S extends State<S>> implements Universal<S> {

	private class Node {
		final Operation<S, ?> operation;
		final AtomicReference<Node> next = new AtomicReference<Node>(null);

		Node(Operation<S, ?> operation) { this.operation = operation; }
	}

	private final Node first = new Node(null);
	private final S initialState;
	private final SnapshotObject<Node> announce = new CollectObject<Node>();

	public WaitFreeUniversal(S initial) {
		initialState = initial;
	}

	/**
	 * Performs the given operation in a linearizable manner
	 * @param operation the operation that must be performed
	 * @return a result of the operation, so that it is linearizable
	 */
	public <R> R invoke(Operation<S, R> operation) {
		// The value that will be returned at the end
		R result = null;
		// Create a new node that must be appended to the list
		Node myNode = new Node(operation);

		// When a thread invokes an operation, it first announces it in the announce array,
		// then reads what the others have announced
		announce.update(myNode);
		List<Node> toHelp = announce.snapshot();
		
		// Like in the lock-free construction, this loop appends operations at the end of list
		// The loops ends only when all operations in the scan have terminated
		// This ensures that all other threads will be able to progress as well
		Node current = first;
		S state = initialState.copy();

		// Make sure all the operations in the collect are appended to the list
		while(!toHelp.isEmpty()) {
			// Try to append some node at the end of the list. 
			current.next.compareAndSet(null, toHelp.get(0));
			current = current.next.get();

			// Execute the operation that was appended at the end of the list (it may be toHelp[i]
			// There is a special case if the operation is the one for which the method was called, 
			// in which case we must store the result to return it later
			if(current == null) {
				System.out.println("current null");
			}
			if (current == myNode) result = operation.invoke(state);
			else current.operation.invoke(state);

			// When an operation has been executed, remove it from the collect
			toHelp.remove(current);
		}
		return result;
	}
}
