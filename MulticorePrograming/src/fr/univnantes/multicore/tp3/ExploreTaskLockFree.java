package fr.univnantes.multicore.tp3;

import java.io.IOException;

/**
 * @author: Louis Boursier
 *
 * ExploreTask using the String Dictionary for saving memory
 * Used with an optimistic locking technique with a global pointer on the current dictionary
 * See {@link DictionaryImmutable}
 *
 */

public class ExploreTaskLockFree implements Runnable {

    private String address;

    /**
     *
     * @param address the address to parse
     */
    public ExploreTaskLockFree(String address) {
        this.address = address;
    }

    /**
     * Parses the page, inserts it in the buffer for further printing, submit new parsing tasks for links it found
     */
    @Override
    public void run() {

        // saves the pointer of the dictionary before the operation
        DictionaryImmutable dictionaryBefore = WebGrep.dictionaryPointer.get();
        // tries to add an address to the dictionary
        // yields the dictionaryBefore pointer if the add operation failed, a new one otherwise
        DictionaryImmutable modifiedDictionary = dictionaryBefore.add(address);

        while(!WebGrep.dictionaryPointer.compareAndSet(dictionaryBefore, modifiedDictionary)) {
            // if we are here, compareAndSet fails, it means that an other thread modified the dictionary concurrently
            // we try again with up to date references to stay consistent
            dictionaryBefore = WebGrep.dictionaryPointer.get();
            modifiedDictionary = dictionaryBefore.add(address);
            // we are not locked, and free to do anything. But let us just try to submit our new dictionary...
        }

        // if we are here, we successfully performed the operation on the dictionary
        // and we successfully modified the global pointer / reference to the dictionary so every threads have it

        if(dictionaryBefore == modifiedDictionary) {

            return; // already explored: the dictionary is unchanged so it already contains the address

        }else{

            // Parse the page to find matches and hypertext links
            ParsedPage page = null;
            try {
                page = Tools.parsePage(address);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!page.matches().isEmpty()) {

                /*
                 * Tools.print(page) must be thread safe
                 * so that its printing does not overlap between threads
                 * we use the producer consumer pattern to address the problem
                 * ExploreTask threads are producers and a thread responsible for printing is consumer
                 * Having more than one consumer speed things up but can cause printings to overlap
                 */

                WebGrep.lock.lock();

                try {

                    while (WebGrep.MAX_BUFFER_SIZE == WebGrep.bufferCounter) {
                        try {
                            WebGrep.notFull.await(); // waits after consumer for room to be made in the buffer
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    WebGrep.buffer.add(page); // add the freshly parsed page to the buffer for the consumer
                    WebGrep.bufferCounter++; // we just took an additional slot in the buffer
                    WebGrep.notEmpty.signal(); // tells producers pages are now available in the buffer

                } finally {
                    WebGrep.lock.unlock();
                }

                // Recursively explore other pages
                for (String href : page.hrefs()) {
                    ExploreTaskLockFree newTask = new ExploreTaskLockFree(href);
                    WebGrep.threadPool.submit(newTask);
                }
            }

        }
    }
}
