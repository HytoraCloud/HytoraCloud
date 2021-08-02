package de.lystx.hytoracloud.driver.cloudservices.cloud.lib;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum Repository {

    CENTRAL("https://repo.maven.apache.org/maven2/"),
    MVN("https://mvnrepository.com/artifact/"),
    SONATYPE("https://oss.sonatype.org/content/repositories/releases/"),
    JCENTER("https://jcenter.bintray.com"),
    MINECRAFT_SPIGOT("https://your-repo.net/"),
    MINECRAFT_BUNGEECORD("https://oss.sonatype.org/content/repositories/snapshots");
    
    
    private final String url;
    

}
