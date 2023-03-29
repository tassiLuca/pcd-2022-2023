package pcd.lab03.barrier;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * Creates a CyclicBarrier object by passing the number of threads and
 * the thread to run when all the threads reach the barrier
 */
public class CyclicBarrierTest {

    public static void main(String[] args) {
        // a mixed-double tennis game requires four players; so wait for four players
        // (i.e., four threads) to join to start the game
        System.out.println("Reserving tennis court");
        System.out.println("As soon as four players arrive, game will start");
        CyclicBarrier barrier = new CyclicBarrier(4, () -> {
            // this is called upon the barrier is tripped
            System.out.println("All four players ready, game starts!! Love all...");
        });
        new Player(barrier, "G I Joe");
        new Player(barrier, "Dora");
        new Player(barrier, "Tintin");
        new Player(barrier, "Barbie");
    }

    /**
     * This thread simulates arrival of a player.
     * Once a player arrives, he/she should wait for other players to arrive.
     */
    static class Player extends Thread {
        private final CyclicBarrier waitPoint;

        public Player(CyclicBarrier barrier, String name) {
            this.setName(name);
            waitPoint = barrier;
            this.start();
        }

        public void run() {
            doInitStuff();
            System.out.println("-> Player " + getName() + " is ready :)");
            try {
                waitPoint.await(); // await for all four players to arrive
            } catch (BrokenBarrierException | InterruptedException exception) {
                System.out.println("An exception occurred while waiting... " + exception);
            }
        }

        private void doInitStuff() {
            try {
                TimeUnit.SECONDS.sleep(new Random().nextInt(10) + 2);
            } catch (InterruptedException ignored) { }
        }
    }
}
