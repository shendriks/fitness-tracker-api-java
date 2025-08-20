package dev.shendriks.fitnesstrackerapi.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.library.freeze.FreezingArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;

public class FitnessTrackerApiArchitectureTest {
    @Test
    public void givenApplicationClasses_thenNoLayerViolationsShouldExist() {

        JavaClasses jc = new ClassFileImporter().importPackages("dev.shendriks.fitnesstrackerapi");

        FreezingArchRule arch = freeze(onionArchitecture()
            .domainModels("dev.shendriks.fitnesstrackerapi.domain..")
//            .domainServices("com.myapp.domain.service..")
            .applicationServices("dev.shendriks.fitnesstrackerapi.application..")
            .adapter("persistence", "dev.shendriks.fitnesstrackerapi.adapter.out.jpa..")
            .adapter("rest", "dev.shendriks.fitnesstrackerapi.adapter.in.rest.."));

        arch.check(jc);
    }
}
