package pcd.lab07.vertx;

import io.vertx.core.*;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.*;
import io.vertx.ext.web.handler.BodyHandler;

class WebService extends AbstractVerticle {

	private int numRequests;
	private final int port;

	public WebService(int port) {
		numRequests = 0;
		this.port = port;
	}

	public void start(Promise<Void> startPromise) {
		log("Service initializing...");
		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		// `respond` API
		router.get("/api/numRequests").respond(request -> {
			log("new request arrived: " + request.currentRoute().getPath());
			JsonObject reply = new JsonObject();
			reply.put("numRequests", numRequests);
			return request.response()
			    .putHeader("Content-Type", "application/json")
			    .end(reply.toString());
		});
		// `handler` API
		router.route(HttpMethod.GET, "/api/things/:thingId/state").handler(this::handleGetThingsState);
		router.route(HttpMethod.POST, "/api/task/inc").handler(this::handlePostTask);
		server.requestHandler(router).listen(port, res -> {
			if (res.succeeded()) {
				log("Service ready - port: " + port);
				startPromise.complete();
			} else {
				startPromise.fail(res.cause());
			}
		});
	}

	private void handleGetThingsState(RoutingContext request) {
		log("new request arrived: " + request.currentRoute().getPath());
		JsonObject reply = new JsonObject();
		reply.put("id", request.pathParam("thingId")).put("state", Math.random());
		sendReply(request, reply);
	}

	private void handlePostTask(RoutingContext request) {
		log("new request arrived: " + request.currentRoute().getPath());
		JsonObject msgReq = request.body().asJsonObject();
		double value = msgReq.getInteger("value");
		double result = value + 1;
		JsonObject reply = new JsonObject();
		numRequests++;
		reply.put("numReq", numRequests).put("result", result);
		log("reply: " + reply.encodePrettily());
		sendReply(request, reply);
	}

	private void sendReply(RoutingContext request, JsonObject reply) {
		request.response()
			.putHeader("content-type", "application/json")
			.end(reply.toString());
	}

	private void log(String msg) {
		System.out.println("[" + Thread.currentThread() + "] " + msg);
	}
}

public class Step8_WebService {
	private static final int PORT_NUMBER = 8081;

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		AbstractVerticle myVerticle = new WebService(PORT_NUMBER);
		vertx.deployVerticle(myVerticle);
	}
}
