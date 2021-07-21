package de.lystx.hytoracloud.driver.cloudservices.global.setup;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.CloudConsole;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.color.ConsoleColor;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This class is used for Setup purposes.
 * To create a Setup just make the class extend {@link SetupExecutor}
 *
 * @param <T> the generic setup type
 */
@Getter
public abstract class SetupExecutor<T> {

    /**
     * All cached html headers
     */
    private static final Map<String, String> HEADER_CACHE = new HashMap<>();

    /**
     * The setup parts
     */
    private final Map<Field, Setup> map;

    /**
     * The console to display questions
     */
    private final CloudConsole console;

    /**
     * The current setup part
     */
    private int current;

    /**
     * If the setup is cancelled
     */
    private boolean cancelled;

    /**
     * If the setup was exited after one answer
     */
    private boolean exitAfterAnswer;

    /**
     * The current setup fields and their questions
     */
    private Map.Entry<Field, Setup> setup;

    /**
     * The consumer when its finished
     */
    private Consumer<T> consumer;

    /**
     * All registered question handlers
     */
    private final List<BiConsumer<Setup, String[]>> questionHandlers;

    /**
     * If a custom header should be used and what to display
     */
    protected String customHeader = null;

    /**
     * If this setup is allowed to be cancelled
     */
    protected boolean cancellable;

    /**
     * If a header should be printed
     */
    protected boolean printHeader;

    private String htmlHeader;

    public SetupExecutor() {
        this.map = new HashMap<>();
        this.current = 1;
        this.console = CloudDriver.getInstance().getParent().getConsole();
        this.questionHandlers = new LinkedList<>();

        this.cancelled = false;
        this.cancellable = true;
        this.printHeader = true;
        this.exitAfterAnswer = false;

        //Caching the setup fields and parts
        for (Field field : getClass().getDeclaredFields()) {
            if (field.getAnnotation(Setup.class) != null) {
                this.map.put(field, field.getAnnotation(Setup.class));
            }
        }

    }

    /**
     * Registers a consumer to display additional info
     * if needed so you can check the id and display messages
     *
     * @param consumer the consumer
     */
    public void registerConsumer(BiConsumer<Setup, String[]> consumer) {
        this.questionHandlers.add(consumer);
    }

    /**
     * Starts the setup and loops through the given questions
     *
     * @param consumer the consumer after its finished
     */
    public void start(Consumer<T> consumer) {

        this.consumer = consumer;
        this.console.setCurrentSetup(this);

        if (this.printHeader) {
            try {
                this.htmlHeader = "\n" + loadHeaderFromWeb();

            } catch (Exception e) {
                this.htmlHeader = "§7\n" +
                        "   _____      __            \n" +
                        "  / ___/___  / /___  ______ \n" +
                        "  \\__ \\/ _ \\/ __/ / / / __ \\\n" +
                        " ___/ /  __/ /_/ /_/ / /_/ /\n" +
                        "/____/\\___/\\__/\\__,_/ .___/ \n" +
                        "                   /_/      \n";
            }
            this.printHeader();
        }

        //Sending custom header with spaces between
        if (this.customHeader != null) {
            this.console.sendMessage("§8");
            this.console.sendMessage(this.customHeader);
            this.console.sendMessage("§8");
        }

        //Sending empty spaces
        this.console.getLogger().sendMessage("§9");
        this.console.getLogger().sendMessage("§9");

        //Sending info that its cancellable
        if (this.isCancellable()) {
            this.console.getLogger().sendMessage("SETUP", "§aIf you want to abort this setup just type §2'cancel'§a!");
        }

        //Caching the setup fields and parts
        for (Field field : getClass().getDeclaredFields()) {
            if (field.getAnnotation(Setup.class) != null) {
                this.map.put(field, field.getAnnotation(Setup.class));
            }
        }

        //Setting current setup
        this.setup = this.getEntry(1);

        //Sending first question without any input
        this.console.getLogger().getConsoleReader().setPrompt("");
        this.console.getLogger().sendMessage("SETUP", this.setup.getValue().question());
        this.printExtraMessage();
        this.console.getLogger().getConsoleReader().setPrompt("");


        //While current id is in range of map-cache
        while (this.current < this.map.size() + 1) {
            try {

                //Reading input and executing AbstractSetup#next(String)
                String line = this.console.getLogger().getConsoleReader().readLine(ConsoleColor.formatColorString(this.console.getPrefix()));
                if (line != null) {
                    this.console.getLogger().getConsoleReader().setPrompt("");
                    this.handleQuestion(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.exit();
    }


    public void exit() {
        //If already exited by another code line
        if (this.consumer != null) {
            //Setup done and accepting consumer
            this.console.setCurrentSetup(null);
            this.consumer.accept((T) this);
            this.consumer = null;
        }
        CloudDriver.getInstance().getInstance(CommandService.class).setActive(true);
    }

    /**
     * Handles the current question with a given input
     * It checks if the answer should exit after or jump
     * to a other question and then set the current part higher
     * or lower depending on the {@link QuestionSkip} for example
     *
     * Checks for disallowed answers or only allowed answers
     *
     * @param input the input
     */
    public void handleQuestion(String input) {
        if (this.setup != null) {

            //No input provided
            if (input.trim().isEmpty()) {
                this.console.getLogger().sendMessage("ERROR", "§cPlease do not enter §eempty §cinput!");
                return;
            }

            //Cancelling setup
            if (input.equalsIgnoreCase("cancel")) {
                this.console.getLogger().sendMessage("SETUP", "§cThe current setup was §ecancelled§c!");
                this.cancelled = true;
                this.current += 10000;

                this.exit();
                return;
            }

            //If the current input should be changed to something
            if (this.isChangeAnswer(this.setup.getValue(), input) != null) {
                input = isChangeAnswer(this.setup.getValue(), input);
            }

            //If answer is enum only
            if (!isAnswerEnumOnly(this.setup.getValue(), input)) {
                this.console.getLogger().sendMessage("ERROR", "§cPossible answers: §e"  + Arrays.toString(setup.getValue().enumOnly().getEnumConstants()).replace("]", ")").replace("[", "("));
                return;
            }

            //If the current input is not allowed for this setup question because you provided a wrong type
            if (!isAnswerAllowed(this.setup.getValue(), input)) {
                this.console.getLogger().sendMessage("ERROR", "§cPossible answers: §e"  + Arrays.toString(this.setup.getValue().onlyAnswers()).replace("]", "").replace("[", ""));
                this.console.getLogger().sendMessage("ERROR", "§cRequired Type: §e" + this.setup.getKey().getType().getSimpleName());
                return;
            }

            //If the current input is forbidden to use
            if (this.isAnswerForbidden(this.setup.getValue(), input)) {
                this.console.getLogger().sendMessage("ERROR", !input.trim().isEmpty() ? ("§cThe answer '§e" + input + " §cmay not be used for this question!") : "§cThis §eanswer §cmay not be used for this question!");
                return;
            }

            //If a Goto is provided to jump to a different question
            QuestionSkip questionSkip = isAnswerSkip(this.setup.getValue(), input);
            if (questionSkip != null) {
                if (input.equalsIgnoreCase(questionSkip.value()) || questionSkip.value().trim().isEmpty()) {
                    this.current = questionSkip.id() - 1;
                } else {
                    this.current = questionSkip.elseID() - 1;
                }
            }

            //Accessing the setup field
            this.setup.getKey().setAccessible(true);
            try {
                Object value;
                Field field = this.setup.getKey();
                try {
                    if (field.getType() == Integer.class || field.getType() == int.class) {
                        value =  Integer.parseInt(input);
                    } else if (field.getType() == Double.class || field.getType() == double.class) {
                        value =  Double.parseDouble(input);
                    } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                        if (input.equalsIgnoreCase("true")) {
                            value = true;
                        } else if (input.equalsIgnoreCase("false")) {
                            value = false;
                        } else {
                            value = null;
                        }
                    } else if (field.getType() == Byte.class || field.getType() == byte.class) {
                        value =  Byte.parseByte(input);
                    } else if (field.getType() == Long.class || field.getType() == long.class) {
                        value =  Long.parseLong(input);
                    } else if (field.getType() == String.class) {
                        value = input;
                    } else {
                        value = null;
                    }
                } catch (Exception e) {
                    value = null;
                }
                if (value == null) {
                    this.console.getLogger().sendMessage("ERROR", "§cPlease try again");
                    return;
                }

                //Setting setup value
                this.setup.getKey().set(this, value);

            } catch (Exception ex) {
                this.console.getLogger().sendMessage("ERROR", "§cThe §einput §cdidn't match any of the available §eAnswerTypes§c!");
                return;
            }

            //If the setup should exit after this answer
            if (this.isAnswerExit(this.setup.getValue(), input)) {
                this.current += 10000;
                this.exitAfterAnswer = true;
                return;
            }
        }

        //Going to next question going +1
        this.current++;
        this.setup = this.getEntry(this.current);

        //Could be last question and setup is not found
        if (this.setup != null) {
            if (printHeader) {
                this.printHeader();
            }

            //Sending question again and waiting for input
            this.console.getLogger().getConsoleReader().setPrompt("");
            this.console.getLogger().sendMessage("SETUP", "§b" + this.setup.getValue().question());
            this.printExtraMessage();
            this.console.getLogger().getConsoleReader().setPrompt("");
        } else {
            this.exit();
        }
    }

    /**
     * Prints extra message for current setup
     */
    public void printExtraMessage() {

        //Handling question handlers
        for (BiConsumer<Setup, String[]> handler : this.questionHandlers) {
            handler.accept(this.setup.getValue(), this.setup.getValue().message());
        }

        if (this.setup.getValue().enumOnly() != Enum.class) {
            this.console.sendMessage("SETUP", "§7Possible Answers§h: §b" + Arrays.toString(setup.getValue().enumOnly().getEnumConstants()).replace("]", "§h)").replace("[", "§h(§b").replace(",", "§h, §b"));
        }

        //Printing extra messages
        for (String s : this.setup.getValue().message()) {
            try {
                String[] splits = s.split("%%");
                String prefix = splits[0];
                String message = splits[1];
                this.console.getLogger().sendMessage(prefix, message);
            } catch (Exception e) {
                this.console.getLogger().sendMessage(s);
            }
        }
    }

    /**
     * Checks if an answer is forbidden to use
     *
     * @param setup the current setup
     * @param answer the answer you provided
     * @return forbidden
     */
    public boolean isAnswerForbidden(Setup setup, String answer) {
        if ((setup.forbiddenAnswers()).length > 0) {
            for (String forbiddenAnswer : setup.forbiddenAnswers()) {
                if (forbiddenAnswer.equalsIgnoreCase(answer)) return true;
            }
        }
        return false;
    }


    /**
     * Checks if answer is a goto provided
     *
     * @param setup the current setup
     * @param answer the answer you provided
     * @return goto or null
     */
    public QuestionSkip isAnswerSkip(Setup setup, String answer) {
        if (setup.skip().id() != -1) {
            return setup.skip();
        }
        return null;
    }

    /**
     * Checks if an answer is allowed (type - based)
     *
     * @param setup the current setup
     * @param answer the answer you provided
     * @return allowed
     */
    public boolean isAnswerAllowed(Setup setup, String answer) {
        if ((setup.onlyAnswers()).length > 0) {
            for (String forbiddenAnswer : setup.onlyAnswers()) {
                if (forbiddenAnswer.equalsIgnoreCase(answer)) return true;
            }
            return false;
        }
        return true;
    }

    /**
     * Checks if a setup passes the enum only
     *
     * @param setup the setup
     * @param answer the answer
     * @return if allowed to pass
     */
    public boolean isAnswerEnumOnly(Setup setup, String answer) {
        if (setup.enumOnly() == Enum.class) {
            return true;
        } else {
            Class<? extends Enum> aClass = setup.enumOnly();
            for (Enum<?> enumConstant : aClass.getEnumConstants()) {
                if (enumConstant.name().equalsIgnoreCase(answer)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if an answer changes input
     *
     * @param setup the current setup
     * @param answer the answer you provided
     * @return if change
     */
    public String isChangeAnswer(Setup setup, String answer) {
        if ((setup.onlyAnswers()).length > 0) {
            for (String forbiddenAnswer : setup.changeAnswers()) {
                String change = forbiddenAnswer.split("->")[0];
                if (change.equalsIgnoreCase(answer)) return forbiddenAnswer.split("->")[1];
            }
        }
        return null;
    }

    /**
     * Checks if an answer is exit
     *
     * @param setup the current setup
     * @param answer the answer you provided
     * @return exit
     */
    public boolean isAnswerExit(Setup setup, String answer) {
        if ((setup.exitAfterAnswer()).length > 0) {
            for (String forbiddenAnswer : setup.exitAfterAnswer()) {
                if (forbiddenAnswer.equalsIgnoreCase(answer) || forbiddenAnswer.trim().isEmpty()) return true;
            }
        }
        return false;
    }

    /**
     * Get an entry from the map cache
     *
     * @param id the question id
     * @return entry
     */
    public Map.Entry<Field, Setup> getEntry(int id) {
        Map.Entry<Field, Setup> entry = null;
        for (Map.Entry<Field, Setup> currentEntry : this.map.entrySet()) {
            if (currentEntry.getValue().id() == id) entry = currentEntry;
        }
        return entry;
    }

    /**
     * ==========================
     *  Other utility methods
     * ==========================
     */

    /**
     * Forwarding-Method to display extra messages
     * in consumer or anything like that
     *
     * @param message the message
     */
    public void display(String message) {
        CloudDriver.getInstance().getParent().getConsole().sendMessage("SETUP", message);
    }

    /**
     * Prints the header with its information
     *
     * > If its cancellable
     * > Current Question ID
     * > Setup-Name
     */
    private void printHeader() {
        Utils.clearConsole();

        this.console.getLogger().sendMessage("§8");
        this.console.getLogger().sendMessage(this.htmlHeader);
        this.console.getLogger().sendMessage("§8");
        this.console.getLogger().sendMessage("INFO", "§7» §7Setup §f: §b" + getClass().getSimpleName());
        this.console.getLogger().sendMessage("INFO", "§7» §7Cancellable §f: §b" + (this.cancellable ? "Yes" : "No"));
        this.console.getLogger().sendMessage("INFO", "§7» §7Question §f: §b" + (this.current == 1 ? 1 : current ) + "/" + (this.map.keySet().size() == 0 ? "Loading" : this.map.keySet().size() + ""));
        this.console.getLogger().sendMessage("§8");
    }

    /**
     * Loads the header from the web
     * @return header
     * @throws Exception if something goes wrong
     */
    public String loadHeaderFromWeb() throws Exception {
        if (HEADER_CACHE.containsKey(getClass().getSimpleName())) {
            return HEADER_CACHE.get(getClass().getSimpleName());
        } else {

            StringBuilder result = new StringBuilder();
            URL url = new URL("https://artii.herokuapp.com/make?text=" + getClass().getSimpleName());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line).append("\n");
                }
            }

            //Caching
            String string = result.toString();
            HEADER_CACHE.put(getClass().getSimpleName(), string);
            return string;
        }
    }
}
