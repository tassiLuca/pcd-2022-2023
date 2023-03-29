package pcd.lab03.semaphore.pingpong;

import java.util.concurrent.Semaphore;

public class Pinger extends Thread {

	private final Semaphore pingDone;
	private final Semaphore pongDone;

	public Pinger(final Semaphore pingDone, final Semaphore pongDone) {
		this.pingDone = pingDone;
		this.pongDone = pongDone;
	}	
	
	public void run() {
		while (true) {
			try {
				pingDone.acquire();
				System.out.println("ping!");
				pongDone.release();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}