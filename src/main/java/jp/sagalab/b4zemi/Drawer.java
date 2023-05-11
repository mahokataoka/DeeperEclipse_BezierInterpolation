package jp.sagalab.b4zemi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class Drawer extends JPanel {
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

  public void drawLine(double _x1, double _y1, double _x2, double _y2, Color _color, Graphics _g) {
    _g.setColor(_color);
    Graphics2D g2D = _g instanceof Graphics2D ? ((Graphics2D) _g) : null;
    if (g2D != null) {
      g2D.draw(new Line2D.Double(_x1, _y1, _x2, _y2));
    } else {
      _g.drawLine((int) _x1, (int) _y1, (int) _x2, (int) _y2);
    }
  }

  public int getWidth() {
    return WIDTH_SIZE;
  }

  public int getHeight() {
    return HEIGHT_SIZE;
  }

  public Drawer() {
    this.setPreferredSize(new Dimension(WIDTH_SIZE, HEIGHT_SIZE));
    this.setBackground(Color.WHITE);
    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

      }
    });
  }

  private static final int WIDTH_SIZE = 800;
  private static final int HEIGHT_SIZE = 600;
  private static final float STROKE_WIDTH = 1.5f;
  private static final double POINT_RADIUS = 3.0;

}
