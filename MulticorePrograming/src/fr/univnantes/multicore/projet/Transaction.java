package fr.univnantes.multicore.projet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
        birthdate = Test.globalClock.get();
    }

    @Override
    public void tryToCommit() throws CustomAbortException {

        final String[] algorithms = {"simple", "optimized"};
        final int algorithmIndex = 0;

        switch (algorithms[algorithmIndex]){
            case "simple":
                simpleTryToCommit();
                break;
            case "optimized":
                optimizedTryToCommit();
                break;
            default:
                throw new CustomAbortException("Commit algorithm " + algorithmIndex + " not implemented.");
        }
    }

    /**
     * Optimized version - does not work! Yet
     * if at the beginning, all the locks cannot be immediately obtained,
     * TL2 can abort the transaction (and restart it later). This can allow for more efficient behaviors.
     * @throws CustomAbortException
     */
    private void optimizedTryToCommit() throws CustomAbortException {
        lock.lock();
        try{
            for(Register lwst : locallyWritten){ lwst.getLock().lock(); }

            try {

                for(Register lrst : locallyRead){
                    // if locallyRead register is locked it means either two things:
                    // 1 - it has been written on elsewhere, and locked just above because tryToCommit() was called
                    // 2 - it has been locked just above because the same register is in locallyWritten & locallyRead
                    //    this is because the register has been read when its local register copy was null
                    //    so it was added to our locallyRead register list
                    //    AND the same register has also been added to the locallyWritten register list
                    //    happens when same register is read and written in the same transaction
                    //    following exception will be raised
                    // TODO isLocked() is outdated once called: is it a problem? I do not think so...
                    if(lrst.getLock().isLocked()) { throw   new CustomAbortException("Read/Write incoherence"); }
                }

                for(Register lrst : locallyRead) {
                    // if locallyRead date is more recent than our register, it means it has been updated elsewhere (dirty read)
                    if(lrst.getDate() > birthdate) { throw new CustomAbortException("Date incoherence"); }
                }

                Integer commitDate = Test.globalClock.incrementAndGet();

                for(Register register : locallyWritten){
                    register.setValue(localCopies.get(register).getValue());
                    register.setDate(commitDate);
                }

                isCommited = true;

            }finally {
                for(Register lwst : locallyWritten){ lwst.getLock().unlock(); }
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * Simplified version of TL2 commit function
     * @throws CustomAbortException
     */
    private void simpleTryToCommit() throws CustomAbortException{
        lock.lock();
        try {

            for (Register lwst : locallyWritten) {
                lwst.getLock().lock();
            }
            for (Register lrst : locallyRead) {
                lrst.getLock().lock();
            }

            try {
                for (Register lrst : locallyRead) {
                    // if locallyRead date is more recent than our register, it means it has been updated elsewhere (dirty read)
                    if (lrst.getDate() > birthdate) {
                        throw new CustomAbortException("Date incoherence");
                    }
                }
                Integer commitDate = Test.globalClock.incrementAndGet();
                for (Register register : locallyWritten) {
                    register.setValue(localCopies.get(register).getValue());
                    register.setDate(commitDate);
                }
                isCommited = true;

            } finally {
                for (Register lwst : locallyWritten) {
                    lwst.getLock().unlock();
                }
                for (Register lrst : locallyRead) {
                    lrst.getLock().unlock();
                }
            }
        }finally {
            lock.unlock();
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
