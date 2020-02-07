package fr.univnantes.multicore.tp2.unisex;

public class ParallelBathroom implements Bathroom {

    Person currentSex = null;
    int availableRooms;
    int usedRooms = 0;
    int nbBathroom;

    public ParallelBathroom(int nbBathroom) {
        this.nbBathroom = nbBathroom;
        this.availableRooms = nbBathroom;
    }

    @Override
    public synchronized void enter(Person person) throws InterruptedException {
        while (!roomAvailable() || !sameSex(person)) { wait(); }
        // from this point we are in the bathroom
        availableRooms--; // takes a room
        if (currentSex == null) currentSex = person; // sets the current sex if bathroom was empty
    }

    @Override
    public synchronized void leave(Person person) {
        availableRooms++; // frees a room
        if (availableRooms == nbBathroom) currentSex = null; // allows a new sex to enter
        notifyAll();
        // uses notifyAll and not notify
        // because if notify wakes a female and the bathroom is occupied by men: deadlock
    }

    private boolean roomAvailable() {
        return usedRooms < availableRooms;
    }

    private boolean sameSex(Person person) {
        return currentSex == null || currentSex.isMale() == person.isMale();
    }
}
