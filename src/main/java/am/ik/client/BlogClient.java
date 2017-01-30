package am.ik.client;

import static java.util.Spliterator.SIZED;
import static java.util.Spliterators.spliterator;
import static java.util.stream.StreamSupport.stream;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientOperations;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Flux;

public class BlogClient {
	private final WebClientOperations operations;

	public BlogClient(WebClient webClient) {
		this.operations = WebClientOperations.builder(webClient)
				.uriBuilderFactory(new DefaultUriBuilderFactory(
						"https://blog-api.cfapps.pez.pivotal.io/api/"))
				.build();
	}

	public Flux<Entry> findEntries(int size) {
		return this.operations.get()
				.uri(f -> f.uriString("entries").queryParam("excludeContent", true)
						.queryParam("size", size).build())
				.exchange().then(x -> x.bodyToMono(JsonNode.class))
				.map(res -> res.get("content").elements())
				.flatMap(x -> Flux.fromStream(stream(spliterator(x, size, SIZED), false)))
				.map(n -> new Entry(n.get("entryId").asLong(),
						n.get("frontMatter").get("title").asText(),
						n.get("created").get("date").asText(),
						n.get("updated").get("date").asText()))
				.switchOnError(Flux.empty());
	}

	public static class Entry {
		private Long entryId;
		private String title;
		private String createdAt;
		private String updatedAt;

		public Entry(Long entryId, String title, String createdAt, String updatedAt) {
			this.entryId = entryId;
			this.title = title;
			this.createdAt = createdAt;
			this.updatedAt = updatedAt;
		}

		public Long getEntryId() {
			return entryId;
		}

		public String getTitle() {
			return title;
		}

		public String getCreatedAt() {
			return createdAt;
		}

		public String getUpdatedAt() {
			return updatedAt;
		}

		public void setEntryId(Long entryId) {
			this.entryId = entryId;
		}

		@Override
		public String toString() {
			return "Entry{" + "entryId=" + entryId + ", title='" + title + '\''
					+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
		}
	}
}
