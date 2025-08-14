package com.lerdorf.fancymagic.enchants;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.lerdorf.fancymagic.FancyMagic;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public interface FancyEnchant {

    @NotNull
    Key getKey();

    @NotNull
    Component getDescription();

    int getAnvilCost();

    int getMaxLevel();

    int getWeight();

    @NotNull
    EnchantmentRegistryEntry.EnchantmentCost getMinimumCost();

    @NotNull
    EnchantmentRegistryEntry.EnchantmentCost getMaximumCost();

    @NotNull
    Iterable<EquipmentSlotGroup> getActiveSlots();

    @NotNull
    Set<TagEntry<ItemType>> getSupportedItems();

    @NotNull
    Set<TagKey<Enchantment>> getEnchantTagKeys();

    @NotNull
    default TagKey<ItemType> getTagForSupportedItems() {
        return TagKey.create(RegistryKey.ITEM, Key.key( getKey().asString() + "_enchantable"));
    }

    @NotNull
    default TagEntry<Enchantment> getTagEntry() {
        return TagEntry.valueEntry(TypedKey.create(RegistryKey.ENCHANTMENT, getKey()));
    }

    static @Nullable ItemStack findFirstWithEnchant(
            @NotNull EntityEquipment equipment,
            @NotNull Enchantment enchantment
    ) {

        Set<EquipmentSlotGroup> equipmentSlotGroups = enchantment.getActiveSlotGroups();

        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.HAND) || equipmentSlotGroups.contains(EquipmentSlotGroup.MAINHAND)) {
            if (equipment.getItemInMainHand().getEnchantmentLevel(enchantment) > 0) return equipment.getItemInMainHand();
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.HAND) || equipmentSlotGroups.contains(EquipmentSlotGroup.OFFHAND)) {
            if (equipment.getItemInOffHand().getEnchantmentLevel(enchantment) > 0) return equipment.getItemInOffHand();
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.ARMOR) || equipmentSlotGroups.contains(EquipmentSlotGroup.HEAD)) {
            ItemStack helmet = equipment.getHelmet();
            if (helmet != null && helmet.getEnchantmentLevel(enchantment) > 0) return helmet;
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.ARMOR) || equipmentSlotGroups.contains(EquipmentSlotGroup.CHEST)) {
            ItemStack chestplate = equipment.getChestplate();
            if (chestplate != null && chestplate.getEnchantmentLevel(enchantment) > 0) return chestplate;
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.ARMOR) || equipmentSlotGroups.contains(EquipmentSlotGroup.LEGS)) {
            ItemStack leggings = equipment.getLeggings();
            if (leggings != null && leggings.getEnchantmentLevel(enchantment) > 0) return leggings;

        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.ARMOR) || equipmentSlotGroups.contains(EquipmentSlotGroup.FEET)) {
            ItemStack boots = equipment.getBoots();
            if (boots != null && boots.getEnchantmentLevel(enchantment) > 0) return boots;
        }
        return null;
    }

    
    public static Set<TagEntry<ItemType>> getItemTagEntriesFromList(@NotNull List<String> tags) {
        Set<TagEntry<ItemType>> supportedItemTags = new HashSet<>();
        for (String itemTag : tags) {
            if (itemTag == null) continue;
            if (itemTag.startsWith("#")) {
                itemTag = itemTag.substring(1);
                try {
                    Key key = Key.key(itemTag);
                    TagKey<ItemType> tagKey = ItemTypeTagKeys.create(key);
                    TagEntry<ItemType> tagEntry = TagEntry.tagEntry(tagKey);
                    supportedItemTags.add(tagEntry);
                } catch (IllegalArgumentException e) {
                }
                continue;
            }
            try {
                Key key = Key.key(itemTag);
                TypedKey<ItemType> typedKey = TypedKey.create(RegistryKey.ITEM, key);
                TagEntry<ItemType> tagEntry = TagEntry.valueEntry(typedKey);
                supportedItemTags.add(tagEntry);
            } catch (IllegalArgumentException | NullPointerException e) {
            }
        }
        return supportedItemTags;
    }
    
    public static Set<TagKey<Enchantment>> getEnchantmentTagKeysFromList(@NotNull List<String> tags) {
        Set<TagKey<Enchantment>> enchantTagKeys = new HashSet<>();
        for (String enchantmentTag : tags) {
            if (enchantmentTag == null) continue;
            if (enchantmentTag.startsWith("#")) {
                enchantmentTag = enchantmentTag.substring(1);
                try {
                    Key key = Key.key(enchantmentTag);
                    TagKey<Enchantment> tagKey = EnchantmentTagKeys.create(key);
                    enchantTagKeys.add(tagKey);
                } catch (IllegalArgumentException ignored) {
                }
                continue;
            }
            try {
                Key key = Key.key(enchantmentTag);
                TypedKey<Enchantment> typedKey = TypedKey.create(RegistryKey.ENCHANTMENT, key);
                TagKey<Enchantment> tagKey = EnchantmentTagKeys.create(key);
                enchantTagKeys.add(tagKey);
            } catch (IllegalArgumentException | NullPointerException ignored) {
            }
        }
        return enchantTagKeys;
    }
    
    public static Set<EquipmentSlotGroup> getEquipmentSlotGroups(@NotNull List<String> slots) {
        Set<EquipmentSlotGroup> equipmentSlotGroups = new HashSet<>();
        for (String slot : slots) {
            EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.getByName(slot);
            if (equipmentSlotGroup == null) {
                continue;
            }
            equipmentSlotGroups.add(equipmentSlotGroup);
        }
        return equipmentSlotGroups;
    }
    

    public static void init() throws IOException {
        if (FancyMagic.initialized) {
            return;
        }
        FancyMagic.initialized = true;

        Spellbound.create();
        // add other enchants here
    }
}