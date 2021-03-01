package de.lystx.cloudsystem.library.service.lib;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;

@Getter
public class MavenLibrary {

    private String groupId, artifactId, version, repo;
    private final LibraryService libraryService;

    public MavenLibrary(LibraryService libraryService, String groupId, String artifactId, String version, String repo) {
        this.groupId = groupId;
        this.libraryService = libraryService;
        this.artifactId = artifactId;
        this.version = version;
        this.repo = repo.endsWith("/") ? repo : repo + "/";
    }


    public void install(String url) {
        if (!new File(this.libraryService.getDirectory(), path()).exists()) {
            System.out.println("[Libraries] Downloading dependency for " + groupId + ":" + artifactId + " - " + version + " [" + url + "]");
            try {
                if (!Files.exists(this.getPath())) {
                    Path parent = this.getPath().getParent();
                    if (parent != null && !Files.exists(parent)) {
                        try {
                            Files.createDirectories(parent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                URLConnection connection = new URL(url).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                connection.setConnectTimeout(2000);
                connection.connect();

                try (InputStream inputStream = connection.getInputStream()) {
                    Files.copy(inputStream, this.getPath());
                }

                System.out.println("[Libraries] Succesfully downloaded " + artifactId + ":" + groupId);
            } catch (IOException e) {
                System.out.println("[Libraries] Couldn't download " + artifactId + ":" + groupId);
            }
        }
        this.libraryService.addURL(this.getPath());
    }



    public void install() {
        this.install(this.repo + path());
    }

    public Path getPath() {
        return Paths.get(this.libraryService.getDirectory() + "/" + path());
    }

    private String path() {
        return this.getGroupId().replace('.', '/') + "/" + this.getArtifactId() + "/" + this.getVersion() + "/" + this.getArtifactId() + "-" + this.getVersion() + ".jar";
    }

}
