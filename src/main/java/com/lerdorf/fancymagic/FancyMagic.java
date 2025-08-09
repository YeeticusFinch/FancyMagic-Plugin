package com.lerdorf.fancymagic;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

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

	public static Collection<NamespacedKey> recipes = new ArrayList<>();
	
	@Override
	public void onEnable() {
		plugin = this;
		getServer().getPluginManager().registerEvents(this, this);
		
		this.getCommand("magic").setExecutor(this);

		//this.getCommand("wall").setExecutor(this);

		loadConfig();
		saveConfig();
		
		registerRecipes();

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

	public boolean rightClick(Player p, Block block) {
		if (p.isSneaking()) {
			// return false;
		} else {

		}
		return false;
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
    
    public boolean rightClick(Player player) {
    	
    }
    
    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
    	boolean cancelEvent = rightClick(event.getPlayer());
    	if (cancelEvent)
    		event.setCancelled(true);
    }
    
    @EventHandler
    public void playerInteractEntityEvent(PlayerInteractEntityEvent event) {
    	boolean cancelEvent = rightClick(event.getPlayer());
    	if (cancelEvent)
    		event.setCancelled(true);
    	
    }
    
    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent event) {
    	boolean cancelEvent = rightClick(event.getPlayer());
    	if (cancelEvent)
    		event.setCancelled(true);
    	
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