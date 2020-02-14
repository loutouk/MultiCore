package fr.univnantes.multicore.tp3;

import java.io.IOException;

/**
 * Author: Louis Boursier
 */

public class ExploreTask implements Runnable {

    private String address;

    /**
     *
     * @param address the address to parse
     */
    public ExploreTask(String address) {
        this.address = address;
    }

    /**
     * Parses the page, inserts it in the buffer for further printing, submit new parsing tasks for links it found
     */
    @Override
    public void run() {
        try {
            /*
             *  Check that the page was not already explored and adds it
             *  Get and set operations should be atomic to not store duplicates
             *  Use ConcurrentSkipListSet data structure
             */
            if (!WebGrep.explored.contains(address)) {
                WebGrep.explored.add(address);
                // Parse the page to find matches and hypertext links
                ParsedPage page = Tools.parsePage(address);
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
                        ExploreTask newTask = new ExploreTask(href);
                        WebGrep.threadPool.submit(newTask);
                    }
                }
            }
        } catch (IOException e) {/*We could retry later...*/}
    }
}
