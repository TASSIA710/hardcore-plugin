package net.tassia.hardcore;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.MetadataValue;

import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;

final class PlayerDeathListener implements Listener {

	private final Hardcore hardcore;

	public PlayerDeathListener(Hardcore hardcore) {
		this.hardcore = hardcore;
	}

	@EventHandler
	public final void onPlayerDeath(PlayerDeathEvent event) {
		Player ply = event.getEntity();

		// Update lives
		try {
			hardcore.takeLives(ply.getUniqueId(), 1);
		} catch (SQLException ex) {
			hardcore.plugin.getLogger().log(Level.WARNING, "Failed to update lives for " + ply.getName(), ex);
		}

		// Send message
		if (ply.isOnline()) {
			Optional<MetadataValue> data = ply.getMetadata("HardcoreLives").stream().findFirst();
			if (data.isPresent()) {
				int lives = data.get().asInt();
				ply.sendMessage(hardcore.config.getOnDeath(lives));
			}
		}
	}

}
