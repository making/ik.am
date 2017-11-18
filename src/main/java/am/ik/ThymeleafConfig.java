package am.ik;

import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.context.ApplicationContext;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.web.reactive.result.view.View;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveView;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import reactor.core.publisher.Mono;

public class ThymeleafConfig {
	static ViewResolver viewResolver() {

		ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
		resolver.setPrefix("/templates/");
		resolver.setSuffix(".html");
		resolver.setCacheable(System.getenv("VCAP_APPLICATION") != null);
		SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
		templateEngine.setTemplateResolver(resolver);
		ReactiveAdapterRegistry registry = new ReactiveAdapterRegistry();
		ApplicationContext fake = (ApplicationContext) Proxy.newProxyInstance(
				ThymeleafConfig.class.getClassLoader(),
				new Class[] { ApplicationContext.class }, (proxy, method, args) -> {
					if ("containsBean".equals(method.getName())) {
						return false;
					}
					if ("getBean".equals(method.getName())
							&& args[0].equals(ReactiveAdapterRegistry.class)) {
						return registry;
					}
					else {
						new Exception().printStackTrace();
					}
					return null;
				});

		return new ThymeleafReactiveViewResolver() {
			@Override
			protected Mono<View> loadView(String viewName, Locale locale) {
				ThymeleafReactiveView view = new ThymeleafReactiveView() {
					{
						setTemplateEngine(templateEngine);
						setTemplateName(viewName);
						setDefaultCharset(StandardCharsets.UTF_8);
						setLocale(Locale.getDefault());
						setApplicationContext(fake);
						if (shouldUseChunkedExecution(viewName)) {
							setResponseMaxChunkSizeBytes(getResponseMaxChunkSizeBytes());
						}
					}
				};
				return Mono.just(view);
			}
		};
	}
}