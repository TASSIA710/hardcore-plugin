package net.tassia.hardcore;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

final class PlayerJoinListener implements Listener {

	private final Hardcore hardcore;

	public PlayerJoinListener(Hardcore hardcore) {
		this.hardcore = hardcore;
	}

	@EventHandler
	public final void onPlayerJoin(PlayerJoinEvent event) {
		Player ply = event.getPlayer();

		// Fetch lives
		MetadataValue meta = ply.getMetadata("HardcoreLives").get(0);
		int lives = meta.asInt();

		// Check enough lives remaining
		if (lives <= 0) {
			ply.kickPlayer(hardcore.config.getNoLivesRemaining());
			return;
		}

		// Store player metadata
		ply.setMetadata("HardcoreLives", new FixedMetadataValue(hardcore.plugin, lives));

		// Send message
		if (ply.hasPlayedBefore()) {
			ply.sendMessage(hardcore.config.getOnJoin(lives));
		} else {
			ply.sendMessage(hardcore.config.getOnFirstJoin(lives));
		}
	}

}
