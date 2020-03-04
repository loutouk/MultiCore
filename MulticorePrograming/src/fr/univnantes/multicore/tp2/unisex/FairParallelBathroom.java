package fr.univnantes.multicore.tp2.unisex;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FairParallelBathroom implements Bathroom {

    Person currentSex = null;
    int availableRooms;
    int usedRooms = 0;
    // for each person in the queue we have a condition
    // creating Condition in a queue allows to control which thread we notify (with signal())
    LinkedList<Condition> queue = new LinkedList<>();
    private final Lock lock = new ReentrantLock();
    int nbBathroom;

    public FairParallelBathroom(int nbBathroom) {
        this.nbBathroom = nbBathroom;
        this.availableRooms = nbBathroom;
    }

    @Override
    public void enter(Person person) throws InterruptedException {

        lock.lock();
        boolean firstTryToEnter = true;
        Condition myTurn = null;

        try {
            while (!roomAvailable() || !sameSex(person) || needToQueue(myTurn)) {
                if(firstTryToEnter){
                    myTurn = lock.newCondition();
                    queue.add(myTurn);
                    firstTryToEnter = false;
                }
                // we can wake up while there's still people of the opposite sex in the bathroom
                myTurn.await(); // go to sleep and frees the lock for others
            }
            // from this point we are in the bathroom
            queue.remove(myTurn);
            availableRooms--; // takes a room
            if (currentSex == null) currentSex = person; // sets the current sex if bathroom was empty
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void leave(Person person) {
        lock.lock();
        try {
            availableRooms++; // frees a room
            if (availableRooms == nbBathroom) currentSex = null; // allows a new sex to enter
            queue.getFirst().signal(); // tells the first person in the queue that someone left the bathroom
        } finally {
            lock.unlock();
        }
    }

    private boolean roomAvailable() {
        return usedRooms < availableRooms;
    }

    private boolean sameSex(Person person) {
        return currentSex == null || currentSex.isMale() == person.isMale();
    }

    private boolean needToQueue(Condition myTurn) {
        // as long as there is a queue, we should wait in queue
        // even if there is only 1 female in the toilet and we are a female, because a male is queuing
        return ! queue.isEmpty() && queue.getFirst()!= myTurn;
    }
}
