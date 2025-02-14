package pcd.lab07.vertx;

import java.util.List;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

class TestChain extends AbstractVerticle {

	@Override
	public void start() {
		FileSystem fs = vertx.fileSystem();    		
		fs.readFile("gradlew")
			.compose((Buffer buf) -> {
				log(">> 1 - BUILD \n" + buf.toString().substring(0,160));
				return fs.readFile("settings.gradle");
			}).compose((Buffer buf) -> {
				log(">> 2 - SETTINGS \n" + buf.toString().substring(0,160));
				return fs.readDir("app/src/main/java/pcd");
			}).onComplete((AsyncResult<List<String>> list) -> log(">> 3 - DIR: " + list.result().size()));
	}

	private void log(String msg) {
		System.out.println("[REACTIVE AGENT] " + msg);
	}
}

public class Step3_Chaining {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new TestChain());
	}
}
