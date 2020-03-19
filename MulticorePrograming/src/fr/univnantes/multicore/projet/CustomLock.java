package fr.univnantes.multicore.projet;

public interface CustomLock {
    void lock() throws InterruptedException;
    void unlock();
}
