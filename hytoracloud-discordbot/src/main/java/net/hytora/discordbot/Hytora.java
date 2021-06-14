package net.hytora.discordbot;


import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.service.scheduler.Scheduler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.internal.interactions.ButtonImpl;
import net.hytora.discordbot.commands.HelpCommand;
import net.hytora.discordbot.listener.*;
import net.hytora.discordbot.manager.command.CommandCategory;
import net.hytora.discordbot.manager.command.CommandManager;
import net.hytora.discordbot.manager.logger.LogManager;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.*;
import java.util.Objects;

public class Hytora {

    /**
     * The instance
     */
    private static Hytora hytora;

    /**
     * The manager to log everything
     */
    private final LogManager logManager;

    /**
     * The manager for commands
     */
    private CommandManager commandManager;

    /**
     * The config where all values are stored
     */
    private JsonBuilder jsonConfig;

    /**
     * The JDA to manage all discord stuff
     */
    private JDA discord;

    /**
     * The cloud guild
     */
    private Guild guild;

    /**
     * The botManagingChannel
     */
    private TextChannel botManaging;

    public Hytora() {
        long start = System.currentTimeMillis();

        hytora = this;
        this.logManager = new LogManager("logs/");

        this.logManager.log("\n  _    _       _                  \n" +
                " | |  | |     | |                 \n" +
                " | |__| |_   _| |_ ___  _ __ __ _ \n" +
                " |  __  | | | | __/ _ \\| '__/ _` |\n" +
                " | |  | | |_| | || (_) | | | (_| |\n" +
                " |_|  |_|\\__, |\\__\\___/|_|  \\__,_|\n" +
                "          __/ |                   \n" +
                "         |___/                    \n");
        this.logManager.log("§8");
        this.logManager.log("INFO", "§7Loading §3HytoraBot §7by §bLystx§8...");

        if (this.loadConfig() && this.loadJDA(new BotStopListener(), new CommandListener(), new JoinListener())) {
            Scheduler.getInstance().scheduleDelayedTask(() -> {
                if (this.loadGuild()) {

                    this.commandManager = new CommandManager(this.jsonConfig.getString("command"));
                    this.registerCommands();

                    this.logManager.log("WELCOME", "§7Bot logged in as §3" + this.discord.getSelfUser().getAsTag() + " §7in §b" + (System.currentTimeMillis() - start) + "ms");
                    this.logManager.log("WELCOME", "§7Logged in on Guild §3" + this.guild.getName() + " §7@ §b" + this.guild.getId());
                    this.logManager.log("WELCOME", "§7On the guild are §b" + this.guild.getMembers().size() + "§8/§b" + this.guild.getMaxMembers() + " Members!");
                    this.logManager.log("§8");

                    if (this.checkOtherValues()) {
                        this.manageDefaultRoles();

                        this.logManager.preset(this.botManaging, "Welcome", this.discord.getSelfUser(), message -> {

                        }, new Button[]{
                                new ButtonImpl("stop", "Stop HytoraCloud", ButtonStyle.DANGER, false, null)
                        }, "HytoraCloud DiscordBot", "Is now active and may be used!", "----------", "Click the stop-button", "to stop the bot at any time!");

                    } else {
                        this.logManager.log("ERROR", "§cCouldn't load the §econfig.json properly!");
                        this.shutdown();
                    }

                } else {
                    this.logManager.log("ERROR", "§cCouldn't get §eGuild §cwith ID §e" + this.jsonConfig.getString("guildID"));
                    this.logManager.log("INFO", "§7Available §3Guilds§8: " + (this.discord.getGuilds().size() == 0 ? " §cNone" : ""));
                    for (Guild discordGuild : this.discord.getGuilds()) {
                        this.logManager.log("INFO", " §8> §3" + discordGuild.getName() + " §8| §b" + discordGuild.getId());
                    }
                    this.shutdown();
                }
            }, 20L);
            return;
        }
        this.logManager.log("ERROR", "§cCouldn't load the §econfig.json §cor the §ebot §ccouldn't log in properly!");
        this.shutdown();
    }

    /**
     * Shuts down the bot
     */
    public void shutdown() {
        this.logManager.log("ERROR", "§cShutting down....");
        this.logManager.save();
        this.discord.shutdown();
        System.exit(1);
    }

    /**
     * Manages the default roles
     * and gives everyone the default role
     * if they don't have any
     */
    public void manageDefaultRoles() {

    }

    /**
     * Registers all {@link net.hytora.discordbot.manager.command.CommandHandler}s
     */
    public void registerCommands() {
        this.commandManager.registerCommand(new HelpCommand("help", "Shows this message", CommandCategory.GENERAL, "?", "hilfe"));
    }

    /**
     * Checks for all other config values
     * if they're set and exist
     *
     * @return boolean
     */
    public boolean checkOtherValues() {
        try {

            //Bot Managing channel
            String botManagingId = this.jsonConfig.getString("botManagingId");
            this.botManaging = this.guild.getTextChannelById(botManagingId);

            //Roles
            JsonBuilder roles = this.jsonConfig.getJson("roles");
            JsonBuilder defaultRole = roles.getJson("default");
            String name = defaultRole.getString("name");
            String color = defaultRole.getString("color");
            boolean showOrder = defaultRole.getBoolean("showOrder");
            boolean mentionable = defaultRole.getBoolean("mentionable");

            Role role = guild.getRoles().stream().filter(role1 -> role1.getName().equalsIgnoreCase(name)).findFirst().orElse(null);

            if (role == null) {
                guild.createRole()
                        .setName(name)
                        .setColor((Color) Color.class.getDeclaredField(color).get(Color.WHITE))
                        .setHoisted(showOrder)
                        .setMentionable(mentionable)
                        .queue(df -> {
                            logManager.log("INFO", "§7Had to create §b" + df.getName() + "§7-Role§8!");

                            for (Member member : guild.getMembers()) {
                                Role memberRole = member.getRoles().stream().filter(role1 -> role1.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
                                if (memberRole == null) {
                                    guild.addRoleToMember(member, df).queue();
                                }
                            }

                        });
            }

            //

            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * Loads the config.json
     */
    public boolean loadConfig() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Hytora.class.getResourceAsStream("/config.json"))));
            this.jsonConfig = new JsonBuilder(reader);
            return !this.jsonConfig.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Loads and builds the jda
     */
    public boolean loadJDA(ListenerAdapter... listenerAdapters) {
        String token = this.jsonConfig.getString("token");

        if (token.trim().isEmpty()) {
            this.logManager.log("INFO", "§cCan't connect with §eempty token§c!");
            return false;
        }

        this.logManager.log("INFO", "§7Trying to connect to §3Hytora-Bot with token §b" + token + "§8...");

        JDABuilder api = JDABuilder.createDefault(this.jsonConfig.getString("token"))
                .setMemberCachePolicy(MemberCachePolicy.ALL) //Member caching
                .setStatus(OnlineStatus.ONLINE) //Status
                .enableIntents(
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_BANS,
                        GatewayIntent.GUILD_MESSAGES
                )
                .setActivity(Activity.playing(" HytoraCloud")); //activity
        try {
            //Registering the listeners
            for (ListenerAdapter listenerAdapter : listenerAdapters) {
                api.addEventListeners(listenerAdapter);
            }

            this.discord = api.build(); //Building

            return true; //everything went well
        } catch (LoginException e) {
            return false;
        }
    }

    /**
     * Loads the current guild
     *
     * @return if success
     */
    public boolean loadGuild() {

        String guildID = this.jsonConfig.getString("guildID");

        if (guildID.trim().isEmpty()) {
            this.logManager.log("ERROR", "§cCan't search for a guild with an §empty id§c!");
            return false;
        }

        this.guild = this.discord.getGuildById(guildID);

        return this.guild != null;
    }

    public static void main(String[] args) {
        new Hytora();
    }


    public static Hytora getHytora() {
        return hytora;
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public JsonBuilder getJsonConfig() {
        return jsonConfig;
    }

    public JDA getDiscord() {
        return discord;
    }

    public Guild getGuild() {
        return guild;
    }

    public TextChannel getBotManaging() {
        return botManaging;
    }
}
