package am.ik;

import java.io.IOException;

import org.springframework.web.reactive.result.view.AbstractUrlBasedView;
import org.springframework.web.reactive.result.view.View;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerView;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerViewResolver;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

class FreeMarkerConfig {
	static ViewResolver viewResolver() {
		Configuration configuration = configuration();
		FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver() {
			@Override
			protected View applyLifecycleMethods(String viewName,
					AbstractUrlBasedView view) {
				FreeMarkerView freeMarkerView = (FreeMarkerView) view;
				freeMarkerView.setConfiguration(configuration);
				return freeMarkerView;
			}
		};
		viewResolver.setPrefix("");
		viewResolver.setSuffix(".ftl");
		return viewResolver;
	}

	static Configuration configuration() {
		FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
		configurer.setTemplateLoaderPath("classpath:/templates/");
		configurer.setDefaultEncoding("UTF-8");
		try {
			configurer.afterPropertiesSet();
		}
		catch (IOException | TemplateException e) {
			throw new IllegalStateException(e);
		}
		return configurer.getConfiguration();
	}
}
