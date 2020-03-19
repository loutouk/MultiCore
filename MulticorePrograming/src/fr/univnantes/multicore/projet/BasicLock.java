package fr.univnantes.multicore.projet;

/**
 * Code from Jenkov.com
 *
 * If you compare the Lock and FairLock classes you will notice that there is
 * somewhat more going on inside the lock() and unlock() in the FairLock class.
 * This extra code will cause the FairLock to be a sligtly slower synchronization mechanism than Lock.
 * How much impact this will have on your application depends on how long time the code in the critical section
 * guarded by the FairLock takes to execute.
 * The longer this takes to execute, the less significant the added overhead of the synchronizer is.
 * It does of course also depend on how often this code is called
 */
public class BasicLock implements CustomLock{
    private boolean isLocked      = false;
    private Thread  lockingThread = null;

    public synchronized void lock() throws InterruptedException{
        while(isLocked){
            wait();
        }
        isLocked      = true;
        lockingThread = Thread.currentThread();
    }

    public synchronized void unlock(){
        if(this.lockingThread != Thread.currentThread()){
            throw new IllegalMonitorStateException(
                    "Calling thread has not locked this lock");
        }
        isLocked      = false;
        lockingThread = null;
        notify();
    }
}
