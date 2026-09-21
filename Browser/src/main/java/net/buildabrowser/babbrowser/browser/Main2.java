package net.buildabrowser.babbrowser.browser;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

public class Main2 {

  public static void main(String[] args) {
    if (getJavaVersion() < 24) {
      if (GraphicsEnvironment.isHeadless()) {
        System.err.println(
          "This system does not have the facilities to run BuildABrowser Test Program,"
          + " nor does it have the facilities to report the issue to the user.");
        System.exit(1);
      }

      String errorMessage = "BuildABrowser Test Program requires Java 24 or newer.\n" +
        "You are currently running Java " + System.getProperty("java.version") + "\n\n" +
        "Please update your Java runtime to launch the browser.";
      System.err.println(errorMessage);
      JOptionPane.showMessageDialog(
        null,
        errorMessage,
        "Unsupported Java Version",
        JOptionPane.ERROR_MESSAGE
      );
      System.exit(1);
    }

    try {
      Class<?> appClass = Class.forName("net.buildabrowser.babbrowser.browser.Main");
      Method startMethod = appClass.getMethod("main", String[].class);
      startMethod.invoke(null, (Object) args);
    } catch (
      IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException e
    ) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static int getJavaVersion() {
    String ver = System.getProperty("java.version");
    if (ver.startsWith("1.")) {
      return Integer.parseInt(ver.substring(2, 3));
    }
    int dot = ver.indexOf('.');
    return dot != -1 ? Integer.parseInt(ver.substring(0, dot)) : Integer.parseInt(ver);
  }

}
