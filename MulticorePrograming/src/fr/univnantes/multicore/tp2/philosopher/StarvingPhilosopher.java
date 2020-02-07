package fr.univnantes.multicore.tp2.philosopher;

public class StarvingPhilosopher extends Philosopher {

	/**
	 * This leads to a deadlock
	 *
	 * Because philosophers share the sticks
	 * When they take their left stick on the first step,
	 * they also take what is going to be the right stick on their neighbor for the second step
	 *
	 * A solution is to give each shared resources (stick) an order,
	 * and make sure that resources will be requested in order
	 * then there can not be deadlock (https://en.wikipedia.org/wiki/Dining_philosophers_problem#Solutions)
	 */

	public StarvingPhilosopher(DinnerTable table, int philosopherId) {
		super(table, philosopherId);
	}

	@Override
	public void startEat() throws InterruptedException {
		table.takeLeftStick(philosopherId);
		table.takeRightStick(philosopherId);
	}

	@Override
	public void startThink() {
		table.dropLeftStick(philosopherId);
		table.dropRightStick(philosopherId);
	}

}
