package pcd.lab08.rx;

import io.reactivex.rxjava3.subjects.PublishSubject;

public class Test02e_CreationHotPubSub {

	public static void main(String[] args) throws Exception {
		System.out.println("\n=== TEST Hot streams with pubsub ===\n");
		/* Subjects: bridges functioning both as observer and observable */
		PublishSubject<Integer> source = PublishSubject.create();
		log("subscribing.");
		source.subscribe((s) -> {
			log("subscriber A: "+s); 
		}, Throwable::printStackTrace);
		log("generating.");
		new Thread(() -> {
				int i = 0;
				while (i < 100){
					try {
						log("source: "+i); 
						source.onNext(i);
						Thread.sleep(10);
						i++;
					} catch (Exception ignored) { }
				}
			}).start();
		log("waiting.");
		Thread.sleep(100);
		source.subscribe((s) -> {
			log("subscriber B: "+s); 
		}, Throwable::printStackTrace);
	}

	static private void log(String msg) {
		System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
	}
}
