package de.lystx.cloudsystem.library.service.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.lystx.cloudsystem.library.CloudLibrary;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Getter @AllArgsConstructor
public class HytoraClassLoader {

    private final File file;

    /**
     * Used for {loadJson}
     * @param filename
     * @return content of provided file
     */
    public String loadFile(String filename) {
        try {
            JarFile jf = new JarFile(this.file);
            JarEntry je = jf.getJarEntry(filename);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(jf.getInputStream(je)))) {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                jf.close();
                br.close();
                return builder.toString();
            } catch (Exception e) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     *
     * @param filename
     * @return JsonObject from File content
     */
    public JsonObject loadJson(String filename) {
        return new JsonParser().parse(this.loadFile(filename)).getAsJsonObject();
    }

    /**
     * Finds a class
     * @param name
     * @return class by name
     */
    public Class<?> findClass(String name) {
        try {
            URLClassLoader child  = new URLClassLoader(new URL[] {new URL("file:" + this.file.toString())}, CloudLibrary.class.getClassLoader());
            return Class.forName(name, true, child);
        } catch (MalformedURLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Finds a class
     * @param name
     * @return class by name but uses JarEntry
     */
    public Class<?> findClassWithJarEntry(String name) {
        try {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> e = jarFile.entries();

            URL[] urls = {new URL("jar:file:" + file.getAbsolutePath())};
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if(je.isDirectory() || !je.getName().endsWith(".class")){
                    continue;
                }
                if (je.getName().contains(name)){
                    String className = je.getName().substring(0,je.getName().length()-6);
                    className = className.replace('/', '.');
                    Class<?> c = cl.loadClass(className);
                    return c;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
        return null;
    }
}
