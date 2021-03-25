package de.lystx.cloudsystem.library.service.util;

import java.util.LinkedList;
import java.util.List;

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
}
