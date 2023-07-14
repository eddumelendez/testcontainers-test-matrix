///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS org.assertj:assertj-core:3.24.2
//DEPS org.junit.jupiter:junit-jupiter-api:5.9.3
//DEPS org.junit.jupiter:junit-jupiter-engine:5.9.3
//DEPS org.junit.jupiter:junit-jupiter-params:5.9.3
//DEPS org.junit.platform:junit-platform-launcher:1.7.2
//DEPS ch.qos.logback:logback-classic:1.3.8
//DEPS org.testcontainers:vault:1.18.3

//JAVA 17

//FILES ../logback-test.xml

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.testcontainers.vault.VaultContainer;
import org.junit.platform.launcher.listeners.LoggingListener;

import static java.lang.System.out;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class vaultTest {

    @ParameterizedTest
    @MethodSource("majorMinorVersions")
    void testpostgres(String image) {
        try(VaultContainer<?> vault = new VaultContainer<>(image)) {
            vault.start();
            assertThat(vault.isRunning()).isTrue();
        }
    }

    static Stream<String> majorMinorVersions() {
        return Stream.of("vault:0.6.0", "vault:0.7.0", "vault:0.8.0",
        "vault:0.9.0", "vault:0.10.0", "vault:0.11.0", "vault:1.0.0", "vault:1.1.0", "vault:1.2.0", "vault:1.3.0", "vault:1.4.0", "vault:1.5.0", "vault:1.6.0", "vault:1.7.0", "vault:1.8.0", "vault:1.9.0", "vault:1.10.0", "vault:1.11.0", "vault:1.12.0", "vault:1.13.0", "hashicorp/vault:1.14.0");
    } 

    public static void main(final String... args) {
        final LauncherDiscoveryRequest request =
                LauncherDiscoveryRequestBuilder.request()
                        .selectors(selectClass(vaultTest.class))
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
