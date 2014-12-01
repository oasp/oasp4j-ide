package io.oasp.ide.eclipse.configurator.constants;

/**
 * 
 * @author trippl
 * 
 */
public abstract class Strings {

  /**
   * Name of the Application.
   */
  public static final String APPLICATION_NAME = "EclipseConfigurator";

  /**
   * The systems file separator character.
   */
  public static final String FILE_SEPARATOR = System.getProperty("file.separator");

  /**
   * The {@link java.io.File#getName() name} of the {@link java.io.File#isDirectory() folder} with the configuration
   * templates for the initial setup of an workspace.
   */
  public static final String FOLDER_SETUP = "setup";

  /**
   * The {@link java.io.File#getName() name} of the {@link java.io.File#isDirectory() folder} with the configuration
   * templates for the update of an workspace.
   */
  public static final String FOLDER_UPDATE = "update";

  /**
   * The {@link java.io.File#getName() name} of the {@link java.io.File#isDirectory() folder} with the plugin
   * configuration in the eclipse workspace.
   */
  public static final String FOLDER_PLUGINS = ".plugins";

  /**
   * The {@link java.io.File#getName() name} of the {@link java.io.File#isDirectory() folder} with the metadata in the
   * eclipse workspace.
   */
  public static final String FOLDER_METADATA = ".metadata";

  /** The directory where java was executed from. */
  public static final String CURRENT_WORKING_DIRECTORY = System.getProperty("user.dir");

  // public static final String CLIENT_ENV_HOME = "C:\\Users\\trippl\\Desktop\\Projekte\\Client-PE\\test";

  /**
   * Variable to be replaced with CLIENT_ENV_HOME.
   */
  public static final String CLIENT_ENV_HOME_VARIABLE = "client.env.home";

  /**
   * REG_EX for pattern ${} e.g. ${client.env.home}.
   */
  public static final String REPLACEMENT_REG_EX = "\\$\\{([^\\}]+)\\}";

  /**
   * Prefix of the replacement pattern.
   */
  public static final String PATTERN_PREFIX = "${";

  /**
   * Suffix of the replacement pattern.
   */
  public static final String PATTERN_SUFFIX = "}";

  /** {@link System#getenv(String) Name of environment variable} for eclipse workspace path. */
  public static final String ECLIPSE_WORKSPACE_PATH = "WORKSPACE_PATH";

  /** {@link System#getenv(String) Name of environment variable} with path to replacement properties. */
  public static final String REPLACEMENT_PATTERNS_PATH = "REPLACEMENT_PATTERNS_PATH";

  /** {@link System#getenv(String) Name of environment variable} for eclipse configuration templates path. */
  public static final String ECLIPSE_TEMPLATES_PATH = "ECLIPSE_TEMPLATES_PATH";

}
