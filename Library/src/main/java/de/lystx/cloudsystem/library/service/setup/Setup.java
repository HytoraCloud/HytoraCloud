package de.lystx.cloudsystem.library.service.setup;

import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.console.color.ConsoleColor;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public abstract class Setup<T> {

    private final Map<Field, SetupPart> setupParts = new HashMap<>();
    private int current = 43084380;
    private CloudConsole cloudConsole;
    private boolean cancelled;
    private boolean skipped;
    private Map.Entry<Field, SetupPart> currentPart;
    private Consumer<T> consumer;

    /**
     * Starts the setup and loops through the given questions
     * @param scanner
     * @param consumer
     */
    public void start(CloudConsole scanner, Consumer<T> consumer) {
        scanner.getLogger().sendMessage("§9");
        scanner.getLogger().sendMessage("§9");
        scanner.getLogger().sendMessage("SETUP", "§aIf you want to setup just cloudType §2'cancel'§a!");
        this.consumer = consumer;
        this.cloudConsole = scanner;
        this.current = 1;
        this.cancelled = false;
        this.skipped = false;

        for (Field field : getClass().getDeclaredFields()) {
            if (field.getAnnotation(SetupPart.class) != null) {
                this.setupParts.put(field, field.getAnnotation(SetupPart.class));
            }
        }

        this.currentPart = this.getEntry(1);

        this.cloudConsole.getLogger().getConsoleReader().setPrompt("");
        this.cloudConsole.getLogger().sendMessage("SETUP", this.currentPart.getValue().question() + " §7(§a" + this.currentPart.getKey().getType().getSimpleName() + "§7)");
        this.cloudConsole.getLogger().getConsoleReader().setPrompt("");

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
                this.cloudConsole.getLogger().sendMessage("SETUP", "§cThe current was §ecancelled§c!");
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
     * @param setupPart
     * @param answer
     * @return if answer is forbidden
     */
    public boolean isAnswerForbidden(SetupPart setupPart, String answer) {
        if ((setupPart.forbiddenAnswers()).length > 0)
            for (String forbiddenAnswer : setupPart.forbiddenAnswers()) {
                if (forbiddenAnswer.equalsIgnoreCase(answer)) return true;
            }
        return false;
    }

    /**
     * @param setupPart
     * @param answer
     * @return if answer is a goTO
     */
    public GoTo isAnswerGoto(SetupPart setupPart, String answer) {
        if (setupPart.goTo().id() != -1) {
            return setupPart.goTo();
        }
        return null;
    }

    /**
     * @param setupPart
     * @param answer
     * @return if answer is allowed
     */
    public boolean isAnswerAllowed(SetupPart setupPart, String answer) {
        if ((setupPart.onlyAnswers()).length > 0) {
            for (String forbiddenAnswer : setupPart.onlyAnswers()) {
                if (forbiddenAnswer.equalsIgnoreCase(answer)) return true;
            }
        }
        return true;
    }

    /**
     * @param setupPart
     * @param answer
     * @return if answer is change
     */
    public String isChangeAnswer(SetupPart setupPart, String answer) {
        if ((setupPart.onlyAnswers()).length > 0)
            for (String forbiddenAnswer : setupPart.changeAnswers()) {
                String change = forbiddenAnswer.split("->")[0];
                if (change.equalsIgnoreCase(answer)) return forbiddenAnswer.split("->")[1];
            }
        return null;
    }

    /**
     * @param setupPart
     * @param answer
     * @return if answer is exit
     */
    public boolean isAnswerExit(SetupPart setupPart, String answer) {
        if ((setupPart.exitAfterAnswer()).length > 0) {
            for (String forbiddenAnswer : setupPart.exitAfterAnswer()) {
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
    public Map.Entry<Field, SetupPart> getEntry(int id) {
        Map.Entry<Field, SetupPart> entry = null;
        for (Map.Entry<Field, SetupPart> currentEntry : this.setupParts.entrySet()) {
            if (currentEntry.getValue().id() == id) entry = currentEntry;
        }
        return entry;
    }
}
