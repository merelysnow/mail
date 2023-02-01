package com.github.merelysnow.mail.thread;

import com.github.merelysnow.mail.MailPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;

public class MailCleanerThread extends BukkitRunnable {
    @Override
    public void run() {
        MailPlugin.getInstance().getUserController().getUsers().forEach(users -> {
            if (!users.getEmails().isEmpty()) {
                users.getEmails().removeIf(mail -> Instant.now().toEpochMilli() > mail.getExpireAt().toEpochMilli());
            }
        });
    }
}
