package de.lystx.cloudsystem.library.elements.other;


import java.lang.reflect.Array;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public final class CollectionWrapper {

    public static <E> java.util.List<E> newCopyOnWriteArrayList() {
        return new CopyOnWriteArrayList<>();
    }

    public static <E> CopyOnWriteArrayList<E> transform(Collection<E> defaults) {
        return new CopyOnWriteArrayList<>(defaults);
    }

    public static Collection<String> toCollection(String input, String splitter) {
        return new CopyOnWriteArrayList<>(input.split(splitter));
    }

    public static <E> boolean equals(E[] array, E value) {
        for (E a : array) {
            if (a.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static <E> int filled(E[] array) {
        int i = 0;
        for (E element : array) {
            if (element != null) {
                i++;
            }
        }
        return i;
    }

    public static <E> boolean isEmpty(E[] array) {
        for (E element : array) {
            if (element != null) {
                return false;
            }
        }
        return true;
    }

    public static <E> void remove(E[] array, E element) {
        int i = index(array, element);
        array[i] = null;
    }

    public static <E> int index(E[] array, E element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)) {
                return i;
            }
        }
        return 0;
    }

    public static <E> E[] dynamicArray(Class<E> clazz) {
        return (E[]) Array.newInstance(clazz, Integer.MAX_VALUE);
    }
}
