package com.lerdorf.fancymagic;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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

import de.tr7zw.nbtapi.NBTItem;

import org.joml.Vector3f;
import org.joml.AxisAngle4f;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.world.item.crafting.Recipe;

import org.bukkit.block.data.Bisected;

public class FancyMagic extends JavaPlugin implements Listener, TabExecutor {

	private File configFile;
	private Map<String, Object> configValues;

	public static Plugin plugin;

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
		getServer().getPluginManager().registerEvents(this, this);
		
		this.getCommand("magic").setExecutor(this);

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
    
    public HashMap<Player, List<Boolean>> clicks = new HashMap<>();
    public HashMap<Player, Long> lastClick = new HashMap<>();
    
    public boolean use(Player player, boolean rightClick) {
    	
    	ItemStack item = player.getEquipment().getItemInMainHand();
    	if (item != null) {
    		ItemMeta meta = item.getItemMeta();
    		if (meta != null && meta.getPersistentDataContainer().has(new NamespacedKey(FancyMagic.plugin, "focus"), PersistentDataType.INTEGER) && meta.getPersistentDataContainer().get(new NamespacedKey(FancyMagic.plugin, "focus"), PersistentDataType.INTEGER) > 0) {
    			if (player.getEquipment().getItemInOffHand() != null && player.getEquipment().getItemInOffHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(FancyMagic.plugin, "spellbook")) && player.getEquipment().getItemInOffHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(FancyMagic.plugin, "spellbook"), PersistentDataType.INTEGER) > 0) {
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
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
    	if (event.getDamager() instanceof Player p && event.getDamageSource().getDamageType() == DamageType.PLAYER_ATTACK) {
	    	boolean cancelEvent = use(p, false);
	    	if (cancelEvent) {
	    		event.setCancelled(true);
	    		return;
	    	}
    	}
    	if (event.getEntity().getScoreboardTags().contains("fire_shield") && event.getDamager().getLocation().distance(event.getEntity().getLocation()) < 6) {
    		for (String tag : event.getEntity().getScoreboardTags()) {
    			if (tag.contains("fire_shield_ticks:")) {
    				int ticks = Integer.parseInt(tag.substring(tag.indexOf(':')+1));
    				if (event.getDamager() instanceof LivingEntity le) {
    					DamageSource source = DamageSource.builder(DamageType.MAGIC)
    						    .withDirectEntity(event.getEntity()) // the entity causing the damage
    						    .build();
    					le.damage(ticks*0.1f, source);
        				le.setFireTicks(ticks);
    				}
    				break;
    			}
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
    
    
    @Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command.");
			return true;
		}
		
		if (args.length > 0) {
			String spellName = args[0];
			byte level = 1;
			if (args.length > 1)
				level = Byte.parseByte(args[1]);
			for (SpellType spellType : Spell.spellTypes) {
				if (spellType.name.replace(' ', '_').equalsIgnoreCase(spellName)) {
					player.give(spellType.getScroll(level));
				}
			}
		}
		
		return true;
    }

}