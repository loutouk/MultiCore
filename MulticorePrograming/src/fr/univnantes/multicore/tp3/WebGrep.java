package fr.univnantes.multicore.tp3;

import fr.univnantes.multicore.projet.Register;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Louis Boursier
 */

public class WebGrep {

    // TODO: wrap all producer consumer related objects in a class and feed it to their constructor

    static ExecutorService threadPool;

    // for producer consumer pattern used for printing webpages
    static final int MAX_BUFFER_SIZE = 100; // no need to be atomic because it will be used within locks
    static final Lock lock = new ReentrantLock();
    static final Condition notFull = lock.newCondition();
    static final Condition notEmpty = lock.newCondition();
    // having more than one printTask speeds up printing but may cause overlaps in printings
    static private final int PRINTER_THREADS = 1;
    static int bufferCounter = 0; // no need to be atomic because it will be used within locks
    static LinkedList<ParsedPage> buffer = new LinkedList<>(); // buffer for producer consumer printing

    // used if we are not using the immutableDictionary for keeping track of the explored addresses
    static ConcurrentSkipListSet<String> explored = new ConcurrentSkipListSet<>(); // remembers explored addresses

    // for lock free version of the task
    static final AtomicReference<DictionaryImmutable> dictionaryPointer = new AtomicReference<>();
    static DictionaryImmutable immutableDictionary = new DictionaryImmutable();

    // for wait free version of the task
    static final DictionaryWaitFree dictionaryWaitFree = new DictionaryWaitFree();

    // for TL2 version of the task
    public static AtomicInteger globalClock = new AtomicInteger(0);
    public static volatile Register register = new Register(WebGrep.globalClock.get(), immutableDictionary);

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

        // for lock free version of the task
        // dictionaryPointer.set(immutableDictionary);

        // Get the starting URL given in argument
        for (String address : Tools.startingURL()) {
            //ExploreTaskLockFree newTask = new ExploreTaskLockFree(address);
            //ExploreTask newTask = new ExploreTask(address);
            ExploreTaskTL2 newTask = new ExploreTaskTL2(address, register);
            threadPool.submit(newTask);
        }
    }
}
