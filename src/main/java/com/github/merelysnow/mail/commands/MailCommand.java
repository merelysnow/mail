package com.github.merelysnow.mail.commands;

import com.github.merelysnow.mail.MailPlugin;
import com.github.merelysnow.mail.data.User;
import com.github.merelysnow.mail.view.MailView;
import com.google.common.collect.ImmutableMap;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.entity.Player;

public class MailCommand {

    @Command(
            name = "correio",
            target = CommandTarget.PLAYER
    )
    public void handleCommad(Context<Player> context) {

        Player player = context.getSender();
        User user = MailPlugin.getInstance().getUserController().get(player.getName());

        if(user == null) {
            player.sendMessage("§cVocê não esta registrado no banco de dados.");
            return;
        }

        MailPlugin.getInstance().getViewFrame().open(MailView.class, player, ImmutableMap.of("emails", user.getEmails()));
    }
}
