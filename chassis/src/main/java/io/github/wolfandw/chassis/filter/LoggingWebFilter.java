package io.github.wolfandw.chassis.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Фильтр HTTP-запросов.
 */
public class LoggingWebFilter implements WebFilter {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingWebFilter.class);
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (path.contains("/actuator/")) {
            return chain.filter(exchange);
        }
        long start = System.currentTimeMillis();
        return chain.filter(exchange)
                .doOnSuccess(v -> {
                    long duration = System.currentTimeMillis() - start;
                    LOG.info("Method: {}, URI: {}, status: {}, time: {}ms",
                            exchange.getRequest().getMethod(), exchange.getRequest().getURI(),
                            exchange.getResponse().getStatusCode(), duration);
                });
    }
}
