package com.bitquest.bitquest;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SignTransaction {

	public Boolean isValid;
	public String errorMessage;
	public String saleType;
	public Material itemMaterial;
	public int quantity;
	public int price;
	public String payName;
	Player player;

	public SignTransaction(String[] signText) {
		this.isValid = false;
		this.quantity = 0;
		this.price = 0;
		this.player = null;

		// Determine if this is a buy or sell sign and sanitize the input.
		if(signText[0].equalsIgnoreCase("buy")) {
			this.saleType = "buy";
		} else if(signText[0].equalsIgnoreCase("sell")) {
			this.saleType = "sell";
		} else {
			this.errorMessage = "ERROR: Trying to parse sale sign that does not have 'buy' or 'sell' on first line.";
			return;
		}

		// Check to see if the item for sale is a valid item type.
		this.itemMaterial = Material.matchMaterial(signText[1]);
		if(this.itemMaterial == null) {
			this.errorMessage = "ERROR: Unrecognized item name on " + saleType + " sign.";
			return;
		}

		// Check to see we are selling more than 1 item or just a single one.
		// TODO: Put this in a try-catch block to catch parse errors.
		if(signText[2].toLowerCase().contains("for")) {
			String priceLineString = signText[2].toLowerCase().replace(" ", "").replace("b", "");
			String[] priceLineItems = priceLineString.split("for");
			this.quantity = Integer.parseInt(priceLineItems[0]);
			this.price = Integer.parseInt(priceLineItems[1]);
		} else {
			this.quantity = 1;
			this.price = Integer.parseInt(signText[2].toLowerCase().replace(" ", "").replace("b", ""));
		}

		// Make sure the player included their name on the last line for payment.
		// TODO: Add a check to see if this is a prefix to a long name that doesn't fit on a sign and if so, upgrade it to the full name.
		if(signText[3].length() > 0) {
			this.payName = signText[3];
		} else {
			this.errorMessage = "ERROR: The last line of a sale sign must be a player name to credit/charge for the sale.";
			return;
		}

		// Find the bukkit player instance for the player whose name is on the sign.
		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			// TODO: Add option to match the name if the sign contains a prefix of the name at
			// least N characters long to handle player names that don't fit on a sign.
			if(p.getName().equalsIgnoreCase(signText[3])) {
				this.player = p.getPlayer();
				break;
			}
		}

		if(this.player == null) {
			this.errorMessage = "ERROR: The last line of a sale sign must be a player name to credit/charge for the sale.";
			return;
		}

		this.isValid = true;
	}

}

