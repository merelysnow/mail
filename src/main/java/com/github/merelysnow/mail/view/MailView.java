package com.github.merelysnow.mail.view;

import com.github.merelysnow.mail.MailPlugin;
import com.github.merelysnow.mail.data.Mail;
import com.github.merelysnow.mail.data.User;
import com.github.merelysnow.mail.utils.InventoryUtils;
import com.github.merelysnow.mail.utils.ItemBuilder;
import me.saiintbrisson.minecraft.PaginatedView;
import me.saiintbrisson.minecraft.PaginatedViewSlotContext;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class MailView extends PaginatedView<Mail> {

    private final SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.UK)
            .withZone(ZoneId.systemDefault());

    public MailView() {
        super(6, "Correio");

        setSource(ctx -> ctx.get("emails"));
        setLayout("XXXXXXXXX",
                "XXOOOOOXX",
                "XXOOOOOXX",
                "XXOOOOOXX",
                "XXXXXXXXX",
                "XXXXXXXXX");

        setCancelOnClick(true);
    }

    @Override
    protected void onRender(@NotNull ViewContext context) {

        User user = MailPlugin.getInstance().getUserController().get(context.getPlayer().getName());

        context.slot(49, new ItemBuilder(Material.ARROW)
                        .name("§cFechar").make())
                .onClick(event -> {
                    event.getPlayer().closeInventory();
                });

        if (user.getEmails().isEmpty()) {
            context.slot(22, new ItemBuilder(Material.WEB)
                    .name("§cVazio...").make());
        }
    }

    @Override
    protected void onItemRender(@NotNull PaginatedViewSlotContext<Mail> paginatedViewSlotContext, @NotNull ViewItem viewItem, @NotNull Mail mail) {

        Player player = paginatedViewSlotContext.getPlayer();
        User user = MailPlugin.getInstance().getUserController().get(player.getName());

        viewItem.withItem(new ItemBuilder(mail.getItemStack())
                        .amount(mail.getItemStack().getAmount())
                        .name("§eNova entrega!")

                        .lore("",
                                "§f Enviado por: §7" + mail.getSender(),
                                "§f Data: §7" + formatador.format(mail.getCreatedAt()),
                                " §fExpira em: §7" + DATE_TIME_FORMATTER.format(mail.getExpireAt()),
                                "",
                                "§eClique para coletar.")
                        .make())
                .onClick(event -> {
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage("§cVocê não possui espaço suficiente!");
                        return;
                    }

                    InventoryUtils.give(player, mail.getItemStack());
                    user.getEmails().remove(mail);
                    MailPlugin.getInstance().getUserDatabase().insert(user);
                    player.sendMessage("§eYeah! Entrega coletada com sucesso!");
                    close();
                });
    }
}
