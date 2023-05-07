package pcd.lab07.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

class SimpleServer extends AbstractVerticle {

	private int numRequests;
	private final int port;

	public SimpleServer(int port) {
		numRequests = 0;
		this.port = port;
	}
	
	public void start(Promise<Void> startPromise) {
		this.getVertx().createHttpServer().requestHandler(req -> {
			numRequests++;
			String fileName = req.path().substring(1);
			log("request " + numRequests + " arrived for file: " + fileName);
			this.getVertx().fileSystem().readFile(fileName).onComplete(result -> {
				log("result ready");
				if (result.succeeded()) {
					log(result.result().toString());
					req.response().putHeader("content-type", "text/plain").end(result.result().toString());
				} else {
					log("Oh oh..." + result.cause() + " :(");
					req.response().putHeader("content-type", "text/plain").end("File not found");
				}
			});
		}).listen(port, res -> {
			if (res.succeeded()) {
				log("Server listening on port " + this.port);
				startPromise.complete();
			} else {
				startPromise.fail(res.cause());
			}
		});
	}

	private void log(String msg) {
		System.out.println("[" + Thread.currentThread() + "] " + msg);
	}
}

public class Step7_SimpleServer {
	private static final int PORT_NUMBER = 8081;

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		SimpleServer myVerticle = new SimpleServer(PORT_NUMBER);
		vertx.deployVerticle(myVerticle);
	}
}
