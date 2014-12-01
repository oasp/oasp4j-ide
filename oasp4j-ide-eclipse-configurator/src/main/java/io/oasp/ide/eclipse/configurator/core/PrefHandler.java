package io.oasp.ide.eclipse.configurator.core;

import io.oasp.ide.eclipse.configurator.entity.SortedProperties;
import io.oasp.ide.eclipse.configurator.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Class to resolve variables within .pref files.
 * 
 * @author trippl
 * 
 */
public class PrefHandler {

  /**
   * {@link Resolver} to resolve variables within pref files.
   */
  private Resolver resolver;

  /**
   * Creates a new {@link PrefHandler} that uses the given {@link Resolver} to resolve variables within pref files.
   * 
   * @param resolver - resolver for variables.
   */
  public PrefHandler(Resolver resolver) {

    this.resolver = resolver;
  }

  /**
   * Creates or updates the workspacePrefFile with setupPrefFile or updatePrefFile. If the workspacePrefFile does not
   * exist, the workspacePrefFile will be the setupPrefFile merged with the updatePrefFile. If the workspacePrefFile
   * does exist, it will be merged with the updatePrefFile.
   * 
   * @param workspacePrefFile - the prefFile to be updated.
   * @param setupPrefFile - prefFile needed for creation.
   * @param updatePrefFile - prefFile need for creation and update.
   */
  public void update(File workspacePrefFile, File setupPrefFile, File updatePrefFile) {

    SortedProperties properties = new SortedProperties();
    if (workspacePrefFile.exists()) {
      mergeProperties(properties, workspacePrefFile);
    } else if (setupPrefFile.exists()) {
      mergeProperties(properties, setupPrefFile);
    }
    mergeProperties(properties, updatePrefFile);
    resolveVariables(properties);
    writeProperties(properties, workspacePrefFile);
  }

  /**
   * Saves the changes in the workspacePrefFile into the updatePrefFile.
   * 
   * @param workspacePrefFile - new prefFile
   * @param updatePrefFile - old prefFile to be updated.
   * @param saveNewProperties - specifies if new properties are saved as well.
   */
  public void saveChanges(File workspacePrefFile, File updatePrefFile, boolean saveNewProperties) {

    if (workspacePrefFile.exists()) {
      SortedProperties changes = getChanges(updatePrefFile, workspacePrefFile, saveNewProperties);
      if (!changes.isEmpty()) {
        SortedProperties updatedPropeties = updateProperties(changes, updatePrefFile);
        if (!updatedPropeties.isEmpty()) {
          reResolveVariables(updatedPropeties);
          writeProperties(updatedPropeties, updatePrefFile);
          Log.LOGGER.info("Saved changes in " + workspacePrefFile.getName() + " to: "
              + updatePrefFile.getAbsolutePath());
        }
      }
    }
  }

  /**
   * Calls {@link Resolver#reResolveVariables(String)} for every property's value within the specified
   * {@link SortedProperties}.
   * 
   * @param properties - Properties to be re resolved.
   */
  public void reResolveVariables(SortedProperties properties) {

    Set<Object> keys = properties.keySet();
    for (Object key : keys) {
      String value = properties.getProperty(key.toString());
      properties.setProperty(key.toString(), this.resolver.reResolveVariables(value));
    }
  }

  /**
   * Calls {@link Resolver#resolveVariables(String)} for every property's value within the specified
   * {@link SortedProperties}.
   * 
   * @param properties - properties to be resolved.
   */
  private void resolveVariables(SortedProperties properties) {

    Set<Object> keys = properties.keySet();
    for (Object key : keys) {
      String value = properties.getProperty(key.toString());
      properties.setProperty(key.toString(), this.resolver.resolveVariables(value));
    }
  }

  /**
   * Writes the specified {@link SortedProperties} into the specified {@link File}.
   * 
   * @param properties - properties to be written.
   * @param file - destination file.
   */
  private void writeProperties(SortedProperties properties, File file) {

    File parentDir = file.getParentFile();
    if (!parentDir.exists()) {
      if (!parentDir.mkdirs()) {
        Log.LOGGER.log(Level.ALL, "Could not create required directories for file: " + file.getAbsolutePath());
        return;
      }
    }
    OutputStream os = null;
    try {
      os = new FileOutputStream(file);
      properties.store(os, null);
    } catch (IOException e) {
      Log.LOGGER.log(Level.ALL, "Could not write properties", e.getStackTrace());
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (IOException e) {
          Log.LOGGER.log(Level.ALL, "Could not close OutputStream", e.getStackTrace());
        }
      }
    }
  }

  /**
   * Merges the {@link SortedProperties} within the specified pref {@link File} with the specified
   * {@link SortedProperties}.
   * 
   * @param properties - properties to be merged into.
   * @param file - pref file to be read.
   */
  private void mergeProperties(SortedProperties properties, File file) {

    InputStream is = null;
    try {
      is = new FileInputStream(file);
      Reader reader = new InputStreamReader(is, "UTF-8");
      properties.load(reader);
    } catch (Exception e) {
      Log.LOGGER.log(Level.ALL, "Could not read properties", e.getStackTrace());
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (Exception e) {
          Log.LOGGER.log(Level.ALL, "Could not close InputStream", e.getStackTrace());
        }
      }
    }
  }

  /**
   * Updates the {@link SortedProperties} within specified pref {@link File} by merging them with the given
   * {@link SortedProperties}.
   * 
   * @param properties - the new properties.
   * @param file - pref file to be updated.
   * @return the updated properties.
   */
  private SortedProperties updateProperties(SortedProperties properties, File file) {

    InputStream is = null;
    SortedProperties updatedProperties = new SortedProperties();
    try {
      is = new FileInputStream(file);
      Reader reader = new InputStreamReader(is, "UTF-8");
      updatedProperties.load(reader);
      updatedProperties.putAll(properties);
    } catch (Exception e) {
      Log.LOGGER.log(Level.ALL, "Could not read properties", e.getStackTrace());
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (Exception e) {
          Log.LOGGER.log(Level.ALL, "Could not close InputStream", e.getStackTrace());
        }
      }
    }
    return updatedProperties;
  }

  /**
   * Gets the changes in the specified pref {@link File}s.
   * 
   * @param prefFile1 - the old pref file.
   * @param prefFile2 - the new pref file.
   * @param getNewProperties - specifies if new properties are read as well.
   * @return properties containing the changes.
   */
  private SortedProperties getChanges(File prefFile1, File prefFile2, boolean getNewProperties) {

    SortedProperties prefFile1Properties = loadProperties(prefFile1);
    resolveVariables(prefFile1Properties);
    SortedProperties prefFile2Properties = loadProperties(prefFile2);
    SortedProperties changes = new SortedProperties();

    for (Map.Entry<Object, Object> entry : prefFile2Properties.entrySet()) {
      Object prefFile2Value = prefFile2Properties.get(entry.getKey());
      Object prefFile1Value = prefFile1Properties.get(entry.getKey());
      if (prefFile1Value == null && !getNewProperties) {
        continue;
      }
      if (!prefFile2Value.equals(prefFile1Value)) {
        changes.put(entry.getKey(), prefFile2Value);
      }
    }
    return changes;
  }

  /**
   * Loads the {@link SortedProperties} within specified pref {@link File}.
   * 
   * @param file - pref file.
   * @return properties loaded.
   */
  private SortedProperties loadProperties(File file) {

    SortedProperties properties = new SortedProperties();
    InputStream is = null;
    try {
      is = new FileInputStream(file);
      Reader reader = new InputStreamReader(is, "UTF-8");
      properties.load(reader);
    } catch (FileNotFoundException e) {
      Log.LOGGER.log(Level.ALL, "Could not read properties, file not found.", e.getStackTrace());
    } catch (UnsupportedEncodingException e) {
      // should not occur
    } catch (IOException e) {
      Log.LOGGER.log(Level.ALL, "Could not read properties", e.getStackTrace());
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          Log.LOGGER.log(Level.ALL, "Could not close InputStream", e.getStackTrace());
        }
      }
    }
    return properties;
  }

}
