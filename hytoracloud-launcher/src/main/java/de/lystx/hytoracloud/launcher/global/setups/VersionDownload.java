package de.lystx.hytoracloud.launcher.global.setups;

import de.lystx.hytoracloud.driver.cloudservices.global.setup.SetupExecutor;
import de.lystx.hytoracloud.driver.cloudservices.global.setup.QuestionSkip;
import de.lystx.hytoracloud.driver.cloudservices.global.setup.Setup;
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
            message = {
                    "INFO%%§7There are following §bextra versions§f:",
                    "INFO%%§7If you §cdon't §fuse any of the above extra versions just type the version you want §h(§b1.8.8§h, §b1.9§h, §b1.12 etc.§h)"
            },
            exitAfterAnswer = ""
    )
    private String spigotVersion;

    @Setup(
            id = 3,
            question = "What Proxy-Version do you want to download? §h(§bBUNGEECORD§h, §bWATERFALL§h, §bVELOCITY§h)"
    )
    private String proxyVersion;
}
