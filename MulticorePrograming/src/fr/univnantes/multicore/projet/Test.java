package fr.univnantes.multicore.projet;

public class Test {

    public static volatile Register r = new Register(Main.globalClock.get(), 0);
    private static int THREAD_NB = 8;
    private static int COUNTER = 100;

    public static void main(String[] args) {

        Thread[] threads = new Thread[THREAD_NB];

        for(int i=0 ; i<THREAD_NB ; i++){
            threads[i] = new Thread(() -> {
                for(int j=0 ; j<COUNTER ; j++) {
                    Transaction t = new Transaction();
                    while (!t.isCommited()) {
                        try {
                            t.begin();
                            r.write(t, (int) r.read(t) + 1);
                            t.tryToCommit();
                        } catch (CustomAbortException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        for(int i=0 ; i<THREAD_NB ; i++){ threads[i].start(); }

        for(int i=0 ; i<THREAD_NB ; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        System.out.println("Register = " + r.getValue());
        System.out.println("Clock = " + Main.globalClock.get());

        // on first register read, we add it to the lrst list of the transaction (because local copy of register is null)
        // this has for effect to throw an exception during the commit because the the lrst list contains this register
        // and this same register is also contained in the lwst list because it has been used for a write
        // so it has been locked during the commit function, and the exception is thrown because this register is locked in lrst
        // so the transaction will need a second try, and this time there will not be the register in the lrst list
        // because the commit clears the list, and the second call to read will not add the register to lrst (local copy not null)
        // on the second try, the counter will have been incremented two times
        // this is because it retrieves the incremented counter of the first try on the read call
        // !!!UNLESS!!! a local copy has been created elsewhere with a read call on the register
        // this is why the register might vary in the range of the +1 (first read on the register)
        // !!!this is only specific to thread transaction code in the lambda expression above!!!
        assert ((int)r.getValue() >= THREAD_NB*COUNTER) && ((int)r.getValue() <= THREAD_NB*COUNTER + 1);
    }

}
