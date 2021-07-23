package de.lystx.hytoracloud.driver.utils;

import de.lystx.hytoracloud.driver.cloudservices.cloud.console.progressbar.ProgressBar;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.progressbar.ProgressBarStyle;
import de.lystx.hytoracloud.driver.commons.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.commons.enums.versions.SpigotVersion;
import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.commons.interfaces.RunTaskSynchronous;
import de.lystx.hytoracloud.driver.cloudservices.global.scheduler.Scheduler;
import lombok.SneakyThrows;

import java.io.*;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;


public class Utils {

    public static final String INTERNAL_RECEIVER = "InternalReceiver";
    public static final String PASTE_SERVER_URL_DOCUMENTS = "https://paste.labymod.net/documents";
    public static final String PASTE_SERVER_URL = "https://paste.labymod.net/";
    public static final String PASTE_SERVER_URL_RAW = "https://paste.labymod.net/raw/";


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

    /**
     * Clears the console screen
     */
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

    /**
     * Gets the dateformat for console
     *
     * @return format
     */
    public static SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat("hh:mm:ss");
    }


    /**
     * Downloads a file from a website
     *
     * @param search > URL
     * @param location > File to download to
     */
    public static void download(String search, File location, String task)  {
        InputStream inputStream;
        OutputStream outputStream;

        try {
            ProgressBar pb = new ProgressBar(task, 100, 1000, System.err, ProgressBarStyle.ASCII, "", 1, false, null, ChronoUnit.SECONDS, 0L, Duration.ZERO);
            URL url = new URL(search);
            String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", USER_AGENT);

            int contentLength = con.getContentLength();
            inputStream = con.getInputStream();

            outputStream = new FileOutputStream(location);
            byte[] buffer = new byte[2048];
            int length;
            int downloaded = 0;

            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
                downloaded+=length;
                pb.stepTo((long) ((downloaded * 100L) / (contentLength * 1.0)));
            }
            pb.setExtraMessage("Cleaning up...");
            outputStream.close();
            inputStream.close();
            pb.close();
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Deletes a folder with content
     *
     * @param folder the folder
     */
    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    /**
     * Does an operation until the list is empty and
     * then does another operation with the emptyConsumer
     *
     * @param list the list
     * @param listConsumer the consumer for every list item
     * @param emptyConsumer the consumer when list is empty
     * @param <T> the generic
     */
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
     * Copies a resource from the resource folder to a location
     *
     * @param res the resource name
     * @param dest the location name
     * @param c the class
     * @throws IOException if something goes wrong
     */
    public static void copyResource(String res, String dest, Class<?> c) throws IOException {
        InputStream src = c.getResourceAsStream(res);
        Files.copy(src, Paths.get(dest), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Sets a field inside a class
     *
     * @param _class the class
     * @param instance the object
     * @param name the field name
     * @param value the value
     */
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

    /**
     * Checks if a class exists
     *
     * @param name the name
     * @return boolean
     */
    public static boolean existsClass(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
