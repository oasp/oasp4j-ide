package io.oasp.ide.eclipse.configurator.core;

import io.oasp.ide.eclipse.configurator.constants.Strings;
import io.oasp.ide.eclipse.configurator.entity.SortedProperties;
import io.oasp.ide.eclipse.configurator.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Class to create and update eclipse workspaces.
 *
 * @author trippl
 *
 */
public class Configurator {

  /**
   * Handler for .pref files.
   */
  private final PrefHandler prefHandler;

  /**
   * Handler for .xml files.
   */
  private final XmlHandler xmlHandler;

  /**
   * Path to the eclipse workspace.
   */
  private final String workspacePath;

  /**
   * Relative path to eclipse workspace setup templates.
   */
  private final String pluginsSetupDirectoryPath;

  /**
   * Relative path to eclipse workspace update templates.
   */
  private final String pluginsUpdateDirectoryPath;

  /**
   * Creates a new {@link Configurator} with the given paths.
   *
   * @param workspacePath - relative path to the workspace's plug-ins folder
   * @param replacementPatternsPath - relative path to replacement patterns.
   * @param eclipseTemplatesPath - relative path to eclipse workspace templates.
   */
  public Configurator(String workspacePath, String replacementPatternsPath, String eclipseTemplatesPath) {

    this.workspacePath = workspacePath;
    Resolver resolver = createResolver(replacementPatternsPath, Strings.REPLACEMENT_REG_EX);
    this.prefHandler = new PrefHandler(resolver);
    this.xmlHandler = new XmlHandler(resolver);
    this.pluginsSetupDirectoryPath = eclipseTemplatesPath + Strings.FILE_SEPARATOR + Strings.FOLDER_SETUP;
    this.pluginsUpdateDirectoryPath = eclipseTemplatesPath + Strings.FILE_SEPARATOR + Strings.FOLDER_UPDATE;
  }

  /**
   * Creates/updates the workspace. See {@link #mergeFiles(EclipseWorkspaceFile)} for further details.
   */
  public void updateWorkspace() {

    for (EclipseWorkspaceFile file : collectWorkspaceFiles()) {
      mergeFiles(file);
    }
  }

  /**
   * Saves changes in the workspace .pref files into the update .pref files.
   *
   * @param saveNewProperties - specifies if new properties are saved as well.
   */
  public void saveChangesInWorkspace(boolean saveNewProperties) {

    for (EclipseWorkspaceFile file : collectWorkspaceFiles()) {
      saveChanges(file.workspaceFile, file.updateFile, saveNewProperties);
    }
  }

  /**
   * Creates or updates the workspaceFile with setupFile or updateFile. If the workspaceFile does not exist, the
   * workspaceFile will be the setupFile merged with the updateFile. If the workspaceFile does exist, it will be merged
   * with the updateFile.
   *
   * @param file is the {@link EclipseWorkspaceFile}.
   */
  protected void mergeFiles(EclipseWorkspaceFile file) {

    if (file.relativePath.endsWith(".prefs")) {
      this.prefHandler.update(file.workspaceFile, file.setupFile, file.updateFile);
    } else {
      File source;
      if (file.updateFile.exists()) {
        source = file.updateFile;
      } else if (file.setupFile.exists() && !file.workspaceFile.exists()) {
        source = file.setupFile;
      } else {
        return;
      }
      if ((file.relativePath.endsWith(".xml")) || (file.relativePath.endsWith(".xmi"))
          || (file.relativePath.endsWith(".launch"))) {
        this.xmlHandler.update(source, file.workspaceFile);
      } else {
        // if (!file.relativePath.endsWith(".dat")) {
        // Log.LOGGER.warning("Unknown filetype: " + file.relativePath);
        // }
        copy(source, file.workspaceFile);
      }
    }
  }

  /**
   * Copies the {@link File} <code>source</code> to <code>destination</code>. Will overwrite <code>destination</code>
   * without warning.
   *
   * @param source is the source file to copy.
   * @param destination is the destination file to copy.
   */
  private void copy(File source, File destination) {

    destination.getParentFile().mkdirs();
    try {
      FileInputStream sourceStream = new FileInputStream(source);
      try {
        FileOutputStream destinationStream = new FileOutputStream(destination);
        try {
          FileChannel sourceChannel = sourceStream.getChannel();
          sourceChannel.transferTo(0, sourceChannel.size(), destinationStream.getChannel());
        } finally {
          try {
            destinationStream.close();
          } catch (Exception e) {
            Log.LOGGER.warning("Failed to close file: " + destination.getAbsolutePath());
          }
        }
      } finally {
        try {
          sourceStream.close();
        } catch (Exception e) {
          Log.LOGGER.warning("Failed to close file: " + source.getAbsolutePath());
        }
      }
    } catch (IOException e) {
      Log.LOGGER.severe("Failed to copy file into workspace: " + source.getAbsolutePath() + ", error: " + e.toString());
      e.printStackTrace();
    }
  }

  /**
   * Saves the changes of the specified pref files into updateFile.
   *
   * @param workspaceFile - the workspace pref file (new pref file).
   * @param updateFile - the updated pref file (pref file to be updated).
   * @param saveNewProperties - specifies if new properties are saved as well.
   */
  private void saveChanges(File workspaceFile, File updateFile, boolean saveNewProperties) {

    if (workspaceFile.exists() && workspaceFile.getName().endsWith(".prefs")) {
      this.prefHandler.saveChanges(workspaceFile, updateFile, saveNewProperties);
    }
  }

  /**
   * Creates a {@link Resolver} with the replacement patterns specified by the file at the replacementPatternsPath and
   * the given regEx to find variables to resolve.
   *
   * @param replacementPatternsPath - path to the replacement patterns file.
   * @param regEx - regEx to search for.
   * @return the created resolver.
   */
  private Resolver createResolver(String replacementPatternsPath, String regEx) {

    SortedProperties replacements =
        readReplacementsConfig(Strings.CURRENT_WORKING_DIRECTORY + Strings.FILE_SEPARATOR + replacementPatternsPath);
    replacements.put(Strings.CLIENT_ENV_HOME_VARIABLE, Strings.CURRENT_WORKING_DIRECTORY);
    return new Resolver(replacements, regEx);
  }

  /**
   * Reads the patterns and their replacements out of the file specified by the configPath and saves them into
   * {@link Properties}.
   *
   * @param configPath - path to the pref file containing the replacement patterns.
   * @return {@link Properties} containing the replacement patterns.
   */
  private SortedProperties readReplacementsConfig(String configPath) {

    File replacementsConfig = new File(configPath);
    SortedProperties replacements = new SortedProperties();
    if (replacementsConfig.exists()) {
      InputStream is = null;
      try {
        is = new FileInputStream(replacementsConfig);
        Reader reader = new InputStreamReader(is, "UTF-8");
        replacements.load(reader);
      } catch (Exception e) {
        Log.LOGGER.log(Level.WARNING, "Could not read replacement properties, continuing without these.",
            e.getStackTrace());
      }
    }
    return replacements;
  }

  private static String getSystemVariable(String variable, boolean required) {

    String value = System.getenv(variable);
    if (value == null) {
      if (required) {
        System.err.println("Variable '" + variable
            + "' was not found. This variable is required in order to run the application.");
        System.exit(-1);
      } else {
        Log.LOGGER.warning("Variable '" + variable + "' was not found. This is an optional variable - resuming.");
      }
    }
    return value;
  }

  /**
   * Runs the application. <br>
   * In order run properly this application requires the following system environment variables:
   * <p>
   * <b>WORKSPACE_PLUGINS_PATH</b> - relative path to the workspace that has to be created. <br>
   * <b>ECLIPSE_TEMPLATES_PATH</b> - relative path to the eclipse settings templates.
   * <p>
   * Optional system environment variables for the application are:
   * <p>
   * <b>REPLACEMENT_PATTERNS_PATH</b> - relative path to the properties file providing additional replacement patterns.
   *
   * @param args - main requires one argument: <br>
   *        args[0] - "-u" for update or "-c" for changes or "-cn" for changes including new properties.
   */
  public static void main(String[] args) {

    logCall(args);
    String workspacePath = getSystemVariable(Strings.ECLIPSE_WORKSPACE_PATH, true);
    String replacementPatternsPath = getSystemVariable(Strings.REPLACEMENT_PATTERNS_PATH, false);
    String eclipseTemplatesPath = getSystemVariable(Strings.ECLIPSE_TEMPLATES_PATH, true);

    if (args.length <= 0) {
      System.err.println("Too few arguments");
      System.exit(1);
    } else if (args.length > 1) {
      System.err.println("Too many arguments");
      System.exit(1);
    } else {
      File pluginsUpdateDirectory =
          new File(Strings.CURRENT_WORKING_DIRECTORY + Strings.FILE_SEPARATOR + eclipseTemplatesPath
              + Strings.FILE_SEPARATOR + Strings.FOLDER_UPDATE);
      File metadataFolder = new File(pluginsUpdateDirectory, Strings.FOLDER_METADATA);
      if (!metadataFolder.isDirectory()) {
        // Client-PE is suggested to have a ".metadata" folder in setup/update
        // However old client-PEs directly have the content of ".metadata" or ".plugins" in setup/update
        // This code allows downward compatibility for legacy format
        String workspacePathSuffix = Strings.FOLDER_METADATA;
        File pluginsFolder = new File(pluginsUpdateDirectory, Strings.FOLDER_PLUGINS);
        if (!pluginsFolder.isDirectory()) {
          // If not even a ".plugins" folder is present in setup/update, then
          // expect legacy format with content of ".plugins" folder.
          workspacePathSuffix = workspacePathSuffix + Strings.FILE_SEPARATOR + Strings.FOLDER_PLUGINS;
        }
        workspacePath = workspacePath + Strings.FILE_SEPARATOR + workspacePathSuffix;
        Log.LOGGER.info("No .metadata folder found. Relocated to " + workspacePathSuffix);
      }

      Configurator configurator = new Configurator(workspacePath, replacementPatternsPath, eclipseTemplatesPath);

      if (args[0].equals("-u")) {
        Log.LOGGER.info("Updating workspace");
        configurator.updateWorkspace();
        Log.LOGGER.info("Completed");
      } else if (args[0].equals("-c")) {
        Log.LOGGER.info("Merging workspace changes back into templates (excluding new properties)");
        configurator.saveChangesInWorkspace(false);
        Log.LOGGER.info("Completed");
      } else if (args[0].equals("-cn")) {
        Log.LOGGER.info("Merging workspace changes back into templates (including new properties)");
        configurator.saveChangesInWorkspace(true);
        Log.LOGGER.info("Completed");
      } else {
        System.err.println("Invalid argument:" + args[0]);
      }
    }
  }

  private static void logCall(String[] args) {

    StringBuilder buffer = new StringBuilder();
    buffer.append(Configurator.class.getName());
    for (String arg : args) {
      buffer.append(' ');
      buffer.append(arg);
    }
    Log.LOGGER.info(buffer.toString());
  }

  /**
   * @return a {@link Collection} with all available {@link EclipseWorkspaceFile}.
   */
  private Collection<EclipseWorkspaceFile> collectWorkspaceFiles() {

    Map<String, EclipseWorkspaceFile> fileMap = new HashMap<String, EclipseWorkspaceFile>();
    collectWorkspaceFiles(new File(this.pluginsUpdateDirectoryPath), fileMap);
    collectWorkspaceFiles(new File(this.pluginsSetupDirectoryPath), fileMap);
    Collection<EclipseWorkspaceFile> values = fileMap.values();
    Log.LOGGER.info("Collected " + values.size() + " configuration files.");
    return values;
  }

  /**
   * This method scans a directory for (configuration template) files and adds them to the given {@link Map}.
   *
   * @param path is the current path of the scan relative to the starting point. Should be the empty string on initial
   *        call.
   * @param directory is the current directory to scan.
   * @param fileMap is the {@link Map} where the {@link File#isFile() files} are collected.
   */
  private void collectWorkspaceFiles(File directory, Map<String, EclipseWorkspaceFile> fileMap) {

    if (!directory.isDirectory()) {
      throw new IllegalArgumentException("Expected directory: " + directory.getPath());
    }
    collectWorkspaceFiles("", directory, fileMap);
  }

  /**
   * This method recursively scans a directory (eclipse template folder such as {@link Strings#FOLDER_UPDATE} or
   * {@link Strings#FOLDER_SETUP}) for (configuration template) files and adds them to the given {@link Map}.
   *
   * @param path is the current path of the scan relative to the starting point. Should be the empty string on initial
   *        call.
   * @param directory is the current directory to scan.
   * @param fileMap is the {@link Map} where the {@link File#isFile() files} are collected.
   */
  private void collectWorkspaceFiles(String path, File directory, Map<String, EclipseWorkspaceFile> fileMap) {

    for (File currentFile : directory.listFiles()) {
      String currentPath;
      if (path.isEmpty()) {
        currentPath = currentFile.getName();
      } else {
        currentPath = path + Strings.FILE_SEPARATOR + currentFile.getName();
      }
      if (currentFile.isDirectory()) {
        collectWorkspaceFiles(currentPath, currentFile, fileMap);
      } else if (isAcceptFile(currentFile)) {
        if (!fileMap.containsKey(currentPath)) {
          fileMap.put(currentPath, new EclipseWorkspaceFile(currentPath));
        }
      }
    }

  }

  /**
   * @param currentFile is the {@link File} to check.
   * @return <code>true</code> if the file is accepted as template, <code>false</code> if the file should be ignored.
   */
  private boolean isAcceptFile(File currentFile) {

    if (currentFile.getName().endsWith(".bak")) {
      return false;
    }
    return true;
  }

  /**
   * This class represents a configuration file in the eclipse workspace
   *
   * @author hohwille
   */
  public class EclipseWorkspaceFile {

    /** The path relative to workspace and setup/update directory. */
    private final String relativePath;

    /** The file in the eclipse workspace. May not {@link File#exists() exist} on initial setup. */
    private final File workspaceFile;

    /** The file template for the initial setup. */
    private final File setupFile;

    /** The file template for update. */
    private final File updateFile;

    /**
     * Creates a new instance.
     *
     * @param relativePath is the relative path in the workspace (e.g.
     *        ".plugins/org.eclipse.core.runtime/.settings/org.eclipse.jdt.core.prefs")
     */
    public EclipseWorkspaceFile(String relativePath) {

      super();
      if (relativePath == null) {
        throw new NullPointerException("relativePath");
      }
      this.relativePath = relativePath;
      this.workspaceFile = new File(Configurator.this.workspacePath + Strings.FILE_SEPARATOR + relativePath);
      this.setupFile = new File(Configurator.this.pluginsSetupDirectoryPath + Strings.FILE_SEPARATOR + relativePath);
      this.updateFile = new File(Configurator.this.pluginsUpdateDirectoryPath + Strings.FILE_SEPARATOR + relativePath);
      assert (this.setupFile.exists() || this.updateFile.exists());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

      return this.relativePath.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {

      if (this == obj) {
        return true;
      }
      if ((obj == null) || (obj.getClass() != EclipseWorkspaceFile.class)) {
        return false;
      }
      EclipseWorkspaceFile other = (EclipseWorkspaceFile) obj;
      return this.relativePath.equals(other.relativePath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

      return this.relativePath;
    }
  }
}
