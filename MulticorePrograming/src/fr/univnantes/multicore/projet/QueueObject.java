package fr.univnantes.multicore.projet;

/**
 * Code from Jenkov.com
 *
 * QueueObject is really a semaphore.
 * The doWait() and doNotify() methods store the signal internally in the QueueObject.
 * This is done to avoid missed signals caused by a thread being preempted just before calling queueObject.doWait(),
 * by another thread which calls unlock() and thereby queueObject.doNotify().
 * he queueObject.doWait() call is placed outside the synchronized(this) block to avoid nested monitor lockout,
 * so another thread can actually call unlock() when no thread is executing inside the synchronized(this) block
 * in lock() method.
 */
public class QueueObject {

    private boolean isNotified = false;

    /**
     * called inside a try - catch block.
     * In case an InterruptedException is thrown the thread leaves the lock() method,
     * and we need to dequeue it.
     * @throws InterruptedException
     */
    public synchronized void doWait() throws InterruptedException {
        while(!isNotified){
            this.wait();
        }
        this.isNotified = false;
    }

    public synchronized void doNotify() {
        this.isNotified = true;
        this.notify();
    }

    public boolean equals(Object o) {
        return this == o;
    }
}
