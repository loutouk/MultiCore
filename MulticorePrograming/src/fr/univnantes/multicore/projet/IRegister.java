package fr.univnantes.multicore.projet;

/**
 * @author Louis boursier
 * Date: 15/03/2020
 */
public interface IRegister<T> {
    T read(Transaction transaction) throws CustomAbortException;

    void write(Transaction transaction, T value) throws CustomAbortException;
}
