package de.lystx.cloudsystem.library.value;


import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ValueObject implements Serializable, Iterable<Object> {

    private final List<Value<String, ?>> values;

    public ValueObject() {
        this(new LinkedList<>());
    }

    public ValueObject(ValueObject valueObject) {
        this(valueObject.getValues());
    }

    private ValueObject(List<Value<String, ?>> values) {
        this.values = values;
    }


    public void append(String key, Object value) {
        this.values.add(new Value<>(key, value));
    }

    public void append(Integer position, String key, Object value) {
        this.values.add(position, new Value<>(key, value));
    }

    public Object getData(String key) {
        return get(key).getValue();
    }

    public Value<?, ?> get(String key) {
        for (Value<?, ?> value : this.values) {
            if (value.getKey().equals(key)) {
                return value;
            }
        }
        return null;
    }

    public Value<?, ?> getByValue(Object value) {
        for (Value<?, ?> data : this.values) {
            if (data.getValue().equals(value)) {
                return data;
            }
        }
        return null;
    }

    public Value<?, ?> get(Integer position) {
        return this.values.get(position);
    }

    public List<Value<String, ?>> getValues() {
        return values;
    }


    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("<\n");
        for (Value<?, ?> value : getValues()) {
            string.append("   ").append(value.getKey()).append(" <> ").append(value.getValue()).append("\n");
        }
        string.append("?>");
        return string.toString();
    }

    @Override
    public Iterator<Object> iterator() {
        List<Object> list = new LinkedList<>();
        for (Value<String, ?> value : this.values) {
            list.add(value.getValue());
        }
        return list.iterator();
    }
}
