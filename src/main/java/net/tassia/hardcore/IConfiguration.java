package net.tassia.hardcore;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Interface for basic configurations.
 *
 * @since Hardcore 1.0
 * @author Tassilo
 */
public interface IConfiguration {

	/**
	 * Loads this configuration from the given {@link Configuration}.
	 *
	 * @param config the configuration to load from
	 */
	void load(Configuration config);

	/**
	 * Stores this configuration into the given {@link Configuration}.
	 *
	 * @param config the configuration to store into
	 */
	void save(Configuration config);

	/**
	 * Ensures this config is populated by valid values (e.g. that all ports are in the range of 0 to 65535, etc.)
	 *
	 * @throws IllegalArgumentException if this config is not valid
	 */
	void validate() throws IllegalArgumentException;



	/**
	 * Loads this config from the given file.
	 *
	 * @param file the file to load this config from
	 * @throws IOException if an I/O error occurs
	 * @throws InvalidConfigurationException if the file is not a valid YAML file
	 */
	default void load(File file) throws IOException, InvalidConfigurationException {
		YamlConfiguration config = new YamlConfiguration();
		config.load(file);
		load(config);
	}

	/**
	 * Saves this config to the given file.
	 *
	 * @param file the file to save this config to
	 * @throws IOException if an I/O error occurs
	 */
	default void save(File file) throws IOException {
		YamlConfiguration config = new YamlConfiguration();
		save(config);
		config.save(file);
	}

}
