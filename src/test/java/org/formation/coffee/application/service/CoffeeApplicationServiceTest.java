package org.formation.coffee.application.service;

import org.formation.coffee.application.port.in.CreateCoffeeCommand;
import org.formation.coffee.application.port.in.UpdateCoffeeCommand;
import org.formation.coffee.application.port.out.LoadCoffeePort;
import org.formation.coffee.application.port.out.SaveCoffeePort;
import org.formation.coffee.application.port.out.TransactionRunner;
import org.formation.coffee.domain.exception.CoffeeNotFoundException;
import org.formation.coffee.domain.model.Coffee;
import org.formation.coffee.domain.model.CoffeeId;
import org.formation.coffee.domain.model.Price;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Teste le service avec de faux ports en mémoire : ni Spring, ni base de données.
 */
class CoffeeApplicationServiceTest {

    private final InMemoryCoffeeStore store = new InMemoryCoffeeStore();
    private final CoffeeApplicationService service =
            new CoffeeApplicationService(store, store, new NoTransactionRunner());

    @Test
    void createAssignsIdAndStoresCoffee() {
        Coffee created = service.createCoffee(new CreateCoffeeCommand("Granador", new Price(10)));

        assertThat(created.getId()).isEqualTo(new CoffeeId(1));
        assertThat(service.listCoffees()).containsExactly(created);
    }

    @Test
    void getUnknownCoffeeThrows() {
        assertThatThrownBy(() -> service.getCoffee(new CoffeeId(999)))
                .isInstanceOf(CoffeeNotFoundException.class)
                .hasMessage("Coffee 999 not found");
    }

    @Test
    void updateChangesOnlyProvidedFields() {
        Coffee created = service.createCoffee(new CreateCoffeeCommand("Granador", new Price(10)));
        CoffeeId id = created.getId();

        Coffee updated = service.updateCoffee(new UpdateCoffeeCommand(id, null, new Price(12)));

        assertThat(updated.getName()).isEqualTo("Granador");
        assertThat(updated.getPrice()).isEqualTo(new Price(12));
    }

    /** Faux port de persistance : une simple Map. */
    static class InMemoryCoffeeStore implements LoadCoffeePort, SaveCoffeePort {

        private final Map<Long, Coffee> coffees = new LinkedHashMap<>();
        private long nextId = 1;

        @Override
        public List<Coffee> findAll() {
            return List.copyOf(coffees.values());
        }

        @Override
        public Optional<Coffee> findById(CoffeeId id) {
            return Optional.ofNullable(coffees.get(id.value()));
        }

        @Override
        public Coffee save(Coffee coffee) {
            CoffeeId id = coffee.getId();
            Coffee stored = id == null
                    ? Coffee.reconstitute(new CoffeeId(nextId++), coffee.getName(), coffee.getPrice(), coffee.getReviews())
                    : coffee;
            coffees.put(stored.getId().value(), stored);
            return stored;
        }
    }

    /** Faux TransactionRunner : exécute le travail directement. */
    static class NoTransactionRunner implements TransactionRunner {

        @Override
        public <T> T execute(Supplier<T> work) {
            return work.get();
        }

        @Override
        public <T> T executeReadOnly(Supplier<T> work) {
            return work.get();
        }
    }
}
