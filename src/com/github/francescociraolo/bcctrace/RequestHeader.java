package com.github.francescociraolo.bcctrace;

/**
 * A simple header for a variable's request.
 * It model the variable expected type and a name used to retrieve the variable itself.
 *
 * @param <T> the expected type of the required variable.
 */
public interface RequestHeader<T> {

    /**
     * Return a simple request header implementation.
     *
     * @param name the request's name
     * @param valueType the type of the requested variable
     * @param <T> the type of the retrieving variable
     * @return a simple RequestHeader
     */
    static <T> RequestHeader<T> getSimpleRequestHeader(String name, ValueType<T> valueType) {
        return new SimpleRequestHeader<>(name, valueType);
    }

    /**
     * Utility method to obtain the casted variable.
     *
     * @param traceOutputVariable the variable obtained from the trace software.
     * @return the casted variable.
     */
    default T castVariable(String traceOutputVariable) {
        return getValueType().getValueCaster().cast(traceOutputVariable);
    }

    /**
     * Provide the name of the request, it is used as trace's output template and for retrieve the variable.
     *
     * @return the {@link String} representation of the name.
     */
    String getName();

    /**
     * Return the type of the requested variable; it is usable to obtain the format and the caster.
     *
     * @return the {@link ValueType} of the requested variable.
     */
    ValueType<T> getValueType();
}
