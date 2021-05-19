package net.tassia.hardcore;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.requireNonNull;

public class HardcoreConfiguration implements IConfiguration {

	public EnabledRule enabled;
	public int defaultLives;

	public String onFirstJoin;
	public String onJoin;
	public String onDeath;
	public List<String> noLivesRemaining;

	public HardcoreConfiguration() {
		this.enabled = EnabledRule.INHERITED;
		this.defaultLives = 3;

		this.onFirstJoin = "&7[&eHardcore&7] &6This server is running on &ehardcore mode&6. Initially you have &e&l{lives} &6lives. After you have used all your lives, you will be unable to play again.";
		this.onJoin = "&7[&eHardcore&7] &6Welcome back! You have &e&l{lives} &6remaining.";
		this.onDeath = "&7[&eHardcore&7] &6You have died! You have &e&l{lives} &6remaining.";

		this.noLivesRemaining = new ArrayList<>();
		this.noLivesRemaining.add("&7[&eHardcore&7] &cConnection closed.");
		this.noLivesRemaining.add("");
		this.noLivesRemaining.add("&cYou have no more lives left and thus cannot play on this server anymore.");
		this.noLivesRemaining.add("&cYou may contact an administrator to reset your lives.");
	}





	public String getOnFirstJoin(int lives) {
		return ChatColor.translateAlternateColorCodes('&', onFirstJoin)
			.replace("{lives}", Integer.toString(lives));
	}

	public String getOnJoin(int lives) {
		return ChatColor.translateAlternateColorCodes('&', onJoin)
			.replace("{lives}", Integer.toString(lives));
	}

	public String getOnDeath(int lives) {
		return ChatColor.translateAlternateColorCodes('&', onDeath)
			.replace("{lives}", Integer.toString(lives));
	}

	public String getNoLivesRemaining() {
		return ChatColor.translateAlternateColorCodes('&', String.join("\n", noLivesRemaining));
	}





	@Override
	public void load(Configuration config) {
		this.enabled = EnabledRule.parse(requireNonNull(config.getString("Settings.Enabled")));
		this.defaultLives = config.getInt("Settings.DefaultLives");

		this.onFirstJoin = requireNonNull(config.getString("Message.OnFirstJoin")).trim();
		this.onJoin = requireNonNull(config.getString("Message.OnJoin")).trim();
		this.onDeath = requireNonNull(config.getString("Message.OnDeath")).trim();
		this.noLivesRemaining = requireNonNull(config.getStringList("Message.NoLivesRemaining"));
	}

	@Override
	public void save(Configuration config) {
		config.set("Settings.Enabled", this.enabled);
		config.set("Settings.DefaultLives", this.defaultLives);

		config.set("Message.OnFirstJoin", this.onFirstJoin);
		config.set("Message.OnJoin", this.onJoin);
		config.set("Message.OnDeath", this.onDeath);
		config.set("Message.NoLivesRemaining", this.noLivesRemaining);
	}

	protected void require(boolean condition, String message) throws IllegalArgumentException {
		if (!condition) {
			throw new IllegalArgumentException(message);
		}
	}

	@Override
	public void validate() throws IllegalArgumentException {
		require(enabled != null, "'Settings.Enabled' is not a valid value.");
		require(defaultLives > 0, "'Settings.DefaultLives' cannot be " + defaultLives);

		require(!onFirstJoin.isEmpty(), "Invalid 'Message.OnFirstJoin'.");
		require(!onJoin.isEmpty(), "Invalid 'Message.OnJoin'.");
		require(!onDeath.isEmpty(), "Invalid 'Message.OnDeath'.");
		require(!noLivesRemaining.isEmpty(), "Invalid 'Message.NoLivesRemaining'.");
	}

}
