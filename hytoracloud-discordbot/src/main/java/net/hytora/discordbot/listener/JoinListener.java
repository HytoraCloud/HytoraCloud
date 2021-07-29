package net.hytora.discordbot.listener;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hytora.discordbot.Hytora;
import org.jetbrains.annotations.NotNull;

public class JoinListener extends ListenerAdapter {


    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

        Member member = event.getMember();

        String name = Hytora.getHytora().getJsonConfig().getObject("roles").getObject("default").getString("name");

        Role memberRole = member.getRoles().stream().filter(role1 -> role1.getName().equalsIgnoreCase(name)).findFirst().orElse(null);

        if (memberRole == null) {
            Hytora.getHytora().getGuild().addRoleToMember(member, Hytora.getHytora().getGuild().getRolesByName(name, true).get(0)).queue();
        }
    }
}
