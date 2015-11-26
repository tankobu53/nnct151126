package com.wcaokaze.nnct01;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class Main {
  private static final String SETTING_FILE_NAME = "setting.ini";

  private Map<String, String> dataMap;
  private Operator operator;
  private int firstOperand;
  private int secondOperand;

  public static void main(String... args) {
    new Main().start();
  }

  private void start() {
    dataMap = parseData();
    if (dataMap == null) return;

    operator      = getOperator(dataMap, "calc");
    firstOperand  = getOperand (dataMap, "first");
    secondOperand = getOperand (dataMap, "second");

    if (operator      == null ||
        firstOperand  == -1   ||
        secondOperand == -1   ) return;

    System.out.println("calc: " + operator.symbol);
    System.out.println("first: " + firstOperand);
    System.out.println("second: " + secondOperand);
    System.out.println();

    int result = operator.operate(firstOperand, secondOperand);
    System.out.printf("%d %s %d = %d",
        firstOperand, operator.symbol, secondOperand, result);
  }

  private static Map<String, String> parseData() {
    DataParser dataParser = new DataParser(System.lineSeparator(), "=");

    try {
      return dataParser.parse(new FileReader(new File(SETTING_FILE_NAME)));
    } catch (FileNotFoundException e) {
      System.out.println(SETTING_FILE_NAME + " is not exist.");
    } catch (DataParser.DuplicatedKeyException e) {
      System.out.println(e.key + " is duplicated.");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return null;
  }

  private static Operator getOperator(Map<String, String> dataMap, String key) {
    String operatorStr = dataMap.get(key);

    if (operatorStr == null) {
      System.out.println(key + ": Not found");
      return null;
    }

    try {
      return Operator.valueOf(operatorStr.toUpperCase());
    } catch (IllegalArgumentException e) {
      System.out.println("Unknown value for " + key + ": " + operatorStr);
      return null;
    }
  }

  private static int getOperand(Map<String, String> dataMap, String key) {
    String operandStr = dataMap.get(key);

    if (operandStr == null) {
      System.out.println(key + ": Not found");
      return -1;
    }

    int operand;

    try {
      operand = Integer.valueOf(operandStr);
    } catch (NumberFormatException e) {
      System.out.println(operandStr + " is not a number.");
      return -1;
    }

    if (operand < 0 || operand > 99999) {
      System.out.println("Out of range: " + operand);
      return -1;
    }

    return operand;
  }

  private static enum Operator {
    ADD  ("+") {
      @Override
      public int operate(int firstOperand, int secondOperand) {
        return firstOperand + secondOperand;
      }
    },

    SUB  ("-") {
      @Override
      public int operate(int firstOperand, int secondOperand) {
        return firstOperand - secondOperand;
      }
    },

    MULTI("*") {
      @Override
      public int operate(int firstOperand, int secondOperand) {
        return firstOperand * secondOperand;
      }
    },

    DIV  ("/") {
      @Override
      public int operate(int firstOperand, int secondOperand) {
        return firstOperand / secondOperand;
      }
    };

    public final String symbol;

    private Operator(String symbol) {
      this.symbol = symbol;
    }

    public abstract int operate(int firstOperand, int secondOperand);
  }
}
