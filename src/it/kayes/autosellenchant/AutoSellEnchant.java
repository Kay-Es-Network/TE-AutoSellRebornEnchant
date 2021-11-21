package it.kayes.autosellenchant;

import java.util.HashMap;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import com.vk2gpz.tokenenchant.api.EnchantInfo;
import com.vk2gpz.tokenenchant.api.InvalidTokenEnchantException;
import com.vk2gpz.tokenenchant.api.PotionHandler;
import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;

import it.aendrix.asreborn.main.AutoSellAPI;
import net.md_5.bungee.api.ChatColor;

public class AutoSellEnchant extends PotionHandler {
	
	String[] message;
	AutoSellAPI api = new AutoSellAPI();

	public AutoSellEnchant(TokenEnchantAPI plugin) throws InvalidTokenEnchantException {
		super(plugin);
		loadConfig();
	}

	public void loadConfig() {
		super.loadConfig();
		this.alias = ChatColor.translateAlternateColorCodes('&',this.config.getString("Potions.AutoSell.alias"));
		this.description = ChatColor.translateAlternateColorCodes('&',this.config.getString("Potions.AutoSell.description"));
		HashMap<String,EventPriority> event = new HashMap<String,EventPriority>();
		for (String s : this.config.getConfigurationSection("Potions.AutoSell.event_map").getKeys(false)) 
			event.put(s, EventPriority.valueOf(this.config.getString("Potions.AutoSell.event_map."+s)));
		this.eventPriorityMap = event;
		this.price = this.config.getDouble("Potions.AutoSell.price");
		this.max = this.config.getInt("Potions.AutoSell.max");
		this.occurrence = 100;
		
		List<String> msg = this.config.getStringList("Potions.AutoSell.display_format");
		message = msg.toArray(new String[msg.size()]);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return false;
	}

	public String getName() {
		return "AutoSell";
	}
	
	@Override
	public String getVersion() {
		return "1.0.0";
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		final Block broken = e.getBlock();
		Inventory inv = p.getInventory();
		
		//EnchantInfo ei = hasCE(p); ALL
		
		EnchantInfo ei = hasCE(p, p.getItemInHand());

		if (ei == null || !canExecute(ei) || !checkCooldown(p) || !isValid(broken.getLocation()) || api.hasSpaceInventory(inv))
			return;

		int count = api.countItem(inv);
			if (count <= 0) return;
		
		float sell = api.sellAll(p);
		
		for (String s : message)
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replaceAll("%itemssold%", count+"").replaceAll("%price%", sell+"")));
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Inventory inv = p.getInventory();
		
		//EnchantInfo ei = hasCE(p); ALL
		
		EnchantInfo ei = hasCE(p, p.getItemInHand());

		if (ei == null || !canExecute(ei) || !checkCooldown(p) || api.hasSpaceInventory(inv))
			return;

		int count = api.countItem(inv);
			if (count <= 0) return;
		
		float sell = api.sellAll(p);
		
		for (String s : message)
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replaceAll("%itemssold%", count+"").replaceAll("%price%", sell+"")));
	}
	
	
}

