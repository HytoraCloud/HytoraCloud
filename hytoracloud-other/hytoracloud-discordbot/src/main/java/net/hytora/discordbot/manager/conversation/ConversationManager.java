package net.hytora.discordbot.manager.conversation;

import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.hytora.discordbot.util.MultiConsumer;

import java.util.*;

@Getter
public class ConversationManager {

    /**
     * The registered triggers and their answers
     */
    private final Map<String, List<String>> answers;

    /**
     * The accuracy for registered triggers
     */
    private final Map<String, Double> accuracy;

    /**
     * List of all replacers for placeholders
     */
    private final Map<String, MultiConsumer<String, String, User, Message>> replacers;

    public ConversationManager() {
        this.answers = new LinkedHashMap<>();
        this.accuracy = new LinkedHashMap<>();
        this.replacers = new LinkedHashMap<>();
    }

    /**
     * Registers an Answer
     *
     * @param trigger the trigger
     * @param answers the answers to give
     * @param accuracy the accuracy in percent
     */
    public void registerAnswer(String trigger, double accuracy, String... answers) {
        this.answers.put(trigger.toLowerCase(), Arrays.asList(answers));
        this.accuracy.put(trigger.toLowerCase(), accuracy);
    }

    /**
     * Registers an Answer
     *
     * @param trigger the trigger
     * @param answers the answers to give
     * @param accuracy the accuracy in percent
     */
    public void registerAnswer(String trigger, double accuracy, MultiConsumer<String, String, User, Message> replacer, String... answers) {
        this.registerAnswer(trigger, accuracy, answers);
        this.replacers.put(trigger.toLowerCase(), replacer);
    }

    /**
     * Checks for a trigger
     *
     * @param trigger the trigger
     * @return list of answers
     */
    public List<String> matches(String trigger, GuildMessageReceivedEvent event) {
        List<String> list = new ArrayList<>();
        double accuracyOrDefault = this.accuracy.getOrDefault(trigger.toLowerCase(), 0.70);
        double percent = -1;

        for (String s : this.answers.keySet()) {
            System.out.println(trigger + " - " + s);
            if (s.equalsIgnoreCase(trigger)) {
                List<String> answers = this.answers.getOrDefault(s.toLowerCase(), new LinkedList<>());
                if ((percent = Utils.getPercentMatch(trigger.toLowerCase(), s.toLowerCase(), true)) >= accuracyOrDefault) {
                    list.addAll(answers);
                }
            }

        }

        MultiConsumer<String, String, User, Message> consumer = this.replacers.get(trigger.toLowerCase());
        for (String s : list) {
            int i = list.indexOf(s);

            if (consumer != null) {
                s = consumer.accept(s, event.getAuthor(), event.getMessage());
            }

            list.set(i, s);
        }

        list.add("Match required " + accuracyOrDefault + "% | Provided: " + percent + "%");

        return list;
    }
}
