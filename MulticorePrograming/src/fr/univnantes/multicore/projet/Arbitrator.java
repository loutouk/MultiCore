package fr.univnantes.multicore.projet;

import java.util.HashSet;
import java.util.List;

/**
 * @author Louis boursier
 * Date: 19/03/2020
 * Arbitrator should only give access to shared resources to one thread at a time to ensure no deadlock
 * But if an other thread ask for a lock on unused resources, we can give them to speed up the process
 * The thread should always tells us when it has finished so that we can release the lock and go on
 * The fairness is decided within this class
 */
public class Arbitrator {
    private static HashSet<Register> lockedRegisters = new HashSet<>();
    //private static CustomLock lock = new BasicLock(); // not fair
    private static CustomLock lock = new FairLock(); // fair

    public static boolean askForLocks(List<Register> locallyWritten, List<Register> locallyRead) throws InterruptedException {
        Arbitrator.lock.lock();
        for(Register r : locallyWritten){ if(lockedRegisters.contains(r)) return false; }
        for(Register r : locallyRead){ if(lockedRegisters.contains(r)) return false; }
        lockedRegisters.addAll(locallyWritten);
        lockedRegisters.addAll(locallyRead);
        Arbitrator.lock.unlock();
        return true;
    }

    public static void releaseLocks(List<Register> locallyWritten, List<Register> locallyRead) throws InterruptedException {
        Arbitrator.lock.lock();
        lockedRegisters.removeAll(locallyWritten);
        lockedRegisters.retainAll(locallyRead);
        Arbitrator.lock.unlock();
    }
}
