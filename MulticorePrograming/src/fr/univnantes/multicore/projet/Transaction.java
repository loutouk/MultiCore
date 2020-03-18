package fr.univnantes.multicore.projet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Louis boursier
 * Date: 15/03/2020
 */
public class Transaction implements ITransaction{

    private List<Register> locallyWritten = new LinkedList<>(); // all written registers (called lwst in material)
    private List<Register> locallyRead = new LinkedList<>(); // all read registers (called lrst in material)
    private Map<Register, Register> localCopies = new HashMap<>(); // maps a register to its local copy with new value
    private int birthdate; // date of the global shared clock counter at the beginning of the transaction
    private boolean isCommited = false;
    private ReentrantLock lock = new ReentrantLock(true);

    @Override
    public void begin() {
        isCommited = false;
        locallyWritten.clear();
        locallyRead.clear();
        localCopies.clear();
        birthdate = EventuallyCommittedTest.globalClock.get();
    }

    @Override
    public void tryToCommit() throws CustomAbortException {

        final String[] algorithms = {"simple"};
        final int algorithmIndex = 0;

        switch (algorithms[algorithmIndex]){
            case "simple":
                try {
                    simpleTryToCommit();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new CustomAbortException("Commit algorithm " + algorithmIndex + " not implemented.");
        }
    }

    /**
     * Simplified version of TL2 commit function
     * @throws CustomAbortException
     */
    private void simpleTryToCommit() throws CustomAbortException, InterruptedException {
        // lock on this function should be useless because a transaction should be used locally and not between threads

        // at the beginning; if all the locks cannot be immediately obtained, TL2 should abort the transaction
        // this should prevent from deadlocks
        for (Register lwst : locallyWritten) {
            // if this lock has been set to use a fair ordering policy then an available lock will not be acquired
            // if any other threads are waiting for the lock (contrary to tryLock() without parameters) see doc
            // this should prevent starvation, as the lock constructor has been set to the fair policy
            if(!lwst.getLock().tryLock(0, TimeUnit.SECONDS)){ return; }
        }
        for (Register lrst : locallyRead) {
            // same as before
            if(!lrst.getLock().tryLock(0, TimeUnit.SECONDS)){ return; }
        }

        try {
            for (Register lrst : locallyRead) {
                // checks if the current values of the objects register it has read are still mutually consistent,
                // and consistent with respect to the new values it has (locally) written
                // if one of these dates is greater than its birthdate,
                // there is a possible inconsistency and consequently transaction is aborted
                if (lrst.getDate() > birthdate) {
                    throw new CustomAbortException("Date incoherence");
                }
            }
            Integer commitDate = EventuallyCommittedTest.globalClock.incrementAndGet();
            for (Register register : locallyWritten) {
                register.setValue(localCopies.get(register).getValue());
                register.setDate(commitDate);
            }
            isCommited = true;

        } finally {
            // in case of an abortion, we must verify that we obtained the lock if we want to unlock it
            for (Register lwst : locallyWritten) {
                if(lwst.getLock().isHeldByCurrentThread()){lwst.getLock().unlock();}
            }
            for (Register lrst : locallyRead) {
                if(lrst.getLock().isHeldByCurrentThread()){lrst.getLock().unlock();}
            }
        }
    }

    @Override
    public boolean isCommited() {
        return isCommited;
    }

    public List<Register> getLocallyWritten() {
        return locallyWritten;
    }

    public List<Register> getLocallyRead() {
        return locallyRead;
    }

    public int getBirthdate() {
        return birthdate;
    }

    public Map<Register, Register> getLocalCopies() {
        return localCopies;
    }
}
