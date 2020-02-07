package fr.univnantes.multicore.tp2.unisex;

public class Person implements Runnable {

	private boolean male;
	private int id;
	private Bathroom bathroom;
	private int nbEntries = 0;

	public Person(int id, boolean male, Bathroom bathroom) {
		this.male = male;
		this.id = id;
		this.bathroom = bathroom;
	}

	public boolean isMale() {
		return male;
	}

	public int numberEntries() {
		return nbEntries;
	}

	@Override
	public String toString() {
		if (male) {
			return "male " + id;
		} else {
			return "female " + id;
		}
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				bathroom.enter(this);
				try {
					nbEntries++;
					System.out.println(toString() + " enters the bathroom");
					Thread.sleep(100);
				} catch (InterruptedException e) {
					break;
				} finally {
					System.out.println(toString() + " leaves the bathroom");
					bathroom.leave(this);
				}
				Thread.sleep(100);
			} catch (InterruptedException e) {
				break;
			}
		}
		System.out.println(toString() + " entered the bathroom " + numberEntries() +  " times");
	}
}
