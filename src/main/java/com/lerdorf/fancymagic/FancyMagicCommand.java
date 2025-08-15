package com.lerdorf.fancymagic;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FancyMagicCommand implements BasicCommand {

	@Override
	public void execute(CommandSourceStack commandSourceStack, String[] args) {
		
		CommandSender sender = commandSourceStack.getSender();
		
		if (!(sender instanceof Player player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command.");
			return;
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
	}
}