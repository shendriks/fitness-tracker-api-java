package dev.shendriks.fitnesstrackerapi.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.library.Architectures.OnionArchitecture;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

public class FitnessTrackerApiArchitectureTest {
    @Test
    public void givenApplicationClasses_thenNoLayerViolationsShouldExist() {

        JavaClasses jc = new ClassFileImporter().importPackages("dev.shendriks.fitnesstrackerapi");

        OnionArchitecture arch = onionArchitecture()
            .domainModels(
                "dev.shendriks.fitnesstrackerapi.domain.entity..",
                "dev.shendriks.fitnesstrackerapi.domain.value.."
            )
            .domainServices("dev.shendriks.fitnesstrackerapi.domain.service..")
            .applicationServices("dev.shendriks.fitnesstrackerapi.application..")
            .adapter("persistence", "dev.shendriks.fitnesstrackerapi.adapter.out.jpa..")
            .adapter("rest", "dev.shendriks.fitnesstrackerapi.adapter.in.rest..")
            // not really an adapter, but rather crosscutting concerns, but we want to allow access to the domain
            .adapter("infrastructure", "dev.shendriks.fitnesstrackerapi.infrastructure..");

        arch.check(jc);
    }
}
