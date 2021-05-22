package net.tassia.hardcore;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginDescriptionFile;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

final class HardcoreCommand implements CommandExecutor, TabCompleter {

	private final Hardcore hardcore;

	public HardcoreCommand(Hardcore hardcore) {
		this.hardcore = hardcore;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String usage = "/hardcore <check/delete/give/rebuild/reset/take/version> [<player>] [<amount>]";
		if (args.length == 0) {
			sender.sendMessage(hardcore.config.getInvalidUsage(usage));
			return true;
		}

		String cmd = args[0].toLowerCase(Locale.US);
		switch (cmd) {
			case "check":
				check(sender, args);
				break;

			case "delete":
				delete(sender, args);
				break;

			case "give":
				give(sender, args);
				break;

			case "rebuild":
				rebuild(sender, args);
				break;

			case "reset":
				reset(sender, args);
				break;

			case "take":
				take(sender, args);
				break;

			case "version":
				version(sender, args);
				break;

			default:
				sender.sendMessage(hardcore.config.getInvalidUsage(usage));
				break;
		}
		return true;
	}

	private void check(CommandSender sender, String[] args) {
		// Check usage
		if (args.length != 2) {
			sender.sendMessage(hardcore.config.getInvalidUsage("/hardcore check <player>"));
			return;
		}

		// Find player
		OfflinePlayer target = findOfflinePlayer(args[1], sender);
		if (target == null) return;

		// Execute
		sender.sendMessage(hardcore.config.getFetchingData());
		try {
			int lives = hardcore.getLives(target.getUniqueId());
			sender.sendMessage(hardcore.config.getResultCheck(target.getName(), lives));
		} catch (SQLException ex) {
			ex.printStackTrace();
			sender.sendMessage(hardcore.config.getSqlError());
		}
	}

	private void delete(CommandSender sender, String[] args) {
		// Check usage
		if (args.length != 2) {
			sender.sendMessage(hardcore.config.getInvalidUsage("/hardcore delete <player>"));
			return;
		}

		// Find player
		OfflinePlayer target = findOfflinePlayer(args[1], sender);
		if (target == null) return;

		// Ensure player is offline
		if (target.isOnline()) {
			sender.sendMessage(hardcore.config.getPlayerOnlineWhenDeleting(target.getName()));
			return;
		}

		// Execute
		sender.sendMessage(hardcore.config.getDeletingData());
		try {
			hardcore.deleteData(target.getUniqueId());
			sender.sendMessage(hardcore.config.getResultDelete(target.getName()));
		} catch (SQLException ex) {
			ex.printStackTrace();
			sender.sendMessage(hardcore.config.getSqlError());
		}
	}

	private void give(CommandSender sender, String[] args) {
		// Check usage
		if (args.length != 2 && args.length != 3) {
			sender.sendMessage(hardcore.config.getInvalidUsage("/hardcore give <player> [<amount>]"));
			return;
		}

		// Find player
		OfflinePlayer target = findOfflinePlayer(args[1], sender);
		if (target == null) return;

		// Parse amount
		int amount;
		if (args.length == 3) {
			amount = parsePositiveNumber(args[2], sender);
			if (amount == -1) return;
		} else {
			amount = 1;
		}

		// Give lives
		sender.sendMessage(hardcore.config.getUpdatingData());
		try {
			hardcore.giveLives(target.getUniqueId(), amount);
			sender.sendMessage(hardcore.config.getResultGive(target.getName(), amount));
		} catch (SQLException ex) {
			ex.printStackTrace();
			sender.sendMessage(hardcore.config.getSqlError());
		}
	}

	private void rebuild(CommandSender sender, String[] args) {
		// Check usage
		if (args.length != 2) {
			sender.sendMessage(hardcore.config.getInvalidUsage("/hardcore rebuild <player>"));
			return;
		}

		// Find player
		OfflinePlayer target = findOfflinePlayer(args[1], sender);
		if (target == null) return;

		// Rebuild data
		sender.sendMessage(hardcore.config.getRebuildingData());
		try {
			hardcore.rebuildLives(target.getUniqueId());
			sender.sendMessage(hardcore.config.getResultRebuild(target.getName()));
		} catch (SQLException ex) {
			ex.printStackTrace();
			sender.sendMessage(hardcore.config.getSqlError());
		}
	}

	private void reset(CommandSender sender, String[] args) {
		// Check usage
		if (args.length != 2) {
			sender.sendMessage(hardcore.config.getInvalidUsage("/hardcore reset <player>"));
			return;
		}

		// Find player
		OfflinePlayer target = findOfflinePlayer(args[1], sender);
		if (target == null) return;

		// Reset lives
		sender.sendMessage(hardcore.config.getResettingData());
		try {
			hardcore.resetLives(target.getUniqueId());
			sender.sendMessage(hardcore.config.getResultReset(target.getName()));
		} catch (SQLException ex) {
			ex.printStackTrace();
			sender.sendMessage(hardcore.config.getSqlError());
		}
	}

	private void take(CommandSender sender, String[] args) {
		// Check usage
		if (args.length != 2 && args.length != 3) {
			sender.sendMessage(hardcore.config.getInvalidUsage("/hardcore give <player> [<amount>]"));
			return;
		}

		// Find player
		OfflinePlayer target = findOfflinePlayer(args[1], sender);
		if (target == null) return;

		// Parse amount
		int amount;
		if (args.length == 3) {
			amount = parsePositiveNumber(args[2], sender);
			if (amount == -1) return;
		} else {
			amount = 1;
		}

		// Take lives
		sender.sendMessage(hardcore.config.getFetchingData());
		try {
			int lives = hardcore.getLives(target.getUniqueId());
			if (amount > lives) {
				// TODO
				return;
			}
			sender.sendMessage(hardcore.config.getUpdatingData());
			hardcore.takeLives(target.getUniqueId(), amount);
			sender.sendMessage(hardcore.config.getResultTake(target.getName(), amount));
		} catch (SQLException ex) {
			ex.printStackTrace();
			hardcore.config.getSqlError();
		}
	}

	private void version(CommandSender sender, String[] args) {
		if (args.length == 1) {
			PluginDescriptionFile desc = hardcore.plugin.getDescription();
			sender.sendMessage(hardcore.config.getResultVersion(desc.getVersion(), String.join(", ", desc.getAuthors())));
		} else {
			sender.sendMessage(hardcore.config.getInvalidUsage("/hardcore version"));
		}
	}



	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return null;
	}



	private OfflinePlayer findOfflinePlayer(String search, CommandSender sender) {
		List<OfflinePlayer> targets = findOfflinePlayer(search);
		if (targets.isEmpty()) {
			sender.sendMessage(hardcore.config.getPlayerNotFound(search));
			return null;
		} else if (targets.size() > 1) {
			sender.sendMessage(hardcore.config.getPlayerMatchesMultiple(search));
			return null;
		} else {
			return targets.get(0);
		}
	}
	private List<OfflinePlayer> findOfflinePlayer(String search) {
		return Arrays.stream(Bukkit.getOfflinePlayers()).distinct().filter((ply) -> {
			String name = ply.getName();
			return name != null && name.equalsIgnoreCase(search);
		}).collect(Collectors.toList());
	}

	private int parsePositiveNumber(String search, CommandSender sender) {
		try {
			int num = Integer.parseInt(search);
			if (num <= 0) {
				sender.sendMessage(hardcore.config.getNotAPositiveNumber(num));
				return -1;
			}
			return num;
		} catch (NumberFormatException ex) {
			sender.sendMessage(hardcore.config.getNotANumber(search));
			return -1;
		}
	}

}
