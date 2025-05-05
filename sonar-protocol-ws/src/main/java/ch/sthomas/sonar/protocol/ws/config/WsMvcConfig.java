package ch.sthomas.sonar.protocol.ws.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WsMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(
                MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.ALL);
    }

    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.removeIf(
                converter ->
                        MappingJackson2XmlHttpMessageConverter.class.isAssignableFrom(
                                converter.getClass()));
    }
}
