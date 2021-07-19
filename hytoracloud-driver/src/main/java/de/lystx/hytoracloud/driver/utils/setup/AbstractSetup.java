package de.lystx.hytoracloud.driver.utils.setup;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.CloudConsole;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.color.ConsoleColor;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class is used for
 * Setup purposes.
 * To create a Setup just make the class extend
 * {@link AbstractSetup}
 *
 * @param <T>
 */
@Getter
public abstract class AbstractSetup<T> {

    private final Map<Field, Setup> setupParts = new HashMap<>();
    private int current = 1;
    private CloudConsole cloudConsole;
    private boolean cancelled;
    private boolean skipped;
    private Map.Entry<Field, Setup> currentPart;
    private Consumer<T> consumer;

    protected String customHeader = null;
    protected boolean cancellable = true;
    protected boolean printHeader = true;

    /**
     * Starts the setup and loops through the given questions
     * @param scanner
     * @param consumer
     */
    public void start(CloudConsole scanner, Consumer<T> consumer) {
        this.consumer = consumer;
        this.cloudConsole = scanner;

        if (printHeader) {
            print();
        }
        if (this.customHeader != null) {
            scanner.sendMessage("§8");
            scanner.sendMessage(this.customHeader);
            scanner.sendMessage("§8");
        }
        scanner.getLogger().sendMessage("§9");
        scanner.getLogger().sendMessage("§9");
        if (this.isCancellable()) {
            scanner.getLogger().sendMessage("SETUP", "§aIf you want to abort this setup just type §2'cancel'§a!");
        }

        for (Field field : getClass().getDeclaredFields()) {
            if (field.getAnnotation(Setup.class) != null) {
                this.setupParts.put(field, field.getAnnotation(Setup.class));
            }
        }

        this.currentPart = this.getEntry(1);

        this.cloudConsole.getLogger().getConsoleReader().setPrompt("");
        this.cloudConsole.getLogger().sendMessage("SETUP", this.currentPart.getValue().question());
        this.printExtraMessage();
        this.cloudConsole.getLogger().getConsoleReader().setPrompt("");


        this.cloudConsole.setCurrentSetup(this);
        while (this.current < this.setupParts.size() + 1) {
            try {
                String line = this.cloudConsole.getLogger().getConsoleReader().readLine(ConsoleColor.formatColorString(this.cloudConsole.getPrefix()));
                if (line != null) {
                    this.cloudConsole.getLogger().getConsoleReader().setPrompt("");
                    this.next(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.cloudConsole.setCurrentSetup(null);
        this.consumer.accept((T) this);

    }

    /**
     * Handles next question
     * @param lastAnswer
     */
    public void next(String lastAnswer) {
        if (this.currentPart != null) {
            if (lastAnswer.trim().isEmpty()) {
                this.cloudConsole.getLogger().sendMessage("ERROR", "§cPlease enter a §evalid §cvalue!");
                return;
            }
            if (lastAnswer.equalsIgnoreCase("cancel")) {
                this.cloudConsole.getLogger().sendMessage("SETUP", "§cThe current setup was §ecancelled§c!");
                this.cancelled = true;
                this.current += 10000;
                return;
            }
            if (isChangeAnswer(this.currentPart.getValue(), lastAnswer) != null) {
                lastAnswer = isChangeAnswer(this.currentPart.getValue(), lastAnswer);
            }
            if (!isAnswerAllowed(this.currentPart.getValue(), lastAnswer)) {
                this.cloudConsole.getLogger().sendMessage("ERROR", "§cPossible answers: §e"  + Arrays.toString(this.currentPart.getValue().onlyAnswers()).replace("]", "").replace("[", ""));
                this.cloudConsole.getLogger().sendMessage("ERROR", "§cRequired Type: §e" + this.currentPart.getKey().getType().getSimpleName());
                return;
            }
            if (isAnswerForbidden(this.currentPart.getValue(), lastAnswer)) {
                this.cloudConsole.getLogger().sendMessage("ERROR", !lastAnswer.trim().isEmpty() ? ("§cThe answer '§e" + lastAnswer + " §cmay not be used for this question!") : "§cThis §eanswer §cmay not be used for this question!");
                return;
            }
            GoTo goTo = isAnswerGoto(this.currentPart.getValue(), lastAnswer);
            if (goTo != null) {
                if (lastAnswer.equalsIgnoreCase(goTo.value()) || goTo.value().trim().isEmpty()) {
                    this.current = goTo.id() - 1;
                } else {
                    this.current = goTo.elseID() - 1;
                }
            }
            this.currentPart.getKey().setAccessible(true);
            try {
                Object value;
                Field field = this.currentPart.getKey();
                try {
                    if (field.getType() == Integer.class || field.getType() == int.class) {
                        value =  Integer.parseInt(lastAnswer);
                    } else if (field.getType() == Double.class || field.getType() == double.class) {
                        value =  Double.parseDouble(lastAnswer);
                    } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                        if (lastAnswer.equalsIgnoreCase("true")) {
                            value = true;
                        } else if (lastAnswer.equalsIgnoreCase("false")) {
                            value = false;
                        } else {
                            value = null;
                        }
                    } else if (field.getType() == Byte.class || field.getType() == byte.class) {
                        value =  Byte.parseByte(lastAnswer);
                    } else if (field.getType() == Long.class || field.getType() == long.class) {
                        value =  Long.parseLong(lastAnswer);
                    } else if (field.getType() == String.class) {
                        value = lastAnswer;
                    } else {
                        value = null;
                    }
                } catch (Exception e) {
                    value = null;
                }
                if (value == null) {
                    this.cloudConsole.getLogger().sendMessage("ERROR", "§cPlease try again");
                    return;
                }
                this.currentPart.getKey().set(this, value);

            } catch (Exception ex) {
                this.cloudConsole.getLogger().sendMessage("ERROR", "§cPlease enter a valid format");
                return;
            }
            if (isAnswerExit(this.currentPart.getValue(), lastAnswer)) {
                this.current += 10000;
                this.skipped = true;
                return;
            }
        }
        this.current++;
        this.currentPart = this.getEntry(this.current);
        if (this.currentPart != null) {
            if (printHeader) {
                print();
            }
            this.cloudConsole.getLogger().getConsoleReader().setPrompt("");
            this.cloudConsole.getLogger().sendMessage("SETUP", "§b" + this.currentPart.getValue().question());
            this.printExtraMessage();
            this.cloudConsole.getLogger().getConsoleReader().setPrompt("");
        }
    }

    /**
     * A common method for all enums since they can't have another base class
     * @param <T> Enum type
     * @param c enum type. All enums must be all caps.
     * @param string case insensitive
     * @return corresponding enum, or null
     */
    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
        if( c != null && string != null ) {
            try {
                return Enum.valueOf(c, string.trim().toUpperCase());
            } catch(IllegalArgumentException ex) {
            }
        }
        return null;
    }

    public void printExtraMessage() {

        for (String s : this.currentPart.getValue().message()) {
            try {
                String[] splits = s.split("%%");
                String prefix = splits[0];
                String message = splits[1];
                this.cloudConsole.getLogger().sendMessage(prefix, message);
            } catch (Exception e) {
                this.cloudConsole.getLogger().sendMessage(s);
            }
        }
    }

    void print() {
        Utils.clearConsole();

        this.cloudConsole.getLogger().sendMessage("§8");
        this.cloudConsole.getLogger().sendMessage("§7\n" +
                "   _____      __            \n" +
                "  / ___/___  / /___  ______ \n" +
                "  \\__ \\/ _ \\/ __/ / / / __ \\\n" +
                " ___/ /  __/ /_/ /_/ / /_/ /\n" +
                "/____/\\___/\\__/\\__,_/ .___/ \n" +
                "                   /_/      \n");
        this.cloudConsole.getLogger().sendMessage("§8");
        this.cloudConsole.getLogger().sendMessage("INFO", "§7» §7Cancellable §f: §b" + (this.cancellable ? "Yes" : "No"));
        this.cloudConsole.getLogger().sendMessage("INFO", "§7» §7Setup §f: §b" + getClass().getSimpleName());
        this.cloudConsole.getLogger().sendMessage("INFO", "§7» §7Question §f: §b" + (this.current == 1 ? 1 : current ) + "/" + (this.setupParts.keySet().size() == 0 ? "Loading" : this.setupParts.keySet().size() + ""));
        this.cloudConsole.getLogger().sendMessage("§8");
    }

    /**
     * @param setup
     * @param answer
     * @return if answer is forbidden
     */
    public boolean isAnswerForbidden(Setup setup, String answer) {
        if ((setup.forbiddenAnswers()).length > 0)
            for (String forbiddenAnswer : setup.forbiddenAnswers()) {
                if (forbiddenAnswer.equalsIgnoreCase(answer)) return true;
            }
        return false;
    }


    /**
     * @param setup
     * @param answer
     * @return if answer is a goTO
     */
    public GoTo isAnswerGoto(Setup setup, String answer) {
        if (setup.goTo().id() != -1) {
            return setup.goTo();
        }
        return null;
    }

    /**
     * @param setup
     * @param answer
     * @return if answer is allowed
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
     * @param setup
     * @param answer
     * @return if answer is change
     */
    public String isChangeAnswer(Setup setup, String answer) {
        if ((setup.onlyAnswers()).length > 0)
            for (String forbiddenAnswer : setup.changeAnswers()) {
                String change = forbiddenAnswer.split("->")[0];
                if (change.equalsIgnoreCase(answer)) return forbiddenAnswer.split("->")[1];
            }
        return null;
    }

    /**
     * @param setup
     * @param answer
     * @return if answer is exit
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
     * Getting Map.Entry
     * @param id
     * @return entry by ID
     */
    public Map.Entry<Field, Setup> getEntry(int id) {
        Map.Entry<Field, Setup> entry = null;
        for (Map.Entry<Field, Setup> currentEntry : this.setupParts.entrySet()) {
            if (currentEntry.getValue().id() == id) entry = currentEntry;
        }
        return entry;
    }
}
