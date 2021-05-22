package net.tassia.hardcore;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.requireNonNull;

public class HardcoreConfiguration implements IConfiguration {

	public EnabledRule enabled;
	public int defaultLives;

	public String prefix;

	public String deletingData;
	public String fetchingData;
	public String updatingData;
	public String resettingData;
	public String rebuildingData;

	public String onFirstJoin;
	public String onJoin;
	public String onDeath;
	public List<String> noLivesRemaining;

	public String resultCheck;
	public String resultDelete;
	public String resultGive;
	public String resultRebuild;
	public String resultReset;
	public String resultTake;

	public String invalidUsage;
	public String playerNotFound;
	public String playerMatchesMultiple;
	public String sqlError;
	public String playerOnlineWhenDeleting;
	public String notANumber;
	public String notAPositiveNumber;

	public HardcoreConfiguration() {
		this.enabled = EnabledRule.INHERITED;
		this.defaultLives = 3;

		this.prefix = "&7[&eHardcore&7]";

		this.deletingData = "{prefix} Deleting data...";
		this.fetchingData = "{prefix} Fetching data...";
		this.updatingData = "{prefix} Updating data...";
		this.resettingData = "{prefix} Resetting data...";
		this.rebuildingData = "{prefix} Rebuilding data...";

		this.onFirstJoin = "{prefix} &6This server is running on &ehardcore mode&6. Initially you have &e&l{lives} &6lives. After you have used all your lives, you will be unable to play again.";
		this.onJoin = "{prefix} &6Welcome back! You have &e&l{lives} &6remaining.";
		this.onDeath = "{prefix} &6You have died! You have &e&l{lives} &6remaining.";

		this.noLivesRemaining = new ArrayList<>();
		this.noLivesRemaining.add("{prefix} &cConnection closed.");
		this.noLivesRemaining.add("");
		this.noLivesRemaining.add("&cYou have no more lives left and thus cannot play on this server anymore.");
		this.noLivesRemaining.add("&cYou may contact an administrator to reset your lives.");

		this.resultCheck = "{prefix} &e{target} &6has &e&l{lives} &6remaining.";
		this.resultDelete = "{prefix} &eYou &adeleted all data associated with &e{target}&a.";
		this.resultGive = "{prefix} &eYou &agave &e{amount} &alives to &e{target}&a.";
		this.resultRebuild = "{prefix} &eYou &arebuilt data associated with &e{target}&a.";
		this.resultReset = "{prefix} &eYou &areset &e{target}&as lives.";
		this.resultTake = "{prefix} &eYou &atook &e{amount} &alives from &e{target}&a.";

		this.invalidUsage = "{prefix} &cCorrect usage: &o{usage}";
		this.playerNotFound = "{prefix} &cThis player was not found.";
		this.playerMatchesMultiple = "{prefix} &cMultiple players with name &e{name} &cwere found.";
		this.sqlError = "{prefix} &cSomething went wrong while querying the database. Check the console for details.";
		this.playerOnlineWhenDeleting = "{prefix} &e{target} &cis currently online. To prevent data corruption you cannot delete data of online players.";
		this.notANumber = "{prefix} &e{input} &cis not a valid number.";
		this.notAPositiveNumber = "{prefix} &e{input} &cis not a positive number.";
	}





	public String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&', prefix);
	}



	public String getDeletingData() {
		return ChatColor.translateAlternateColorCodes('&', deletingData)
			.replace("{prefix}", getPrefix());
	}

	public String getFetchingData() {
		return ChatColor.translateAlternateColorCodes('&', fetchingData)
			.replace("{prefix}", getPrefix());
	}

	public String getUpdatingData() {
		return ChatColor.translateAlternateColorCodes('&', updatingData)
			.replace("{prefix}", getPrefix());
	}

	public String getResettingData() {
		return ChatColor.translateAlternateColorCodes('&', resettingData)
			.replace("{prefix}", getPrefix());
	}

	public String getRebuildingData() {
		return ChatColor.translateAlternateColorCodes('&', rebuildingData)
			.replace("{prefix}", getPrefix());
	}



	public String getOnFirstJoin(int lives) {
		return ChatColor.translateAlternateColorCodes('&', onFirstJoin)
			.replace("{prefix}", getPrefix())
			.replace("{lives}", Integer.toString(lives));
	}

	public String getOnJoin(int lives) {
		return ChatColor.translateAlternateColorCodes('&', onJoin)
			.replace("{prefix}", getPrefix())
			.replace("{lives}", Integer.toString(lives));
	}

	public String getOnDeath(int lives) {
		return ChatColor.translateAlternateColorCodes('&', onDeath)
			.replace("{prefix}", getPrefix())
			.replace("{lives}", Integer.toString(lives));
	}

	public String getNoLivesRemaining() {
		return ChatColor.translateAlternateColorCodes('&', String.join("\n", noLivesRemaining))
			.replace("{prefix}", getPrefix());
	}



	public String getResultCheck(String target, int amount) {
		return ChatColor.translateAlternateColorCodes('&', resultCheck)
			.replace("{prefix}", getPrefix())
			.replace("{target}", target)
			.replace("{amount}", Integer.toString(amount));
	}

	public String getResultDelete(String target) {
		return ChatColor.translateAlternateColorCodes('&', resultDelete)
			.replace("{prefix}", getPrefix())
			.replace("{target}", target);
	}

	public String getResultGive(String target, int amount) {
		return ChatColor.translateAlternateColorCodes('&', resultGive)
			.replace("{prefix}", getPrefix())
			.replace("{target}", target)
			.replace("{amount}", Integer.toString(amount));
	}

	public String getResultRebuild(String target) {
		return ChatColor.translateAlternateColorCodes('&', resultRebuild)
			.replace("{prefix}", getPrefix())
			.replace("{target}", target);
	}

	public String getResultReset(String target) {
		return ChatColor.translateAlternateColorCodes('&', resultReset)
			.replace("{prefix}", getPrefix())
			.replace("{target}", target);
	}

	public String getResultTake(String target, int amount) {
		return ChatColor.translateAlternateColorCodes('&', resultTake)
			.replace("{prefix}", getPrefix())
			.replace("{target}", target)
			.replace("{amount}", Integer.toString(amount));
	}

	public String getResultVersion(String version, String authors) {
		String msg = "{prefix} &6This server is running &eHardcore&6, version &e{version} &6by &e{authors}";
		return ChatColor.translateAlternateColorCodes('&', msg)
			.replace("{prefix}", getPrefix())
			.replace("{version}", version)
			.replace("{authors}", authors);
	}



	public String getInvalidUsage(String correct) {
		return ChatColor.translateAlternateColorCodes('&', invalidUsage)
			.replace("{prefix}", getPrefix())
			.replace("{usage}", correct);
	}

	public String getPlayerNotFound(String player) {
		return ChatColor.translateAlternateColorCodes('&', playerNotFound)
			.replace("{prefix}", getPrefix())
			.replace("{player}", player);
	}

	public String getPlayerMatchesMultiple(String name) {
		return ChatColor.translateAlternateColorCodes('&', playerNotFound)
			.replace("{prefix}", getPrefix())
			.replace("{name}", name);
	}

	public String getSqlError() {
		return ChatColor.translateAlternateColorCodes('&', sqlError)
			.replace("{prefix}", getPrefix());
	}

	public String getPlayerOnlineWhenDeleting(String target) {
		return ChatColor.translateAlternateColorCodes('&', sqlError)
			.replace("{prefix}", getPrefix())
			.replace("{target}", target);
	}

	public String getNotANumber(String input) {
		return ChatColor.translateAlternateColorCodes('&', sqlError)
			.replace("{prefix}", getPrefix())
			.replace("{input}", input);
	}

	public String getNotAPositiveNumber(int input) {
		return ChatColor.translateAlternateColorCodes('&', sqlError)
			.replace("{prefix}", getPrefix())
			.replace("{input}", Integer.toString(input));
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
