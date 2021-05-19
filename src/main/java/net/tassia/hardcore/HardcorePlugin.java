package net.tassia.hardcore;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

/**
 * The {@link JavaPlugin} implementation for the Hardcore plugin.
 *
 * @since Hardcore 1.0
 * @author Tassilo
 */
public final class HardcorePlugin extends JavaPlugin {

	/**
	 * The {@link Hardcore} instance in use.
	 */
	private Hardcore hardcore = null;

	@Override
	public final void onLoad() {
		// Initialize
		getLogger().fine("Initializing...");
		Hardcore hardcore = new Hardcore(this);
		this.hardcore = hardcore;
		Hardcore.INSTANCE = hardcore;

		// Load configuration
		getLogger().fine("Loading configuration...");
		try {
			hardcore.config.load(getConfig());
			hardcore.config.validate();
		} catch (IllegalArgumentException ex) {
			getLogger().log(Level.SEVERE, "Config is configured invalidly.", ex);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		// Should enable?
		EnabledRule shouldEnable = hardcore.config.enabled;
		if (shouldEnable == EnabledRule.INHERITED && !getServer().isHardcore()) {
			getLogger().info("Disabling plugin, as hardcore mode is disabled in server.properties");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (shouldEnable == EnabledRule.ALWAYS_OFF) {
			getLogger().info("Disabling plugin, as configured in config.yml");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		// Connect to database
		getLogger().fine("Connecting to database...");
		hardcore.connectDatabase();

		// Prepare database
		getLogger().finer("Preparing database...");
		try {
			hardcore.prepareDatabase();
		} catch (SQLException ex) {
			getLogger().log(Level.SEVERE, "Failed to prepare database.", ex);
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	@Override
	public final void onEnable() {
		// Register command
		getLogger().fine("Registering commands...");
		// TODO

		// Hook listeners
		getLogger().fine("Hooking listeners...");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerDeathListener(hardcore), this);
		pm.registerEvents(new PlayerJoinListener(hardcore), this);
		pm.registerEvents(new PlayerLoginListener(hardcore), this);

		// Check online players
		getLogger().fine("Checking online players...");
		for (Player ply : getServer().getOnlinePlayers()) {
			getLogger().finer("- " + ply.getName());
			try {
				int lives = hardcore.getLives(ply.getUniqueId());
				ply.setMetadata("HardcoreLives", new FixedMetadataValue(this, lives));
				if (lives <= 0) {
					ply.kickPlayer(hardcore.config.getNoLivesRemaining());
				}
			} catch (SQLException ex) {
				getLogger().log(Level.WARNING, "Failed to fetch lives for " + ply.getName(), ex);
			}
		}
	}

	@Override
	public final void onDisable() {
		// Disconnect from database
		getLogger().fine("Disconnecting from database...");
		hardcore.disconnectDatabase();

		// Pop plugin instance from static context
		getLogger().fine("Popping plugin instance from static context...");
		Hardcore.INSTANCE = null;
		this.hardcore = null;
	}

}
