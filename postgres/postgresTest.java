///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS org.assertj:assertj-core:3.24.2
//DEPS org.junit.jupiter:junit-jupiter-api:5.9.3
//DEPS org.junit.jupiter:junit-jupiter-engine:5.9.3
//DEPS org.junit.jupiter:junit-jupiter-params:5.9.3
//DEPS org.junit.platform:junit-platform-launcher:1.7.2
//DEPS ch.qos.logback:logback-classic:1.3.8
//DEPS org.testcontainers:postgresql:1.18.3

//JAVA 17

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.testcontainers.containers.PostgreSQLContainer;
import org.junit.platform.launcher.listeners.LoggingListener;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static java.lang.System.out;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.util.stream.Stream;

class postgresTest {

    @ParameterizedTest
    @MethodSource("majorMinorVersions")
    void testpostgres(String image) {
        try(PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(image)) {
            postgres.start();
            assertThat(postgres.isRunning()).isTrue();
        }
    }

    static Stream<String> majorMinorVersions() {
        return Stream.of("postgres:9-alpine", "postgres:10-alpine","postgres:11-alpine", "postgres:12-alpine", "postgres:13-alpine","postgres:14-alpine");
    }

    public static void main(final String... args) {
        final LauncherDiscoveryRequest request =
                LauncherDiscoveryRequestBuilder.request()
                        .selectors(selectClass(postgresTest.class))
                        .build();
        final Launcher launcher = LauncherFactory.create();
        final LoggingListener logListener = LoggingListener.forBiConsumer((t,m) -> {
            System.out.println(m.get());
            if(t!=null) {
                t.printStackTrace();
            };
        });
        final SummaryGeneratingListener execListener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(execListener, logListener);
        launcher.execute(request);
        execListener.getSummary().printTo(new java.io.PrintWriter(out));
    }
}
