package org.acme;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.quarkus.runtime.StartupEvent;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileReader;
import java.io.IOException;

@ApplicationScoped
public class MyStartupMetrics {

    public MyStartupMetrics(MeterRegistry registry) {
        registry.counter("api.model.version", Tags.of("version", getVersion()));
    }

    void onStart(@Observes StartupEvent ev) {
        // do nothing, class has just to be invoked during startup
    }

    private String getVersion() {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model;

        try {
            model = reader.read(new FileReader("pom.xml"));
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException(e);
        }

        return model.getDependencies().stream()
                .filter(dependency -> dependency.getArtifactId().contains("maven-model"))
                .findFirst()
                .map(Dependency::getVersion)
                .orElse("0.0.0");
    }
}