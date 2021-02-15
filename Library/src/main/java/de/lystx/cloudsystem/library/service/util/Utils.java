package de.lystx.cloudsystem.library.service.util;

import java.util.LinkedList;
import java.util.List;

public class Utils {

    public static List<String> toStringList(List<?> input) {
        List<String> list = new LinkedList<>();

        for (Object o : input) {
            list.add(o.toString());
        }

        return list;
    }
}
