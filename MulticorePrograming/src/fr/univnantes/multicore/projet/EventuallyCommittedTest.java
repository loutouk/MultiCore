package fr.univnantes.multicore.projet;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Verifies that all transactions eventually succeed to commit in a high throughput environment
 * @author Louis boursier
 * Date: 15/03/2020
 */
public class EventuallyCommittedTest {

    // TODO: can we use one clock per register to reduce date incoherence abortion?
    public static AtomicInteger globalClock = new AtomicInteger(0);

    public static volatile Register r1 = new Register(EventuallyCommittedTest.globalClock.get(), 0);
    public static volatile Register r2 = new Register(EventuallyCommittedTest.globalClock.get(), 0);
    private static final int INCREMENT_R1 = 1;
    private static final int INCREMENT_R2 = 10;
    private static final int THREAD_NB = 4;
    private static final int COUNTER = 10;

    public static void main(String[] args) {

        Thread[] threads = new Thread[THREAD_NB];

        for(int i=0 ; i<THREAD_NB ; i++){
            threads[i] = new Thread(() -> {
                for(int j=0 ; j<COUNTER ; j++) {
                    Transaction t = new Transaction();
                    while (!t.isCommited()) {
                        try {
                            t.begin();
                            int valA = (int) r1.read(t) + INCREMENT_R1;
                            int valB = (int) r2.read(t) + INCREMENT_R2;
                            // artificially generates concurrency problems
                            try{
                                Thread.sleep(Math.abs(new Random().nextInt()%500));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            r1.write(t, valA);
                            r2.write(t, valB);
                            t.tryToCommit();
                        } catch (CustomAbortException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        for(int i=0 ; i<THREAD_NB ; i++){ threads[i].start(); }
        for(int i=0 ; i<THREAD_NB ; i++){ try { threads[i].join(); } catch (InterruptedException e) { e.printStackTrace(); } }

        System.out.println("Register 1 = " + r1.getValue() + " | Clock = " + EventuallyCommittedTest.globalClock.get());
        System.out.println("Register 2 = " + r2.getValue() + " | Clock = " + EventuallyCommittedTest.globalClock.get());

        assert ((int)r1.getValue() == THREAD_NB*COUNTER*INCREMENT_R1);
        assert ((int)r2.getValue() == THREAD_NB*COUNTER*INCREMENT_R2);
    }

}
