package net.buildabrowser.babbrowser.browser;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.lang.foreign.Linker;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.buildabrowser.babbrowser.a11y.core.A11YProvider;
import net.buildabrowser.babbrowser.a11y.core.noop.NoOpA11YProvider;
import net.buildabrowser.babbrowser.browser.chrome.WindowSetGUI;
import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.browser.uistate.Window.WindowOptions;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.browser.util.FileUtil;
import net.buildabrowser.babbrowser.cookies.CookieStore;
import net.buildabrowser.babbrowser.cookies.stores.InMemoryCookieStore;
import net.buildabrowser.babbrowser.debugger.core.Debugger;
import net.buildabrowser.babbrowser.debugger.swing.SwingDebugger;
import net.buildabrowser.babbrowser.embedding.standardcommon.net.imp.PublicSuffixListImp;
import net.buildabrowser.babbrowser.painter.core.CanvasCallbacks;
import net.buildabrowser.babbrowser.painter.core.ComponentPainter;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.painter.java2d.Java2DPainter;
import net.buildabrowser.babbrowser.renderer.uistate.Frame;

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  private static final long GRAPHICS_CHECK_TIMEOUT = 1500;
  
  public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
    BrowserArguments arguments = BrowserArguments.parse(args);
    if (arguments == null) return;

    if (!arguments.noRelaunch()) {
      LOGGER.info("Relaunching process because user did not specify --no-relaunch.");
      Relauncher.relaunchWithFlags(args);
      return;
    }

    if (GraphicsEnvironment.isHeadless()) {
      LOGGER.error(
        "This system does not have the facilities to run BuildABrowser Test Program,"
        + " nor does it have the facilities to report the issue to the user.");
      System.exit(1);
    }

    System.setProperty("javax.accessibility.assistive_technologies", "");
    setLookAndFeel();

    String osName = System.getProperty("os.name").toLowerCase();

    boolean isLinux = osName.contains("linux");
    if (isLinux) {
      System.setProperty("org.lwjgl.opengl.contextAPI", "GLX");
    }

    boolean isSupportedOS = osName.contains("win") || osName.contains("linux");
    boolean isMacOS = osName.contains("mac");
    if (!isSupportedOS) {
      String errorMessage = "OS named '" + osName + "' is not supported. BuildABrowser Test Program will not work here.";
      if (isMacOS) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        errorMessage += " Please contribute.";
      }
      showDialog("OS Not Supported!", errorMessage, JOptionPane.WARNING_MESSAGE);
      // no return;
    }

    // Linux is not "GNU/Linux" or whatever names you feel like making up
    // Despite that, BAB's natives are linked against glibc, so need to double check
    if (isLinux && !hasGlibc()) {
      showDialog("glibc?", "glibc?", JOptionPane.WARNING_MESSAGE);
      // Will fall back to Java2D in a later check
    }

    URI profilePath = FileUtil.asDirectory(arguments.profilePath());
    new File(profilePath.getSchemeSpecificPart()).mkdirs();

    ComponentPainter<Component> painter;
    if (!testPainter(arguments.painter())) {
      showDialog(
        "Graphics Initialization Failed!",
        "Failed to initialize graphics backend. Falling back to Java2D - Your browsing experience will be significantly degraded (pass `-gbe java2d` to suppress).",
        JOptionPane.ERROR_MESSAGE
      );
      
      painter = new Java2DPainter();
    } else {
      painter = arguments.painter().get();
    }

    CookieStore cookieStore;
    try {
      cookieStore = arguments.cookieStore().get(
        profilePath, new PublicSuffixListImp());
      cookieStore.initialize();
    } catch (Throwable e) {
      showDialog(
        "Cookie Store Initialization Failed!",
        "Failed to initialize cookie store. Falling back to in-memory cookie store - cookies will not persist (pass `-cs memory` to suppress).",
        JOptionPane.ERROR_MESSAGE
      );
      e.printStackTrace();
      cookieStore = new InMemoryCookieStore(new PublicSuffixListImp());
    }
    
    A11YProvider a11yProvider = arguments.a11yProvider().get();
    try {
      a11yProvider.initialize();
    } catch (Throwable e) {
      e.printStackTrace();
      a11yProvider = null;
    }

    if (a11yProvider == null) {
      showDialog(
        "A11Y Initialization Failed!",
        "A11Y FFM bindings either have not been implemented for this OS, or failed to load. Disabling A11Y (pass `-a11y disabled` to suppress).",
        JOptionPane.ERROR_MESSAGE);
      a11yProvider = new NoOpA11YProvider();
    }
    
    Debugger debugger = new SwingDebugger();

    java.awt.Frame sharedFrame = JOptionPane.getRootFrame();
    if (sharedFrame != null && sharedFrame.isDisplayable()) {
        sharedFrame.dispose();
    }

    BrowserInstance browserInstance = BrowserInstance.create(
      profilePath, painter, cookieStore, a11yProvider);
  
    WindowSet windowSet = browserInstance.windowSet();
    Window window = windowSet.openWindow(new WindowOptions(false));
    for (URI url: arguments.launchPaths()) {
      window.openTab().navigate(url);
    }

    WindowSetGUI.create(windowSet, painter, debugger);
  }

  private static void showDialog(String title, String message, int messageType) {
    if (messageType == JOptionPane.ERROR_MESSAGE) {
      LOGGER.error(message);
    } else {
      LOGGER.warn(message);
    }

    JOptionPane pane = new JOptionPane(message, messageType);
    JDialog dialog = pane.createDialog(title);
    dialog.setAlwaysOnTop(true);
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    dialog.dispose();
  }

  private static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean testPainter(Supplier<ComponentPainter<Component>> painter) {
    CompletableFuture<Boolean> future = new CompletableFuture<>();

    SwingUtilities.invokeLater(() -> {
      JFrame dummyFrame = new JFrame();
      try {
        Component dummyComponent = painter.get().createComponent(new CanvasCallbacks() {
          @Override 
          public void paint(PaintCanvas canvas) {
            future.complete(true);
            SwingUtilities.invokeLater(dummyFrame::dispose);
          }
        });

        dummyFrame.setSize(10, 10);
        dummyFrame.setUndecorated(true);
        dummyFrame.add(dummyComponent);
        dummyFrame.setVisible(true);

        new Thread(() -> {
          try {
            Thread.sleep(GRAPHICS_CHECK_TIMEOUT);
          } catch (InterruptedException e) {}
          if (dummyFrame.isVisible()) {
            dummyFrame.dispose();
          }
        }).start();
      } catch (Throwable t) {
        t.printStackTrace();
        future.complete(false);
      }
    });

    try {
      return future.get(GRAPHICS_CHECK_TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      return false;
    }
  }

  private static boolean hasGlibc() {
    try {
      return Linker.nativeLinker().defaultLookup().find("gnu_get_libc_version").isPresent();
    } catch (Exception e) {
      return false;
    }
  }

}