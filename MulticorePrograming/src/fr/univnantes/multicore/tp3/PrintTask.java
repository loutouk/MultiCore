package fr.univnantes.multicore.tp3;


import java.util.regex.Matcher;

/**
 * Author: Louis Boursier
 */

public class PrintTask implements Runnable {

    /**
     * Continuously waits for the buffer not to be empty and print parsed page from buffer if so
     */
    @Override
    public void run() {
        while (true) {
            WebGrep.lock.lock();

            try {
                while (WebGrep.bufferCounter == 0) {
                    try {
                        WebGrep.notEmpty.await(); // waits for pages to be available
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                print(WebGrep.buffer.remove()); // takes a page from the buffer and prints it
                WebGrep.bufferCounter--; // we just consumed one page by printing it
                WebGrep.notFull.signal(); // tells producers room has been made in the buffer
            } finally {
                WebGrep.lock.unlock();
            }
        }
    }

    /**
     * Outputs the matches found in the given Web page, according to the options passed at initialization
     *
     * @param p Parsed page obtained as the result of Tools.parsePage(address);
     */
    public void print(ParsedPage p) {
        if (!Tools.isQuiet() && !p.matches().isEmpty()) {
            // Print the header with address and count
            if (!Tools.isNoFilename() && Tools.isCount())
                System.out.print(p.address() + ": " + p.matches().size() + "\n");
            else if (!Tools.isNoFilename())
                System.out.print(p.address() + "\n");
            else if (Tools.isCount())
                System.out.print(p.matches().size() + "\n");
            // Print the list of matches
            if (!Tools.isFilesWithMatches()) {
                for (String s : p.matches()) {
                    Matcher m = Tools.getMatchPattern().matcher(s);
                    while (m.find()) {
                        if (Tools.isInitialTab()) System.out.print("\t");
                        if (!Tools.isOnlyMatching()) System.out.print(s.substring(0, m.start(0)));
                        if (Tools.isEmphasize()) System.out.print("\033[0;31m");
                        System.out.print(m.group(0));
                        if (Tools.isEmphasize()) System.out.print("\u001B[0m");
                        if (!Tools.isOnlyMatching()) System.out.print(s.substring(m.end(0)));
                    }
                    System.out.println();
                }
            }
        }
    }


}
