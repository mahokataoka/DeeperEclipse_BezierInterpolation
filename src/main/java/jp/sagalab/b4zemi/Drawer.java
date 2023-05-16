package jp.sagalab.b4zemi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

/**
 * 描画用のパネルを生成します．
 */
public class Drawer extends JPanel {

  /**
   * 描画用のパネルの生成を行うためのstaticファクトリーメソッドです．
   */
  public static void create() {
    if (WIDTH_SIZE <= 0) {
      throw new IllegalArgumentException("WIDTH_SIZE must be grater than 0");
    }
    if (HEIGHT_SIZE <= 0) {
      throw new IllegalArgumentException("HEIGHT_SIZE must be grater than 0");
    }
    if (STROKE_WIDTH <= 0) {
      throw new IllegalArgumentException("STROKE_WIDTH must be grater than 0");
    }
    if (POINT_RADIUS <= 0) {
      throw new IllegalArgumentException("POINT_RADIUS must be grater than 0");
    }
    JFrame frame = new JFrame();
    frame.setTitle("Drawer");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    Drawer drawer = new Drawer();
    frame.getContentPane().add(drawer);
    frame.pack();
    frame.setVisible(true);
  }

  /**
   * 指定した座標を指定した色でパネルに描画します．
   * @param _x     点のx座標
   * @param _y     点のy座標
   * @param _color 色
   * @param _g     グラフィックス
   */
  public void drawPoint(double _x, double _y, Color _color, Graphics _g) {
    double radius = POINT_RADIUS;
    _g.setColor(_color);
    Graphics2D g2D = _g instanceof Graphics2D ? ((Graphics2D) _g) : null;
    if (g2D != null) {
      g2D.setStroke(new BasicStroke(STROKE_WIDTH));
      g2D.draw(new Ellipse2D.Double(_x - radius, _y - radius, radius * 2, radius * 2));
    } else {
      _g.drawOval((int) (_x - radius), (int) (_y - radius), (int) (radius * 2), (int) (radius * 2));
    }
  }

  /**
   * 指定した２つの点の座標を結ぶ線分を指定した色で描画します．
   * @param _x1    1点目のx座標
   * @param _y1    1点目のy座標
   * @param _x2    2点目のx座標
   * @param _y2    2点目のy座標
   * @param _color 色
   * @param _g     グラフィックス
   */
  public void drawLine(double _x1, double _y1, double _x2, double _y2, Color _color, Graphics _g) {
    _g.setColor(_color);
    Graphics2D g2D = _g instanceof Graphics2D ? ((Graphics2D) _g) : null;
    if (g2D != null) {
      g2D.draw(new Line2D.Double(_x1, _y1, _x2, _y2));
    } else {
      _g.drawLine((int) _x1, (int) _y1, (int) _x2, (int) _y2);
    }
  }

  /**
   * 描画用のパネルの生成をする．
   */
  public Drawer() {
    this.setPreferredSize(new Dimension(WIDTH_SIZE, HEIGHT_SIZE));
    this.setBackground(Color.WHITE);
    this.addMouseListener(new MouseAdapter() {
      @Override
      //マウスをクリックしたときの処理
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

      }
    });
  }

  /** 描画パネルの横幅 */
  private static final int WIDTH_SIZE = 800;
  /** 描画パネルの縦幅 */
  private static final int HEIGHT_SIZE = 600;
  /** 描画する際の線の幅 */
  private static final float STROKE_WIDTH = 1.5f;
  /** 点を描画する際の半径 */
  private static final double POINT_RADIUS = 3.0;
}
