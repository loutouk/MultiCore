package fr.univnantes.multicore.projet;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Louis boursier
 * Date: 15/03/2020
 */
public class Register<T> implements IRegister {

    // shared values between thread should either be declared final or volatile
    // even though ReentrantLock implements Lock that ensures the updates to memory is seen by other threads (doc)
    private volatile int date; // date for which the last transaction was committed in this register
    private volatile T value; // last value
    private ReentrantLock lock; // lock used during the commit function of the transaction (volatile by default)
    private volatile Transaction lastTransactionWriter; // reference to the last transaction that wrote to our value

    public Register(int date, T value) {
        this.date = date;
        this.value = value;
        this.lastTransactionWriter = null;
        this.lock = new ReentrantLock(true); // true parameter for fairness to avoid starvation
    }

    private Register makeCopy() {
        // When value is not a primitive type, we should think about the copying mechanism
        // We need a pointer on a new object
        // Choice between shallow or deep copy can be handled by the object's implementation
        Register copy = new Register(this.date, this.value);
        copy.lock = this.lock;
        return copy;
    }

    @Override
    public void write(Transaction transaction, Object value) {
        if(transaction.getLocalCopies().get(this) == null){
            transaction.getLocalCopies().put(this,this.makeCopy());
        }
        transaction.getLocalCopies().get(this).setValue(value);
        transaction.getLocallyWritten().add(this);
        lastTransactionWriter = transaction; // used for the read method
    }

    @Override
    public Object read(Transaction transaction) throws CustomAbortException {
        // if the register has been written by the same transaction, we return its value
        if(transaction.getLocalCopies().get(this) != null && lastTransactionWriter ==transaction){
            return transaction.getLocalCopies().get(this).value;
        } else {
            transaction.getLocalCopies().put(this, this.makeCopy());
            transaction.getLocallyRead().add(this);
            if(transaction.getLocalCopies().get(this).date > transaction.getBirthdate()){ throw new CustomAbortException("Date incoherence"); }
            else{ return transaction.getLocalCopies().get(this).value; }
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

    public ReentrantLock getLock() {
        return lock;
    }
}
