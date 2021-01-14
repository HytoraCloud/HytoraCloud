package de.lystx.cloudapi.proxy.command;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.util.UUIDService;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class PermsCommand extends Command {

	public PermsCommand() {
		super("perms", null, "cloudperms", "hperms");
	}

	public void execute(CommandSender sender, String[] args) {
    ProxiedPlayer player = (ProxiedPlayer)sender;
    if (player.hasPermission("cloudsystem.perms.command")) {
    	if (args.length == 2) {
    		if (args[0].equalsIgnoreCase("user")) {
				if (!CloudAPI.getInstance().getPermissionPool().isRegistered(args[1])) {
					player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the database!");
					return;
				}
				CloudPlayerData data = CloudAPI.getInstance().getPermissionPool().getPlayerData(args[1]);
				player.sendMessage("§8§m----------§8┃ §bInfo §8§m┃§8§m---------");
	        	player.sendMessage("§8");
	        	player.sendMessage("§8» §bName §8● §7" + args[1] +" §8«");
	        	player.sendMessage("§8» §bGroup §8● §7" + CloudAPI.getInstance().getPermissionPool().getPermissionGroupFromName(data.getPermissionGroup()).getName() +" §8«");
	        	player.sendMessage("§8» §bValidality §8● §7" + (data.getValidadilityTime().trim().isEmpty() ? "Lifetime": data.getValidadilityTime()) + " §8«");
	        	player.sendMessage("§8» §bSpecial-perms §8● §7" + data.getPermissions());
	        	player.sendMessage("§8");
				player.sendMessage("§8§m----------§8┃ §bInfo §8§m┃§8§m---------");
    		} else if (args[0].equalsIgnoreCase("check")) {
				if (!CloudAPI.getInstance().getPermissionPool().isRegistered(args[1])) {
					player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the database!");
					return;
				}
				CloudPlayerData data = CloudAPI.getInstance().getPermissionPool().getPlayerData(args[1]);
    			if (data.getValidadilityTime().equalsIgnoreCase("lifetime") || data.getValidadilityTime().trim().isEmpty()) {
    				player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe rank of the player §e" + args[1] + " §cis §elifetime§c!");
    				return;
    			}
    			player.sendMessage(CloudAPI.getInstance().getPrefix() + "§77Rank is valid §8● §b" + CloudAPI.getInstance().getPermissionPool().isRankValid(args[1]));
    		} else if (args[0].equalsIgnoreCase("group")) {
    			if (args[1].equalsIgnoreCase("list")) {
    				if (CloudAPI.getInstance().getPermissionPool().getPermissionGroups().isEmpty()) {
    					player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThere aren't any groups created!");
    					return;
					}
    				player.sendMessage("§8§m----------§8┃ §bGroups §8§m┃§8§m---------");
		        	player.sendMessage("§8");
					for (PermissionGroup group : CloudAPI.getInstance().getPermissionPool().getPermissionGroups()) {
						player.sendMessage("§8» §b" + group.getName() + " §8┃ §bID§8: §7" + group.getId() + "§8┃ §bPrefix§8: §7" + group.getPrefix() + "§8┃ §bSuffix§8: §7" + group.getSuffix() + "§8┃ §bDisplay§8: §7" + group.getDisplay());
					}
		        	player.sendMessage("§8");
    				player.sendMessage("§8§m----------§8┃ §bGroups §8§m┃§8§m---------");
    				return;
    			}
    			PermissionGroup group = CloudAPI.getInstance().getPermissionPool().getPermissionGroupFromName(args[1]);
    			if (group != null) {
	    			player.sendMessage("§8§m----------§8┃ §bInfo §8§m┃§8§m---------");
		        	player.sendMessage("§8");
		        	player.sendMessage("§8» §bName §8● §7" + args[1] +" §8«");
		        	player.sendMessage("§8» §bPrefix §8● §7" + group.getPrefix().replace("§", "&") +" §8«");
		        	player.sendMessage("§8» §bSuffix §8● §7" + group.getSuffix().replace("§", "&") +" §8«");
		        	player.sendMessage("§8» §bDisplay §8● §7" + group.getDisplay().replace("§", "&") +" §8«");
		        	player.sendMessage("§8» §bId §8● §7" + group.getId() +" §8«");
		        	player.sendMessage("§8» §bPermissions §8● §7" + group.getPermissions() +" §8«");
					StringBuilder message = new StringBuilder("§7");
					int check = 0;
					for (String inheritance : group.getInheritances()) {
						check++;
						if (group.getInheritances().size() > check) {
							message.append(inheritance).append("§8, §7");
						} else {
							message.append(inheritance);
						}
					}
		        	player.sendMessage("§8» §bInheritances §8● §7" + message.toString() +" §8«");
		        	player.sendMessage("§8");
					player.sendMessage("§8§m----------§8┃ §bInfo §8§m┃§8§m---------");
    			} else {
    				player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe group §e" + args[1] + " §cdoesn't exist!");
    			}
    		}
    	} else if (args.length == 3) {
    		if (args[0].equalsIgnoreCase("user")) {
    			if (args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("remove")) {
    				player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cPlease provide a permission!");
    			} else {
    				help(player);
				}
    		} else {
        		help(player);
    		}
    	} else if (args.length == 4) {
    		if (args[0].equalsIgnoreCase("user")) {
    			if (args[2].equalsIgnoreCase("add")) {
    				String permission = args[3];
					if (!CloudAPI.getInstance().getPermissionPool().isRegistered(args[1])) {
						player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the Permissions-Database!");
						return;
					}
					CloudAPI.getInstance().getPermissionPool().updatePermissionEntry(args[1], permission, true);
					CloudAPI.getInstance().getPermissionPool().update(CloudAPI.getInstance().getCloudClient());
					player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7You added the permission §b" + permission + " §7 to the player §b" + args[1] + "§8!");

    			} else if (args[2].equalsIgnoreCase("remove")) {
    				String permission = args[3];
					if (!CloudAPI.getInstance().getPermissionPool().isRegistered(args[1])) {
						player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the Permissions-Database!");
						return;
					}
					CloudAPI.getInstance().getPermissionPool().updatePermissionEntry(args[1], permission, false);
					CloudAPI.getInstance().getPermissionPool().update(CloudAPI.getInstance().getCloudClient());
    				player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7You removed the permission §b" + permission + " §7 from the player §b" + args[1] + "§8!");
    			} else {
    				help(player);
    			}
    		} else {
        		help(player);
    		}
    	} else if (args.length == 6) {
    		if (args[0].equalsIgnoreCase("user")) {
    			if (args[2].equalsIgnoreCase("group")) {
    				if (args[3].equalsIgnoreCase("set")) {
    					String rang = args[4];
    					if (CloudAPI.getInstance().getPermissionPool().getPermissionGroupFromName(rang) == null) {
							player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe group §e" + rang + " §cdoesn't exist!");
    						return;
    					}
        				UUID uuid = UUIDService.getUUID(args[1]);
            			if (uuid == null) {
							player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the database!");
        					return;
        				}
						if (!CloudAPI.getInstance().getPermissionPool().isRegistered(args[1])) {
							player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the database!");
							return;
						}
    					if (args[5].equalsIgnoreCase("lifetime")) {
    						CloudAPI.getInstance().getPermissionPool().updatePermissionGroup(args[1], CloudAPI.getInstance().getPermissionPool().getPermissionGroupFromName(rang), -1);
    						CloudAPI.getInstance().getPermissionPool().update(CloudAPI.getInstance().getCloudClient());
    						if (ProxyServer.getInstance().getPlayer(uuid) != null) {
								ProxyServer.getInstance().getPlayer(uuid).disconnect(CloudAPI.getInstance().getPrefix() + "§cPlease rejoin, you received a new rank!");
    						}
    						player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7The player §b" + args[1] + " §7is now member of group §b" + rang + " §8[§b" + args[5] + "§8]");
    					} else {
    						String data = args[5];
    						try {
								Integer i = Integer.parseInt(args[5]);
								CloudAPI.getInstance().getPermissionPool().updatePermissionGroup(args[1], CloudAPI.getInstance().getPermissionPool().getPermissionGroupFromName(rang), i);
								CloudAPI.getInstance().getPermissionPool().update(CloudAPI.getInstance().getCloudClient());
								if (ProxyServer.getInstance().getPlayer(uuid) != null) {
									ProxyServer.getInstance().getPlayer(uuid).disconnect(CloudAPI.getInstance().getPrefix() + "§cPlease rejoin, you received a new rank!");
								}
								player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7The player §b" + args[1] + " §7is now member of group §b" + rang + " §8[§b" + data + " days§8]");
							} catch (NumberFormatException e) {
    							player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cPlease provide a §evalid number §cfor the duration of the rank (in days)");
							}
    					}
    				} else {
    		    		help(player);
    				}
    			} else {
    	    		help(player);
    			}
    		} else {
        		help(player);
    		}
    	} else {
    		help(player);
    	}
    } else {
		player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cYou aren't allowed to perform this command!");
    } 
  }

	public void help(ProxiedPlayer player) {
		player.sendMessage("§bCloudPerms §7Help§8:");
		player.sendMessage("§8§m--------------------------------------");
		player.sendMessage("  §8» §b/perms user <player> add <permission> §8┃ §7Adds a permission to a player");
		player.sendMessage("  §8» §b/perms user <player> remove <permission> §8┃ §7Removes a permission from a player");
		player.sendMessage("  §8» §b/perms user <player> group set <group> §8┃ §7Sets group of player");
		player.sendMessage("  §8» §b/perms user <player> §8┃ §7Gives infos about player");
		player.sendMessage("  §8» §b/perms group <group> §8┃ §7Gives infos about a group");
		player.sendMessage("  §8» §b/perms group list §8┃ §7Lists all groups");
		player.sendMessage("  §8» §b/perms check <player> §8┃ §7Checks rank of player");
		player.sendMessage("§8§m--------------------------------------");
	  }
}
