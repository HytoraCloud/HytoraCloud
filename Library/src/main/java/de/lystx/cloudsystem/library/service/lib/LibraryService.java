package de.lystx.cloudsystem.library.service.lib;

import lombok.Getter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;

@Getter
public class LibraryService {

    private final File directory;
    private final ClassLoader classLoader;
    private final List<MavenLibrary> libraries;

    public LibraryService(String direrctory, ClassLoader classLoader) {
        this.directory = new File(direrctory);
        this.directory.mkdirs();
        this.classLoader = classLoader;
        this.libraries = new LinkedList<>();
    }

    public void addURL(Path path) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(this.classLoader, path.toUri().toURL());
        } catch (IllegalAccessException | InvocationTargetException | MalformedURLException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void install(String groupId, String artifactId, String version, Repository repo) {
        MavenLibrary mavenLibrary = new MavenLibrary(this, groupId, artifactId, version, repo.getUrl());
        libraries.add(mavenLibrary);
        mavenLibrary.install();
    }

    public void install(String groupId, String artifactId, String version, String url) {
        MavenLibrary mavenLibrary = new MavenLibrary(this, groupId, artifactId, version, url);
        libraries.add(mavenLibrary);
        mavenLibrary.install(url);
    }


}
