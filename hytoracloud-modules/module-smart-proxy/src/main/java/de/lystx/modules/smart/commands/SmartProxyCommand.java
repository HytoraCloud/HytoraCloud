package de.lystx.modules.smart.commands;

import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandListener;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.modules.smart.SmartProxy;

@CommandInfo(
        name = "smartproxy",
        description = "Manages this module",
        aliases = {"proxy", "sp"}
)
public class SmartProxyCommand implements CommandListener {

    @Override
    public void execute(CommandExecutor executor, String[] args) {
        JsonDocument config = SmartProxy.getInstance().getConfig();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("toggle")) {
                boolean toggle = !config.getBoolean("enabled");
                config.append("enabled", toggle);
                config.save();
                SmartProxy.getInstance().setEnabled(toggle);
                executor.sendMessage("SmartProxy", "§7System is now " + (toggle ? "§aenabled" : "§cdisabled") + "§h!");
                return;
            }
            if (args[0].equalsIgnoreCase("switchMode")) {
                String mode = config.getString("proxySearchMode");

                if (mode.equalsIgnoreCase("RANDOM")) {
                    mode = "FILL";
                } else if (mode.equalsIgnoreCase("FILL")) {
                    mode = "BALANCED";
                } else {
                    mode = "RANDOM";
                }

                config.append("proxySearchMode", mode);
                config.save();
                SmartProxy.getInstance().setProxySearchMode(mode);
                executor.sendMessage("SmartProxy", "§7System now searches for free proxies with §e" + mode.toUpperCase() + "-Mode§h!");
                return;
            }
        }
        executor.sendMessage("INFO", "§9Help for §bSmartProxy§7:");
        executor.sendMessage("INFO", "§9smartproxy <toggle> §7| " + (SmartProxy.getInstance().isEnabled() ? "Disables this System" : "Enables this System"));
        executor.sendMessage("INFO", "§9smartproxy <switchMode> §7| Switches the proxySearchMode");
    }
}
