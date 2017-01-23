package testing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

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
     * Run a concurrency test multiple times using a {@link CountDownLatch} on a default number of
     * threads.
     *
     * @param test The test to execute.
     */
    public static void runConcurrencyTest(final Runnable test) {
        // run concurrency test on 10 threads, 10 times
        for (int i = 0; i < 10; i++) {
            runConcurrencyTest(test, 10);
        }
    }

    /**
     * Run a concurrency test on a specified number of threads using a {@link CountDownLatch}.
     *
     * @param test       The test to execute.
     * @param numThreads The number of threads to run this test on, concurrently.
     */
    public static void runConcurrencyTest(final Runnable test, int numThreads) {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean failed = new AtomicBoolean(false);

        Runnable testWrapper = new Runnable() {
            public void run() {
                try {
                    latch.await();
                    test.run();
                } catch (Exception e) {
                    e.printStackTrace();
                    failed.set(true);
                }
            }
        };

        Thread[] threads = new Thread[numThreads];

        // create and start threads
        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(testWrapper);
            thread.start();
            threads[i] = thread;
        }

        // run all tests concurrently
        latch.countDown();

        // wait for threads to end
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (failed.get()) {
            fail("Concurrency test failed");
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
