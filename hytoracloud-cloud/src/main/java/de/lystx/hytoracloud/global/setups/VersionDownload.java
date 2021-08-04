package de.lystx.hytoracloud.global.setups;

import de.lystx.hytoracloud.driver.setup.SetupExecutor;
import de.lystx.hytoracloud.driver.setup.QuestionSkip;
import de.lystx.hytoracloud.driver.setup.Setup;
import de.lystx.hytoracloud.driver.utils.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.utils.enums.versions.SpigotVersion;
import lombok.Getter;

@Getter
public class VersionDownload extends SetupExecutor<VersionDownload> {


    public VersionDownload() {
        this.printHeader = false;
    }

    @Setup(
            id = 1,
            onlyAnswers = {
                    "SPIGOT",
                    "PROXY"
            },
            question = "What type are you trying to download? §h(§eSPIGOT §7or §bPROXY§h)",
            skip = @QuestionSkip(
                    id = 2,
                    elseID = 3,
                    value = "SPIGOT"
            )
    )
    private String type;

    @Setup(
            id = 2,
            question = "What Spigot-Version do you want to download?",
            enumOnly = SpigotVersion.class,
            exitAfterAnswer = ""
    )
    private String spigotVersion;

    @Setup(
            id = 3,
            question = "What Proxy-Version do you want to download?",
            enumOnly = ProxyVersion.class

    )
    private String proxyVersion;
}
