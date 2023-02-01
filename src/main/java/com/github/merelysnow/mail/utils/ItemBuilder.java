package com.github.merelysnow.mail.utils;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemBuilder implements Cloneable
{
    private final ItemStack stack;
    private ItemMeta meta;

    public ItemBuilder() {
        this(Material.AIR);
    }

    public ItemBuilder(Material type) {
        this((Material)type, (short)0);
    }

    public ItemBuilder(Material type, int amount) {
        this((Material)type, amount, (short)0);
    }

    public ItemBuilder(Material type, short damage) {
        this((Material)type, 1, damage);
    }

    public ItemBuilder(Material type, int amount, short damage) {
        this(new ItemStack(type, amount, damage));
    }

    public ItemBuilder(MaterialData materialData) {
        this(new ItemStack(materialData.getItemType(), 1, (short)0));
    }

    public ItemBuilder(MaterialData materialData, int amount) {
        this(new ItemStack(materialData.getItemType(), amount, (short)0));
    }

    public ItemBuilder(MaterialData materialData, short damage) {
        this(new ItemStack(materialData.getItemType(), 1, damage));
    }

    public ItemBuilder(MaterialData materialData, int amount, short damage) {
        this(new ItemStack(materialData.getItemType(), amount, damage));
    }

    public ItemBuilder(ItemStack itemStack) {
        this(itemStack, false);
    }

    public ItemBuilder(ItemStack stack, boolean keepOriginal) {
        this.stack = keepOriginal ? stack : stack.clone();
        this.meta = this.stack.getItemMeta();
    }

    public static ItemBuilder of(Material type) {
        return new ItemBuilder(type);
    }

    public static ItemBuilder of(Material type, int amount) {
        return new ItemBuilder(type, amount);
    }

    public static ItemBuilder of(Material type, short damage) {
        return new ItemBuilder(type, damage);
    }

    public static ItemBuilder of(Material type, int amount, short damage) {
        return new ItemBuilder(type, amount, damage);
    }

    public static ItemBuilder of(ItemStack itemStack, boolean keepOriginal) {
        return new ItemBuilder(itemStack, keepOriginal);
    }

    public static ItemBuilder of(ItemStack itemStack) {
        return of(itemStack, false);
    }

    public ItemBuilder type(Material material) {
        this.make().setType(material);
        return this;
    }

    public Material type() {
        return this.make().getType();
    }

    public ItemBuilder amount(Integer itemAmt) {
        this.make().setAmount(itemAmt);
        return this;
    }

    public ItemBuilder name(String name) {
        this.meta().setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.make().setItemMeta(this.meta());
        return this;
    }

    public String name() {
        return this.meta().getDisplayName();
    }

    public ItemBuilder lore(List<String> lore) {
        this.meta().setLore(lore);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        return this.lore(false, lore);
    }

    public ItemBuilder lore(boolean override, String... lore) {
        LinkedList<String> lines = new LinkedList();
        String[] var4 = lore;
        int var5 = lore.length;

        int i;
        String line;
        for(i = 0; i < var5; ++i) {
            line = var4[i];
            String s = ChatColor.translateAlternateColorCodes('&', line);
            lines.add(s);
        }

        if (!override) {
            List<String> oldLines = this.meta().getLore();
            if (oldLines != null && !oldLines.isEmpty()) {
                lines.addAll(0, oldLines);
            }
        }

        Pattern COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-F]");
        Pattern START_COLOR_PATTERN = Pattern.compile("^(?i)§[0-9A-F].*$");

        for(i = 0; i < lines.size() - 1; ++i) {
            line = (String)lines.get(i);
            if (line != null && !line.isEmpty()) {
                Matcher nextMatcher = START_COLOR_PATTERN.matcher((CharSequence)lines.get(i + 1));
                if (!nextMatcher.find()) {
                    Matcher currentMatcher = COLOR_PATTERN.matcher(line);
                    if (currentMatcher.find()) {
                        String lastColor = currentMatcher.group(currentMatcher.groupCount());
                        lines.set(i + 1, lastColor + (String)lines.get(i + 1));
                    }
                }
            }
        }

        for(i = 0; i < lines.size(); ++i) {
            line = (String)lines.get(i);
            if (line != null && !line.isEmpty() && !START_COLOR_PATTERN.matcher(line).find()) {
                lines.set(i, ChatColor.GRAY + line);
            }

            if (ChatColor.stripColor(line).isEmpty()) {
                lines.set(i, "");
            }
        }

        this.meta().setLore(lines);
        this.make().setItemMeta(this.meta());
        return this;
    }

    public List<String> lore() {
        return (List)(this.meta().getLore() == null ? Collections.emptyList() : Lists.newArrayList(this.meta().getLore()));
    }

    public short durability() {
        return this.make().getDurability();
    }

    public ItemBuilder durability(int durability) {
        this.make().setDurability((short)durability);
        return this;
    }

    public ItemBuilder data(int data) {
        this.make().setData(new MaterialData(this.make().getType(), (byte)data));
        return this;
    }

    public ItemBuilder patterns(List<org.bukkit.block.banner.Pattern> patterns) {
        if (this.make().getType() == Material.BANNER) {
            BannerMeta meta = (BannerMeta)this.meta();
            meta.setPatterns(patterns);
            this.make().setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder glowing(boolean glowing) {
        if (this.make().getType().equals(Material.GOLDEN_APPLE)) {
            this.durability((short)(glowing ? 1 : 0));
            this.make().setItemMeta(this.meta());
            return this;
        } else {
            if (this.enchantments().isEmpty()) {
                if (glowing) {
                    this.nbt("ench", (NBTBase)(new NBTTagList()));
                } else {
                    this.removeNbt("ench");
                }
            }

            this.make().setItemMeta(this.meta());
            return this;
        }
    }

    public ItemBuilder clearFlags(ItemFlag... flags) {
        if (flags == null || flags.length == 0) {
            flags = ItemFlag.values();
        }

        this.meta().removeItemFlags(flags);
        this.make().setItemMeta(this.meta());
        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        this.meta().addItemFlags(flags);
        this.make().setItemMeta(this.meta());
        return this;
    }

    public Set<ItemFlag> flags() {
        return this.meta().getItemFlags();
    }

    public ItemBuilder persistent(boolean value) {
        this.make().setItemMeta(this.meta());
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        this.meta().spigot().setUnbreakable(unbreakable);
        this.make().setItemMeta(this.meta());
        return this;
    }

    public ItemBuilder metaEnchantment(Enchantment enchantment, int level) {
        if (this.meta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta)this.meta();
            storageMeta.addStoredEnchant(enchantment, level, true);
            this.make().setItemMeta(this.meta());
        }

        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        this.make().addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment) {
        this.make().addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder enchantments(Enchantment[] enchantments, int level) {
        this.make().getEnchantments().clear();
        Enchantment[] var3 = enchantments;
        int var4 = enchantments.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Enchantment enchantment = var3[var5];
            this.make().addUnsafeEnchantment(enchantment, level);
        }

        return this;
    }

    public ItemBuilder enchantments(Enchantment[] enchantments) {
        this.make().getEnchantments().clear();
        Enchantment[] var2 = enchantments;
        int var3 = enchantments.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Enchantment enchantment = var2[var4];
            this.make().addUnsafeEnchantment(enchantment, 1);
        }

        return this;
    }

    public ItemBuilder clearEnchantment(Enchantment enchantment) {
        if (this.meta().hasEnchant(enchantment)) {
            this.meta().removeEnchant(enchantment);
        }

        return this;
    }

    public ItemBuilder clearEnchantments() {
        this.make().getEnchantments().clear();
        return this;
    }

    public Map<Enchantment, Integer> enchantments() {
        return this.make().getEnchantments();
    }

    public ItemBuilder clearLore(String lore) {
        if (this.meta().getLore().contains(lore)) {
            this.meta().getLore().remove(lore);
        }

        this.make().setItemMeta(this.meta());
        return this;
    }

    public ItemBuilder clearLores() {
        if (this.meta().getLore() != null) {
            this.meta().getLore().clear();
        }

        this.make().setItemMeta(this.meta());
        return this;
    }

    public ItemBuilder effect(PotionEffect potionEffect, boolean overwrite) {
        if (this.meta() instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta)this.meta();
            potionMeta.addCustomEffect(potionEffect, overwrite);
            this.make().setItemMeta(potionMeta);
        }

        return this;
    }

    public ItemBuilder color(Color color) {
        if (this.make().getType() == Material.LEATHER_HELMET || this.make().getType() == Material.LEATHER_CHESTPLATE || this.make().getType() == Material.LEATHER_LEGGINGS || this.make().getType() == Material.LEATHER_BOOTS) {
            LeatherArmorMeta meta = (LeatherArmorMeta)this.meta();
            meta.setColor(color);
            this.make().setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder color(DyeColor color) {
        if (this.make().getType() == Material.BANNER) {
            BannerMeta meta = (BannerMeta)this.meta();
            meta.setBaseColor(color);
            this.make().setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder clearColor() {
        if (this.make().getType() == Material.LEATHER_HELMET || this.make().getType() == Material.LEATHER_CHESTPLATE || this.make().getType() == Material.LEATHER_LEGGINGS || this.make().getType() == Material.LEATHER_BOOTS) {
            LeatherArmorMeta meta = (LeatherArmorMeta)this.meta();
            meta.setColor((Color)null);
            this.make().setItemMeta(meta);
        }

        if (this.make().getType() == Material.BANNER) {
            BannerMeta meta = (BannerMeta)this.meta();
            meta.setBaseColor((DyeColor)null);
            this.make().setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder skullOwner(String name) {
        if (this.make().getType() == Material.SKULL_ITEM && this.make().getDurability() == 3) {
            SkullMeta skullMeta = (SkullMeta)this.meta();
            skullMeta.setOwner(name);
            this.make().setItemMeta(this.meta());
        }

        return this;
    }

    public ItemBuilder skull(Player player) {
        if (this.make().getType() == Material.SKULL_ITEM && this.make().getDurability() == 3) {
            SkullMeta skullMeta = (SkullMeta)this.meta();
            skullMeta.setOwner("CustomHead");

            try {
                GameProfile playerProfile = ((CraftPlayer)player).getHandle().getProfile();
                GameProfile gameProfile = new GameProfile(UUID.randomUUID(), (String)null);
                gameProfile.getProperties().putAll("textures", playerProfile.getProperties().get("textures"));
                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, gameProfile);
                this.make().setItemMeta(skullMeta);
            } catch (NoSuchFieldException | IllegalAccessException var6) {
                var6.printStackTrace();
            }
        }

        return this;
    }

    public ItemBuilder skull(String value, String signature) {
        if (this.make().getType() == Material.SKULL_ITEM && this.make().getDurability() == 3) {
            SkullMeta skullMeta = (SkullMeta)this.meta();
            skullMeta.setOwner("CustomHead");

            try {
                GameProfile gameProfile = new GameProfile(UUID.randomUUID(), (String)null);
                gameProfile.getProperties().put("textures", new Property("textures", value, signature));
                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, gameProfile);
                this.make().setItemMeta(skullMeta);
            } catch (NoSuchFieldException | IllegalAccessException var6) {
                var6.printStackTrace();
            }
        }

        return this;
    }

    public ItemBuilder skullUrl(String id) {
        if (this.make().getType() == Material.SKULL_ITEM && this.make().getDurability() == 3) {
            SkullMeta skullMeta = (SkullMeta)this.meta();
            skullMeta.setOwner("CustomHead");
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), (String)null);
            byte[] encodedData;
            if (!id.startsWith("http://") && !id.startsWith("https://")) {
                encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/%s\"}}}", id).getBytes());
            } else {
                encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", id).getBytes());
            }

            gameProfile.getProperties().put("textures", new Property("textures", new String(encodedData), (String)null));
            Field profileField = null;

            try {
                profileField = skullMeta.getClass().getDeclaredField("profile");
            } catch (SecurityException | NoSuchFieldException var8) {
                var8.printStackTrace();
            }

            profileField.setAccessible(true);

            try {
                profileField.set(skullMeta, gameProfile);
            } catch (IllegalAccessException | IllegalArgumentException var7) {
                var7.printStackTrace();
            }

            this.make().setItemMeta(skullMeta);
        }

        return this;
    }

    private void nbt(Consumer<NBTTagCompound> consumer) {
        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(this.make());
        NBTTagCompound compound = nmsCopy.hasTag() ? nmsCopy.getTag() : new NBTTagCompound();
        consumer.accept(compound);
        nmsCopy.setTag(compound);
        this.meta = CraftItemStack.asBukkitCopy(nmsCopy).getItemMeta();
        this.make().setItemMeta(this.meta());
    }

    private <T> T nbt(Function<NBTTagCompound, T> function) {
        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(this.make());
        NBTTagCompound compound = nmsCopy.hasTag() ? nmsCopy.getTag() : new NBTTagCompound();
        return function.apply(compound);
    }

    public boolean hasNbt(String tag) {
        return (Boolean)this.nbt((compound) -> {
            return compound.hasKey(tag);
        });
    }

    public void removeNbt(String tag) {
        this.nbt((compound) -> {
            compound.remove(tag);
        });
    }

    public ItemBuilder nbt(String tag, NBTBase value) {
        this.nbt((compound) -> {
            compound.set(tag, value);
        });
        return this;
    }

    public ItemBuilder nbt(String tag, int value) {
        this.nbt((compound) -> {
            compound.setInt(tag, value);
        });
        return this;
    }

    public ItemBuilder nbt(String tag, boolean value) {
        this.nbt((compound) -> {
            compound.setBoolean(tag, value);
        });
        return this;
    }

    public ItemBuilder nbt(String tag, long value) {
        this.nbt((compound) -> {
            compound.setLong(tag, value);
        });
        return this;
    }

    public ItemBuilder nbt(String tag, String value) {
        this.nbt((compound) -> {
            compound.setString(tag, value);
        });
        return this;
    }

    public String nbtString(String tag) {
        return (String)this.nbt((compound) -> {
            return compound.hasKey(tag) ? compound.getString(tag) : null;
        });
    }

    public Integer nbtInt(String tag) {
        return (Integer)this.nbt((compound) -> {
            return compound.hasKey(tag) ? compound.getInt(tag) : null;
        });
    }

    public Long nbtLong(String tag) {
        return (Long)this.nbt((compound) -> {
            return compound.hasKey(tag) ? compound.getLong(tag) : null;
        });
    }

    public Double nbtDouble(String tag) {
        return (Double)this.nbt((compound) -> {
            return compound.hasKey(tag) ? compound.getDouble(tag) : null;
        });
    }

    public Boolean nbtBoolean(String tag) {
        return (Boolean)this.nbt((compound) -> {
            return compound.hasKey(tag) ? compound.getBoolean(tag) : null;
        });
    }

    public NBTTagList nbtList(String tag) {
        return (NBTTagList)this.nbt((compound) -> {
            NBTBase base = compound.get(tag);
            return base instanceof NBTTagList ? (NBTTagList)base : new NBTTagList();
        });
    }

    public NBTTagCompound nbt() {
        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(this.make());
        return nmsCopy.hasTag() ? nmsCopy.getTag() : new NBTTagCompound();
    }

    public ItemMeta meta() {
        return this.meta;
    }

    public ItemStack make() {
        return this.stack;
    }

    public ItemBuilder clone() {
        return new ItemBuilder(this.make().clone());
    }
}
