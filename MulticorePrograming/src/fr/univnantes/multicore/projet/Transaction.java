package fr.univnantes.multicore.projet;

import java.util.LinkedList;
import java.util.List;

public class Transaction implements ITransaction{

    private List<Register> locallyWritten = new LinkedList<>(); // all written registers (called lwst in material)
    private List<Register> locallyRead = new LinkedList<>(); // all read registers (called lrst in material)
    private int birthdate; // date of the global shared clock counter at the beginning of the transaction
    private boolean isCommited = false;

    @Override
    public void begin() {
        isCommited = false;
        locallyWritten.clear();
        locallyRead.clear();
        birthdate = Main.globalClock.get();
    }

    @Override
    public void tryToCommit() throws CustomAbortException {

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
                if(lrst.getLock().isLocked()) { throw new CustomAbortException("Read/Write incoherence"); }
            }

            for(Register lrst : locallyRead) {
                // if locallyRead date is more recent than our register, it means it has been updated elsewhere (dirty read)
                if(lrst.getDate() > birthdate) { throw new CustomAbortException("Date incoherence"); }
            }

            Integer commitDate = Main.globalClock.getAndIncrement();

            for(Register register : locallyWritten){
                register.setValue(register.getLocalCopy().getValue());
                register.setDate(commitDate);
            }

            isCommited = true;

        }finally {
            for(Register lwst : locallyWritten){ lwst.getLock().unlock(); }
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
}
