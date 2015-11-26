package com.wcaokaze.nnct02;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses String that expresses list of key-value pair; such as URL argument,
 * JSON Object, and so on.
 */
public class DataParser {
  private final String recordSeparator;
  private final String valueSeparator;
  private final boolean allowDuplicateKey;

  /**
   * constructs new Parser which doesn't allow duplicated keys.
   * @see #constructor(String, String, boolean, boolean)
   */
  public DataParser(String recordSeparator, String valueSeparator) {
    this(recordSeparator, valueSeparator, false);
  }

  /**
   * @param recordSeparator String between each key-value pair
   * @param valueSeparator String between key and value
   * @param allowDuplicateKey whether this Parser allows duplicated keys or not
   */
  public DataParser(String recordSeparator,
                    String valueSeparator,
                    boolean allowDuplicateKey) {
    this.recordSeparator = recordSeparator;
    this.valueSeparator = valueSeparator;
    this.allowDuplicateKey = allowDuplicateKey;
  }

  /**
   * parses {@link dataStr} and maps it.
   * @throws DuplicatedKeyException when this parser notice a duplicate of keys,
   *     never thrown if allowDuplicateKey of {@link #constructor(String, String,
   *     boolean, boolean)} is true
   */
  public Map<String, String> parse(String dataStr) throws DuplicatedKeyException {
    Map<String, String> dataMap = new HashMap<String, String>();

    String[] recordStrs = dataStr.split(recordSeparator);
    for (String recordStr : recordStrs) {
      putData(dataMap, recordStr);
    }

    return dataMap;
  }

  /**
   * similar to {@link #parse(String)}.
   * @throws IOException when {@link dataReader} throws
   */
  public Map<String, String> parse(Reader dataReader)
      throws DuplicatedKeyException, IOException {

    Map<String, String> dataMap = new HashMap<String, String>();

    String recordStr;
    while ((recordStr = readRecordStr(dataReader)) != null) {
      putData(dataMap, recordStr);
    }

    return dataMap;
  }

  private void putData(Map<String, String> dataMap, String recordStr)
      throws DuplicatedKeyException {

    Record record = new Record(recordStr, valueSeparator);

    if (!allowDuplicateKey && dataMap.containsKey(record.key)) {
      throw new DuplicatedKeyException(record.key);
    }

    dataMap.put(record.key, record.value);
  }

  private String readRecordStr(Reader dataReader) throws IOException {
    StringBuilder stringBuilder = new StringBuilder();

    while (true) {
      int character = dataReader.read();

      if (character == -1) break;

      stringBuilder.append((char) character);

      if (stringBuilder.toString().endsWith(recordSeparator)) {
        stringBuilder.delete(stringBuilder.length() - recordSeparator.length(),
                             stringBuilder.length());
        break;
      }
    }

    if (stringBuilder.length() == 0) {
      return null;
    } else {
      return stringBuilder.toString();
    }
  }

  public static class DuplicatedKeyException extends Exception {
    public final String key;

    DuplicatedKeyException(String key) {
      super("key \"" + key + "\" is duplicated.");

      this.key = key;
    }
  }

  private static class Record {
    public final String key;
    public final String value;

    Record(String recordStr, String valueSeparator) {
      int separatorIndex = recordStr.indexOf(valueSeparator);

      if (separatorIndex == -1) {
        key = recordStr;
        value = "";
      } else {
        key = recordStr.substring(0, separatorIndex);
        value = recordStr.substring(separatorIndex + valueSeparator.length());
      }
    }
  }
}
