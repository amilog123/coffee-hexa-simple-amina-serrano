package org.formation.coffee.domain.model;

import org.formation.coffee.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoffeeTest {

    @Test
    void createdCoffeeIsNewAndHasNoReviews() {
        Coffee coffee = Coffee.create("Granador", new Price(10));

        assertThat(coffee.isNew()).isTrue();
        assertThat(coffee.getId()).isNull();
        assertThat(coffee.getReviews()).isEmpty();
    }

    @Test
    void rejectsBlankName() {
        assertThatThrownBy(() -> Coffee.create(" ", new Price(10)))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void rejectsTooLongName() {
        assertThatThrownBy(() -> Coffee.create("x".repeat(256), new Price(10)))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void renameAndChangePriceApplyRules() {
        Coffee coffee = Coffee.create("Granador", new Price(10));

        coffee.rename("Tres Puntas");
        coffee.changePrice(new Price(70));

        assertThat(coffee.getName()).isEqualTo("Tres Puntas");
        assertThat(coffee.getPrice()).isEqualTo(new Price(70));
        assertThatThrownBy(() -> coffee.rename("")).isInstanceOf(DomainValidationException.class);
    }

    @Test
    void reviewRejectsRatingOutOfRange() {
        assertThatThrownBy(() -> new Review(new ReviewId(1), "Ana", "ok", 6))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void identityIsBasedOnId() {
        Coffee a = Coffee.reconstitute(new CoffeeId(1), "A", new Price(1), List.of());
        Coffee sameId = Coffee.reconstitute(new CoffeeId(1), "B", new Price(2), List.of());
        Coffee other = Coffee.reconstitute(new CoffeeId(2), "A", new Price(1), List.of());

        assertThat(a).isEqualTo(sameId).isNotEqualTo(other);
    }
}
