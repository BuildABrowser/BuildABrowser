package net.buildabrowser.babbrowser.embedding.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.embedding.swing.SwingEmbedding.FrameAndComponent;
Z
public class SwingEmbeddingTest {
  
  public static void main(String[] args) {
    SwingUtilities.invokeLater(SwingEmbeddingTest::start);
  }

  private static void start() {
    FrameAndComponent frameAndComponent = CommonUtil.rethrow(
      () -> SwingEmbedding.newFrameComponent("https://whatismybrowser.com/"));
    Component frameComponent = frameAndComponent.component();

    JFrame jframe = new JFrame("BuildABrowser - Swing Embedded Renderer");
    jframe.setSize(new Dimension(800, 600));
    jframe.add(frameComponent);
    jframe.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        CommonUtil.rethrowV(() -> frameAndComponent.frame().close());
        jframe.dispose();
      }
    });
    jframe.setVisible(true);
  }

}