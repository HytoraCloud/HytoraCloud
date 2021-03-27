package de.lystx.cloudsystem.library.service.util;

import lombok.SneakyThrows;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Class for utiliities
 * made static because it's
 * easier to call the methods
 */
public class Utils {

    /**
     * Adds all objects to a string
     * @param input
     * @return List with given objects
     */
    public static List<String> toStringList(List<?> input) {
        List<String> list = new LinkedList<>();

        for (Object o : input) {
            list.add(o.toString());
        }

        return list;
    }

    public static <T> void doUntilEmpty(List<T> list, Consumer<T> listConsumer, Consumer<List<T>> emptyConsumer) {
        int i = list.size();
        for (T t : list) {
            listConsumer.accept(t);
            i--;
            if (i <= 0) {
                emptyConsumer.accept(list);
            }
        }
    }

    /**
     * Checks if a class exists
     * @param name
     * @return
     */
    public static boolean existsClass(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @SneakyThrows
    public static void createFile(File file) {
        file.createNewFile();
    }
}
