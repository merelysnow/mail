package com.github.merelysnow.mail.listeners;

import com.github.merelysnow.mail.MailPlugin;
import com.github.merelysnow.mail.data.User;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayersListeners implements Listener {

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();
        User user = MailPlugin.getInstance().getUserController().get(player.getName());

        if (user == null) {
            user = new User(player.getName(), Lists.newArrayList());

            MailPlugin.getInstance().getUserController().registerUser(user);
        }

        if (!user.getEmails().isEmpty()) {
            player.sendMessage("§eAparentemente você possui novas entregar para coletar! §8(/correio)");
            return;
        }
    }
}
