package jp.sagalab.b4zemi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 * 描画用のパネルを生成します．
 * @author inagaki
 */
public class Drawer extends JPanel {

  /**
   * 描画用のパネルの生成を行うためのstaticファクトリーメソッドです．
   * 描画用ウィンドウを生成する場合はこちらのメソッドを使用してください．
   * @throws IllegalArgumentException 描画パネルの横幅が0以下であった場合
   * @throws IllegalArgumentException 描画パネルの縦幅が0以下であった場合
   * @throws IllegalArgumentException 線の幅が0以下であった場合
   * @throws IllegalArgumentException 点の半径が0以下であった場合
   */
  public static void create() {
    // インスタンス生成前に不正な値がないかチェックします．
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
    // ウィンドウを表示するためにJFrameのインスタンスを生成します．
    JFrame frame = new JFrame();
    // ウィンドウのタイトルを設定します．
    frame.setTitle("Drawer");
    // ウィンドウを閉じたときにプログラムが終了するように設定します．
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    // JPanelに描画を行うための機能を追加したDrawerのインスタンスを生成します．
    Drawer drawer = new Drawer();
    // 先程生成したウィンドウの中の要素としてDrawerを追加します．
    frame.getContentPane().add(drawer);
    // フレームサイズを最適化します．
    frame.pack();
    // ウィンドウを表示します．
    frame.setVisible(true);
  }

  /**
   * パネルに描画を行います．
   * @param _g  the <code>Graphics</code> context in which to paint
   */
  @Override
  // JComponentクラスにあるpaintメソッドをオーバーライドしています．
  //　repaintメソッドを使用するとpaintメソッドが呼び出されるため，repaintメソッドを用いて再描画を行うことをおすすめします．
  public void paint(Graphics _g) {
    super.paint(_g);
    // 描画処理を記述する．
    /*
      制御点列のリストに制御点が入っている場合は描画を行うような処理を記述する．
     */

    /*
      評価点列のリストに評価点が入っている場合は描画を行うような処理を記述する．
     */
  }

  /**
   * 指定した点を指定した色でパネルに描画します．
   * @param _point 点
   * @param _color 色
   * @param _g     グラフィックス
   */
  public void drawPoint(Point _point, Color _color, Graphics _g) {
    // 半径を決定します．
    double radius = POINT_RADIUS;
    // 描画する色を引数の色で設定します．
    _g.setColor(_color);
    // Graphics型の引数_gをGraphics2D型にキャストします．
    //キャストに失敗した場合，変数g2Dにはnullが入ります．
    Graphics2D g2D = _g instanceof Graphics2D ? ((Graphics2D) _g) : null;
    if (g2D != null) {
      // 描画する際の線の幅を設定します．
      g2D.setStroke(new BasicStroke(STROKE_WIDTH));
      // 指定した座標に指定した半径の点を描画します．
      g2D.draw(new Ellipse2D.Double(_point.getX() - radius, _point.getY() - radius, radius * 2, radius * 2));
    } else {
      // 指定した座標に指定した半径の点を描画します．
      _g.drawOval((int) (_point.getX() - radius), (int) (_point.getY() - radius), (int) (radius * 2), (int) (radius * 2));
    }
  }

  /**
   * 指定した２つの点の座標を結ぶ線分を指定した色で描画します．
   * @param _p1    1つ目の点
   * @param _p2    ２つ目の点
   * @param _color 色
   * @param _g     グラフィックス
   */
  public void drawLine(Point _p1, Point _p2, Color _color, Graphics _g) {
    // 描画する色を引数の色で設定します．
    _g.setColor(_color);
    // Graphics型の引数_gをGraphics2D型にキャストします．
    //キャストに失敗した場合，変数g2Dにはnullが入ります．
    Graphics2D g2D = _g instanceof Graphics2D ? ((Graphics2D) _g) : null;
    if (g2D != null) {
      // 描画する際の線の幅を設定します．
      g2D.setStroke(new BasicStroke(STROKE_WIDTH));
      // 1点目と2点目を結ぶ線分を描画します．
      g2D.draw(new Line2D.Double(_p1.getX(), _p1.getY(), _p2.getX(), _p2.getY()));
    } else {
      // 1点目と2点目を結ぶ線分を描画します．
      _g.drawLine((int) _p1.getX(), (int) _p1.getY(), (int) _p2.getX(), (int) _p2.getY());
    }
  }

  /**
   * 制御点列からBezierCurveのインスタンスを生成し，評価点列を求めます.
   */
  public void calculate() {
    /*
      ここにBezierCurveのインスタンスを生成し評価点列を求める処理を記述する．
     */

    // 求めた評価点列をm_evaluatePointsに設定します．
    setEvaluatePoints();
  }

  /**
   * メンバ変数の評価点列を引数で指定した評価点列に設定します．
   * @param _evaluatePoints 評価点列
   */
  public void setEvaluatePoints(List<Point> _evaluatePoints) {
    m_evaluatePoints = _evaluatePoints;
  }

  /**
   * コンストラクタ
   */
  private Drawer() {
    // 描画用パネルのサイズを設定します．
    this.setPreferredSize(new Dimension(WIDTH_SIZE, HEIGHT_SIZE));
    // 描画用パネルの背景色を設定します．
    this.setBackground(Color.WHITE);
    // 描画用パネルにマウス処理を追加します．
    this.addMouseListener(new MouseAdapter() {
      @Override
      //マウスをクリックしたときの処理(MouseAdapterクラスにあるmouseClickedメソッドをオーバーライドしている)
      public void mouseClicked(MouseEvent e) {
        // スーパークラスであるMouseAdapterクラスにあるmouseClickedメソッドを呼び出します．
        //ただし今回の場合は，MouseAdapterクラスにあるmouseClickedメソッドは処理の中身が書かれていないため呼び出す必要はないが，
        //慣例として記載しています．
        super.mouseClicked(e);
        /*
          ここにマウスをクリックしたときの処理を記述する．
         */

        // repaintメソッドを用いてpaintメソッドを呼び出す
        repaint();
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
  /** 制御点列　*/
  private final List<Point> m_controlPoints = new ArrayList<>();
  /** 評価点列 */
  private List<Point> m_evaluatePoints = new ArrayList<>();
}
