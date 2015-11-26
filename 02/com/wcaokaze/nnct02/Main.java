package com.wcaokaze.nnct02;

import java.util.Map;

public class Main {
  private static final String URL = "uwanolab.jp/parser.php?" +
                                    "ID=sushi&" +
                                    "MAIL=wcaokaze@gmail.com&" +
                                    "LIST=114514-1919-810-893";

  public static void main(String... args) {
    String argumentStr = getArgument(URL);

    Map<String, String> dataMap = parseData(argumentStr);
    if (dataMap == null) return;

    String id      = getStr(dataMap, "ID");
    String mail    = getStr(dataMap, "MAIL");
    String listStr = getStr(dataMap, "LIST");

    if (id      == null ||
        mail    == null ||
        listStr == null) return;

    checkConstraint: {
      if (!id.matches("[a-zA-Z]{5}")) {
        System.out.println("Illegal ID");
        return;
      }

      if (!mail.matches("[a-zA-Z0-9\\.\\-]+@[a-zA-Z0-9\\.\\-]+")) {
        System.out.println("Illegal Mail");
        return;
      }

      if (!listStr.matches("(\\d{1,10}-)*\\d{1,10}")){
        System.out.println("Illegal number");
        return;
      }
    }

    System.out.printf("%s: %s\n", id, mail);
    for (String numberStr : listStr.split("-")) {
      System.out.println(numberStr);
    }
  }

  private static String getArgument(String url) {
    int separatorIndex = url.indexOf('?');

    if (separatorIndex == -1) {
      throw new IllegalArgumentException("The URL doesn't contain any arguments");
    }

    return url.substring(separatorIndex + 1);
  }

  private static Map<String, String> parseData(String argumentStr) {
    DataParser dataParser = new DataParser("&", "=");

    try {
      return dataParser.parse(argumentStr);
    } catch (DataParser.DuplicatedKeyException e) {
      System.out.println(e.key + " is duplicated.");
      return null;
    }
  }

  private static String getStr(Map<String, String> dataMap, String key) {
    String id = dataMap.get(key);

    if (id != null) {
      return id;
    } else {
      System.out.println(key + ": Not found");
      return null;
    }
  }
}
