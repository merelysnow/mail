package com.github.merelysnow.mail.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InventoryUtils {
    public static int getRow(int slot) {
        return (slot + 9) / 9 - 1;
    }

    public static int getSlot(int row, int column) {
        return (row - 1) * 9 + column - 1;
    }

    public static int getColumn(int slot) {
        return slot - 9 * getRow(slot);
    }

    public static String serializeContents(ItemStack[] contents) {
        if (contents == null) {
            return null;
        } else {
            BukkitObjectOutputStream dataOutput = null;

            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Throwable var3 = null;

                try {
                    Object var4;
                    try {
                        dataOutput = new BukkitObjectOutputStream(outputStream);
                        dataOutput.writeInt(contents.length);

                        for(int i = 0; i < contents.length; ++i) {
                            dataOutput.writeInt(i);
                            dataOutput.writeObject(contents[i]);
                        }

                        var4 = Base64Coder.encodeLines(outputStream.toByteArray());
                        return (String)var4;
                    } catch (Throwable var28) {
                        var4 = var28;
                        var3 = var28;
                        throw var28;
                    }
                } finally {
                    if (outputStream != null) {
                        if (var3 != null) {
                            try {
                                outputStream.close();
                            } catch (Throwable var27) {
                                var3.addSuppressed(var27);
                            }
                        } else {
                            outputStream.close();
                        }
                    }

                }
            } catch (Exception var30) {
                var30.printStackTrace();
            } finally {
                if (dataOutput != null) {
                    try {
                        dataOutput.close();
                    } catch (IOException var26) {
                        var26.printStackTrace();
                    }
                }

            }

            return null;
        }
    }

    public static ItemStack[] deserializeContents(String str) {
        if (str != null && !str.isEmpty()) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(str));

            try {
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                Throwable var3 = null;

                try {
                    int size = dataInput.readInt();
                    ItemStack[] contents = new ItemStack[size];

                    for(int i = 0; i < size; ++i) {
                        try {
                            contents[dataInput.readInt()] = (ItemStack)dataInput.readObject();
                        } catch (ClassNotFoundException | IOException var32) {
                            var32.printStackTrace();
                        }
                    }

                    ItemStack[] var37 = contents;
                    return var37;
                } catch (Throwable var33) {
                    var3 = var33;
                    throw var33;
                } finally {
                    if (dataInput != null) {
                        if (var3 != null) {
                            try {
                                dataInput.close();
                            } catch (Throwable var31) {
                                var3.addSuppressed(var31);
                            }
                        } else {
                            dataInput.close();
                        }
                    }

                }
            } catch (IOException var35) {
                var35.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException var30) {
                    var30.printStackTrace();
                }

            }

            return null;
        } else {
            return null;
        }
    }

    public static Inventory copy(Inventory inventory) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, inventory.getSize(), inventory.getTitle());
        ItemStack[] orginal = inventory.getContents();
        ItemStack[] clone = new ItemStack[orginal.length];

        for(int i = 0; i < orginal.length; ++i) {
            if (orginal[i] != null) {
                clone[i] = orginal[i].clone();
            }
        }

        inv.setContents(clone);
        return inv;
    }

    public static boolean fits(Inventory inventory, ItemStack... stacks) {
        Inventory clonedInventory = copy(inventory);
        ItemStack[] clone = new ItemStack[stacks.length];

        for(int i = 0; i < stacks.length; ++i) {
            if (stacks[i] != null) {
                clone[i] = stacks[i].clone();
            }
        }

        return clonedInventory.addItem(clone).isEmpty();
    }

    public static int removeItems(Inventory inventory, ItemStack item, int amount) {
        int removedItems = 0;

        for(int slot = 0; slot < inventory.getSize(); ++slot) {
            ItemStack targetItem = inventory.getItem(slot);
            if (targetItem != null && isSimilar(item, targetItem)) {
                if (amount == -1) {
                    removedItems += targetItem.getAmount();
                    inventory.setItem(slot, (ItemStack)null);
                } else {
                    if (amount < targetItem.getAmount()) {
                        removedItems += amount;
                        targetItem.setAmount(targetItem.getAmount() - amount);
                        break;
                    }

                    removedItems += targetItem.getAmount();
                    amount -= targetItem.getAmount();
                    inventory.setItem(slot, (ItemStack)null);
                }
            }
        }

        return removedItems;
    }

    public static int removeItemsByMaterial(Inventory inventory, Material item, int amount) {
        int removedItems = 0;

        for(int slot = 0; slot < inventory.getSize(); ++slot) {
            ItemStack targetItem = inventory.getItem(slot);
            if (targetItem != null && targetItem.getType() != item) {
                if (amount == -1) {
                    removedItems += targetItem.getAmount();
                    inventory.setItem(slot, (ItemStack)null);
                } else {
                    if (amount < targetItem.getAmount()) {
                        removedItems += amount;
                        targetItem.setAmount(targetItem.getAmount() - amount);
                        break;
                    }

                    removedItems += targetItem.getAmount();
                    amount -= targetItem.getAmount();
                    inventory.setItem(slot, (ItemStack)null);
                }
            }
        }

        return removedItems;
    }

    public static int countItems(Inventory inventory, ItemStack item) {
        int sum = 0;
        ItemStack[] var3 = inventory.getContents();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            ItemStack itemTarget = var3[var5];
            if (isSimilar(itemTarget, item)) {
                sum += itemTarget.getAmount();
            }
        }

        return sum;
    }

    public static int countItems(Inventory inventory, Material material) {
        int sum = 0;
        ItemStack[] var3 = inventory.getContents();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            ItemStack itemTarget = var3[var5];
            if (itemTarget != null && itemTarget.getType() == material) {
                sum += itemTarget.getAmount();
            }
        }

        return sum;
    }

    public static boolean subtractOne(Player player, ItemStack stack) {
        ItemStack[] contents = player.getInventory().getContents();

        for(int i = 0; i < contents.length; ++i) {
            ItemStack content = player.getInventory().getContents()[i];
            if (isSimilar(content, stack)) {
                if (content.getAmount() > 1) {
                    content.setAmount(content.getAmount() - 1);
                    contents[i] = content;
                } else {
                    contents[i] = null;
                }

                player.getInventory().setContents(contents);
                player.updateInventory();
                return true;
            }
        }

        return false;
    }

    public static void subtractOneOnHand(PlayerInteractEvent event) {
        subtractOneOnHand(event.getPlayer());
    }

    public static void subtractOneOnHand(Player player) {
        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            ItemStack item = player.getItemInHand();
            int amount = item.getAmount();
            if (amount > 1) {
                item.setAmount(amount - 1);
                player.setItemInHand(item);
            } else {
                item.setAmount(0);
                item.setType(Material.AIR);
                item.setData(new MaterialData(Material.AIR));
                item.setItemMeta((ItemMeta)null);
                player.setItemInHand(new ItemStack(Material.AIR));
            }

        }
    }

    public static void give(Player player, ItemStack... item) {
        Map<Integer, ItemStack> left = player.getInventory().addItem(item);
        player.updateInventory();
        if (left != null && !left.isEmpty()) {
            Iterator var3 = left.values().iterator();

            while(var3.hasNext()) {
                ItemStack l = (ItemStack)var3.next();
                player.getWorld().dropItemNaturally(player.getLocation(), l);
            }
        }

    }

    public static Map<Integer, ItemStack> addAllItems(Inventory inventory, boolean simulate, ItemStack... items) {
        Inventory fakeInventory = Bukkit.getServer().createInventory((InventoryHolder)null, inventory.getType());
        fakeInventory.setContents(inventory.getContents());
        Map<Integer, ItemStack> overFlow = addOversizedItems(fakeInventory, items);
        if (overFlow.isEmpty()) {
            if (!simulate) {
                addOversizedItems(inventory, items);
            }

            return null;
        } else {
            return addOversizedItems(fakeInventory, items);
        }
    }

    private static Map<Integer, ItemStack> addOversizedItems(Inventory inventory, ItemStack... items) {
        Map<Integer, ItemStack> leftover = new HashMap();
        ItemStack[] combined = new ItemStack[items.length];
        ItemStack[] var4 = items;
        int var5 = items.length;

        int maxAmount;
        int firstFree;
        for(maxAmount = 0; maxAmount < var5; ++maxAmount) {
            ItemStack item = var4[maxAmount];
            if (item != null && item.getAmount() >= 1) {
                for(firstFree = 0; firstFree < combined.length; ++firstFree) {
                    if (combined[firstFree] == null) {
                        combined[firstFree] = item.clone();
                        break;
                    }

                    if (isSimilar(combined[firstFree], item)) {
                        combined[firstFree].setAmount(combined[firstFree].getAmount() + item.getAmount());
                        break;
                    }
                }
            }
        }

        label55:
        for(int i = 0; i < combined.length; ++i) {
            ItemStack item = combined[i];
            if (item != null && item.getType() != Material.AIR) {
                while(true) {
                    while(true) {
                        maxAmount = Math.max(0, item.getType().getMaxStackSize());
                        int firstPartial = firstPartial(inventory, item, maxAmount);
                        if (firstPartial == -1) {
                            firstFree = inventory.firstEmpty();
                            if (firstFree == -1) {
                                leftover.put(i, item);
                                continue label55;
                            }

                            if (item.getAmount() <= maxAmount) {
                                inventory.setItem(firstFree, item);
                                continue label55;
                            }

                            ItemStack stack = item.clone();
                            stack.setAmount(maxAmount);
                            inventory.setItem(firstFree, stack);
                            item.setAmount(item.getAmount() - maxAmount);
                        } else {
                            ItemStack partialItem = inventory.getItem(firstPartial);
                            int amount = item.getAmount();
                            int partialAmount = partialItem.getAmount();
                            if (amount + partialAmount <= maxAmount) {
                                partialItem.setAmount(amount + partialAmount);
                                continue label55;
                            }

                            partialItem.setAmount(maxAmount);
                            item.setAmount(amount + partialAmount - maxAmount);
                        }
                    }
                }
            }
        }

        return leftover;
    }

    private static int firstPartial(Inventory inventory, ItemStack item, int maxAmount) {
        if (item == null) {
            return -1;
        } else {
            ItemStack[] stacks = inventory.getContents();

            for(int i = 0; i < stacks.length; ++i) {
                ItemStack cItem = stacks[i];
                if (cItem != null && cItem.getAmount() < maxAmount && isSimilar(cItem, item)) {
                    return i;
                }
            }

            return -1;
        }
    }

    private static boolean isSimilar(ItemStack source, ItemStack anotherStack) {
        if (source != null && anotherStack != null) {
            if (source.getTypeId() == anotherStack.getTypeId() && source.getDurability() == anotherStack.getDurability()) {
                boolean sourceHasMeta = source.hasItemMeta();
                boolean anotherStackHasMeta = anotherStack.hasItemMeta();
                return !sourceHasMeta && !anotherStackHasMeta ? true : Bukkit.getItemFactory().equals(source.getItemMeta(), anotherStack.getItemMeta());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private InventoryUtils() {
    }
}
