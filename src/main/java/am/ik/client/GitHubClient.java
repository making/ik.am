package am.ik.client;

import static java.util.Spliterator.SIZED;
import static java.util.Spliterators.spliterator;
import static java.util.stream.StreamSupport.stream;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class GitHubClient {

	private final WebClient webClient;

	private AtomicReference<Tuple2<String, List<GitHubEvent>>> last = new AtomicReference<>(
			null);

	public GitHubClient(ClientHttpConnector httpConnector) {
		this.webClient = WebClient.builder().baseUrl("https://api.github.com/")
				.clientConnector(httpConnector).build();
	}

	public Flux<GitHubEvent> findEvents() {
		Mono<ClientResponse> exchange;
		if (last.get() != null) {
			exchange = this.webClient.get().uri("users/making/events")
					.header("User-Agent", "am.ik.client.GitHubClient")
					.ifNoneMatch(last.get().getT1()).exchange();
		}
		else {
			exchange = this.webClient.get().uri("users/making/events")
					.header("User-Agent", "am.ik.client.GitHubClient").exchange();
		}
		return exchange.then(res -> res.statusCode() == HttpStatus.NOT_MODIFIED
				? Mono.just(last.get().getT2())
				: res.bodyToMono(JsonNode.class).map(node -> {
					List<GitHubEvent> events = bodyToList(node);
					last.set(Tuples.of(res.headers().asHttpHeaders().getETag(), events));
					return events;
				})).flatMap(Flux::fromIterable).switchOnError(last.get() == null
						? Flux.empty() : Flux.fromIterable(last.get().getT2()));
	}

	private static List<GitHubEvent> bodyToList(JsonNode node) {
		return stream(spliterator(node.elements(), node.size(), SIZED), false)
				.flatMap(n -> {
					String type = n.get("type").asText().replace("Event", "");
					String repo = n.get("repo").get("name").asText();
					String createdAt = n.get("created_at").asText();
					switch (type) {
					case "PullRequest":
						return Stream.of(new GitHubEvent(type, repo, null,
								n.get("payload").get("pull_request").get("html_url")
										.asText(),
								createdAt));
					case "Push": {
						JsonNode commits = n.get("payload").get("commits");
						return stream(
								spliterator(commits.elements(), commits.size(), SIZED),
								false).map(c -> {
									return new GitHubEvent(type, repo,
											c.get("message").asText(),
											"https://github.com/" + repo + "/commit/"
													+ c.get("sha").asText(),
											createdAt);
								});
					}
					default:
						return Stream
								.of(new GitHubEvent(type, repo, null, null, createdAt));
					}
				}).distinct().collect(Collectors.toList());
	}

	public static class GitHubEvent {
		private String type;
		private String repo;
		private String message;
		private String url;
		private String createdAt;

		public GitHubEvent(String type, String repo, String message, String url,
				String createdAt) {
			this.type = type;
			this.repo = repo;
			this.message = message;
			this.url = url;
			this.createdAt = createdAt;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getRepo() {
			return repo;
		}

		public void setRepo(String repo) {
			this.repo = repo;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(String createdAt) {
			this.createdAt = createdAt;
		}

		@Override
		public String toString() {
			return "GitHubEvent{" + "type='" + type + '\'' + ", repo='" + repo + '\''
					+ ", message='" + message + '\'' + ", url='" + url + '\'' + '}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof GitHubEvent))
				return false;

			GitHubEvent that = (GitHubEvent) o;

			if (type != null ? !type.equals(that.type) : that.type != null)
				return false;
			if (repo != null ? !repo.equals(that.repo) : that.repo != null)
				return false;
			if (message != null ? !message.equals(that.message) : that.message != null)
				return false;
			return url != null ? url.equals(that.url) : that.url == null;
		}

		@Override
		public int hashCode() {
			int result = type != null ? type.hashCode() : 0;
			result = 31 * result + (repo != null ? repo.hashCode() : 0);
			result = 31 * result + (message != null ? message.hashCode() : 0);
			result = 31 * result + (url != null ? url.hashCode() : 0);
			return result;
		}
	}
}
