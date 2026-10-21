package org.formation.coffee.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Fait échouer le build si les dépendances ne pointent plus vers l'intérieur.
 */
class HexagonalArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("org.formation.coffee");

    @Test
    void coreDoesNotDependOnFrameworks() {
        noClasses().that().resideInAnyPackage("..domain..", "..application..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta.persistence..", "org.mapstruct..")
                .check(classes);
    }

    @Test
    void applicationDoesNotDependOnAdapters() {
        noClasses().that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage("..adapter..", "..config..")
                .check(classes);
    }

    @Test
    void domainDoesNotDependOnApplication() {
        noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("..application..", "..adapter..", "..config..")
                .check(classes);
    }
}
