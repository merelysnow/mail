package com.github.merelysnow.mail.controller;

import com.github.merelysnow.mail.data.Mail;
import com.github.merelysnow.mail.data.User;
import com.github.merelysnow.mail.database.UserDatabase;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class UserController {

    private final HashMap<String, User> cache = new HashMap<>();
    private final UserDatabase userDatabase;

    public User get(String name) {
        if (cache.containsKey(name)) {
            return cache.get(name);
        }

        return null;
    }

    public void registerUser(User user) {
        this.cache.put(user.getName(), user);
        this.userDatabase.insert(user);
    }

    public Collection<User> getUsers() {
        return this.cache.values();
    }

    public void addMail(User user, ItemStack itemStack) {
        Mail mail = new Mail("Console", new Date(), itemStack, Instant.now().plusMillis(TimeUnit.HOURS.toMillis(12L)));

        user.getEmails().add(mail);
        this.userDatabase.insert(user);
    }
}
