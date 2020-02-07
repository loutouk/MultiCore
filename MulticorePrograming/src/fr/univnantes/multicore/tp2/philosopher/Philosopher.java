package fr.univnantes.multicore.tp2.philosopher;

public abstract class Philosopher implements Runnable {

	protected DinnerTable table;
	protected int philosopherId;
	private int timesEaten = 0;


	public Philosopher(DinnerTable table, int philosopherId) {
		this.philosopherId = philosopherId;
		this.table = table;
	}

	public int timesEaten() {
		return timesEaten;
	}

	public abstract void startEat() throws InterruptedException;

	public abstract void startThink();

	public String toString() {
		return "philosopher " + philosopherId;
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				startEat();
			} catch (InterruptedException e) {
				table.dropLeftStick(philosopherId);
				table.dropRightStick(philosopherId);
				return;
			}
			if(table.nbSticks(philosopherId) == 0) {
				System.out.println("Philosopher " + philosopherId + " tried to eat without sticks and died of suffocation");
				return;
			}
			if(table.nbSticks(philosopherId) == 1) {
				System.out.println("Philosopher " + philosopherId + " tried to eat with only one fork, broke it and then died");
				return;
			}
			timesEaten++;
			System.out.println("Philosopher " + philosopherId + " is Eating");
			try {
				Thread.sleep(100);
				startThink();
				Thread.sleep(100);
			} catch (InterruptedException e) {
				table.dropLeftStick(philosopherId);
				table.dropRightStick(philosopherId);
				return;
			}
		}
	}
}
