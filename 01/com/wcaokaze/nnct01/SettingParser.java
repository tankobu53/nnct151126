package com.wcaokaze.nnct01;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class SettingParser {
  private final char settingSeparator;
  private final char keyValueSeparator;
  private final boolean allowDuplicateKey;

  public SettingParser(char settingSeparator, char keyValueSeparator) {
    this(settingSeparator, keyValueSeparator, false);
  }

  public SettingParser(char settingSeparator, char keyValueSeparator, boolean allowDuplicateKey) {
    this.settingSeparator = settingSeparator;
    this.keyValueSeparator = keyValueSeparator;
    this.allowDuplicateKey = allowDuplicateKey;
  }

  public Map<String, String> parse(String settingStr) throws DuplicatedKeyException {
    Reader settingReader = new StringReader(settingStr);

    try {
      return parse(settingReader);
    } catch (IOException e) {
      throw new AssertionError();
    } finally {
      try {
        settingReader.close();
      } catch (IOException e) {
        // ignore
      }
    }
  }

  public Map<String, String> parse(Reader settingReader)
      throws DuplicatedKeyException, IOException {
    Map<String, String> settingMap = new HashMap<String, String>();

    String settingStr;
    while ((settingStr = readSettingStr(settingReader)) != null) {
      KeyValuePair keyValuePair = splitSettingStr(settingStr);

      if (!allowDuplicateKey && settingMap.containsKey(keyValuePair.key)) {
        throw new DuplicatedKeyException(keyValuePair.key);
      }

      settingMap.put(keyValuePair.key, keyValuePair.value);
    }

    return settingMap;
  }

  private String readSettingStr(Reader settingReader) throws IOException {
    StringBuilder stringBuilder = new StringBuilder();

    while (true) {
      int charactor = settingReader.read();

      if (charactor == settingSeparator ||
          charactor == -1) {
        return stringBuilder.length() != 0 ?
            stringBuilder.toString() : null;
      } else {
        // XXX involves truncation
        stringBuilder.append((char) charactor);
      }
    }
  }

  private KeyValuePair splitSettingStr(String settingStr) {
    int separatorIndex = settingStr.indexOf(settingSeparator);

    if (separatorIndex == -1) {
      return new KeyValuePair(settingStr, "");
    }

    String key = settingStr.substring(0, separatorIndex);
    String value = settingStr.substring(separatorIndex + 1);

    return new KeyValuePair(key, value);
  }

  public static class DuplicatedKeyException extends Exception {
    DuplicatedKeyException(String key) {
      super("key \"" + key + "\" is duplicated.");
    }
  }

  private static class KeyValuePair {
    public final String key;
    public final String value;

    KeyValuePair(String key, String value) {
      this.key = key;
      this.value = value;
    }
  }
}
