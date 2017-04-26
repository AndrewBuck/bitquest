package com.bitquest.bitquest;

import com.evilmidget38.UUIDFetcher;
import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.material.Sign;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;
import java.lang.*;

/**
 * Created by cristian on 12/17/15.
 */
public class SignEvents implements Listener {
	BitQuest bitQuest;
	public SignEvents(BitQuest plugin) {
		bitQuest = plugin;
	}
	@EventHandler
	public void onSignChange(SignChangeEvent event) throws ParseException, org.json.simple.parser.ParseException, IOException {
		final String specialCharacter = "^";
		final String[] lines = event.getLines();
		final String signText = lines[0] + lines[1] + lines[2] + lines[3];

		final Player player = event.getPlayer();
		// Check that the world is overworld
		if(!event.getBlock().getWorld().getName().endsWith("_nether") && !event.getBlock().getWorld().getName().endsWith("_end")) {
			Chunk chunk = event.getBlock().getWorld().getChunkAt(event.getBlock().getLocation());

			if (signText.length() > 0 && signText.substring(0,1).equals(specialCharacter) && signText.substring(signText.length()-1).equals(specialCharacter)) {

				final String name = signText.substring(1,signText.length()-1);
				bitQuest.claimLand(name,chunk,player);

			}

		} else if(event.getBlock().getWorld().getName().endsWith("_nether")) {
			player.sendMessage(ChatColor.RED + "No claiming in the nether!");
		} else if(event.getBlock().getWorld().getName().endsWith("_end")) {
			player.sendMessage(ChatColor.RED + "No claiming in the end!");
		}

		// Check to see if the sign is a sale sign for a player market.
		Sign sign = (Sign) event.getBlock().getState().getData();
		Block attached = event.getBlock().getRelative(sign.getAttachedFace());
		if(attached.getType() == Material.CHEST && (lines[0].equalsIgnoreCase("buy") || lines[0].equalsIgnoreCase("sell"))) {
			// Sell signs must use the players own name, you can't offer to buy on someone else's behalf.
			// If the payee is 'me' then autocomplete the players name for them.
			// TODO: Check to make sure the players name is not too long for the sign.
			if(lines[0].equalsIgnoreCase("sell") || lines[3].equalsIgnoreCase("me"))
				lines[3] = player.getName();

			SignTransaction signTransaction = new SignTransaction(lines);

			if(signTransaction.isValid) {
				if(signTransaction.payName.equalsIgnoreCase(player.getName())) {
					player.sendMessage(ChatColor.GREEN + "Shop created: Offering: " + signTransaction.itemMaterial.toString() + " Qty: " + signTransaction.quantity + " Price: " + signTransaction.price);
				} else {
					player.sendMessage(ChatColor.RED + "ERROR: You must include your name at the bottom of a sale sign to allow for payment.");
				}
			} else {
				player.sendMessage(ChatColor.RED + signTransaction.errorMessage);
			}
		}
	
	}
}

