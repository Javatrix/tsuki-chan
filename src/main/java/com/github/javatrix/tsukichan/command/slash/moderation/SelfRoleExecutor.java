/*
 * This file is part of Tsuki-Chan Discord bot project (https://github.com/Javatrix/tsuki-chan).
 * Copyright (c) 2023-2023 Javatrix.
 * The project license can be seen here: https://raw.githubusercontent.com/Javatrix/tsuki-chan/main/LICENSE
 */

package com.github.javatrix.tsukichan.command.slash.moderation;

import com.github.javatrix.tsukichan.TsukiChan;
import com.github.javatrix.tsukichan.command.slash.SlashCommandExecutor;
import com.github.javatrix.tsukichan.event.button.TsukiChanButtonListener;
import com.github.javatrix.tsukichan.util.MemberUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SelfRoleExecutor implements SlashCommandExecutor {

    private static final Map<Member, Set<Role>> roles = new HashMap<>();
    private static final Map<Member, Map<Role, Emoji>> emojis = new HashMap<>();

    public SelfRoleExecutor() {
        TsukiChanButtonListener.register((ButtonInteractionEvent event) -> {
            Role role;
            try {
                role = event.getGuild().getRoleById(event.getComponentId());
            } catch (NumberFormatException e) {
                return;
            }
            if (role == null) {
                return;
            }
            String response;
            if (event.getMember().getRoles().contains(role)) {
                event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
                response = "The role was removed!";
            } else {
                event.getGuild().addRoleToMember(event.getMember(), role).queue();
                response = "Added the role!";
            }
            event.reply(response).setEphemeral(true).queue();
        });
    }

    @Override
    public void process(SlashCommandInteractionEvent context) {
        switch (context.getSubcommandName()) {
            case "add" -> addRole(context);
            case "remove" -> removeRole(context);
            case "list" -> listRoles(context);
            case "send" -> sendRoles(context);
        }
    }

    private void removeRole(SlashCommandInteractionEvent context) {
        if (roles.get(context.getMember()) == null || roles.get(context.getMember()).isEmpty()) {
            context.reply("You don't have any roles selected. Add them with /selfrole add @Role.").setEphemeral(true).queue();
            return;
        }
        roles.get(context.getMember()).remove(context.getOption("role").getAsRole());
        context.reply("Role removed from your selection!").setEphemeral(true).queue();
    }

    private void addRole(SlashCommandInteractionEvent context) {
        Role role = context.getOption("role").getAsRole();
        if (!MemberUtils.canModify(TsukiChan.getInstance().asMember(context.getGuild()), role)) {
            context.reply("Sorry, but I can't assign this role. I can only assign roles that are below me in the hierarchy.\n## :sweat:").setEphemeral(true).queue();
            return;
        }
        roles.computeIfAbsent(context.getMember(), k -> new HashSet<>());
        emojis.computeIfAbsent(context.getMember(), k -> new HashMap<>());

        roles.get(context.getMember()).add(role);

        OptionMapping emoji = context.getOption("emoji");
        if (emoji != null) {
            emojis.get(context.getMember()).put(role, Emoji.fromUnicode(emoji.getAsString()));
        }
        context.reply("Role added to your selection!").setEphemeral(true).queue();
    }

    private void listRoles(SlashCommandInteractionEvent context) {
        Set<Role> roleSet = roles.get(context.getMember());
        if (roleSet == null || roleSet.isEmpty()) {
            context.reply("You have not selected any roles! Use /selfrole add to add them.").setEphemeral(true).queue();
            return;
        }

        StringBuilder response = new StringBuilder("Currently selected roles:\n");
        for (Role role : roleSet) {
            Emoji emoji = emojis.get(context.getMember()).get(role);
            response.append("\t").append(role.getName()).append(" ").append(emoji == null ? "" : emoji.getName()).append("\n");
        }
        context.reply(response.toString()).setEphemeral(true).queue();
    }

    private void sendRoles(SlashCommandInteractionEvent context) {
        ReplyCallbackAction reply = context.reply("Click buttons below to add roles!");
        Set<Role> roleSet = roles.get(context.getMember());
        if (roleSet == null || roleSet.isEmpty()) {
            context.reply("You didn't provide any roles to add. Use /selfrole add @Role to add roles.").queue();
            return;
        }
        for (Role role : roleSet) {
            Emoji emoji = emojis.get(context.getMember()).get(role);
            reply.addActionRow(Button.primary(role.getId(), role.getName()).withEmoji(Emoji.fromFormatted(emoji.getFormatted())));
        }
        reply.queue();
        roles.get(context.getMember()).clear();
    }

}
