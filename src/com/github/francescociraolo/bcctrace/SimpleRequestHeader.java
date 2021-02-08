package com.github.francescociraolo.bcctrace;

class SimpleRequestHeader<T> implements RequestHeader<T> {

    private final String name;
    private final ValueType<T> valueType;

    public SimpleRequestHeader(String name, ValueType<T> valueType) {
        this.name = name;
        this.valueType = valueType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ValueType<T> getValueType() {
        return valueType;
    }
}
