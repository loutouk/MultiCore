package fr.univnantes.multicore.distanciel;

import java.util.concurrent.Future;

/**
 * Manages the computation of the blocks
 * @author Matthieu Perrin
 * TODO: That is the interface that must be implemented
 */
public interface Server {
	/**
	 * This method is called each time a block has to be computed
	 * @param task a Callable<Block> that contains enough information to compute the block
	 * @return a Future<Block> object that, when ready, contains the corresponding block
	 */
	public Future<Block> getBlock(Task task);
}
