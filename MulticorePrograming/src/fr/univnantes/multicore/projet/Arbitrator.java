package fr.univnantes.multicore.projet;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Louis boursier - Eloi Filaudeau
 * Date: 19/03/2020
 * Arbitrator should only give access to shared resources to one thread at a time to ensure no deadlock
 * But if an other thread ask for a lock on unused resources, we can give them to speed up the process
 * The thread should always tells us when it has finished so that we can release the lock and go on
 * The fairness is decided within this class
 */
public class Arbitrator {

    private static Set<Register> lockedRegisters = new CopyOnWriteArraySet<>();
    private static ReentrantLock reentrantLock = new ReentrantLock(true);

    public static boolean askForLocks(List<Register> registers) {
        Arbitrator.reentrantLock.lock();
        try {
            for (Register r : registers) { if (lockedRegisters.contains(r)) return false; }
            lockedRegisters.addAll(registers);
            return true;
        }finally {
            Arbitrator.reentrantLock.unlock();
        }
    }

    public static void releaseLocks(List<Register> registers) {
        // if the set is thread safe, no lock needed for releasing the registers
        lockedRegisters.removeAll(registers);
    }
}
