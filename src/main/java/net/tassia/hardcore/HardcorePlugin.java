package net.tassia.hardcore;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
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

	/**
	 * Whether the plugin should be enabled.
	 */
	private boolean shouldEnable = true;

	@Override
	public final void onLoad() {
		// Initialize
		getLogger().fine("Initializing...");
		this.shouldEnable = true;
		Hardcore hardcore = new Hardcore(this);
		this.hardcore = hardcore;
		Hardcore.INSTANCE = hardcore;

		// Load configuration
		getLogger().fine("Loading configuration...");
		File configFile = new File(getDataFolder(), "config.yml");
		try {
			extractResource("config.yml", configFile);
			hardcore.config.load(configFile);
			hardcore.config.validate();
		} catch (IllegalArgumentException ex) {
			getLogger().log(Level.SEVERE, "Config is configured invalidly.", ex);
			this.shouldEnable = false;
			return;
		} catch (IOException ex) {
			getLogger().log(Level.SEVERE, "An I/O error occurred while loading the configuration.", ex);
			this.shouldEnable = false;
			return;
		} catch (InvalidConfigurationException ex) {
			getLogger().log(Level.SEVERE, "Cannot load configuration because it is not in a valid YAML format.", ex);
			this.shouldEnable = false;
			return;
		}

		// Should enable?
		EnabledRule shouldEnable = hardcore.config.enabled;
		if (shouldEnable == EnabledRule.INHERITED && !getServer().isHardcore()) {
			getLogger().info("Disabling plugin, as hardcore mode is disabled in server.properties");
			this.shouldEnable = false;
			return;
		}
		if (shouldEnable == EnabledRule.ALWAYS_OFF) {
			getLogger().info("Disabling plugin, as configured in config.yml");
			this.shouldEnable = false;
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
			this.shouldEnable = false;
		}
	}

	@Override
	public final void onEnable() {
		// Should enable?
		if (!this.shouldEnable) {
			setEnabled(false);
			return;
		}

		// Register command
		getLogger().fine("Registering commands...");
		PluginCommand cmd = getCommand("hardcore");
		if (cmd != null) {
			HardcoreCommand exec = new HardcoreCommand(hardcore);
			cmd.setExecutor(exec);
			cmd.setTabCompleter(exec);
		} else {
			getLogger().warning("Failed to register command. Did you modify the plugin.yml?");
		}

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





	/**
	 * The buffer size used for {@link HardcorePlugin#extractResource(String, File)}.
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * Copies a resource from inside the classpath to a given file.
	 *
	 * @param resource the resource to extract
	 * @param target the target file path
	 * @throws IOException if an I/O error occurs
	 */
	private void extractResource(String resource, File target) throws IOException {
		// Ignore if target already exists
		if (target.exists()) return;

		// Create target file
		File parent = target.getParentFile();
		if (!parent.exists()) {
			if (!parent.mkdirs()) {
				throw new IOException("Failed to create directory: " + parent);
			}
		}
		if (!target.createNewFile()) {
			throw new IOException("Failed to create file: " + target);
		}

		// Open streams
		try (InputStream in = getResource(resource)) {
			if (in == null) throw new IOException("Resource '" + resource + "' does not exist.");
			try (OutputStream out = new FileOutputStream(target)) {

				// Write all bytes
				byte[] buffer = new byte[BUFFER_SIZE];
				while (true) {
					int total = in.read(buffer, 0, buffer.length);
					if (total == -1) break;
					out.write(buffer, 0, total);
				}

				// Flush stream
				out.flush();

			}
		}
	}

}
