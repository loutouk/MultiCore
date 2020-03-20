package fr.univnantes.multicore.projet;

/**
 * @author Louis Boursier - Eloi Filaudeau
 * Date: 15/03/2020
 */
public class CustomAbortException extends Exception {
    public CustomAbortException(String message) {
        super(message);
    }
}
