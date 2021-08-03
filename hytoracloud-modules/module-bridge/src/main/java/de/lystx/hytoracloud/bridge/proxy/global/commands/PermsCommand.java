package de.lystx.hytoracloud.bridge.proxy.global.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionPool;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionValidity;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.player.required.OfflinePlayer;
import de.lystx.hytoracloud.driver.command.execution.CommandListener;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@CommandInfo(name = "perms", description = "Manages permissions", aliases = {"cloudperms", "hperms"})
public class PermsCommand implements CommandListener {

	@Override
	public void execute(CommandExecutor sender, String[] args) {
		ICloudPlayer player = (ICloudPlayer)sender;
		if (player.hasPermission("cloudsystem.perms.command")) {
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("user")) {
					try {
						UUID uniqueId = CloudDriver.getInstance().getPermissionPool().getUUIDByName(args[1]);
						if (!CloudDriver.getInstance().getPermissionPool().isRegistered(uniqueId)) {
							player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the database!");
							return;
						}
						OfflinePlayer data = CloudDriver.getInstance().getPermissionPool().getCachedObject(uniqueId);
						ICloudPlayer ICloudPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(args[1]);
						player.sendMessage("§bInfo for §7" + args[1] + "§8:");
						player.sendMessage("§8§m--------------------------------------");
						player.sendMessage("§8");
						player.sendMessage("§8» §bName §8● §7" + args[1] +" §8«");
						player.sendMessage("§8» §bIP §8● §7" + data.getIpAddress() +" §8«");
						player.sendMessage("§8» §bFirstLogin §8● §7" + CloudDriver.getInstance().getPermissionPool().getFormat().format(new Date(data.getFirstLogin())) +" §8«");
						player.sendMessage("§8» §bLastLogin §8● §7" + CloudDriver.getInstance().getPermissionPool().getFormat().format(new Date(data.getLastLogin())) + " §8«");
						player.sendMessage("§8» §bStatus §8● §7" + (ICloudPlayer != null ? "§aOnline" : "§cOffline") +" §8«");
						player.sendMessage("§8» §bPermissionGroups§8:");
						data.getPermissionEntries().forEach(permissionEntry -> player.sendMessage("  §8» §b" + permissionEntry.getPermissionGroup() + " §8● §7" + (permissionEntry.getValidTime().trim().isEmpty() ? "Lifetime": permissionEntry.getValidTime()) +" §8«"));
						player.sendMessage("§8» §bSpecial-perms §8● §7" + data.getExclusivePermissions());
						player.sendMessage("§8");
						player.sendMessage("§8§m--------------------------------------");
					} catch (Exception e) {
						player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cPlease provide a §eValid §cUser!");
					}
				} else if (args[0].equalsIgnoreCase("group")) {
					if (args[1].equalsIgnoreCase("list")) {
						if (CloudDriver.getInstance().getPermissionPool().getPermissionGroups().isEmpty()) {
							player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThere aren't any groups created!");
							return;
						}
						player.sendMessage("§bGroups§8:");
						player.sendMessage("§8§m--------------------------------------");
						player.sendMessage("§8");
						CloudDriver.getInstance().getPermissionPool().getPermissionGroups().forEach(group -> {
							player.sendMessage("§8» §b" + group.getName() + " §8┃ §bID§8: §7" + group.getId() + "§8┃ §bPrefix§8: §7" + group.getPrefix() + "§8┃ §bSuffix§8: §7" + group.getSuffix() + "§8┃ §bDisplay§8: §7" + group.getDisplay());

						});
						player.sendMessage("§8");
						player.sendMessage("§8§m--------------------------------------");
						return;
					}
					PermissionGroup group = CloudDriver.getInstance().getPermissionPool().getPermissionGroupByName(args[1]);
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
						AtomicInteger integerValue = new AtomicInteger();
						group.getInheritances().forEach(inheritance -> {
							integerValue.incrementAndGet();
							if (group.getInheritances().size() > integerValue.get()) {
								message.append(inheritance).append("§8, §7");
							} else {
								message.append(inheritance);
							}
						});
						player.sendMessage("§8» §bInheritances §8● §7" + message.toString() +" §8«");
						player.sendMessage("§8");
						player.sendMessage("§8§m--------------------------------------");
					} else {
						player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe group §e" + args[1] + " §cdoesn't exist!");
					}
				}
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("user")) {
					if (args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("remove")) {
						player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cPlease provide a permission!");
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

						try {
							UUID uniqueId = CloudDriver.getInstance().getPermissionPool().getUUIDByName(args[1]);
							if (!CloudDriver.getInstance().getPermissionPool().isRegistered(uniqueId)) {
								player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the Permissions-Database!");
								return;
							}
							CloudDriver.getInstance().getPermissionPool().setPermissionToUser(uniqueId, permission, true);
							CloudDriver.getInstance().getPermissionPool().update();
							player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7You added the permission §b" + permission + " §7 to the player §b" + args[1] + "§8!");
						} catch (Exception e) {
							player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cPlease provide a §evalid Player§c!");
						}

					} else if (args[2].equalsIgnoreCase("remove")) {
						String permission = args[3];
						try {
							UUID uniqueId = CloudDriver.getInstance().getPermissionPool().getUUIDByName(args[1]);
							if (!CloudDriver.getInstance().getPermissionPool().isRegistered(uniqueId)) {
								player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the Permissions-Database!");
								return;
							}
							CloudDriver.getInstance().getPermissionPool().setPermissionToUser(uniqueId, permission, false);
							CloudDriver.getInstance().getPermissionPool().update();
							player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7You removed the permission §b" + permission + " §7 from the player §b" + args[1] + "§8!");
						} catch (Exception e) {
							player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cPlease provide a §evalid Player§c!");
						}


					} else {
						help(player);
					}
				} else if (args[0].equalsIgnoreCase("group")) {
					String groupname = args[1];
					PermissionPool permissionPool = CloudDriver.getInstance().getPermissionPool();
					PermissionGroup permissionGroup = permissionPool.getPermissionGroupByName(groupname);
					if (permissionGroup == null) {
						player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe group §e" + groupname + " §cdoesn't exist!");
						return;
					}
					if (args[2].equalsIgnoreCase("add")) {
						String permission = args[3];

						permissionGroup.addPermission(permission);
						permissionGroup.update();

						player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7You added the permission §b" + permission + " §7to the group §b" + permissionGroup.getName());
					} else if (args[2].equalsIgnoreCase("remove")) {
						String permission = args[3];

						permissionGroup.removePermission(permission);
						permissionGroup.update();

						player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7You removed the permission §b" + permission + " §7from the group §b" + permissionGroup.getName());
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
							PermissionGroup group = CloudDriver.getInstance().getPermissionPool().getPermissionGroupByName(rang);
							if (group == null) {
								player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe group §e" + rang + " §cdoesn't exist!");
								return;
							}
							UUID uuid = CloudDriver.getInstance().getPermissionPool().getUUIDByName(args[1]);
							if (uuid == null) {
								player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the database!");
								return;
							}
							if (!CloudDriver.getInstance().getPermissionPool().isRegistered(uuid)) {
								player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the database!");
								return;
							}
							if (!CloudDriver.getInstance().getPermissionPool().getPermissionGroups(uuid).contains(group)) {
								player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cdoesn't have this rank!");
								return;
							}

							try {
								UUID uniqueId = CloudDriver.getInstance().getPermissionPool().getUUIDByName(args[1]);
								CloudDriver.getInstance().getPermissionPool().removePermissionGroupFromUser(uniqueId, group);
								CloudDriver.getInstance().getPermissionPool().update();
								player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7The player §b" + args[1] + " §7is was removed from group §b" + rang + "§8!");
							} catch (Exception e) {
								player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cPlease provide a §eValid User§c!");
							}

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
							if (CloudDriver.getInstance().getPermissionPool().getPermissionGroupByName(rang) == null) {
								player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe group §e" + rang + " §cdoesn't exist!");
								return;
							}
							UUID uuid = CloudDriver.getInstance().getPermissionPool().getUUIDByName(args[1]);
							if (uuid == null) {
								player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the database!");
								return;
							}
							if (!CloudDriver.getInstance().getPermissionPool().isRegistered(uuid)) {
								player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §cis unknown to the database!");
								return;
							}
							if (CloudDriver.getInstance().getPermissionPool().getPermissionGroups(uuid).contains(CloudDriver.getInstance().getPermissionPool().getPermissionGroupByName(rang))) {
								player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe player §e" + args[1] + " §calready has this rank!");
								return;
							}
							String data = args[5];
							int time;
							PermissionValidity validity = PermissionValidity.formatValidity(data);
							if (validity == null) {
								player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cPlease provide a valid timespan like §e1d §cor §e1y §cor §e1min§c!");
								return;
							} else if (validity == PermissionValidity.LIFETIME) {
								time = -1;
							} else {
								String str = data.replaceAll("[^\\d.]", "");
								time = Integer.parseInt(str);
							}
							try {
								UUID uniqueId = CloudDriver.getInstance().getPermissionPool().getUUIDByName(args[1]);
								CloudDriver.getInstance().getPermissionPool().addPermissionGroupToUser(uniqueId, CloudDriver.getInstance().getPermissionPool().getPermissionGroupByName(rang), time, validity);
								CloudDriver.getInstance().getPermissionPool().update();
								player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7The player §b" + args[1] + " §7is now member of group §b" + rang + " §8[§b" + validity.name() + "§8]");

							} catch (Exception e) {
								player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cPlease provide a §eValid User§c!");
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
			player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cYou aren't allowed to perform this command!");
		}
	  }

	public void help(ICloudPlayer player) {
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
