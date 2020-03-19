package fr.univnantes.multicore.projet;

import java.util.ArrayList;
import java.util.List;

/**
 * Code from Jenkov.com
 *
 * FairLock creates a new instance of QueueObject and enqueue it for each thread calling lock().
 * The thread calling unlock() will take the top QueueObject in the queue and call doNotify() on it,
 * to awaken the thread waiting on that object. This way only one waiting thread is awakened at a time,
 * rather than all waiting threads. This part is what governs the fairness of the FairLock.
 * Notice how the state of the lock is still tested
 * and set within the same synchronized block to avoid slipped conditions.
 */
public class FairLock implements CustomLock{
    private boolean           isLocked       = false;
    private Thread            lockingThread  = null;
    private List<QueueObject> waitingThreads =
            new ArrayList<QueueObject>();

    public void lock() throws InterruptedException{
        QueueObject queueObject           = new QueueObject();
        boolean     isLockedForThisThread = true;
        synchronized(this){
            waitingThreads.add(queueObject);
        }

        while(isLockedForThisThread){
            synchronized(this){
                isLockedForThisThread =
                        isLocked || waitingThreads.get(0) != queueObject;
                if(!isLockedForThisThread){
                    isLocked = true;
                    waitingThreads.remove(queueObject);
                    lockingThread = Thread.currentThread();
                    return;
                }
            }
            try{
                queueObject.doWait();
            }catch(InterruptedException e){
                synchronized(this) { waitingThreads.remove(queueObject); }
                throw e;
            }
        }
    }

    public synchronized void unlock(){
        if(this.lockingThread != Thread.currentThread()){
            throw new IllegalMonitorStateException(
                    "Calling thread has not locked this lock");
        }
        isLocked      = false;
        lockingThread = null;
        if(waitingThreads.size() > 0){
            waitingThreads.get(0).doNotify();
        }
    }
}
