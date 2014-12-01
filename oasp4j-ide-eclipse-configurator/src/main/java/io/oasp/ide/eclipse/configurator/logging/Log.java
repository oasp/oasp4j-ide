package io.oasp.ide.eclipse.configurator.logging;

import io.oasp.ide.eclipse.configurator.constants.Strings;
import io.oasp.ide.eclipse.configurator.core.Configurator;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class provides a {@link Logger} for logging purposes.
 * 
 * @author trippl
 * 
 */
public class Log {

  /**
   * Logger.
   */
  public static final Logger LOGGER = Logger.getLogger(Configurator.class.getName());

  static {
    FileHandler logFileHandler;
    try {
      logFileHandler = new FileHandler(Strings.APPLICATION_NAME + ".log");
      logFileHandler.setLevel(Level.ALL);
      logFileHandler.setFormatter(new SimpleFormatter());
      LOGGER.addHandler(logFileHandler);
    } catch (Exception e) {
      System.err.println("Could not open log file");
    }
  }
}
