package de.lystx.hytoracloud.driver.cloudservices.cloud.lib;

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

    public LibraryService(File direrctory, ClassLoader classLoader) {
        this.directory = direrctory;
        this.directory.mkdirs();
        this.classLoader = classLoader;
        this.libraries = new LinkedList<>();
    }

    /**
     * Injects a dependency into runtime
     * @param path
     */
    public void addURL(Path path) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(this.classLoader, path.toUri().toURL());
        } catch (IllegalAccessException | InvocationTargetException | MalformedURLException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Injects a dependency into runtime
     * @param path
     */
    public void addURL(Path path, URLClassLoader classLoader) {
        try {
            Method method = classLoader.getClass().getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, path.toUri().toURL());
        } catch (IllegalAccessException | InvocationTargetException | MalformedURLException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Installs a library from a repo
     * @param groupId
     * @param artifactId
     * @param version
     * @param repo
     */
    public void install(String groupId, String artifactId, String version, Repository repo) {
        MavenLibrary mavenLibrary = new MavenLibrary(this, groupId, artifactId, version, repo.getUrl());
        libraries.add(mavenLibrary);
        mavenLibrary.install();
    }

    /**
     * Installs a library from a custom URL
     * @param groupId
     * @param artifactId
     * @param version
     * @param url
     */
    public void install(String groupId, String artifactId, String version, String url) {
        MavenLibrary mavenLibrary = new MavenLibrary(this, groupId, artifactId, version, url);
        libraries.add(mavenLibrary);
        mavenLibrary.install(url);
    }

    /**
     * Installs default maven libraries
     */
    public void installDefaultLibraries() {
        //APACHE
        this.install("org.apache.httpcomponents", "httpcore-nio", "4.4.6", Repository.CENTRAL);
        this.install("org.apache.httpcomponents", "httpcore", "4.3.2", Repository.CENTRAL);
        this.install("org.apache.httpcomponents", "httpmime", "4.5.3", Repository.CENTRAL);
        this.install("org.apache.httpcomponents", "httpclient", "4.5.3", Repository.CENTRAL);
        this.install("org.apache.httpcomponents", "httpclient", "4.5.2", Repository.CENTRAL);
        this.install("org.apache.commons", "commons-lang3", "3.5", Repository.CENTRAL);

        this.install("commons-io", "commons-io", "2.6", Repository.CENTRAL);
        this.install("commons-logging", "commons-logging", "1.2", Repository.CENTRAL);
        this.install("commons-lang", "commons-lang", "2.5", Repository.CENTRAL);
        this.install("org.slf4j", "slf4j-api", "1.7.25", Repository.CENTRAL);
        this.install("org.apache.logging.log4j", "log4j-api", "2.5", Repository.CENTRAL);
        this.install("log4j", "log4j", "1.2.17", Repository.CENTRAL);

        //NETWORK
        this.install("io.netty", "netty-all", "4.1.44.Final", Repository.CENTRAL);
        this.install("javax.servlet", "servlet-api", "2.5", Repository.CENTRAL);

        //Logging and Console
        this.install("jline", "jline", "2.14.6", Repository.CENTRAL);
        this.install("org.jline", "jline-terminal-jna", "3.18.0", Repository.CENTRAL);
        this.install("org.jline", "jline-terminal", "3.19.0", Repository.CENTRAL);
        this.install("ch.qos.logback", "logback-classic", "1.2.3", Repository.CENTRAL);
        this.install("ch.qos.logback", "logback-core", "1.2.3", Repository.CENTRAL);

        //Database
        this.install("mysql", "mysql-connector-java", "8.0.11", Repository.CENTRAL);
        this.install("org.mongodb", "bson", "4.2.0", Repository.CENTRAL);
        this.install("org.mongodb", "mongodb-driver", "3.12.7", Repository.CENTRAL);
        this.install("org.mongodb", "mongodb-driver-core", "3.12.7", Repository.CENTRAL);
        this.install("org.mongodb", "mongo-java-driver", "3.12.7", Repository.CENTRAL);
        //this.install("com.arangodb", "arangodb-java-driver", "6.9.1", Repository.CENTRAL);

        //OTHER
        this.install("org.openjfx", "javafx-base", "11", Repository.CENTRAL);
        this.install("org.projectlombok", "lombok", "1.18.16", Repository.CENTRAL);
        this.install("com.google.code.gson", "gson", "2.8.5", Repository.CENTRAL);
        this.install("com.google.guava", "guava", "25.1-jre", Repository.CENTRAL);
        this.install("com.google.j2objc", "j2objc-annotations", "1.1", Repository.CENTRAL);
        this.install("com.google.protobuf", "protobuf-java", "3.14.0", Repository.CENTRAL);
        this.install("dnsjava", "dnsjava", "3.3.1", Repository.CENTRAL);
    }


}
