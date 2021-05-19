package net.tassia.hardcore;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.sql.SQLException;
import java.util.logging.Level;

final class PlayerLoginListener implements Listener {

	private final Hardcore hardcore;

	public PlayerLoginListener(Hardcore hardcore) {
		this.hardcore = hardcore;
	}

	@EventHandler
	public final void onPlayerLogin(PlayerLoginEvent event) {
		Player ply = event.getPlayer();

		// Fetch lives
		int lives;
		try {
			lives = hardcore.getLives(ply.getUniqueId());
		} catch (SQLException ex) {
			hardcore.plugin.getLogger().log(Level.WARNING, "Failed to fetch data for " + ply.getName(), ex);
			return;
		}

		// Store lives as metadata
		ply.setMetadata("HardcoreLives", new FixedMetadataValue(hardcore.plugin, lives));
	}

}
