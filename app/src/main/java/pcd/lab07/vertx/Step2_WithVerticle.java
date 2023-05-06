package pcd.lab07.vertx;

import io.vertx.core.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

class MyReactiveAgent extends AbstractVerticle {

	@Override
	public void start() throws InterruptedException {
		log("started");
		FileSystem fs = getVertx().fileSystem();
		Future<Buffer> f1 = fs.readFile("gradlew");
		f1.onComplete(res -> log(">> GRADLEW \n" + res.result().toString()));
		fs.readFile("settings.gradle")
			.onComplete(res -> log(">> SETTINGS \n" + res.result().toString()));
		Thread.sleep(3_000);
		log("done");
	}

	private void log(String msg) {
		System.out.println(Thread.currentThread() + " " + msg);
	}
}

public class Step2_WithVerticle {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new MyReactiveAgent());
	}
}
