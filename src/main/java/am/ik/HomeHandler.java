package am.ik;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import am.ik.client.BlogClient;
import am.ik.client.BlogClient.Entry;
import am.ik.client.GitHubClient;
import am.ik.client.GitHubClient.GitHubEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class HomeHandler {

	private final BlogClient blogClient;
	private final GitHubClient gitHubClient;

	public HomeHandler(BlogClient blogClient, GitHubClient gitHubClient) {
		this.blogClient = blogClient;
		this.gitHubClient = gitHubClient;
	}

	public RouterFunction<ServerResponse> route() {
		return RouterFunctions.route(GET("/"), this::indexView);
	}

	public Mono<ServerResponse> indexView(ServerRequest req) {
		Flux<Entry> entries = blogClient.findEntries(10);
		Flux<GitHubEvent> events = gitHubClient.findEvents();
		Map<String, Object> model = new HashMap<>();
		model.put("entries", entries);
		model.put("events", events);
		return ok().contentType(MediaType.TEXT_HTML).render("index", model);
	}
}
