package de.lystx.hytoracloud.bootstrap;

import de.lystx.hytoracloud.driver.DriverInfo;
import de.lystx.hytoracloud.driver.CloudDriver;

import java.util.Arrays;

public class Bootstrap {

    public static void main(String[] args)  {
        if (!Arrays.asList(args).contains("--ignoreCheck")) {
            String javaVersion = System.getProperty("java.version");

            boolean b = false;
            for (String s : CloudDriver.class.getAnnotation(DriverInfo.class).allowedJavaVersions()) {
                if (javaVersion.startsWith(s)) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                System.out.println("\n" +
                        "  _____                                       _   _ _     _      \n" +
                        " |_   _|                                     | | (_) |   | |     \n" +
                        "   | |  _ __   ___ ___  _ __ ___  _ __   __ _| |_ _| |__ | | ___ \n" +
                        "   | | | '_ \\ / __/ _ \\| '_ ` _ \\| '_ \\ / _` | __| | '_ \\| |/ _ \\\n" +
                        "  _| |_| | | | (_| (_) | | | | | | |_) | (_| | |_| | |_) | |  __/\n" +
                        " |_____|_| |_|\\___\\___/|_| |_| |_| .__/ \\__,_|\\__|_|_.__/|_|\\___|\n" +
                        "                                 | |                             \n" +
                        "                                 |_|                             ");
                System.out.println("---------------------------------------------------");
                System.out.println("[ERROR] HytoraCloud does not work with your Java version!");
                System.out.println("[ERROR] Your Version : " + javaVersion);
                System.out.println("[ERROR] Recommended Version : " + "1.8.x");
                System.exit(1);
                return;
            }
        }
        InternalBootstrap.main(args);

        /*
        LibraryService libraryService = new LibraryService(new File("local/global/libs/"), null);
        libraryService.installDefaultLibraries();

        List<URL> dependencies = libraryService.loadDependencies();
        dependencies.add(Utils.getCurrentURL());

        ClassLoader classLoader = new URLClassLoader(dependencies.toArray(new URL[0]));;

        Thread thread = new Thread(() -> {
            try {
                Method method = classLoader.loadClass(InternalBootstrap.class.getName()).getMethod("main", String[].class);
                method.invoke(null, (Object) args);
            } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException exception) {
                exception.printStackTrace();
            }
        });

        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            Field scl = ClassLoader.class.getDeclaredField("scl"); // Get system class loader
            scl.setAccessible(true); // Set accessible
            scl.set(null, classLoader); // Update it to your class loader
        } catch (Exception e) {
            e.printStackTrace();
        }
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setContextClassLoader(classLoader);
        thread.start();
*/
    }
}
