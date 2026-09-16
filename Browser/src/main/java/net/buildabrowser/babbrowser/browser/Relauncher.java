package net.buildabrowser.babbrowser.browser;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Relauncher {

  private Relauncher() {}

  public static void relaunchWithFlags(String[] args) throws IOException, URISyntaxException {

    String currentJvm = ProcessHandle.current()
      .info()
      .command()
      .orElseGet(() -> {
        String javaHome = System.getProperty("java.home");
        boolean isWindows = System.getProperty("os.name", "")
          .toLowerCase(Locale.ROOT)
          .contains("win");
        return Path.of(javaHome, "bin", isWindows ? "java.exe" : "java").toString();
      });

    List<String> command = new ArrayList<>();
    addCustomJavaFlags(currentJvm, command);
    command.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
    addApplicationPath(command);
    command.add("--no-relaunch");

    if (args != null && args.length > 0) {
      command.addAll(Arrays.asList(args));
    }

    new ProcessBuilder(command).inheritIO().start();
    System.exit(0);
  }

  private static void addCustomJavaFlags(String currentJvm, List<String> command) {
    command.add(currentJvm);

    command.add("-Xms32m");
    command.add("-XX:MaxHeapFreeRatio=10");
    command.add("-XX:MinHeapFreeRatio=10");
    command.add("-XX:G1PeriodicGCInterval=3000");
    command.add("-XX:+G1PeriodicGCInvokesConcurrent");
    command.add("-Djava.net.preferIPv4Stack=true");
    command.add("-XX:CICompilerCount=2");
    command.add("--add-opens=java.desktop/sun.font=ALL-UNNAMED");

    int jvmFeature = Runtime.version().feature();
    if (jvmFeature < 25) {
      command.add("-XX:+UnlockExperimentalVMOptions");
      command.add("-XX:+UseCompactObjectHeaders");
    } else if (jvmFeature == 25) {
      command.add("-XX:+UseCompactObjectHeaders");
    }

    // -XstartOnFirstThread causes it to hang, so cannot add that
    // (just hope macOS works by default)
  }

  private static void addApplicationPath(List<String> command) throws URISyntaxException {
    File appLocation = new File(
      Main.class.getProtectionDomain()
        .getCodeSource()
        .getLocation()
        .toURI()
    );
    String appPath = appLocation.getAbsolutePath();

    if (appLocation.isFile() && appPath.endsWith(".jar")) {
      command.add("-jar");
      command.add(appPath);
    } else {
      command.add("-cp");
      command.add(System.getProperty("java.class.path"));
      command.add(Main.class.getName());
    }
  }
  
}