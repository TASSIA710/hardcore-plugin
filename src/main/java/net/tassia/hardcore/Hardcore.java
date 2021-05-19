package net.tassia.hardcore;

import net.tassia.util.ThrowingConsumer;
import net.tassia.util.ThrowingFunction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.sql.*;
import java.util.UUID;

/**
 * The base class of the Hardcore plugin.
 *
 * @since Hardcore 1.0
 * @author Tassilo
 */
public final class Hardcore {

	/**
	 * The current instance of Hardcore.
	 */
	public static Hardcore INSTANCE = null;

	/**
	 * The configuration of the plugin.
	 */
	public final HardcoreConfiguration config;

	/**
	 * The plugin instance.
	 */
	public final HardcorePlugin plugin;

	/**
	 * The database connection.
	 */
	private Connection connection;

	/**
	 * Creates a new instance of the Hardcore plugin base class.
	 *
	 * @param plugin the plugin instance
	 */
	public Hardcore(HardcorePlugin plugin) {
		this.config = new HardcoreConfiguration();
		this.plugin = plugin;
		this.connection = null;
	}





	/**
	 * Returns the current amount of lives a given player has.
	 *
	 * @param player the player whose lives to fetch
	 * @return the amount of lives the player
	 * @throws SQLException if an SQL error occurs
	 */
	public final int getLives(final UUID player) throws SQLException {
		return transaction((db) -> {
			// Select lives
			String sql = "SELECT Lives FROM hardcore_lives WHERE PlayerID = ?;";
			try (PreparedStatement stmt = db.prepareStatement(sql)) {
				stmt.setString(1, player.toString());

				// Read result
				try (ResultSet result = stmt.executeQuery()) {
					if (result.next()) {
						// Return lives
						return result.getInt(1);
					} else {
						// Fetch default lives
						int lives = config.defaultLives;

						// Insert data into table
						String sql2 = "INSERT INTO Lives (PlayerID, Lives) VALUES (?, ?);";
						try (PreparedStatement stmt2 = db.prepareStatement(sql2)) {
							stmt2.setString(1, player.toString());
							stmt2.setInt(2, lives);
							stmt2.executeUpdate();
						}

						// Return lives
						return lives;
					}
				}
			}
		});
	}

	/**
	 * Rebuilds the cached amount of lives the player has by evaluating historic deaths.
	 *
	 * @param player the player to rebuild
	 * @throws SQLException if an SQL error occurs
	 */
	public final void rebuildLives(final UUID player) throws SQLException {
		// TODO
	}

	/**
	 * Updates the cached amount of lives a player has.
	 *
	 * @param player the player to update
	 * @param lives the new cached amount of lives
	 * @throws SQLException if an SQL error occurs
	 */
	protected final void setCachedLives(final UUID player, final int lives) throws SQLException {
		// Update database
		transaction((db) -> {
			// Update row in table
			String sql = "UPDATE hardcore_lives SET Lives = ? WHERE PlayerID = ?;";
			try (PreparedStatement stmt = db.prepareStatement(sql)) {
				stmt.setInt(1, lives);
				stmt.setString(2, player.toString());
				int affected = stmt.executeUpdate();

				// Check if we affected any rows
				if (affected == 0) {
					// No rows were affected, so the player is not yet part of the database.
					// Create the player entry.
					String sql2 = "INSERT INTO Lives (PlayerID, Lives) VALUES (?, ?);";
					try (PreparedStatement stmt2 = db.prepareStatement(sql2)) {
						stmt2.setString(1, player.toString());
						stmt2.setInt(2, lives);
						stmt2.executeUpdate();
					}
				}
			}
		});

		// Is player online? Then update metadata
		Player ply = Bukkit.getPlayer(player);
		if (ply != null) {
			ply.setMetadata("HardcoreLives", new FixedMetadataValue(plugin, lives));
			if (lives <= 0) {
				ply.kickPlayer(config.getNoLivesRemaining());
			}
		}
	}

	/**
	 * Gives a given amount of lives to the player.
	 *
	 * @param player the player
	 * @param lives the amount of lives to give
	 * @throws SQLException if an SQL error occurs
	 */
	public final void giveLives(final UUID player, final int lives) throws SQLException {
		// Check lives
		if (lives <= 0) {
			throw new IllegalArgumentException("Invalid amount of lives specified. (" + lives + " <= 0)");
		}

		// Fetch current lives
		int current = getLives(player);

		// Insert reason
		// TODO

		// Update cache
		setCachedLives(player, lives);
	}

	/**
	 * Takes a given amount of lives from the player.
	 *
	 * @param player the player
	 * @param lives the amount of lives to take
	 * @throws SQLException if an SQL error occurs
	 */
	public final void takeLives(UUID player, int lives) throws SQLException {
		// Check lives
		if (lives <= 0) {
			throw new IllegalArgumentException("Invalid amount of lives specified. (" + lives + " <= 0)");
		}

		// Fetch current lives
		int current = getLives(player);
		if (lives > current) {
			lives = current;
		}

		// Insert reason
		// TODO

		// Update cache
		setCachedLives(player, lives);
	}





	/**
	 * Prepares the database by creating all required tables.
	 *
	 * @throws SQLException if an SQL error occurs
	 */
	public final void prepareDatabase() throws SQLException {
		transaction((db) -> {

			db.prepareStatement("CREATE TABLE IF NOT EXISTS hardcore_lives ("
				+ "PlayerID VARCHAR(36) NOT NULL,"
				+ "Lives INT NOT NULL,"
				+ "PRIMARY KEY (PlayerID)"
				+ ") ENGINE = InnoDB CHARACTER SET utf8_mb4;");

		});
	}





	/**
	 * Executes a transaction on the database.
	 *
	 * @param transaction the transaction body
	 * @throws SQLException if an SQL error occurs
	 */
	public final void transaction(final ThrowingConsumer<Connection, SQLException> transaction) throws SQLException {
		this.transaction((con) -> {
			transaction.invoke(con);
			return Boolean.TRUE;
		});
	}

	/**
	 * Executes a transaction on the database.
	 *
	 * @param transaction the transaction body
	 * @param <T> the return type
	 * @return the value returned from the transaction
	 * @throws SQLException if an SQL error occurs
	 */
	public final synchronized <T> T transaction(final ThrowingFunction<Connection, T, SQLException> transaction) throws SQLException {
		// Fetch database
		Connection con = this.getConnection();

		// Disable auto-committing
		con.setAutoCommit(false);

		// Execute transaction
		T result;
		try {
			result = transaction.invoke(con);
		} catch (Throwable ex) {
			con.rollback();
			con.setAutoCommit(true);
			throw ex;
		}

		// Commit changes and re-enable auto-committing
		con.commit();
		con.setAutoCommit(true);
		return result;
	}

	/**
	 * Returns the currently active connection, or creates a new one of necessary.
	 *
	 * @return the database connection
	 */
	public final Connection getConnection() {
		if (this.connection == null) {
			this.connection = connectDatabase();
			return this.connection;
		}
		return this.connection;
	}

	/**
	 * Connects to the database.
	 *
	 * @return the created database
	 */
	public final Connection connectDatabase() {
		try {
			Class.forName("org.sqlite.JDBC");
			return DriverManager.getConnection("jdbc:sqlite:./plugins/Hardcore/data.db");
		} catch (Exception ex) {
			throw new RuntimeException("Failed to connect to database.", ex);
		}
	}

	/**
	 * Disconnects from the database.
	 */
	public final void disconnectDatabase() {
		try {
			if (this.connection != null && !this.connection.isClosed()) {
				this.connection.close();
			}
			this.connection = null;
		} catch (SQLException ex) {
			throw new RuntimeException("Failed to disconnect from database.", ex);
		}
	}

}
