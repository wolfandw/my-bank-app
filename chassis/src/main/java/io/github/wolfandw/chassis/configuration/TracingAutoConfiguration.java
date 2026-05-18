package io.github.wolfandw.chassis.configuration;

import brave.Span;
import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.propagation.TraceContext;
import io.micrometer.common.KeyValue;
import io.micrometer.observation.ObservationFilter;
import io.micrometer.observation.ObservationPredicate;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.exporter.SpanExportingPredicate;
import io.r2dbc.proxy.observation.ObservationProxyExecutionListener;
import io.r2dbc.proxy.observation.QueryContext;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.r2dbc.autoconfigure.ProxyConnectionFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.support.ScheduledTaskObservationContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Авто-конфигурация трассировки запросов.
 */
@AutoConfiguration
public class TracingAutoConfiguration {
    private static final Pattern TABLE_NAME_PATTERN = Pattern.compile(
            "\\b(from|join|into|update)\\s+[\"]?([a-zA-Z0-9_]+)[\"]?",
            Pattern.CASE_INSENSITIVE
    );
    private static final String OUTBOX_TABLE = "outbox";
    private static final String NOTIFICATIONS_TABLE = "notifications";
    private static final String QUERY = "query";
    private static final String DB_TABLE = "db.table";
    private static final String POSTGRE_SQL = "PostgreSQL";
    private static final String LIMIT_1 = "LIMIT 1";
    public static final String HTTP_URL = "http.url";
    public static final String ACTUATOR_PROMETHEUS = "/actuator/prometheus";

    @Bean
    @ConditionalOnProperty(name = "spring.r2dbc.url")
    public ProxyConnectionFactoryCustomizer proxyConnectionFactoryCustomizer(ObservationRegistry registry,
                                                                   @Lazy ConnectionFactory connectionFactory,
                                                                   @Value("${spring.r2dbc.url}") String connectionFactoryUrl) {
        return (builder) -> {
            ObservationProxyExecutionListener listener = new ObservationProxyExecutionListener(
                    registry,
                    connectionFactory,
                    connectionFactoryUrl
            );
            listener.setIncludeParameterValues(true);
            builder.listener(listener);
        };
    }

    @Bean
    public SpanHandler spanHandler() {
        return new SpanHandler() {
            @Override
            public boolean end(TraceContext context, MutableSpan span, Cause cause) {
                String name = span.name();
                boolean isUnknownName = name == null;
                String remoteServiceName = span.remoteServiceName();
                boolean isPostgres = remoteServiceName != null && remoteServiceName.contains(POSTGRE_SQL);
                boolean isRootClient = span.kind() == Span.Kind.CLIENT;
                return !(isUnknownName && isPostgres && isRootClient);
            }
        };
    }

    @Bean
    public ObservationFilter observationFilter() {
            return (context) -> {
                if (context instanceof QueryContext queryContext) {
                    String sql = queryContext.getQueries().isEmpty()
                            ? ""
                            : queryContext.getQueries().getFirst();

                    if (sql != null && !sql.isEmpty()) {
                        String tableName = "";
                        Matcher matcher = TABLE_NAME_PATTERN.matcher(sql);
                        if (matcher.find()) {
                            tableName = matcher.group(2);
                        }
                        if (!tableName.isEmpty() && !((OUTBOX_TABLE.equalsIgnoreCase(tableName) ||
                                NOTIFICATIONS_TABLE.equalsIgnoreCase(tableName)) &&
                                sql.contains(LIMIT_1))) {
                            context.addLowCardinalityKeyValue(KeyValue.of(DB_TABLE, tableName.toLowerCase()));
                        }
                    }
                }
                return context;
            };
    }

    @Bean
    public ObservationPredicate observationPredicate() {
        return (name, context) -> !(context instanceof ScheduledTaskObservationContext);
    }

    @Bean
    public SpanExportingPredicate spanExportingPredicate() {
        return (finishedSpan) -> {
            String name = finishedSpan.getName();
            if (QUERY.equalsIgnoreCase(name)) {
                String tableName = finishedSpan.getTags().get(DB_TABLE);
                return tableName != null;
            } else if ("security filterchain before".equalsIgnoreCase(name) ||
                        "authorize exchange".equalsIgnoreCase(name) ||
                        "secured request".equalsIgnoreCase(name) ||
                        "security filterchain after".equalsIgnoreCase(name) ||
                        "unknown".equalsIgnoreCase(name)) {
                return false;
            }
            String httpUrl =  finishedSpan.getTags().get(HTTP_URL);
            if (httpUrl != null && !httpUrl.isEmpty()) {
                return !ACTUATOR_PROMETHEUS.equals(httpUrl);
            }
            return true;
        };
    }
}
