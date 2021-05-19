package net.tassia.hardcore;

/**
 * This rule defines whether the plugin should be loaded or not.
 *
 * @since Hardcore 1.0
 * @author Tassilo
 */
public enum EnabledRule {

	/**
	 * The plugin should always be enabled.
	 */
	ALWAYS_ON,

	/**
	 * The plugin should be enabled, if hardcore mode is enabled in the <code>server.properties</code> file.
	 */
	INHERITED,

	/**
	 * The plugin should never be enabled.
	 */
	ALWAYS_OFF;

	/**
	 * Parses the given string.
	 *
	 * @param str the string to parse
	 * @return the parsed rule, or <code>null</code> on failure
	 */
	public static EnabledRule parse(String str) {
		switch (str.toLowerCase()) {
			case "true":
			case "yes":
				return ALWAYS_ON;

			case "false":
			case "no":
				return ALWAYS_OFF;

			case "inherit":
			case "inherited":
				return INHERITED;

			default:
				return null;
		}
	}

}
