package fr.univnantes.multicore.distanciel;

import java.util.LinkedList;

/**
 * Original code from Jakob Jenkov
 * Author: Louis Boursier
 */

public class BlockingQueue {

    private LinkedList queue = new LinkedList();

    public BlockingQueue(){ }

    public synchronized void enqueue(Object item, boolean hasPriority) throws InterruptedException  {
        if(hasPriority){
            this.queue.addFirst(item);
        }else{
            this.queue.addLast(item);
        }
    }

    public synchronized Object dequeue() throws InterruptedException{
        return this.queue.remove(0);
    }

}
