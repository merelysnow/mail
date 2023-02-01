package com.github.merelysnow.mail.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.Date;

@AllArgsConstructor
@Data
public class Mail {

    private final String sender;
    private Date createdAt;
    private ItemStack itemStack;
    private Instant expireAt;

}
