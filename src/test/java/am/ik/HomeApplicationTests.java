package am.ik;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.util.SocketUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.ipc.netty.NettyContext;
import reactor.ipc.netty.http.server.HttpServer;

// TODO use TestSubscriber
public class HomeApplicationTests {
	static WebClient webClient;
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

		webClient = WebClient.builder(String.format("http://%s:%d", host, port))
				.clientConnector(new ReactorClientHttpConnector()).build();
	}

	@Test
	public void root() {
		Mono<ClientResponse> result = webClient.get().uri("").exchange();
		// assertThat(result.block().bodyToMono(String.class).block()).isEqualTo("Sample");
		assertThat(result.block().statusCode()).isEqualTo(HttpStatus.OK);
	}
}
