package de.lystx.cloudsystem.library.service.setup;


import de.lystx.cloudsystem.library.service.console.color.ConsoleColor;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public abstract class Setup {

    private final Map<Field, SetupPart> setupParts = new HashMap<>();
    private int current = 43084380;
    private CloudConsole cloudConsole;
    private boolean cancelled;
    private boolean skipped;
    private Map.Entry<Field, SetupPart> currentPart;
    private Consumer<Setup> consumer;


    public void start(CloudConsole scanner, Consumer<Setup> consumer) {
        scanner.getLogger().sendMessage("§9");
        scanner.getLogger().sendMessage("§9");
        scanner.getLogger().sendMessage("SETUP", "§aIf you want to §2cancel §athe setup just type §7'§2cancel§7'");
        this.consumer = consumer;
        this.cloudConsole = scanner;
        this.current = 1;
        this.cancelled = false;
        this.skipped = false;
        for (Field field : getClass().getDeclaredFields()) {
            if (field.getAnnotation(SetupPart.class) != null)
                this.setupParts.put(field, field.getAnnotation(SetupPart.class));
        }
        this.currentPart = getEntry(1);
        this.cloudConsole.getLogger().sendMessage("SETUP", (this.currentPart.getValue()).question() + " §7(§a" + (this.currentPart.getKey()).getType().getSimpleName() + "§7)");
        while (this.current < this.setupParts.size() + 1) {
            try {
                String line = cloudConsole.getLogger().getConsoleReader().readLine(ConsoleColor.formatColorString(cloudConsole.getPrefix()));
                this.next(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.consumer.accept(this);
    }


    public void next(String lastAnswer) {
        if (this.currentPart != null) {
            if (lastAnswer.trim().isEmpty()) {
                this.cloudConsole.getLogger().sendMessage("ERROR","§cPlease enter a value!");
                return;
            }
            if (lastAnswer.equalsIgnoreCase("cancel")) {
                this.cloudConsole.getLogger().sendMessage("SETUP", "§cThe current §esetup §cwas cancelled!");
                this.cancelled = true;
                this.current = (current + 10000);
                return;
            }
            if (isChangeAnswer(this.currentPart.getValue(), lastAnswer) != null) {
                lastAnswer = isChangeAnswer(this.currentPart.getValue(), lastAnswer);
            }
            if (!isAnswerAllowed(this.currentPart.getValue(), lastAnswer)) {
                this.cloudConsole.getLogger().sendMessage("ERROR", "§cPossible answers §e" + (Arrays.toString(this.currentPart.getValue().onlyAnswers())).replace("]", "").replace("[", ""));
                return;
            }
            if (isAnswerForbidden(this.currentPart.getValue(), lastAnswer)) {
                this.cloudConsole.getLogger().sendMessage("ERROR", !lastAnswer.trim().isEmpty() ? "§cThe answer '§e" + lastAnswer + "§c' §cmay not be used for this question!" : "§cThis §eanswer §cmay not be used for this question!");
                return;
            }
            (this.currentPart.getKey()).setAccessible(true);
            try {
                Object value = parse(this.currentPart.getKey(), lastAnswer);
                if (value == null) {
                    this.cloudConsole.getLogger().sendMessage("ERROR", "§cPlease try again");
                    return;
                }
                (this.currentPart.getKey()).set(this, value);
            } catch (Exception ex) {
                this.cloudConsole.getLogger().sendMessage("ERROR", "§cPlease enter a valid format");
                return;
            }
            if (isAnswerExit(this.currentPart.getValue(), lastAnswer)) {
                this.current = (current + 10000);
                this.skipped = true;
                return;
            }
        }
        this.current++;
        this.currentPart = getEntry(this.current);
        if (this.currentPart != null)
            this.cloudConsole.getLogger().sendMessage("SETUP",  (this.currentPart.getValue()).question() + " §7(§a" + this.currentPart.getKey().getType().getSimpleName() + "§7)");
    }

    public Object parse(Field field, String s) {
        try {
            if (field.getType() == Integer.class || field.getType() == int.class)
                return Integer.parseInt(s);
            if (field.getType() == Double.class || field.getType() == double.class)
                return Double.parseDouble(s);
            if (field.getType() == Boolean.class || field.getType() == boolean.class)
                return Boolean.parseBoolean(s);
            if (field.getType() == Byte.class || field.getType() == byte.class)
                return Byte.parseByte(s);
            if (field.getType() == Long.class || field.getType() == long.class)
                return Long.parseLong(s);
            if (field.getType() == String.class)
                return s;
        } catch (Exception exception) {}
        return null;
    }

    public boolean isAnswerForbidden(SetupPart setupPart, String answer) {
        if ((setupPart.forbiddenAnswers()).length > 0)
            for (String forbiddenAnswer : setupPart.forbiddenAnswers()) {
                if (forbiddenAnswer.equalsIgnoreCase(answer))
                    return true;
            }
        return false;
    }

    public boolean isAnswerAllowed(SetupPart setupPart, String answer) {
        if ((setupPart.onlyAnswers()).length > 0) {
            for (String forbiddenAnswer : setupPart.onlyAnswers()) {
                if (forbiddenAnswer.equalsIgnoreCase(answer)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public String isChangeAnswer(SetupPart setupPart, String answer) {
        if ((setupPart.onlyAnswers()).length > 0) {
            for (String forbiddenAnswer : setupPart.changeAnswers()) {
                String change = forbiddenAnswer.split("->")[0];
                if (change.equalsIgnoreCase(answer)) {
                    return forbiddenAnswer.split("->")[1];
                }
            }
        }
        return null;
    }

    public boolean isAnswerExit(SetupPart setupPart, String answer) {
        if ((setupPart.exitAfterAnswer()).length > 0) {
            for (String forbiddenAnswer : setupPart.exitAfterAnswer()) {
                if (forbiddenAnswer.equalsIgnoreCase(answer)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }


    public Map.Entry<Field, SetupPart> getEntry(int id) {
        Map.Entry<Field, SetupPart> entry = null;
        for (Map.Entry<Field, SetupPart> currentEntry : this.setupParts.entrySet()) {
            if ((currentEntry.getValue()).id() == id)
                entry = currentEntry;
        }
        return entry;
    }

}
