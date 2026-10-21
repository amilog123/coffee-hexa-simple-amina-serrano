package org.formation.coffee.domain.model;

import org.formation.coffee.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceTest {

    @Test
    void acceptsBounds() {
        assertThat(new Price(0).amount()).isZero();
        assertThat(new Price(200).amount()).isEqualTo(200);
    }

    @Test
    void rejectsPriceAboveMax() {
        assertThatThrownBy(() -> new Price(500))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("500");
    }

    @Test
    void rejectsNegativePrice() {
        assertThatThrownBy(() -> new Price(-1)).isInstanceOf(DomainValidationException.class);
    }
}
