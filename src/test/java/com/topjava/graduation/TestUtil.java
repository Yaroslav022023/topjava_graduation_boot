package com.topjava.graduation;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.NonNull;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestUtil {

    public static <T extends Throwable> void validateRootCause(Class<T> rootExceptionClass, Runnable runnable) {
        assertThrows(rootExceptionClass, () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw getRootCause(e);
            }
        });
    }

    @NonNull
    private static Throwable getRootCause(@NonNull Throwable t) {
        Throwable rootCause = NestedExceptionUtils.getRootCause(t);
        return rootCause != null ? rootCause : t;
    }
}