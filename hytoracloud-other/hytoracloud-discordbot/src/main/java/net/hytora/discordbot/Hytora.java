package net.hytora.discordbot;


import de.lystx.hytoracloud.driver.cloudservices.global.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.utils.Utils;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.hytora.discordbot.commands.*;
import net.hytora.discordbot.listener.*;
import net.hytora.discordbot.manager.command.CommandCategory;
import net.hytora.discordbot.manager.command.CommandManager;
import net.hytora.discordbot.manager.conversation.ConversationManager;
import net.hytora.discordbot.manager.logger.LogManager;
import net.hytora.discordbot.manager.other.ReactionRolesManager;
import net.hytora.discordbot.manager.suggestion.SuggestionManager;
import net.hytora.discordbot.manager.ticket.TicketManager;
import net.hytora.discordbot.util.button.DiscordButton;
import net.hytora.discordbot.util.button.DiscordButtonAction;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

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
     * Manager for suggestions
     */
    private SuggestionManager suggestionManager;

    /**
     * Manager for tickets
     */
    private TicketManager ticketManager;

    /**
     * Manager for roles
     */
    private ReactionRolesManager reactionRolesManager;

    /**
     * For managing conversations
     */
    private final ConversationManager conversationManager;

    /**
     * The config where all values are stored
     */
    private JsonObject<?> jsonConfig;

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

    /**
     * All {@link DiscordButton}s
     */
    private final List<DiscordButton> discordButtons;

    public Hytora() {
        long start = System.currentTimeMillis();

        hytora = this;
        this.logManager = new LogManager("logs/");
        this.conversationManager = new ConversationManager();
        this.discordButtons = new ArrayList<>();

        if (this.loadConfig() && this.loadJDA(new CommandListener(), new JoinListener(), new DiscordButtonListener(), new ConversationListener())) {
            Scheduler.getInstance().scheduleDelayedTask(() -> {
                Utils.clearConsole();
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


                if (this.loadGuild()) {

                    this.commandManager = new CommandManager(this.jsonConfig.getString("command"));
                    this.registerCommands();

                    this.logManager.log("WELCOME", "§7Bot logged in as §3" + this.discord.getSelfUser().getAsTag() + " §7in §b" + (System.currentTimeMillis() - start) + "ms");
                    this.logManager.log("WELCOME", "§7Logged in on Guild §3" + this.guild.getName() + " §7@ §b" + this.guild.getId());
                    this.logManager.log("WELCOME", "§7On the guild are §b" + this.guild.getMembers().size() + "§8/§b" + this.guild.getMaxMembers() + " Members!");
                    this.logManager.log("§8");

                    if (this.checkOtherValues()) {
                        this.registerConversations();
                        this.manageDefaultRoles();

                        this.logManager.preset(this.botManaging, "Welcome", this.discord.getSelfUser(), message -> {

                        }, new Button[]{
                                new DiscordButton(0x00, "Stop DiscordBot", ButtonStyle.DANGER, new Consumer<DiscordButtonAction>() {
                                    @Override
                                    public void accept(DiscordButtonAction discordButtonAction) {
                                        TextChannel textChannel = discordButtonAction.getTextChannel();
                                        Message message = discordButtonAction.getMessage();
                                        if (textChannel.getId().equalsIgnoreCase(Hytora.getHytora().getBotManaging().getId())) {
                                            EmbedBuilder embedBuilder = Hytora.getHytora().getLogManager().embedBuilder(Color.DARK_GRAY,"Shutdown", discordButtonAction.getUser(), "The HytoraCloud Bot", "Is shutting down in 1 Second...");
                                            message.editMessage(embedBuilder.build()
                                            ).queue(message1 -> {
                                                Scheduler.getInstance().scheduleDelayedTask(() -> {
                                                    message1.delete().queue(unused -> Hytora.getHytora().shutdown());
                                                }, 20L);
                                            });
                                        }
                                    }
                                }).submit()
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
            }, 60L);
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

        if (this.suggestionManager != null) {
            this.suggestionManager.save();
        }

        this.logManager.save();
        if (this.discord != null) {
            this.discord.shutdown();
        }
        System.exit(1);
    }

    /**
     * Registers all triggers for a Conversation
     */
    private void registerConversations() {

        //HEllo triggers
        this.conversationManager.registerAnswer(
                "Hello",
                0.70, (s, user, message) -> s.replace("%user%", user.getAsMention()),
                "Hello %user%!", "Hey %user%! How was your day?", "Bonjour %user% you had a nice day?"
        );
        this.conversationManager.registerAnswer(
                "Hey",
                0.70, (s, user, message) -> s.replace("%user%", user.getAsMention()),
                "Hello %user%!", "Hey %user%! How was your day?", "Bonjour %user% you had a nice day?"
        );
        this.conversationManager.registerAnswer(
                "Hallo",
                0.70, (s, user, message) -> s.replace("%user%", user.getAsMention()),
                "Hello %user%!", "Hey %user%! How was your day?", "Bonjour %user% you had a nice day?"
        );
        this.conversationManager.registerAnswer(
                "Hi",
                0.70, (s, user, message) -> s.replace("%user%", user.getAsMention()),
                "Hello %user%!", "Hey %user%! How was your day?", "Bonjour %user% you had a nice day?"
        );

        //Other
        this.conversationManager.registerAnswer(
                "Whats the Time?",
                0.50,
                (s, user, message) -> s.replace("%time%", new SimpleDateFormat("hh:mm:ss").format(new Date())),
                "Its currently %time%!", "Oh bro I got you!\nIt's currently %time%!");
        this.conversationManager.registerAnswer(
                "How are you?",
                0.50,
                (s, user, message) -> s.replace("%user%", user.getAsMention()),
                "Thanks %user%, I'm fine!",
                "Whoa I'm not that fine today to be honest...",
                "That's not important! How was your day?",
                "Oh, could have been better!",
                "Well, I'm not even awake long enough to say xD"
        );
    }

    /**
     * Manages the default roles
     * and gives everyone the default role
     * if they don't have any
     */

    public void manageDefaultRoles() {

        //Roles
        JsonObject<?> roles = this.jsonConfig.getObject("roles");
        JsonObject<?> defaultRole = roles.getObject("default");
        JsonObject<?> supportRole = roles.getObject("support");

        this.createRole(defaultRole, df -> {

            logManager.log("INFO", "§7Created §b" + df.getName() + "§7-Role§8!");
            for (Member member : guild.getMembers()) {
                Role memberRole = member.getRoles().stream().filter(role1 -> role1.getName().equalsIgnoreCase(df.getName())).findFirst().orElse(null);
                if (memberRole == null) {
                    guild.addRoleToMember(member, df).queue();
                }
            }

        });
        this.createRole(supportRole, sr -> {
            for (Member member : guild.getMembers()) {
                if (member.hasPermission(Permission.ADMINISTRATOR)) {
                    guild.addRoleToMember(member, sr).queue();
                }
            }
        });
    }

    /**
     * Creates a new {@link Role} and accepts the consumer
     *
     * @param jsonDocument the data for the role
     * @param consumer the consumer
     */
    public void createRole(JsonObject<?> jsonDocument, Consumer<Role> consumer) {

        String name = jsonDocument.getString("name");
        String color = jsonDocument.getString("color");
        boolean showOrder = jsonDocument.getBoolean("showOrder");
        boolean mentionable = jsonDocument.getBoolean("mentionable");

        int[] rgb = new int[3];

        if (color.startsWith("RGB,")) {
            String[] split = color.split(",");

            rgb[0] = Integer.parseInt(split[1]);
            rgb[1] = Integer.parseInt(split[2]);
            rgb[2] = Integer.parseInt(split[3]);
        }

        Role role = guild.getRoles().stream().filter(role1 -> role1.getName().equalsIgnoreCase(name)).findFirst().orElse(null);

        if (role == null) {
            try {
                guild.createRole()
                        .setName(name)
                        .setColor(rgb[0] == 0 ? (Color) Color.class.getDeclaredField(color).get(Color.WHITE) :  new Color(rgb[0], rgb[1], rgb[2]))
                        .setHoisted(showOrder)
                        .setMentionable(mentionable)
                        .queue(consumer);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Registers all {@link net.hytora.discordbot.manager.command.CommandHandler}s
     */
    public void registerCommands() {
        this.commandManager.registerCommand(new HelpCommand("help", "Shows this message", CommandCategory.GENERAL, "?", "hilfe"));
        this.commandManager.registerCommand(new EmbedCommand("embed", "Creates an Embed", CommandCategory.ADMINISTRATION, "em", "bc"));
        this.commandManager.registerCommand(new SuggestCommand("suggest", "Creates a suggestion", CommandCategory.OTHER));
        this.commandManager.registerCommand(new StopCommand("stop", "Stops the bot", CommandCategory.GENERAL));
        this.commandManager.registerCommand(new MemeCommand("meme", "Shows you a meme", CommandCategory.FUN));
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

            //Suggestions
            JsonObject<?> suggestions = this.jsonConfig.getObject("suggestions");
            String commands = suggestions.getString("commands");
            String suggestionsChannel = suggestions.getString("suggestions");

            this.suggestionManager = new SuggestionManager(suggestionsChannel);


            JsonObject<?> tickets = this.jsonConfig.getObject("tickets");
            String channel = tickets.getString("channel");
            this.ticketManager = new TicketManager(channel);


            JsonObject<?> reactionRoles = this.jsonConfig.getObject("reactionRoles");
            this.reactionRolesManager = new ReactionRolesManager(reactionRoles.getString("channel"), reactionRoles);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Loads the config.json
     */
    public boolean loadConfig() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Hytora.class.getResourceAsStream("/config.json"))));
            this.jsonConfig = JsonObject.gson(reader);
            return !this.jsonConfig.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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

    public TicketManager getTicketManager() {
        return ticketManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public JsonObject<?> getJsonConfig() {
        return jsonConfig;
    }

    public JDA getDiscord() {
        return discord;
    }

    public Guild getGuild() {
        return guild;
    }

    public SuggestionManager getSuggestionManager() {
        return suggestionManager;
    }

    public List<DiscordButton> getDiscordButtons() {
        return discordButtons;
    }

    public ConversationManager getConversationManager() {
        return conversationManager;
    }

    public ReactionRolesManager getReactionRolesManager() {
        return reactionRolesManager;
    }

    public TextChannel getBotManaging() {
        return botManaging;
    }
}
