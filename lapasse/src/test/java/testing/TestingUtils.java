package testing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Static testing utility methods.
 */
public final class TestingUtils {

    /**
     * Assert that the instantiation of a utility class's private constructor throws an
     * {@link UnsupportedOperationException}. This is for purposes of code coverage only.
     *
     * @param cls The class to test.
     */
    public static void assertPrivateConstructorInstantiationUnsupported(Class<?> cls) {
        try {
            Constructor<?> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();

            fail("Expected exception");
        } catch (Exception e) {
            assertTrue(e instanceof InvocationTargetException);
            assertTrue(UnsupportedOperationException.class.isInstance(e.getCause()));
        }
    }

}
