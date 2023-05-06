package pcd.lab07.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

public class Step1_Basic {

	public static void main(String[] args) throws InterruptedException {
		Vertx vertx = Vertx.vertx();
		FileSystem fs = vertx.fileSystem();
		log("started ");
		/* version 4.X - future -- **equivalent to JS promise** -- based API */
		Future<Buffer> fut = fs.readFile("settings.gradle");
		fut.onComplete((AsyncResult<Buffer> res) -> log("BUILD \n" + res.result().toString()));
		Thread.sleep(5_000);
		log("done");
	}
	
	private static void log(String msg) {
		System.out.println(Thread.currentThread() + " " + msg);
	}
}
