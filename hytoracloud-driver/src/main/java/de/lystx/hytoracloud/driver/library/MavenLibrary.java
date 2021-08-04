package de.lystx.hytoracloud.driver.library;

import de.lystx.hytoracloud.driver.utils.enums.other.Color;
import lombok.Getter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;

@Getter
public class MavenLibrary {

    private final String groupId, artifactId, version, repo;
    private final LibraryService libraryService;

    public MavenLibrary(LibraryService libraryService, String groupId, String artifactId, String version, String repo) {
        this.groupId = groupId;
        this.libraryService = libraryService;
        this.artifactId = artifactId;
        this.version = version;
        this.repo = repo.endsWith("/") ? repo : repo + "/";
    }

    /**
     * Downloads this Library from a given URL
     * @param url
     */
    public void install(String url) {
        if (!new File(this.libraryService.getDirectory(), path()).exists()) {

            String b = Color.BLACK_BRIGHT.toString();
            String c = Color.CYAN_BRIGHT.toString();
            String w = Color.RESET.toString();

            System.out.println(b + "[" + c + "Libraries" + b + "] " + w + "Downloading " + groupId + b + ":" + w + artifactId + " " + b + "(" + c + "Version: " + w + this.version + b + ")" + w + " Dependency" + b + "..." + w);
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

                this.fileDownload(this.repo + rawPath() + ".pom.asc", new File(this.libraryService.getDirectory(), this.groupId + ".pom.asc"));
                this.fileDownload(this.repo + rawPath() + ".pom", new File(this.libraryService.getDirectory(), this.groupId + ".pom.xml"));
            } catch (IOException e) {
                System.out.println("[Libraries] Couldn't download " + artifactId + ":" + groupId);
            }
        }
        this.libraryService.addURL(this.getPath());
    }

    /**
     * Downloads a file from website
     * @param url
     * @param location
     */
    private void fileDownload(String url, File location) {
        try {
            if (location.exists()) {
                location.createNewFile();
            }
            URL website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(location);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            //Ignoring
        }
    }

    /**
     * Installs this library
     */
    public void install() {
        this.install(this.repo + path());
    }

    /**
     * Returns path
     * @return
     */
    public Path getPath() {
        return Paths.get(this.libraryService.getDirectory() + "/" + path());
    }

    /**
     * Returns string path
     * @return
     */
    private String path() {
        return this.rawPath() + ".jar";
    }

    /**
     * Returns raw path
     * @return
     */
    private String rawPath() {
        return this.getGroupId().replace('.', '/') + "/" + this.getArtifactId() + "/" + this.getVersion() + "/" + this.getArtifactId() + "-" + this.getVersion();
    }

}
