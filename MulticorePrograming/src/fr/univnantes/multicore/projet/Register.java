package fr.univnantes.multicore.projet;

import java.util.concurrent.locks.ReentrantLock;

public class Register<T> implements IRegister {

    private int date; // date for which the last transaction was committed in this register
    private T value; // last value
    private Register localCopy; // might replace value if its transaction is committed (called lcx in material)
    private ReentrantLock lock;

    public Register(int date, T value) {
        this.date = date;
        this.value = value;
        this.localCopy = null;
        this.lock = new ReentrantLock(true); // true parameter for fairness to avoid starvation
    }

    private Register makeCopy() {
        Register copy = new Register(this.date, this.value); // shallow copy
        copy.lock = this.lock; // TODO verify (if it is a copy we should reference the same lock?)
        return copy;
    }

    @Override
    public void write(Transaction transaction, Object value) {
        if(localCopy == null){ localCopy = this.makeCopy(); }
        localCopy.value = value;
        transaction.getLocallyWritten().add(this);
    }

    @Override
    public Object read(Transaction transaction) throws CustomAbortException {
        if(localCopy != null){ return localCopy.value; }
        else {
            localCopy = this.makeCopy();
            transaction.getLocallyRead().add(this);
            if(localCopy.date > transaction.getBirthdate()){ throw new CustomAbortException("Date incoherence"); }
            else{ return localCopy.value; }
        }
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Register getLocalCopy() {
        return localCopy;
    }

    public ReentrantLock getLock() {
        return lock;
    }
}
