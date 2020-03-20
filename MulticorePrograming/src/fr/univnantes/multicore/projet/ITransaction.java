package fr.univnantes.multicore.projet;

/**
 * @author Louis boursier - Eloi Filaudeau
 * Date: 15/03/2020
 */
public interface ITransaction {
    void begin();

    void tryToCommit() throws CustomAbortException;

    boolean isCommited();
}