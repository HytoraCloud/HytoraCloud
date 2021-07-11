package de.lystx.hytoracloud.driver.utils;

import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.commons.interfaces.RunTaskSynchronous;
import de.lystx.hytoracloud.driver.utils.scheduler.Scheduler;
import lombok.SneakyThrows;

import java.io.*;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Class for utiliities
 * made static because it's
 * easier to call the methods
 */
public class Utils {


    public static final String INTERNAL_RECEIVER = "InternalReceiver";
    public static final String PASTE_SERVER_URL_DOCUMENTS = "https://paste.labymod.net/documents";
    public static final String PASTE_SERVER_URL = "https://paste.labymod.net/";
    public static final String PASTE_SERVER_URL_RAW = "https://paste.labymod.net/raw/";

    public static final byte PACKET_HANDSHAKE = 0x00, PACKET_STATUSREQUEST = 0x00, PACKET_PING = 0x01;
    public static final int PROTOCOL_VERSION = 4;
    public static final int STATUS_HANDSHAKE = 1;
    public static final char COLOR_CHAR = '\u00A7';
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-FK-OR]");


    /**
     * Strips the given message of all color codes
     *
     * @param input String to strip of color
     * @return A copy of the input string, without any coloring
     */
    public static String stripColors(String input) {
        return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static void io(boolean b, final String m) throws IOException {
        if (b) {
            throw new IOException(m);
        }
    }

    /**
     */
    public static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();

            i |= (k & 0x7F) << j++ * 7;

            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }

            if ((k & 0x80) != 128) {
                break;
            }
        }

        return i;
    }

    /**
     * @throws IOException
     */
    public static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }


    /**
     * Gets the percent of match of two strings
     *
     * @param s1 the string to compare
     * @param s2 the string to get compared
     * @param ignoreCase if strings should be lowercased
     * @return percent as double (1.0 = 100%, 0.94 = 94%)
     */
    public static double getPercentMatch(String s1, String s2, boolean ignoreCase) {

        if (ignoreCase) {
            s1 = s1.toLowerCase();
            s2 = s2.toLowerCase();
        }

        Set<String> nx = new HashSet<>(); //Set 1
        Set<String> ny = new HashSet<>(); //Set 2

        //String 1 match
        for (int i = 0; i < s1.length() - 1; i++) {
            char x1 = s1.charAt(i);
            char x2 = s1.charAt(i + 1);
            nx.add("" + x1 + x2);
        }

        //String 2 match
        for (int j = 0; j < s2.length() - 1; j++) {
            char y1 = s2.charAt(j);
            char y2 = s2.charAt(j+1);
            ny.add("" + y1 + y2);
        }

        //New set for the match
        Set<String> intersection = new HashSet<>(nx);
        intersection.retainAll(ny); //Removes all not containing elements

        return (2 * intersection.size()) / (nx.size() + ny.size());
    }

    public static void clearConsole() {
        try {
            String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat("hh:mm:ss");
    }

    /**
     * Checks if a {@link Method} should be executed async or sync
     * and if its delayed or not and which unit to use
     *
     * @param object the object where the annotation might be placed
     * @param runnable the runnable to execute
     */
    public static void runTaskMethod(AccessibleObject object, Runnable runnable) {
        if (object.isAnnotationPresent(RunTaskSynchronous.class) || object.getClass().isAnnotationPresent(RunTaskSynchronous.class)) {
            RunTaskSynchronous runTaskSynchronous = object.getAnnotation(RunTaskSynchronous.class);
            if (runTaskSynchronous.delay() != -1 || runTaskSynchronous.unit() != TimeUnit.NANOSECONDS) {
                if (runTaskSynchronous.value()) {
                    Scheduler.getInstance().scheduleDelayedTask(runnable, runTaskSynchronous.delay());
                } else {
                    Scheduler.getInstance().scheduleDelayedTaskAsync(runnable, runTaskSynchronous.delay());
                }
            }
        } else {
            runnable.run();
        }
    }

    /**
     * Adds all objects to a string
     *
     * @param input the input to add as String
     * @return List with given objects
     */
    public static List<String> toStringList(List<?> input) {
        List<String> list = new LinkedList<>();

        for (Object o : input) {
            if (o instanceof Identifiable) {
                list.add(((Identifiable) o).getName());
                continue;
            }
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

    public static void copyResource(String res, String dest, Class<?> c) throws IOException {
        InputStream src = c.getResourceAsStream(res);
        Files.copy(src, Paths.get(dest), StandardCopyOption.REPLACE_EXISTING);
    }

    @SneakyThrows
    public static void setField(Class<?> _class, Object instance, String name, Object value) {
        try {
            Field field = _class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> exceptionToList(Exception e) {
        List<String> list = new LinkedList<>();

        list.add(e.getMessage() == null ? "No message" : e.getMessage());

        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            list.add(stackTraceElement.toString());
        }

        return list;
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
