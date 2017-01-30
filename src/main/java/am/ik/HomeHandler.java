package am.ik;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import am.ik.client.BlogClient;
import am.ik.client.GitHubClient;
import reactor.core.publisher.Mono;

public class HomeHandler {

	private final BlogClient blogClient;
	private final GitHubClient gitHubClient;

	public HomeHandler(BlogClient blogClient, GitHubClient gitHubClient) {
		this.blogClient = blogClient;
		this.gitHubClient = gitHubClient;
	}

	public Mono<ServerResponse> indexView(ServerRequest req) {
		return Mono.when(blogClient.findEntries(10).collectList(),
				gitHubClient.findEvents().collectList()).then(t -> {
					Map<String, Object> model = new HashMap<>();
					model.put("entries", t.getT1());
					model.put("events", t.getT2());
					return ok().render("index", model);
				});
	}
}
