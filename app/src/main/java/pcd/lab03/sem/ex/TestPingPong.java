package pcd.lab03.sem.ex;

import java.util.concurrent.Semaphore;

/**
 * A simple app PING-PONGing.
 */
public class TestPingPong {

	public static void main(String[] args) {
        final var pingDone = new Semaphore(0);
        final var pongDone = new Semaphore(0);
		new Pinger(pingDone, pongDone).start();
		new Ponger(pingDone, pongDone).start();
        pongDone.release();
	}
}
