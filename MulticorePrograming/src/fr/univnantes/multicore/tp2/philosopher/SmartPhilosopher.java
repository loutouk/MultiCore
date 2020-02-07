package fr.univnantes.multicore.tp2.philosopher;

public class SmartPhilosopher extends Philosopher {



    public SmartPhilosopher(DinnerTable table, int philosopherId) {
        super(table, philosopherId);
    }

    @Override
    public void startEat() throws InterruptedException {
        // makes each philosopher take the sticks in ordered sequence to prevent deadlock
        // https://fr.wikipedia.org/wiki/Fichier:An_illustration_of_the_dining_philosophers_problem.png
        if(philosopherId==0){
            table.takeLeftStick(philosopherId);
            table.takeRightStick(philosopherId);
        }else{
            table.takeRightStick(philosopherId);
            table.takeLeftStick(philosopherId);
        }
    }

    @Override
    public void startThink() {
        table.dropLeftStick(philosopherId);
        table.dropRightStick(philosopherId);
    }
}
