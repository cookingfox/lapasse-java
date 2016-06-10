package fixtures.annotations;

import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.impl.internal.HandlerMapper;

/**
 * Fixture generated class with a constructor that throws an exception.
 */
public class ThrowingConstructor$$LaPasseGenerated<T extends FixtureAnnotated> implements HandlerMapper {

    public ThrowingConstructor$$LaPasseGenerated(T origin, Facade facade) {
        throw new RuntimeException("Example error in constructor");
    }

    @Override
    public void mapHandlers() {
        // ignore
    }

}
