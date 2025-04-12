package com.dansoftware.mugify.util;

public interface UncheckedAction {
    void exec() throws Exception;

    /**
     * A utility method for executing code that throws checked exceptions.
     * It wraps it and throws a {@link RuntimeException} instead.
     * @param action the action to execute
     */
    static void run(UncheckedAction action) {
        try {
            action.exec();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
