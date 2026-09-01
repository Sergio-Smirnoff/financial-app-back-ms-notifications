package com.financialapp.notifications.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class LayeredArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.financialapp.notifications");
    }

    @Test
    void layers_respect_inward_dependency_flow() {
        layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Domain").definedBy("com.financialapp.notifications.domain..")
                .layer("Application").definedBy("com.financialapp.notifications.application..")
                .layer("Infrastructure").definedBy("com.financialapp.notifications.infrastructure..")
                .layer("Web").definedBy("com.financialapp.notifications.web..")
                .whereLayer("Domain").mayNotAccessAnyLayer()
                .whereLayer("Application").mayOnlyAccessLayers("Domain")
                .whereLayer("Infrastructure").mayOnlyAccessLayers("Domain", "Application")
                .whereLayer("Web").mayOnlyAccessLayers("Domain", "Application", "Infrastructure")
                .check(classes);
    }

    @Test
    void domain_is_free_of_framework_and_outer_layers() {
        noClasses().that().resideInAPackage("com.financialapp.notifications.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.financialapp.notifications.web..",
                        "com.financialapp.notifications.infrastructure..",
                        "org.springframework..",
                        "jakarta.persistence.."
                )
                .check(classes);
    }

    @Test
    void application_does_not_depend_on_web() {
        noClasses().that().resideInAPackage("com.financialapp.notifications.application..")
                .should().dependOnClassesThat().resideInAPackage("com.financialapp.notifications.web..")
                .check(classes);
    }
}
