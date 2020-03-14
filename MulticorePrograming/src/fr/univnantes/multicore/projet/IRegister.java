package fr.univnantes.multicore.projet;

public interface IRegister<T> {
    T read(Transaction transaction) throws CustomAbortException;
    void write(Transaction transaction, T value) throws CustomAbortException;
}
