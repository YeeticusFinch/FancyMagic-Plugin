package com.lerdorf.fancymagic;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import com.lerdorf.fancymagic.enchants.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FancyMagicBootstrap implements PluginBootstrap {

	private final Logger logger = LoggerFactory.getLogger("FancyMagic");
	@Override
	public void bootstrap(BootstrapContext context) {
		logger.info("=== BOOTSTRAP STARTING ===");
		logger.info("Bootstrap method called successfully!");
		 try {
			 logger.info("Initializing FancyEnchant...");
	            FancyEnchant.init();
	        } catch (IOException e) {
	        	logger.error("Failed to initialize FancyEnchant", e);
	            throw new RuntimeException(e);
	        }

	        Collection<FancyEnchant> fancyEnchants = FancyMagic.ENCHANTS.values();

	        logger.info("Registering supported item tags");
	        context.getLifecycleManager().registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ITEM).newHandler((event) -> {
	            for (FancyEnchant enchant : fancyEnchants) {
	                logger.info("Registering item tag {}", enchant.getTagForSupportedItems().key());
	                event.registrar().addToTag(
	                        ItemTypeTagKeys.create(enchant.getTagForSupportedItems().key()),
	                        enchant.getSupportedItems()
	                );
	            }
	        }));

	        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.freeze().newHandler(event -> {
	            for (FancyEnchant enchant : fancyEnchants) {
	                logger.info("Registering enchantment {}", enchant.getKey());
	                event.registry().register(TypedKey.create(RegistryKey.ENCHANTMENT, enchant.getKey()), enchantment -> {
	                    enchantment.description(enchant.getDescription());
	                    enchantment.anvilCost(enchant.getAnvilCost());
	                    enchantment.maxLevel(enchant.getMaxLevel());
	                    enchantment.weight(enchant.getWeight());
	                    enchantment.minimumCost(enchant.getMinimumCost());
	                    enchantment.maximumCost(enchant.getMaximumCost());
	                    enchantment.activeSlots(enchant.getActiveSlots());
	                    enchantment.supportedItems(event.getOrCreateTag(enchant.getTagForSupportedItems()));
	                });
	            }
	        }));

	        context.getLifecycleManager().registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ENCHANTMENT).newHandler((event) -> {
	            for (FancyEnchant enchant : fancyEnchants) {
	                enchant.getEnchantTagKeys().forEach(enchantmentTagKey -> {
	                    event.registrar().addToTag(enchantmentTagKey, Set.of(enchant.getTagEntry()));
	                });
	            }
	        }));

	    }

}
