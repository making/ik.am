package am.ik;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.SocketUtils;

import reactor.core.publisher.Mono;
import reactor.ipc.netty.NettyContext;
import reactor.ipc.netty.http.server.HttpServer;

public class HomeApplicationTests {
	static WebTestClient webClient;
	static int port;
	static String host;

	@BeforeClass
	public static void setup() throws Exception {

		port = SocketUtils.findAvailableTcpPort();
		host = "localhost";
		// port = 80;
		// host = "demo-router-functions.cfapps.io";

		if ("localhost".equals(host)) {
			HttpServer httpServer = HttpServer.create("0.0.0.0", port);
			Mono<? extends NettyContext> handler = httpServer.newHandler(
					new ReactorHttpHandlerAdapter(HomeApplication.httpHandler()));
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				System.out.println("Shut down ...");
			}));
			handler.block();
		}

		webClient = WebTestClient.bindToServer()
				.baseUrl(String.format("http://%s:%d", host, port)).build();
	}

	@Test
	public void root() {
		webClient.get().uri("").exchange().expectStatus().isOk();
	}
}
