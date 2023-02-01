package com.github.merelysnow.mail.commands;

import com.github.merelysnow.mail.MailPlugin;
import com.github.merelysnow.mail.data.Mail;
import com.github.merelysnow.mail.data.User;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MailSendCommand {

    @Command(
            name = "correio.enviar",
            usage = "correio enviar <jogador>",
            target = CommandTarget.PLAYER
    )
    public void handleCommand(Context<Player> context, String name) {

        Player player = context.getSender();
        Player target = Bukkit.getPlayerExact(name);

        if (target == null) {
            player.sendMessage("§cO jogador alvo não existe ou esta offline.");
            return;
        }

        if (name.equals(player.getName())) {
            player.sendMessage("§cVocê não pode enviar um presente para você mesmo.");
            return;
        }

        if (player.getInventory().getItemInHand() == null || player.getInventory().getItemInHand().getType() == Material.AIR) {
            player.sendMessage("§cVocê precisa estar segurando algum item.");
            return;
        }

        if (isInvalid(player.getInventory().getItemInHand().getAmount())) {
            player.sendMessage("§cNão foi possivel colocar esse item no correio.");
            return;
        }

        if(player.getInventory().getItemInHand().getData().getData() == (byte)32767) {
            player.sendMessage("§cNão foi possivel colocar esse item no correio.");
            return;
        }

        User user = MailPlugin.getInstance().getUserController().get(name);
        Mail mail = new Mail(player.getName(), new Date(), player.getPlayer().getItemInHand(), Instant.now().plusMillis(TimeUnit.HOURS.toMillis(12L)));

        if (user == null) {
            player.sendMessage("§cO jogador alvo não esta registrado em nosso Banco de Dados.");
            return;
        }

        user.getEmails().add(mail);
        player.setItemInHand(new ItemStack(Material.AIR));
        player.sendMessage("§ePresente enviado com sucesso para o jogador alvo.");
    }

    private boolean isInvalid(double value) {
        return value <= 0 || Double.isNaN(value) || Double.isInfinite(value);
    }
}
