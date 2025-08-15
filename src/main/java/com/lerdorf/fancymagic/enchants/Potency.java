package com.lerdorf.fancymagic.enchants;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import com.lerdorf.fancymagic.FancyMagic;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class Potency implements FancyEnchant {
	public static final Key KEY = Key.key("fancymagic:potency");

    private final int anvilCost, weight, maxLevel;
    private final EnchantmentRegistryEntry.EnchantmentCost minimumCost;
    private final EnchantmentRegistryEntry.EnchantmentCost maximumCost;
    private final Set<TagEntry<ItemType>> supportedItemTags = new HashSet<>();
    private final float potencyPerLevel;
    private final Set<TagKey<Enchantment>> enchantTagKeys = new HashSet<>();
    private final Set<EquipmentSlotGroup> activeSlots = new HashSet<>();

   public Potency(
            int anvilCost,
            int weight,
            EnchantmentRegistryEntry.EnchantmentCost minimumCost,
            EnchantmentRegistryEntry.EnchantmentCost maximumCost,
            Collection<TagKey<Enchantment>> enchantTagKeys,
            Collection<TagEntry<ItemType>> supportedItemTags,
            Collection<EquipmentSlotGroup> activeSlots,
            int maxLevel,
            float potencyPerLevel
    ) {
        this.anvilCost = anvilCost;
        this.weight = weight;
        this.minimumCost = minimumCost;
        this.maximumCost = maximumCost;
        this.supportedItemTags.addAll(supportedItemTags);
        this.maxLevel = maxLevel;
        this.potencyPerLevel = potencyPerLevel;
        this.activeSlots.addAll(activeSlots);
        this.enchantTagKeys.addAll(enchantTagKeys);
    }

    @Override
    public @NotNull Key getKey() {
        return KEY;
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.translatable("fancymagic.enchant.potency", "Potency");
    }

    @Override
    public int getAnvilCost() {
        return anvilCost;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    public float getPotencyPerLevel() {
        return potencyPerLevel;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public EnchantmentRegistryEntry.@NotNull EnchantmentCost getMinimumCost() {
        return minimumCost;
    }

    @Override
    public EnchantmentRegistryEntry.@NotNull EnchantmentCost getMaximumCost() {
        return maximumCost;
    }

    @Override
    public @NotNull Iterable<EquipmentSlotGroup> getActiveSlots() {
        return activeSlots;
    }

    @Override
    public @NotNull TagKey<ItemType> getTagForSupportedItems() {
        return TagKey.create(RegistryKey.ITEM, Key.key("fancymagic:potency_enchantable"));
    }

    @Override
    public @NotNull Set<TagEntry<ItemType>> getSupportedItems() {
        return supportedItemTags;
    }

    @Override
    public @NotNull Set<TagKey<Enchantment>> getEnchantTagKeys() {
        return Collections.unmodifiableSet(enchantTagKeys);
    }

    public static Potency create() {
        Potency potencyEnchant = new Potency(
                1, // anvil cost
                1, // weight
                EnchantmentRegistryEntry.EnchantmentCost.of( // minimum cost
                        40, // minimum cost base
                        3 // additional per level
                ),
                EnchantmentRegistryEntry.EnchantmentCost.of( // maximum cost
                        65, // maximum cost base
                        1 // additional per level
                ),
                FancyEnchant.getEnchantmentTagKeysFromList(
                        List.of("#in_enchanting_table") // enchantment tags
                ),
                FancyEnchant.getItemTagEntriesFromList(
                        List.of( // supported item tags
                        		"#minecraft:enchantable/sword"
                        )),
        		FancyEnchant.getEquipmentSlotGroups(
                        List.of( // active slots
                                "MAINHAND"
                        )),
                5, // max level
                0.1f // potency per level
        );

            FancyMagic.ENCHANTS.put(Potency.KEY, potencyEnchant);

        return potencyEnchant;
    }
}
