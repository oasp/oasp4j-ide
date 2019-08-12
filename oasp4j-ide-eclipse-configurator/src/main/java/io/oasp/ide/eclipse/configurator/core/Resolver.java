package io.oasp.ide.eclipse.configurator.core;

import io.oasp.ide.eclipse.configurator.constants.Strings;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to resolve variables in {@link String}s
 * 
 * @author trippl
 * 
 */
public class Resolver {

  /**
   * {@link Properties} containing variables and their replacement.
   */
  private Properties replacementPatterns;

  /**
   * RegEx to look for to find variables.
   */
  private String replacementRegEx;
  
  
  /**
   * Creates a new {@link Resolver} with the given <code>replacementPatterns</code> and <code>regEx</code>.
   * 
   * @param replacementPatterns - containing variables and their replacement.
   * @param regEx - regEx to look for to find variables.
   */
  public Resolver(Properties replacementPatterns, String regEx) {

    this.replacementPatterns = replacementPatterns;
    this.replacementRegEx = regEx;
  }

  /**
   * @return the {@link Pattern#compile(String) regular expression} for replacement variables.
   */
  public String getReplacementRegEx() {

    return this.replacementRegEx;
  }

  /**
   * Adds an entry with the given <code>pattern</code> and corresponding <code>replacement</code>.
   * 
   * @param pattern - pattern to look for.
   * @param replacement - replacement for pattern.
   */
  public void addReplacementPattern(String pattern, String replacement) {

    this.replacementPatterns.put(pattern, replacement);
  }

  /**
   * Resolves all variables in the specified string by calling {@link #resolveVariable(String)} for each match of the
   * {@link #getReplacementRegEx() replacement regex}.
   * 
   * @param data - the string to be resolved.
   * @return the resolved string.
   */
  public String resolveVariables(String data) {

    if (this.replacementPatterns.isEmpty()) {
      return data;
    }
    Pattern p = Pattern.compile(this.replacementRegEx);
    Matcher m = p.matcher(data);
    String resolvedData = data;
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      String match = m.group(1);
      String replacement = resolveVariable(match);
      if (replacement != null) {
        m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
      }
    }
    m.appendTail(sb);
    resolvedData = sb.toString();
    return resolvedData;
  }

  /**
   * Returns the replacement for the given variable, by first checking the {@link Resolver}'s
   * <code>replacementPatterns</code>, if not found there, checking system properties, if not found there either,
   * checking environment variables, if still not found returns null.
   * 
   * @param variableName Name of the variable
   * @return Replacement for the variable or null if not found.
   * @author trippl (14.03.2013)
   */
  protected String resolveVariable(String variableName) {

    String result = null;
    if (this.replacementPatterns.get(variableName) != null) {
      result = this.replacementPatterns.get(variableName).toString();
    } else {
      result = System.getProperty(variableName);
      if (result == null) {
        result = System.getenv(variableName);
      }
    }
    return result;
  }

  /**
   * Re resolves all user specific content of the data to variables e.g. "C:\Users\Mustermann\project" to
   * "${client.env.home}\project"
   * 
   * @param data - String to be re resolved.
   * @return the re resolved String.
   */
  public String reResolveVariables(String data) {

    if (this.replacementPatterns.isEmpty()) {
      return data;
    }
    String reResolvedData = data;
    for (Map.Entry<Object, Object> entry : this.replacementPatterns.entrySet()) {
      reResolvedData = reResolvedData.replaceAll(Pattern.quote(entry.getValue().toString()),
          Matcher.quoteReplacement(Strings.PATTERN_PREFIX + entry.getKey().toString() + Strings.PATTERN_SUFFIX));
    }
    return reResolvedData;
  }

}
