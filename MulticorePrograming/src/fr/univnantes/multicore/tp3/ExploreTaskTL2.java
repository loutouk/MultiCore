package fr.univnantes.multicore.tp3;

import fr.univnantes.multicore.projet.CustomAbortException;
import fr.univnantes.multicore.projet.Register;
import fr.univnantes.multicore.projet.Transaction;

import java.io.IOException;

/**
 * Date: 17/03/2020
 * @author: Louis Boursier
 */

public class ExploreTaskTL2 implements Runnable {

    private String address;
    private Register register;

    /**
     *
     * @param address the address to parse
     */
    public
    ExploreTaskTL2(String address, Register register) {
        this.address = address;
        this.register = register;
    }

    /**
     * Parses the page, inserts it in the buffer for further printing, submit new parsing tasks for links it found
     */
    @Override
    public void run() {
        try {
            /*
             *  Check that the page was not already explored and adds it
             *  Uses TL2 algorithm (Dice, Shalev, Shavit 2006)
             *  We need an immutable data structure to respect the read / write paradigm of TL2
             *  Hence the use of the immutable version of the dictionary
             */

            Transaction t = new Transaction();
            while (!t.isCommited()) {
                try {
                    t.begin();
                    DictionaryImmutable dictionary = (DictionaryImmutable)register.read(t);
                    DictionaryImmutable dictionaryEdited = dictionary.add(address);
                    if(dictionary==dictionaryEdited){
                        // no change in the dictionary reference means it already contains the address
                        return;
                    }
                    register.write(t, dictionaryEdited);
                    t.tryToCommit();
                } catch (CustomAbortException e) {
                    e.printStackTrace();
                }
            }

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
                    ExploreTaskTL2 newTask = new ExploreTaskTL2(href, register);
                    WebGrep.threadPool.submit(newTask);
                }
            }

        } catch (IOException e) {/*We could retry later...*/}
    }
}
