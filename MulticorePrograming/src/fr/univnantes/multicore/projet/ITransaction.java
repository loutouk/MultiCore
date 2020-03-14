package fr.univnantes.multicore.projet;

public interface ITransaction {
    void begin();
    void tryToCommit() throws CustomAbortException;
    boolean isCommited();
}