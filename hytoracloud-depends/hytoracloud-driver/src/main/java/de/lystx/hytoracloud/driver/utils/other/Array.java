package de.lystx.hytoracloud.driver.utils.other;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class Array<T> {

    /**
     * The stored objects
     */
    private final T[] objects;

    public Array(int size) {
        this.objects = (T[]) new Object[size];
    }

    /**
     * Constructs pre array
     *
     * @param javaArray the array
     */
    public Array(T[] javaArray) {
        this.objects = javaArray;
    }

    /**
     * Loads all values from {@link Iterable}
     *
     * @param iterable the iterable
     */
    public Array(Iterable<T> iterable) {
        this(iterable instanceof List ? ((List<?>)iterable).size() : 999);
        int index = 0;
        for (T t : iterable) {
            this.put(index, t);
            index++;
        }
    }

    /**
     * Puts an object into the array
     *
     * @param index the position
     * @param object the object
     * @return current array
     */
    public Array<T> put(int index, T object) {
        this.objects[index] = object;
        return this;
    }

    /**
     * The size of the array
     *
     * @return int size
     */
    public int size() {
        return this.objects.length;
    }

    /**
     * Gets an object at index
     *
     * @param index the index
     * @return object
     */
    public T get(int index) {
        return this.objects[index];
    }

    public String toStringWithChars() {
        int iMax = this.size() - 1;
        if (iMax == -1) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; ; i++) {
            stringBuilder.append("'").append(get(i)).append("'");
            if (i == iMax) {
                return stringBuilder.toString();
            }
            stringBuilder.append(", ");
        }
    }
    /**
     * Prettier toString method
     *
     * @return contents as string
     */
    public String toString() {
        return Arrays.toString(this.objects).replace("[", "").replace("]", "");
    }
}
