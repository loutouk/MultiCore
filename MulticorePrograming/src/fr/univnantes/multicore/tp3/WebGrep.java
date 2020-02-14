package fr.univnantes.multicore.tp3;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WebGrep {

    // TODO: wrap all producer consumer related objects in a class and feed it to their constructor

    static final int MAX_BUFFER_SIZE = 100; // no need to be atomic because it will be used within locks
    static final Lock lock = new ReentrantLock();
    static final Condition notFull = lock.newCondition();
    static final Condition notEmpty = lock.newCondition();

    // producer consumer for printing
    // having more than one printTask speeds up printing but may cause overlaps in printings
    static private final int PRINTER_THREADS = 1;
    static ConcurrentSkipListSet<String> explored = new ConcurrentSkipListSet<>(); // remembers explored addresses
    static ExecutorService threadPool;
    static int bufferCounter = 0; // no need to be atomic because it will be used within locks
    static LinkedList<ParsedPage> buffer = new LinkedList<>(); // buffer for producer consumer printing

    public static void main(String[] args) {
        // Initialize the program using the options given in argument
        if (args.length == 0) Tools.initialize("-celt --threads=8 Nantes https://fr.wikipedia.org/wiki/Nantes");
        else Tools.initialize(args);

        /**
         * Thanks to the ConcurrentSkipListSet we can not submit the same task to the thread pool
         * And an ExecutorService can be shared safely between threads
         */

        threadPool = Executors.newFixedThreadPool(Tools.numberThreads()); // starts a thread pool

        for (int i = 0; i < PRINTER_THREADS; i++) {
            PrintTask printTask = new PrintTask();
            Thread thread = new Thread(printTask);
            thread.start(); // runs a thread for printing parsed pages
        }

        // Get the starting URL given in argument
        for (String address : Tools.startingURL()) {
            ExploreTask newTask = new ExploreTask(address);
            threadPool.submit(newTask);
        }
    }
}
