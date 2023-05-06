package pcd.lab07.vertx;

import java.util.Random;
import io.vertx.core.*;


class TestPromise extends AbstractVerticle {

	@Override
	public void start() {
		log("pre");
		Promise<Double> promise = Promise.promise();
		this.getVertx().setTimer(1_000, res -> {
			log("timeout from the timer...");
			Random rand = new Random();
			double value = rand.nextDouble();
			if (value > 0.5) {
				log("...complete with success.");
				promise.complete(value);
			} else {
				log("...complete with failure.");
				promise.fail("Value below 0.5 " + value);
			}
		});
		// Future configuration
		Future<Double> fut = promise.future();
		fut.onSuccess((Double res) -> log("reacting to timeout - success: " + res))
			.onFailure((Throwable t) -> log("reacting to timeout - failure: " + t.getMessage()))
			.onComplete((AsyncResult<Double> res) -> log("reacting to completion - " + res.succeeded()));
		log("post");
	}

	private void log(String msg) {
		System.out.println("[REACTIVE AGENT]["+Thread.currentThread()+"] " + msg);
	}
}

public class Step4_Promise {
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new TestPromise());
	}
}

