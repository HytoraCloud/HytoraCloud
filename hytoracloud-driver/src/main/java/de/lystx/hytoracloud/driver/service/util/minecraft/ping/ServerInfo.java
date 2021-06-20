
package de.lystx.hytoracloud.driver.service.util.minecraft.ping;

import java.util.List;

import de.lystx.hytoracloud.driver.service.util.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class ServerInfo {


    private Description description;

    private Players players;

    private Version version;

    private String favicon;

    @Setter
    private long ping;

    @Getter
    @ToString
    public static class Description {

        private String text;

        public String getStrippedText() {
            return Utils.stripColors(this.text);
        }

    }

    @Getter
    @ToString
    public static class Players {

        private int max;

        private int online;

        private List<Player> sample;

    }

    @Getter
    @ToString
    public static class Player {

        private String name;

        private String id;

    }

    @Getter
    @ToString
    public static class Version {

        private String name;

        private int protocol;

    }

}
