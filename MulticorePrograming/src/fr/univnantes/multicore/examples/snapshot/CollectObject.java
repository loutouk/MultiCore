package fr.univnantes.multicore.examples.snapshot;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class CollectObject<T> implements SnapshotObject<T> {

	private class Node {
		volatile T value;
		final Node next;
		Node(Node next, T value) {
			this.value = value;
			this.next = next; 
		}
	}
	
	protected final ThreadLocal<Node> selfNode = ThreadLocal.withInitial(() -> null);
	protected final AtomicReference<Node> first = new AtomicReference<Node>(null);
		
	public void update(T value) {
		Node node = selfNode.get();
		if(node == null) {
			do {
				node = new Node(first.get(), value);
			} while (!first.compareAndSet(node.next, node));
			selfNode.set(node);
		}
		node.value = value;
	}

	public T read() {
		Node node = selfNode.get();
		if(node == null) return null;
		else return node.value;
	}

	public final ArrayList<T> snapshot() {
		Node node = first.get();
		ArrayList<T> cpy = new ArrayList<T>();
		while(node != null) {
			if(node.value != null)
				cpy.add(0, node.value);
			node = node.next;
		}
		return cpy;
	}
}

