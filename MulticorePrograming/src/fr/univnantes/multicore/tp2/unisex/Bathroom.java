package fr.univnantes.multicore.tp2.unisex;

public interface Bathroom {
	/**
	 * Called when someone wants to enter the bathroom
	 * @param person the person trying to come in
	 * @throws InterruptedException if interrupt was called while the person was waiting
	 */
	public void enter(Person person) throws InterruptedException;
	/**
	 * Called when someone wants to leave the bathroom
	 * @param person the person trying to get out
	 */
	public void leave(Person person);
}
