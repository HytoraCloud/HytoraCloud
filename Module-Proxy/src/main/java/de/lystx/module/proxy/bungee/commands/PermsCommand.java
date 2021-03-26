package de.lystx.module.proxy.bungee.commands;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionValidality;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.util.Value;
import net.md_5.bungee.api.ProxyServer;

import java.util.Date;
import java.util.UUID;

public class PermsCommand {

	@Command(name = "perms", description = "Manages permissions", aliases = {"cloudperms", "hperms"})
	public void execute(CloudCommandSender sender, String[] args) {
		CloudPlayer player = (CloudPlayer)sender;
		if (player.hasPermission("cloudsystem.perms.command")) {
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("user")) {
					if (!CloudAPI.getInstance().getPermissionPool().isRegistered(args[1])) {
						player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the database!");
						return;
					}
					CloudPlayerData data = CloudAPI.getInstance().getPermissionPool().getPlayerData(args[1]);
					CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayers().get(args[1]);
					player.sendMessage("§bInfo for §7" + args[1] + "§8:");
					player.sendMessage("§8§m--------------------------------------");
					player.sendMessage("§8");
					player.sendMessage("§8» §bName §8● §7" + args[1] +" §8«");
					player.sendMessage("§8» §bIP §8● §7" + data.getIpAddress() +" §8«");
					player.sendMessage("§8» §bFirstLogin §8● §7" + CloudAPI.getInstance().getPermissionPool().getFormat().format(new Date(data.getFirstLogin())) +" §8«");
					player.sendMessage("§8» §bLastLogin §8● §7" + CloudAPI.getInstance().getPermissionPool().getFormat().format(new Date(data.getLastLogin())) + " §8«");
					player.sendMessage("§8» §bStatus §8● §7" + (cloudPlayer != null ? "§aOnline" : "§cOffline") +" §8«");
					player.sendMessage("§8» §bPermissionGroups§8:");
					data.getPermissionEntries().forEach(permissionEntry -> player.sendMessage("  §8» §b" + permissionEntry.getPermissionGroup() + " §8● §7" + (permissionEntry.getValidTime().trim().isEmpty() ? "Lifetime": permissionEntry.getValidTime()) +" §8«"));
					player.sendMessage("§8» §bSpecial-perms §8● §7" + data.getPermissions());
					player.sendMessage("§8");
					player.sendMessage("§8§m--------------------------------------");
				} else if (args[0].equalsIgnoreCase("group")) {
					if (args[1].equalsIgnoreCase("list")) {
						if (CloudAPI.getInstance().getPermissionPool().getPermissionGroups().isEmpty()) {
							player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThere aren't any groups created!");
							return;
						}
						player.sendMessage("§bGroups§8:");
						player.sendMessage("§8§m--------------------------------------");
						player.sendMessage("§8");
						CloudAPI.getInstance().getPermissionPool().getPermissionGroups().forEach(group -> {
							player.sendMessage("§8» §b" + group.getName() + " §8┃ §bID§8: §7" + group.getId() + "§8┃ §bPrefix§8: §7" + group.getPrefix() + "§8┃ §bSuffix§8: §7" + group.getSuffix() + "§8┃ §bDisplay§8: §7" + group.getDisplay());

						});
						player.sendMessage("§8");
						player.sendMessage("§8§m--------------------------------------");
						return;
					}
					PermissionGroup group = CloudAPI.getInstance().getPermissionPool().getPermissionGroupFromName(args[1]);
					if (group != null) {
						player.sendMessage("§bInfo for §7" + group.getName() + "§8:");
						player.sendMessage("§8§m--------------------------------------");
						player.sendMessage("§8");
						player.sendMessage("§8» §bName §8● §7" + args[1] +" §8«");
						player.sendMessage("§8» §bPrefix §8● §7" + group.getPrefix().replace("§", "&") +" §8«");
						player.sendMessage("§8» §bSuffix §8● §7" + group.getSuffix().replace("§", "&") +" §8«");
						player.sendMessage("§8» §bDisplay §8● §7" + group.getDisplay().replace("§", "&") +" §8«");
						player.sendMessage("§8» §bId §8● §7" + group.getId() +" §8«");
						player.sendMessage("§8» §bPermissions §8● §7" + group.getPermissions() +" §8«");
						StringBuilder message = new StringBuilder("§7");
						Value<Integer> integerValue = new Value<>();
						group.getInheritances().forEach(inheritance -> {
							integerValue.increase();
							if (group.getInheritances().size() > integerValue.getValue()) {
								message.append(inheritance).append("§8, §7");
							} else {
								message.append(inheritance);
							}
						});
						player.sendMessage("§8» §bInheritances §8● §7" + message.toString() +" §8«");
						player.sendMessage("§8");
						player.sendMessage("§8§m--------------------------------------");
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
						CloudAPI.getInstance().getPermissionPool().update();
						player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7You added the permission §b" + permission + " §7 to the player §b" + args[1] + "§8!");

					} else if (args[2].equalsIgnoreCase("remove")) {
						String permission = args[3];
						if (!CloudAPI.getInstance().getPermissionPool().isRegistered(args[1])) {
							player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the Permissions-Database!");
							return;
						}
						CloudAPI.getInstance().getPermissionPool().updatePermissionEntry(args[1], permission, false);
						CloudAPI.getInstance().getPermissionPool().update();
						player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7You removed the permission §b" + permission + " §7 from the player §b" + args[1] + "§8!");
					} else {
						help(player);
					}
				} else if (args[0].equalsIgnoreCase("group")) {
					String groupname = args[1];
					PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
					PermissionGroup group = permissionPool.getPermissionGroupFromName(groupname);
					if (group == null) {
						player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe group §e" + groupname + " §cdoesn't exist!");
						return;
					}
					if (args[2].equalsIgnoreCase("add")) {
						String permission = args[3];
						permissionPool.updatePermissionGroupEntry(group, permission, true);
						permissionPool.update();
						player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7You added the permission §b" + permission + " §7to the group §b" + group.getName());
					} else if (args[2].equalsIgnoreCase("remove")) {
						String permission = args[3];
						permissionPool.updatePermissionGroupEntry(group, permission, false);
						permissionPool.update();
						player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7You removed the permission §b" + permission + " §7from the group §b" + group.getName());
					} else {
						this.help(player);
					}
				} else {
					help(player);
				}
			} else if (args.length == 5) {
				if (args[0].equalsIgnoreCase("user")) {
					if (args[2].equalsIgnoreCase("group")) {
						if (args[3].equalsIgnoreCase("remove")) {
							String rang = args[4];
							PermissionGroup group = CloudAPI.getInstance().getPermissionPool().getPermissionGroupFromName(rang);
							if (group == null) {
								player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe group §e" + rang + " §cdoesn't exist!");
								return;
							}
							UUID uuid = CloudAPI.getInstance().getPermissionPool().tryUUID(args[1]);
							if (uuid == null) {
								player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the database!");
								return;
							}
							if (!CloudAPI.getInstance().getPermissionPool().isRegistered(args[1])) {
								player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the database!");
								return;
							}
							if (!CloudAPI.getInstance().getPermissionPool().getPermissionGroups(args[1]).contains(group)) {
								player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cdoesn't have this rank!");
								return;
							}
							CloudAPI.getInstance().getPermissionPool().removePermissionGroup(args[1], group);
							CloudAPI.getInstance().getPermissionPool().update();
							player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7The player §b" + args[1] + " §7is was removed from group §b" + rang + "§8!");
						} else {
							this.help(player);
						}
					} else {
						this.help(player);
					}
				} else {
					this.help(player);
				}
			} else if (args.length == 6) {
				if (args[0].equalsIgnoreCase("user")) {
					if (args[2].equalsIgnoreCase("group")) {
						if (args[3].equalsIgnoreCase("add")) {
							String rang = args[4];
							if (CloudAPI.getInstance().getPermissionPool().getPermissionGroupFromName(rang) == null) {
								player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe group §e" + rang + " §cdoesn't exist!");
								return;
							}
							UUID uuid = CloudAPI.getInstance().getPermissionPool().tryUUID(args[1]);
							if (uuid == null) {
								player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the database!");
								return;
							}
							if (!CloudAPI.getInstance().getPermissionPool().isRegistered(args[1])) {
								player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the database!");
								return;
							}
							if (CloudAPI.getInstance().getPermissionPool().getPermissionGroups(args[1]).contains(CloudAPI.getInstance().getPermissionPool().getPermissionGroupFromName(rang))) {
								player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §calready has this rank!");
								return;
							}
							String data = args[5];
							int time;
							PermissionValidality validality = CloudAPI.getInstance().getPermissionPool().formatValidality(data);
							if (validality == null) {
								player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cPlease provide a valid timespan like §e1d §cor §e1y §cor §e1min§c!");
								return;
							} else if (validality == PermissionValidality.LIFETIME) {
								time = -1;
							} else {
								String str = data.replaceAll("[^\\d.]", "");
								time = Integer.parseInt(str);
							}
							CloudAPI.getInstance().getPermissionPool().updatePermissionGroup(args[1], CloudAPI.getInstance().getPermissionPool().getPermissionGroupFromName(rang), time, validality);
							CloudAPI.getInstance().getPermissionPool().update();
							player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7The player §b" + args[1] + " §7is now member of group §b" + rang + " §8[§b" + validality.name() + "§8]");
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

	public void help(CloudPlayer player) {
		player.sendMessage("§bCloudPerms §7Help§8:");
		player.sendMessage("§8§m--------------------------------------");
		player.sendMessage("  §8» §b/perms user <player> add <permission> §8┃ §7Adds a permission to a player");
		player.sendMessage("  §8» §b/perms user <player> remove <permission> §8┃ §7Removes a permission from a player");
		player.sendMessage("  §8» §b/perms user <player> group add <group> <timeSpan> §8┃ §7Sets group of player (example: 1d)");
		player.sendMessage("  §8» §b/perms user <player> group remove <group> §8┃ §7Removes a player from a group");
		player.sendMessage("  §8» §b/perms user <player> §8┃ §7Gives infos about player");
		player.sendMessage("  §8» §b/perms group <group> §8┃ §7Gives infos about a group");
		player.sendMessage("  §8» §b/perms group <group> add <permission> §8┃ §7Gives a permission to a group");
		player.sendMessage("  §8» §b/perms group <group> remove <permission> §8┃ §7Removes a permission from a group");
		player.sendMessage("  §8» §b/perms group list §8┃ §7Lists all groups");
		player.sendMessage("§8§m--------------------------------------");
	  }
}
