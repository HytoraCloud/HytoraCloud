package de.lystx.hytoracloud.driver.service.util.minecraft.ping;

import com.google.common.base.Charsets;
import lombok.Builder;
import lombok.Getter;

@Builder
public class ServerInfoOptions {

    /**
     * The hostname of the serverInfo
     */
    @Getter
    private final String hostname;

    /**
     * The charset (default UTF-8)
     */
    @Getter
    @Builder.Default
    private final String charset = Charsets.UTF_8.displayName();

    /**
     * The port of the serve
     */
    @Getter
    @Builder.Default
    private final int port = 25565;

    /**
     * The timeout
     */
    @Getter
    @Builder.Default
    private final int timeout = 5000;

}
