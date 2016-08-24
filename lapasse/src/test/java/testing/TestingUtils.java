package testing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    /**
     * Superficial enum code coverage: extracts all values from the enum and calls the synthetic
     * method `valueOf` on each value's name.
     *
     * @param enumClass The enum to test.
     */
    public static void superficialEnumCodeCoverage(Class<? extends Enum<?>> enumClass) {
        try {
            Method valueOf = enumClass.getMethod("valueOf", String.class);
            Enum<?>[] values = (Enum<?>[]) enumClass.getMethod("values").invoke(null);

            for (Enum<?> o : values) {
                valueOf.invoke(null, o.name());
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
