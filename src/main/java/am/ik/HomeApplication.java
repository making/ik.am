package am.ik;

import static org.springframework.web.reactive.function.server.RouterFunctions.resources;
import static org.springframework.web.reactive.function.server.RouterFunctions.toHttpHandler;

import java.util.Optional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import am.ik.client.BlogClient;
import am.ik.client.GitHubClient;
import io.netty.channel.Channel;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.NettyContext;
import reactor.ipc.netty.http.server.HttpServer;

public class HomeApplication {

	public static void main(String[] args) throws Exception {
		int port = Optional.ofNullable(System.getenv("PORT")).map(Integer::parseInt)
				.orElse(8080);
		Channel channel = null;
		try {
			HttpServer httpServer = HttpServer.create("0.0.0.0", port);
			Mono<? extends NettyContext> handler = httpServer
					.newHandler(new ReactorHttpHandlerAdapter(httpHandler()));
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				System.out.println("Shut down ...");
			}));
			channel = handler.block().channel();
			channel.closeFuture().sync();
		}
		finally {
			if (channel != null) {
				channel.eventLoop().shutdownGracefully();
			}
		}
	}

	static HttpHandler httpHandler() {
		ClientHttpConnector httpConnector = new ReactorClientHttpConnector();
		BlogClient blogClient = new BlogClient(httpConnector);
		GitHubClient gitHubClient = new GitHubClient(httpConnector);
		HomeHandler homeHandler = new HomeHandler(blogClient, gitHubClient);

		RouterFunction<?> route = homeHandler.route().and(staticResources());
		return toHttpHandler(route, HandlerStrategies.builder()
				.viewResolver(FreeMarkerConfig.viewResolver()).build());
	}

	static RouterFunction<ServerResponse> staticResources() {
		return resources("/**", new ClassPathResource("static/"));
	}

}
