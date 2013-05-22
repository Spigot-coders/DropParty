/*
 * This file is part of DropParty.
 *
 * Copyright (c) 2013-2013
 *
 * DropParty is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DropParty is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DropParty.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.ampayne2.DropParty.command.commands.set;

import java.util.HashMap;
import java.util.Map;

import me.ampayne2.DropParty.DPMessageController;
import me.ampayne2.DropParty.command.commands.remove.CommandRemoveChest;
import me.ampayne2.DropParty.command.interfaces.DPCommand;
import me.ampayne2.DropParty.database.DatabaseManager;
import me.ampayne2.DropParty.database.tables.DPItemPointsTable;
import me.ampayne2.DropParty.database.tables.DPPartiesTable;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetItempoint implements DPCommand {

	private static Map<String, String> playersSetting = new HashMap<String, String>();

	@Override
	public void execute(CommandSender sender, String[] args) {
		String dpid;
		if (args.length == 1) {
			dpid = args[0];
		} else {
			return;
		}
		if (!sender.hasPermission("dropparty.set.itempoint") && !sender.hasPermission("dropparty.set.*") && !sender.hasPermission("dropparty.*")) {
			return;
		}
		Player player = (Player) sender;
		if (DatabaseManager.getDatabase().select(DPPartiesTable.class).where().equal("dpid", dpid).execute().findOne() == null
				|| !DatabaseManager.getDatabase().select(DPPartiesTable.class).where().equal("dpid", dpid).execute().findOne().dpid.equals(dpid)) {
			DPMessageController.sendMessage(player, DPMessageController.getMessage("dppartydoesntexist"), dpid);
			return;
		}
		if (CommandRemoveChest.isRemoving(player.getName()) && !isSetting(player.getName())) {
			CommandRemoveChest.toggleRemoving(player, dpid);
		}
		if (CommandSetChest.isSetting(player.getName()) && !isSetting(player.getName())) {
			CommandSetChest.toggleSetting(player, dpid);
		}
		if (CommandSetFireworkpoint.isSetting(player.getName()) && !isSetting(player.getName())) {
			CommandSetFireworkpoint.toggleSetting(player, dpid);
		}
		toggleSetting(player, dpid);
	}

	public static void toggleSetting(Player player, String dpid) {
		String playername = player.getName();
		if (isSetting(playername) && getSetting(playername).equals(dpid)) {
			playersSetting.remove(playername);
			DPMessageController.sendMessage(player, DPMessageController.getMessage("dpsetitempointmodeoff"), dpid);
		} else {
			playersSetting.put(playername, dpid);
			DPMessageController.sendMessage(player, DPMessageController.getMessage("dpsetitempointmode"), dpid);
		}
	}

	public static boolean isSetting(String playername) {
		if (playersSetting.containsKey(playername)) {
			return true;
		} else {
			return false;
		}
	}

	public static String getSetting(String playername) {
		return playersSetting.get(playername);
	}

	public static void saveItemPoint(Player player, String playerName, String dpid, String world, int x, int y, int z) {

		if (DatabaseManager.getDatabase().select(DPItemPointsTable.class).where().equal("dpid", dpid).and().equal("x", x).and().equal("y", y).and().equal("z", z).execute().findOne() != null) {
			DPMessageController.sendMessage(player, DPMessageController.getMessage("dpitempointalreadyexists"), dpid);
			return;
		}

		DPItemPointsTable table = new DPItemPointsTable();
		table.dpid = dpid;
		table.world = world;
		table.x = x;
		table.y = y;
		table.z = z;
		DatabaseManager.getDatabase().save(table);
		DPMessageController.sendMessage(player, DPMessageController.getMessage("dpsetitempoint"), dpid);
	}
}
