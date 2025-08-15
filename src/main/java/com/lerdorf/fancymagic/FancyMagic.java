package com.lerdorf.fancymagic;

import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Lectern;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.block.impl.CraftLectern;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.C;
import org.joml.Quaternionf;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.lerdorf.fancymagic.SpellBookMenu.SpellData;
import com.lerdorf.fancymagic.enchants.FalseLife;
import com.lerdorf.fancymagic.enchants.FancyEnchant;
import com.lerdorf.fancymagic.enchants.Potency;
import com.lerdorf.fancymagic.enchants.QuickCast;
import com.lerdorf.fancymagic.enchants.Repelling;
import com.lerdorf.fancymagic.enchants.SpellStealer;
import com.lerdorf.fancymagic.enchants.SpellTwinning;
import com.lerdorf.fancymagic.enchants.Spellbound;

import de.tr7zw.nbtapi.NBTItem;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.registry.RegistryKey;

import org.joml.Vector3f;
import org.joml.AxisAngle4f;

import net.kyori.adventure.key.Key;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.world.item.crafting.Recipe;

import org.bukkit.block.data.Bisected;

public class FancyMagic extends JavaPlugin implements Listener, TabExecutor {

    public static final Map<Key, FancyEnchant> ENCHANTS = new HashMap<>();
    public static boolean initialized = false;
	
	private File configFile;
	private Map<String, Object> configValues;
	
	public static Map<UUID, List<LivingEntity>> minions = new HashMap<>();

	public static Plugin plugin;
	
	private Set<UUID> falseLifePlayers = new HashSet<>();
	private Random random = new Random();

	public void loadConfig() {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // <-- use block style
		options.setIndent(2);
		options.setPrettyFlow(true);

		File pluginFolder = this.getDataFolder();
		if (!pluginFolder.exists())
			pluginFolder.mkdirs();

		configFile = new File(pluginFolder, "config.yml");

		Yaml yaml = new Yaml(options);

		// If file doesn't exist, create it with defaults
		if (!configFile.exists()) {
			configValues = new HashMap<>();
			// configValues.put("requireBothHandsEmpty", requireBothHandsEmpty);
			saveConfig(); // Save default config
		}

		try {
			String yamlAsString = Files.readString(configFile.toPath());
			configValues = (Map<String, Object>) yaml.load(yamlAsString);
			if (configValues == null)
				configValues = new HashMap<>();
		} catch (Exception e) {
			e.printStackTrace();
			configValues = new HashMap<>();
		}

		// Now parse and update values
		/*
		 * try { if (configValues.containsKey("requireBothHandsEmpty"))
		 * requireBothHandsEmpty = (boolean)configValues.get("requireBothHandsEmpty"); }
		 * catch (Exception e) { e.printStackTrace(); }
		 * configValues.put("requireBothHandsEmpty", requireBothHandsEmpty);
		 */

		saveConfig(); // Ensure config is up to date
	}

	public void saveConfig() {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // <-- use block style
		options.setIndent(2);
		options.setPrettyFlow(true);

		Yaml yaml = new Yaml(options);
		try (FileWriter writer = new FileWriter(configFile)) {
			yaml.dump(configValues, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public SpellBookMenu sbMenu;

	public static Collection<NamespacedKey> recipes = new ArrayList<>();
	
	@Override
	public void onEnable() {
		plugin = this;
		
		// Manual bootstrap if automatic bootstrap didn't work
	    if (!FancyMagic.initialized) {
	        getLogger().warning("Bootstrap didn't run automatically, running manually...");
	        try {
	            // Try to run bootstrap manually during plugin enable
	            FancyMagicBootstrap bootstrap = new FancyMagicBootstrap();
	            FancyEnchant.init();
	            getLogger().info("Manual bootstrap completed");
	        } catch (Exception e) {
	            getLogger().severe("Failed to run manual bootstrap: " + e.getMessage());
	            e.printStackTrace();
	        }
	    } else {
	        getLogger().info("Bootstrap already completed during server startup");
	    }
	    
	    Bukkit.getScheduler().runTaskLater(this, () -> {
	    	for (Key key : ENCHANTS.keySet()) {
		        Enchantment enchant = Bukkit.getRegistry(Enchantment.class).get(key);
		        if (enchant != null) {
		            getLogger().info("✓ " + key.asMinimalString() + " enchantment found in registry!");
		        } else {
		            getLogger().severe("✗ " + key.asMinimalString() + " enchantment NOT found in registry!");
		        }
	    	}
	    }, 20L); // Check 1 second after plugin enables
		
		getServer().getPluginManager().registerEvents(this, this);
		
		this.registerCommand("magic", "Gives you a scroll", new FancyMagicCommand());
		//this.getCommand("magic").setExecutor(this);

		//this.getCommand("wall").setExecutor(this);

		loadConfig();
		saveConfig();
		SpellManager.startMainLoop();
		
		registerRecipes();
		sbMenu = new SpellBookMenu(this);

		/*
		 * new BukkitRunnable() {
		 * 
		 * @Override public void run() {
		 * 
		 * } }.runTaskTimer(this, 0L, 1L); // Run every 1 tick
		 */

		getLogger().info("FancyMagic enabled!");
		
	}

	@Override
	public void onDisable() {
		getLogger().info("FancyMagic disabled!");
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.getPlayer().discoverRecipes(recipes);
	}

    private void registerRecipes() {
    	
    	NamespacedKey key = new NamespacedKey(this, "spellbook");
        ShapedRecipe recipe = new ShapedRecipe(key, Items.SPELLBOOK);
        recipe.shape(
        		" S",
        		" S",
        		"LS"
        		);
        recipe.setIngredient('S', Items.SCROLL);
        recipe.setIngredient('L', Material.LEATHER);
        recipes.add(key);
        Bukkit.addRecipe(recipe);
        
        Items.registerRecipes();
        
        for (SpellType spellType : Spell.spellTypes)
        	spellType.addRecipe();
        
    }
    
    public boolean isFocus(ItemStack item) {
    	if (item == null) return false;
    	ItemMeta meta = item.getItemMeta();
    	return meta != null && meta.getPersistentDataContainer().has(new NamespacedKey(FancyMagic.plugin, "focus"), PersistentDataType.INTEGER) && meta.getPersistentDataContainer().get(new NamespacedKey(FancyMagic.plugin, "focus"), PersistentDataType.INTEGER) > 0;
    }
    
    public HashMap<Player, List<Boolean>> clicks = new HashMap<>();
    public HashMap<Player, Long> lastClick = new HashMap<>();
    
    public boolean use(Player player, boolean rightClick) {
    	
    	ItemStack item = player.getEquipment().getItemInMainHand();
    	if (item != null) {
    		ItemMeta meta = item.getItemMeta();
    		if (isFocus(item)) {
    			if (player.getEquipment().getItemInOffHand() != null && player.getEquipment().getItemInOffHand().getItemMeta() != null && player.getEquipment().getItemInOffHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(FancyMagic.plugin, "spellbook")) && player.getEquipment().getItemInOffHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(FancyMagic.plugin, "spellbook"), PersistentDataType.INTEGER) > 0) {
    				if (clicks.containsKey(player) && System.currentTimeMillis() - lastClick.get(player) <= 5) {
        				// Clicked too fast, probably the same click
    					lastClick.put(player, System.currentTimeMillis());
    					return true;
    				}
    				lastClick.put(player, System.currentTimeMillis());
    				if (!clicks.containsKey(player)) {
	    				clicks.put(player, new ArrayList<>());
	    				Bukkit.getScheduler().runTaskLater(plugin, () -> {
	    					if (clicks.containsKey(player)) {
	    						player.sendActionBar("");
								clicks.remove(player);
	    					}
						}, 30);
	    			}
					String bar = "";
					for (boolean b : clicks.get(player))
						bar += b ? "R " : "L ";
					bar += rightClick ? "R" : "L";
					clicks.get(player).add(rightClick);
					player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);
					player.sendActionBar(ChatColor.AQUA + bar);
					List<SpellData> spellData = sbMenu.loadPreparedSpells(player.getEquipment().getItemInOffHand());
					spellData.size();
					if (clicks.get(player).size() >= (spellData.size() <= 2 ? 1 : spellData.size() <= 3 ? 2 : 3)) {

	    				if (player.hasCooldown(item)) {
	    					player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1, 2);
	    					player.sendActionBar(ChatColor.YELLOW + "Cooldown");
	    					return true;
	    				}
						//Spell.getClickCombination(preparedIndex, prepared.size());
						Spell spell = sbMenu.fromClickCombination(spellData, clicks.get(player));
	    				clicks.get(player).clear();
						int cooldown = castSpell(player, spell, item);
						player.setCooldown(item, cooldown);
					}
					return true;
    			} else {
    				player.sendMessage(ChatColor.YELLOW + "Warning: Are you trying to cast a spell? You must have your spellbook in your offhand.");
    				return false;
    			}
    		}
    	}
    	
    	return false;
    }
    
    public int castSpell(Player player, Spell spell, ItemStack item) {
    	if (item != null) {
    		if (spell == null || spell.data == null) {
    			player.sendActionBar(ChatColor.RED + "Invalid Spell");
    			return 40;
    		}
    		ItemMeta meta = item.getItemMeta();
    		if (meta.getPersistentDataContainer().get(new NamespacedKey(FancyMagic.plugin, "focus"), PersistentDataType.INTEGER) > 0) {
    			NBTItem nbt = new NBTItem(item);
    			float rangeMod = nbt.getFloat("Range");
    			float cooldownMod = nbt.getFloat("Cooldown");
    			float potencyMod = nbt.getFloat("Potency");
    			player.sendActionBar(ChatColor.LIGHT_PURPLE + spell.data.name);
    			return spell.cast(player, item, rangeMod, cooldownMod, potencyMod);
    		}
    	}
    	return 40;
    }
    
    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
    	boolean rightClick = false;
    	boolean click = false;
    	
    	if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		if (event.getClickedBlock().getType() == Material.LECTERN) {
    			if (event.getPlayer().getActiveItem() != null && event.getPlayer().getActiveItem().getType() == Material.WRITTEN_BOOK) {
    				return;
    			}
    			Lectern lectern = (Lectern) event.getClickedBlock().getState();
    			ItemStack[] items = lectern.getInventory().getContents();
    			for (ItemStack item : items) {
    				if (item != null && item.getType() == Material.WRITTEN_BOOK && item.getItemMeta().hasItemModel() && item.getItemMeta().getItemModel().toString().toLowerCase().contains("fsp:spellbook")) {
    					event.getPlayer().setMetadata("spellbook_lectern", new FixedMetadataValue(plugin, event.getClickedBlock()));
    					sbMenu.openMainMenu(event.getPlayer(), item);
    					event.setCancelled(true);
    					return;
    				}
    			}
    			return;
    		}
    	}
    	
    	if (event.getAction() == Action.LEFT_CLICK_AIR|| event.getAction() == Action.LEFT_CLICK_BLOCK) {
    		rightClick = false;
    		click = true;
    	} else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		rightClick = true;
    		click = true;
    	}
    	if (click) {
	    	boolean cancelEvent = use(event.getPlayer(), rightClick);
	    	if (cancelEvent)
	    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void playerInteractEntityEvent(PlayerInteractEntityEvent event) {
    	boolean cancelEvent = use(event.getPlayer(), true);
    	if (cancelEvent)
    		event.setCancelled(true);
    	
    }
    
    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent event) {
    	boolean cancelEvent = use(event.getPlayer(), true);
    	if (cancelEvent)
    		event.setCancelled(true);
    	
    }
    
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getHitEntity() instanceof Interaction interaction && interaction.getScoreboardTags().contains("shield")) {
            interaction.addScoreboardTag("hit");
            return;
        }
    }
    
    @EventHandler
    public void entityDamage(EntityDamageEvent event) {
    	if (event.getEntity() instanceof Interaction interaction && interaction.getScoreboardTags().contains("shield")) {
    		interaction.addScoreboardTag("hit");
    		return;
    	} else if (event.getEntity().getScoreboardTags().contains("shielding")) {
    		Location loc = event.getDamageSource().getDamageLocation();
    		if (loc == null)
    			loc = event.getDamageSource().getSourceLocation();
    		if (loc != null && loc.clone().subtract(event.getEntity().getLocation()).toVector().dot(event.getEntity().getLocation().getDirection()) > 0.5f) {
    			event.setDamage(0);
    			event.setCancelled(true);
    			Collection<Entity> entities = event.getEntity().getLocation().add(event.getEntity().getLocation().getDirection()).getNearbyEntities(0, 0, 0);
    			for (Entity e : entities) {
    				if (e instanceof Interaction interaction && interaction.getScoreboardTags().contains("shield")) {
    					if (interaction.hasMetadata("owner") && interaction.getMetadata("owner").get(0).value() instanceof LivingEntity owner) {
    						interaction.addScoreboardTag("hit");
    						return;
    					}
    				}
    			}
    			return;
    		}
    	}
    }
    
    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
    	if (event.getDamager() instanceof Player p && event.getDamageSource().getDamageType() == DamageType.PLAYER_ATTACK) {
	    	boolean cancelEvent = use(p, false);
	    	if (cancelEvent) {
	    		event.setCancelled(true);
	    		return;
	    	}
    	}
    	if (event.getEntity() instanceof Interaction interaction && interaction.getScoreboardTags().contains("shield")) {
    		interaction.addScoreboardTag("hit");
    		return;
    	} else if (event.getEntity().getScoreboardTags().contains("shielding")) {
    		Location loc = event.getDamageSource().getDamageLocation();
    		if (loc == null)
    			loc = event.getDamageSource().getSourceLocation();
    		if (loc == null)
    			loc = event.getDamager().getLocation();
    		if (loc != null && loc.clone().subtract(event.getEntity().getLocation()).toVector().dot(event.getEntity().getLocation().getDirection()) > 0.5f) {
    			event.setDamage(0);
    			event.setCancelled(true);
    			Collection<Entity> entities = event.getEntity().getLocation().add(event.getEntity().getLocation().getDirection()).getNearbyEntities(0, 0, 0);
    			for (Entity e : entities) {
    				if (e instanceof Interaction interaction && interaction.getScoreboardTags().contains("shield")) {
    					if (interaction.hasMetadata("owner") && interaction.getMetadata("owner").get(0).value() instanceof LivingEntity owner) {
    						interaction.addScoreboardTag("hit");
    						return;
    					}
    				}
    			}
    			return;
    		}
    	}
    	if (event.getEntity().getScoreboardTags().contains("fire_shield") && event.getDamager().getLocation().distance(event.getEntity().getLocation()) < 6) {
    		for (String tag : event.getEntity().getScoreboardTags()) {
    			if (tag.contains("fire_shield_ticks:")) {
    				int ticks = Integer.parseInt(tag.substring(tag.indexOf(':')+1));
    				if (event.getDamager() instanceof LivingEntity le) {
    					Bukkit.getScheduler().runTaskLater(plugin, () -> {
    						DamageSource source = DamageSource.builder(DamageType.MAGIC)
        						    .withDirectEntity(event.getEntity()) // the entity causing the damage
        						    .build();
        					le.damage(ticks*0.1f, source);
        					le.setVelocity(le.getVelocity().add(le.getLocation().subtract(event.getEntity().getLocation()).toVector().normalize().multiply(0.8).add(new Vector(0, 0.5f, 0))));
            				le.setFireTicks(ticks);
						}, 2);
    					
        				event.setDamage(event.getDamage()/2);
    				}
    				break;
    			}
    		}
    	}
    	if (event.getEntity() instanceof LivingEntity victim) {
            UUID victimId = victim.getUniqueId();
            if (minions.containsKey(victimId)) {
                // Summoner was attacked
                Entity damager = event.getDamager();
                if (damager instanceof LivingEntity attacker) {
                    for (LivingEntity minion : minions.get(victimId)) {
                        if (minion.isValid()) {
                            ((Mob) minion).setTarget(attacker);
                        }
                    }
                }
            }
        }
    	if (event.getDamager() instanceof LivingEntity attacker) {
            UUID attackerId = attacker.getUniqueId();
            if (minions.containsKey(attackerId)) {
                // Summoner attacked something
                LivingEntity target = (LivingEntity) event.getEntity();
                for (LivingEntity minion : minions.get(attackerId)) {
                    if (minion.isValid()) {
                        ((Mob) minion).setTarget(target);
                    }
                }
            }
        }
    	// Check if the damager is a lightning strike
        if (event.getDamager() instanceof LightningStrike) {
            LightningStrike lightning = (LightningStrike) event.getDamager();
            float damageMod = 1;
            if (event.getEntity() instanceof LivingEntity le && le.getScoreboardTags().contains("PrimordialWard")) {
            	event.setCancelled(true);
            	damageMod = 0;
            } else if (event.getEntity() instanceof LivingEntity le && le.getScoreboardTags().contains("ElementalWard")) {
            	event.setDamage(event.getDamage()/2);
            	damageMod = 0.5f;
            }

            // Check if the lightning has an owner
            if (lightning.hasMetadata("lightningOwner") && lightning.getMetadata("lightningOwner").get(0).value() instanceof LivingEntity owner && event.getEntity() instanceof Damageable de) {
                //LivingEntity owner = (LivingEntity) lightning.getMetadata("lightningOwner").get(0).value();
            	DamageSource source = DamageSource.builder(DamageType.MAGIC)
        			    .withDirectEntity(owner) // the entity causing the damage
        			    .build();
                //event.setDamage(event.getDamage(), owner);
            	if (lightning.hasMetadata("damage")) {
            		de.damage(damageMod * lightning.getMetadata("damage").get(0).asDouble()/lightning.getLocation().distance(de.getLocation()), source);
            	} else
            		de.damage(damageMod * event.getFinalDamage(), source);
                
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
    	//ItemStack result = event.getRecipe().getResult();
    	ItemStack result = event.getInventory().getResult();
    	if (result != null && result.getType() == Material.BOOK) {
    		//Bukkit.broadcastMessage("Crafting a book");
	    	// get the items being used for the craft
	    	CraftingInventory inv = event.getInventory();
	
	        // All items in the crafting grid (includes null/air slots)
	        ItemStack[] matrix = inv.getMatrix();
	        List<ItemStack> scrolls = new ArrayList<ItemStack>();
	
	        for (int i = 0; i < matrix.length; i++) {
	            ItemStack item = matrix[i];
	            //if (item != null && item.getType() == Material.PAPER)
	            //	Bukkit.broadcastMessage(item.getItemMeta().getItemModel().asString());
	            if (item != null && item.getType() == Material.PAPER && item.getItemMeta().getItemModel().asString().toLowerCase().contains("fsp:scroll")) {
	            	scrolls.add(item);

	        		//Bukkit.broadcastMessage("Adding scroll");
	            }
	        }
	        if (scrolls.size() > 0) {
	        	//Bukkit.broadcastMessage("Preparing a spellbook");
	        	result = Items.SPELLBOOK.clone();
	        	BookMeta bmeta = (BookMeta) result.getItemMeta();
	        	Player player = ((Player)event.getView().getPlayer());
	        	bmeta.setAuthor(player.getName());
	        	bmeta.setTitle("Spellbook");
	        	result.setItemMeta(bmeta);
	        	for (ItemStack item : scrolls) {
		        	NBTItem nbt = new NBTItem(item);
	        		String spellName = nbt.getString("Spell");
	        		byte spellLevel = nbt.getByte("Level");
	        		Items.addSpell(result, spellName, spellLevel);
	        	}
	        	//result.setItemMeta(bmeta);
	        	inv.setResult(result);
	        }
    	}
    }
    
    @EventHandler
    public void preventTargetingOwner(EntityTargetEvent e) {
        if (e.getEntity() instanceof LivingEntity entity && entity.hasMetadata("owner")) {
            UUID ownerId = UUID.fromString(entity.getMetadata("owner").get(0).asString());
            if (e.getTarget() != null && (e.getTarget().getUniqueId().equals(ownerId) || (e.getTarget().hasMetadata("owner") && UUID.fromString(e.getTarget().getMetadata("owner").get(0).asString()).equals(ownerId)))) {
                e.setCancelled(true);
            }
        }
    }
    
    public static String toRoman(int number) {
        if (number <= 0 || number > 3999) throw new IllegalArgumentException("Number out of range (1-3999)");

        int[] values =    {1000, 900, 500, 400, 100,  90,  50,  40,  10,   9,   5,   4,   1};
        String[] numerals = {"M", "CM","D", "CD","C","XC","L","XL","X","IX","V","IV","I"};

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                number -= values[i];
                result.append(numerals[i]);
            }
        }

        return result.toString();
    }
    
    @EventHandler
    public void onSwapToOffhand(PlayerSwapHandItemsEvent event) {
        // Called when player presses F
        ItemStack newOffhand = event.getOffHandItem();
        handleOffhandChange(event.getPlayer(), newOffhand);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Only care about the player's own inventory
        if (event.getClickedInventory() == null) return;

        // Case: clicking directly on the offhand slot
        if (event.getSlotType() == InventoryType.SlotType.QUICKBAR && event.getSlot() == 40) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                ItemStack newOffhand = player.getInventory().getItemInOffHand();
                handleOffhandChange(player, newOffhand);
            }, 1L);
        }

        // Case: shift-click into offhand (Bukkit doesn't fire a separate event, so we delay and check)
        if (event.isShiftClick()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                ItemStack newOffhand = player.getInventory().getItemInOffHand();
                handleOffhandChange(player, newOffhand);
            }, 1L);
        }
    }
    /**
     * Called whenever a player's offhand item changes.
     */
    private void handleOffhandChange(Player player, ItemStack newOffhand) {
        // You can now run your logic here
        //player.sendMessage("New offhand: " + (newOffhand == null ? "empty" : newOffhand.getType().name()));
        
        if (player.getEquipment().getItemInOffHand() != null && player.getEquipment().getItemInOffHand().getItemMeta() != null && player.getEquipment().getItemInOffHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(FancyMagic.plugin, "spellbook")) && player.getEquipment().getItemInOffHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(FancyMagic.plugin, "spellbook"), PersistentDataType.INTEGER) > 0) {
        	List<SpellData> prepared = SpellBookMenu.loadPreparedSpells(player.getEquipment().getItemInOffHand()); 
        	for (SpellData spell : prepared) {
        		if (spell.name.equalsIgnoreCase("Chronal Shift")) {
        			SpellManager.trackPlayerState(player);
        		}
        	}
        }
    }

    
    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        // Only target dungeon chests
        //if (!event.getLootTable().getKey().toString().equals("minecraft:chests/simple_dungeon")) return;
    	Bukkit.broadcastMessage("Generating loot for " + event.getLootTable().getKey().toString());
    	
    	List<ItemStack> extraLoot = Structures.getAddedLoot(event.getLootTable().getKey().toString());
    	
        /*
        // Create your custom item
        ItemStack customSword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = customSword.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Sword of Frost");
        meta.addEnchant(Enchantment.SHARPNESS, 5, true);
        customSword.setItemMeta(meta);

        // Add it to the loot
        event.getLoot().add(customSword);
        */
    }
    
    List<Enchantment> focusEnchants = new ArrayList<Enchantment>() {{
    	add(Enchantment.UNBREAKING);
    	add(Bukkit.getRegistry(Enchantment.class).get(Spellbound.KEY));
    	add(Bukkit.getRegistry(Enchantment.class).get(FalseLife.KEY));
    	add(Bukkit.getRegistry(Enchantment.class).get(Potency.KEY));
    	add(Bukkit.getRegistry(Enchantment.class).get(QuickCast.KEY));
    	add(Bukkit.getRegistry(Enchantment.class).get(Repelling.KEY));
    	add(Bukkit.getRegistry(Enchantment.class).get(SpellStealer.KEY));
    	add(Bukkit.getRegistry(Enchantment.class).get(SpellTwinning.KEY));
    }};
    
    @EventHandler
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getEnchanter();
        
        // Check if it's a spellcasting focus
        if (isFocus(item)) {
            // Clear all default offers
            for (int i = 0; i < event.getOffers().length; i++) {
                //event.getOffers()[i] = null;
                event.getOffers()[i].setEnchantment(focusEnchants.get((int)(Math.random()*focusEnchants.size())));
                int cost = event.getOffers()[i].getCost();
                int maxLevel = event.getOffers()[i].getEnchantment().getMaxLevel();
                event.getOffers()[i].setEnchantmentLevel((int)Math.clamp((((float)cost)/30f) * maxLevel, 1, maxLevel));
            }
        }
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        ItemStack item = event.getItem();
        if (isFocus(item)) {
            // Clear all default offers
        	
        	Map<Enchantment, Integer> toAdd = new HashMap<>();
        	Iterator<Map.Entry<Enchantment, Integer>> it = event.getEnchantsToAdd().entrySet().iterator();

        	while (it.hasNext()) {
        	    var entry = it.next();
        	    Enchantment ench = entry.getKey();
        	    int level = entry.getValue();

        	    if (focusEnchants.contains(ench)) {
        	        continue;
        	    } else {
        	        Enchantment newEnch = focusEnchants.get((int)(Math.random() * focusEnchants.size()));
        	        if (event.getEnchantsToAdd().containsKey(newEnch) || toAdd.containsKey(newEnch)) {
        	            it.remove();
        	        } else {
        	            int maxLevel = newEnch.getMaxLevel();
        	            toAdd.put(newEnch, (int)Math.clamp((((float)level)/ench.getMaxLevel()) * maxLevel, 1, maxLevel));
        	            it.remove(); // safely remove the old entry
        	        }
        	    }
        	}

        	// Apply the changes after iteration
        	event.getEnchantsToAdd().putAll(toAdd);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player player) {
            ItemStack weapon = player.getInventory().getItemInMainHand();

            // Check for spellbound
            if (weapon == null) return;

            // Get the custom enchantment from the registry
            Enchantment spellbound = Bukkit.getRegistry(Enchantment.class).get(Spellbound.KEY);
            if (spellbound != null && weapon.containsEnchantment(spellbound)) {
                int level = weapon.getEnchantmentLevel(spellbound);
                // Do something based on the level
                //player.sendMessage("Your weapon had Spellbound level " + level + "!");
                double spellDropChance = ((Spellbound)ENCHANTS.get(Spellbound.KEY)).getChanceToDropSpellPerLevel() * level;
                if (Math.random() < spellDropChance) {
	                switch (event.getEntity().getType()) {
		                case EntityType.ENDERMAN:
		                	event.getDrops().add(Spell.MISTY_STEP.getScroll());
		                	break;
		                case EntityType.HUSK:
		                	event.getDrops().add(Spell.FIREBOLT.getScroll());
		                	break;
		                case EntityType.BLAZE:
		                	event.getDrops().add(Spell.FIRE_BARRAGE.getScroll());
		                	break;
		                case EntityType.GHAST:
		                	event.getDrops().add(Spell.FIREBALL.getScroll());
		                	break;
		                case EntityType.CREEPER:
		                	event.getDrops().add(Spell.EXPLOSION.getScroll());
		                	break;
		                case EntityType.MAGMA_CUBE:
		                	event.getDrops().add(Spell.FIRE_RESISTANCE.getScroll());
		                	break;
		                case EntityType.STRAY:
		                	if (Math.random() < 0.7)
		                		event.getDrops().add(Spell.FREEZE.getScroll());
		                	else
		                		event.getDrops().add(Spell.ICE_KNIFE.getScroll());
		                	break;
		                case EntityType.ELDER_GUARDIAN:
		                	event.getDrops().add(Spell.CHAIN_LIGHTNING.getScroll());
		                	break;
		                case EntityType.EVOKER:
		                	event.getDrops().add(Spell.CHRONAL_SHIFT.getScroll());
		                	break;
		                case EntityType.DROWNED:
		                	event.getDrops().add(Spell.TRANSMUTE_WATER.getScroll());
		                	break;
		                case EntityType.POLAR_BEAR:
		                	event.getDrops().add(Spell.TRANSMUTE_SNOW.getScroll());
		                	break;
		                case EntityType.WITCH:
		                	if (Math.random() < 0.25)
		                		event.getDrops().add(Spell.MAGE_ARMOR.getScroll());
		                	else if (Math.random() < 0.3)
		                		event.getDrops().add(Spell.ENERVATION.getScroll());
		                	else if (Math.random() < 0.5)
		                		event.getDrops().add(Spell.PLANT_GROWTH.getScroll());
		                	else
		                		event.getDrops().add(Spell.TRANSMUTE_PLANTS.getScroll());
		                	break;
		                case EntityType.VEX:
		                	event.getDrops().add(Spell.MAGIC_MISSILE.getScroll());
		                	break;
		                case EntityType.BREEZE:
		                	event.getDrops().add(Spell.WIND_BURST.getScroll());
		                	break;
		                case EntityType.PHANTOM:
		                	event.getDrops().add(Spell.LEVITATION.getScroll());
		                	break;
		                case EntityType.IRON_GOLEM:
		                	event.getDrops().add(Spell.THUNDERWAVE.getScroll());
		                	break;
		                case EntityType.ZOMBIE:
		                	if (event.getEntity() instanceof Zombie zomb && zomb.isBaby()) {
		                		event.getDrops().add(Spell.HASTE.getScroll());
		                	} else {
		                		event.getDrops().add(Spell.NECROTIC_BOLT.getScroll());
		                	}
		                	break;
		                case EntityType.SKELETON:
		                	event.getDrops().add(Spell.NECROTIC_BOLT.getScroll());
		                	break;
		                case EntityType.SPIDER:
		                	event.getDrops().add(Spell.WALL_RUNNING.getScroll());
		                	break;
		                case EntityType.CAVE_SPIDER:
		                	event.getDrops().add(Spell.POISON_SPRAY.getScroll());
		                	break;
		                case EntityType.BOGGED:
		                	if (Math.random() < 0.6)
		                		event.getDrops().add(Spell.POISON_CLOUD.getScroll());
		                	else
		                		event.getDrops().add(Spell.POISON_SPRAY.getScroll());
		                	break;
						default:
							break;
	                }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        
        Enchantment quickcast = Bukkit.getRegistry(Enchantment.class).get(QuickCast.KEY);
        if (isFocus(droppedItem) && quickcast != null && droppedItem.containsEnchantment(quickcast)) {
            event.setCancelled(true); // Cancel the drop
            
            // Cast the LLL spell (index 0)
            ItemStack spellbook = player.getInventory().getItemInOffHand();
            if (spellbook != null && spellbook.getItemMeta() != null && 
                spellbook.getItemMeta().getPersistentDataContainer().has(
                    new NamespacedKey(FancyMagic.plugin, "spellbook"), PersistentDataType.INTEGER)) {
                
                List<SpellData> spells = sbMenu.loadPreparedSpells(spellbook);
                if (!spells.isEmpty()) {
                    // Create LLL click pattern for first spell
                    List<Boolean> lllPattern = new ArrayList<>();
                    lllPattern.add(false); // L
                    lllPattern.add(false); // L  
                    lllPattern.add(false); // L
                    
                    Spell spell = sbMenu.fromClickCombination(spells, lllPattern);
                    if (spell != null) {
                        int cooldown = castSpell(player, spell, droppedItem);
                        player.setCooldown(droppedItem, cooldown);
                        player.sendMessage(ChatColor.AQUA + "Quickcast activated!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        
        // Handle False Life enchantment
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
            ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());
            
            // Remove false life from old item
            Enchantment falselife = Bukkit.getRegistry(Enchantment.class).get(FalseLife.KEY);
            if (isFocus(oldItem) && falselife != null && oldItem.containsEnchantment(falselife)) {
                if (falseLifePlayers.contains(player.getUniqueId())) {
                    player.removePotionEffect(PotionEffectType.ABSORPTION);
                    falseLifePlayers.remove(player.getUniqueId());
                }
            }
            
            // Add false life from new item
            if (isFocus(newItem) && falselife != null && newItem.containsEnchantment(falselife)) {
                int level = newItem.getEnchantmentLevel(falselife);
                player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, level - 1));
                falseLifePlayers.add(player.getUniqueId());
            }
        }, 1L);
    }
    
    

}