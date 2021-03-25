package de.lystx.cloudsystem.library.service.setup;

import de.lystx.cloudsystem.library.elements.list.CloudList;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.console.color.ConsoleColor;
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


    protected boolean cancellable = true;

    /**
     * Starts the setup and loops through the given questions
     * @param scanner
     * @param consumer
     */
    public void start(CloudConsole scanner, Consumer<T> consumer) {
        this.consumer = consumer;
        this.cloudConsole = scanner;

        scanner.getLogger().sendMessage("§9");
        scanner.getLogger().sendMessage("§9");
        if (this.isCancellable()) {
            scanner.getLogger().sendMessage("SETUP", "§aIf you want to setup just type §2'cancel'§a!");
        }

        for (Field field : getClass().getDeclaredFields()) {
            if (field.getAnnotation(Setup.class) != null) {
                this.setupParts.put(field, field.getAnnotation(Setup.class));
            }
        }

        this.currentPart = this.getEntry(1);

        this.cloudConsole.getLogger().getConsoleReader().setPrompt("");
        this.cloudConsole.getLogger().sendMessage("SETUP", this.currentPart.getValue().question() + " §7(§a" + this.currentPart.getKey().getType().getSimpleName() + "§7)");
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
                return;
            }
            if (isAnswerForbidden(this.currentPart.getValue(), lastAnswer)) {
                this.cloudConsole.getLogger().sendMessage("ERROR", !lastAnswer.trim().isEmpty() ? ("§cThe answer '§e" + lastAnswer + " §cmay not be used for this question!") : "§cThis §eanswer §cmay not be used for this question!");
                return;
            }
            GoTo goTo = isAnswerGoto(this.currentPart.getValue(), lastAnswer);
            if (goTo != null) {
                if (lastAnswer.equalsIgnoreCase(goTo.value())) {
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
            this.cloudConsole.getLogger().getConsoleReader().setPrompt("");
            this.cloudConsole.getLogger().sendMessage("SETUP", "§b" + this.currentPart.getValue().question() + " §7(§a" + this.currentPart.getKey().getType().getSimpleName() + "§7)");
            this.cloudConsole.getLogger().getConsoleReader().setPrompt("");
        }
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
                if (forbiddenAnswer.equalsIgnoreCase(answer)) return true;
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
