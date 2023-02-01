package com.github.merelysnow.mail;

import com.github.merelysnow.mail.commands.MailCommand;
import com.github.merelysnow.mail.commands.MailSendCommand;
import com.github.merelysnow.mail.controller.UserController;
import com.github.merelysnow.mail.database.UserDatabase;
import com.github.merelysnow.mail.listeners.PlayersListeners;
import com.github.merelysnow.mail.thread.MailCleanerThread;
import com.github.merelysnow.mail.view.MailView;
import lombok.Getter;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.saiintbrisson.minecraft.AbstractView;
import me.saiintbrisson.minecraft.ViewFrame;
import me.saiintbrisson.minecraft.command.message.MessageHolder;
import me.saiintbrisson.minecraft.command.message.MessageType;
import org.bukkit.plugin.java.JavaPlugin;

public class MailPlugin extends JavaPlugin {

    @Getter
    private UserController userController;
    @Getter
    private UserDatabase userDatabase;
    @Getter
    private ViewFrame viewFrame;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        userDatabase = new UserDatabase(this);
        userController = new UserController(userDatabase);

        userDatabase.selectMany()
                .forEach(user -> {
                    userController.registerUser(user);
                });

        viewFrame = ViewFrame.of(this, new AbstractView[]{new MailView()}).register();

        registerCommands();
        getServer().getPluginManager().registerEvents(new PlayersListeners(), this);

        (new MailCleanerThread()).runTaskTimerAsynchronously(this, 20L, 20L*60*60);
    }

    private void registerCommands() {
        BukkitFrame bukkitFrame = new BukkitFrame(this);

        bukkitFrame.registerCommands(
                new MailCommand(),
                new MailSendCommand()
        );


        MessageHolder messageHolder = bukkitFrame.getMessageHolder();

        messageHolder.setMessage(MessageType.NO_PERMISSION, "§cVocê não tem permissão para executar este comando.");
        messageHolder.setMessage(MessageType.ERROR, "§cUm erro ocorreu! {error}");
        messageHolder.setMessage(MessageType.INCORRECT_USAGE, "§cUtilize /{usage}");
        messageHolder.setMessage(MessageType.INCORRECT_TARGET, "§cVocê não pode utilizar este comando pois ele é direcioado apenas para {target}.");
    }

    public static MailPlugin getInstance() {
        return getPlugin(MailPlugin.class);
    }
}
