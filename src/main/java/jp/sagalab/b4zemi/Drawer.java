package jp.sagalab.b4zemi;

import com.panayotis.gnuplot.JavaPlot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 * 描画用のパネルを生成します．
 * @author inagaki
 */
public class Drawer extends JPanel{

  /**
   * 描画用のパネルの生成を行うためのstaticファクトリーメソッドです．
   * 描画用ウィンドウを生成する場合はこちらのメソッドを使用してください．
   * @throws IllegalArgumentException 描画パネルの横幅が0以下であった場合
   * @throws IllegalArgumentException 描画パネルの縦幅が0以下であった場合
   * @throws IllegalArgumentException 線の幅が0以下であった場合
   * @throws IllegalArgumentException 点の半径が0以下であった場合
   */

//    double w1 = Math.cos(Math.PI/2);
//  double w1 = 0;
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

    //入力点列の座標とパラメータ情報を保存、読み出しするボタンの追加
    JTextField textField = new JTextField("enterpoints/enterpoints_about.csv");
    frame.getContentPane().add(textField, BorderLayout.NORTH);
    JPanel bottom = new JPanel();
    JButton saveEnterPoints = new JButton("入力点の保存");
    saveEnterPoints.addActionListener((e) -> drawer.saveEnterPoints());
    bottom.add(saveEnterPoints);
    JButton saveEvaluatePoints = new JButton("評価点の保存");
    saveEvaluatePoints.addActionListener((e) -> drawer.saveEvaluatePoints());
    bottom.add(saveEvaluatePoints);
    JButton load = new JButton("よみだし");
    load.addActionListener((e) -> drawer.load(textField.getText()));
    bottom.add(load);
    frame.getContentPane().add(bottom, BorderLayout.SOUTH);

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

    for(int i=0; i<=m_points.size()-1; i++){
      drawPoint(m_points.get(i),Color.GRAY, _g,3.0);
    }
    /*
      制御点列のリストに制御点が入っている場合は描画を行うような処理を記述する．
     */
    if(m_controlPoints.size() >= 1){
      for(int i=0; i<m_controlPoints.size(); i++) {
        drawPoint(m_controlPoints.get(i), Color.BLACK, _g, 4.5);
      }
    }

    /*
      評価点列のリストに評価点が入っている場合は描画を行うような処理を記述する．
     */
    if(m_evaluatePoints.size() >= 1){
      for(int i=0; i<m_evaluatePoints.size()-1; i++){
        drawLine(m_evaluatePoints.get(i), m_evaluatePoints.get(i+1), Color.BLUE, _g);
      }
    }




  }

  /**
   * 指定した点を指定した色でパネルに描画します．
   * @param _point 点
   * @param _color 色
   * @param _g     グラフィックス
   */
  public void drawPoint(Point _point, Color _color, Graphics _g, double _radius) {
    // 描画する色を引数の色で設定します．
    _g.setColor(_color);
    // Graphics型の引数_gをGraphics2D型にキャストします．
    //キャストに失敗した場合，変数g2Dにはnullが入ります．
    Graphics2D g2D = _g instanceof Graphics2D ? ((Graphics2D) _g) : null;
    if (g2D != null) {
      // 描画する際の線の幅を設定します．
      g2D.setStroke(new BasicStroke(STROKE_WIDTH));
      // 指定した座標に指定した半径の点を描画します．
      g2D.draw(new Ellipse2D.Double(_point.getX() - _radius, _point.getY() - _radius, _radius * 2, _radius * 2));
    } else {
      // 指定した座標に指定した半径の点を描画します．
      _g.drawOval((int) (_point.getX() - _radius), (int) (_point.getY() - _radius), (int) (_radius * 2), (int) (_radius * 2));
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
    BezierCurve bezierCurve = BezierCurve.create(m_controlPoints);
    List<Point> evaluatelist = new ArrayList<>();
    List<Point> evaluatelistXYT = new ArrayList<>();

    if(m_w>=0) {
      for (double t = 0; t <= 1; t += 0.001) {
        Point points = bezierCurve.evaluate(t, m_w);
        evaluatelist.add(points);
        Point pointXYT = Point.createXYT(points.getX(), points.getY(), t);
        evaluatelistXYT.add(pointXYT);
      }
    }else{
      for (double t = 0; t <= 1; t += 0.001) {
        Point points = bezierCurve.evaluateEx(t, m_w);
        evaluatelist.add(points);
        Point pointXYT = Point.createXYT(points.getX(), points.getY(), t);
        evaluatelistXYT.add(pointXYT);
      }
    }

    // 求めた評価点列をm_evaluatePointsに設定します．
    setEvaluatePoints(evaluatelist);
    setEvaluatePointsXYT(evaluatelistXYT);

  }


  /**
   * メンバ変数の評価点列を引数で指定した評価点列に設定します．
   * @param _evaluatePoints 評価点列
   */
  public void setEvaluatePoints(List<Point> _evaluatePoints) {

    m_evaluatePoints = _evaluatePoints;

  }


  public void setEvaluatePointsXYT(List<Point> _evaluatePointsXYT){
    m_evaluatePointsXYT = _evaluatePointsXYT;
  }

  /**
   * 入力点列の保存
   */
  public void saveEnterPoints() {
    Utility.saveEnterPoints(m_points);
  }
  /**
   * 評価点列の保存
   */
  public void saveEvaluatePoints() {
    Utility.saveEvaluatePoints(m_evaluatePointsXYT);
  }


  /**
   * 点列の読み出し
   * @param fileName ファイル名
   */
  public void load(String fileName) {

    m_points.clear();
    m_points.addAll(Utility.loadPoints(fileName));

    repaint();

    Search search = Search.create(m_points);

    m_controlPoints.clear();

    m_controlPoints.add(search.m_controlPoints.get(0));
    m_controlPoints.add(search.m_controlPoints.get(1));
    m_controlPoints.add(search.m_controlPoints.get(2));
    m_w = search.m_w;

    //求めた制御点からBezier曲線の評価点を求めていて、m_evaluatePointsにその評価点の値が入る
    calculate();

    repaint();
//
//    List<Double> distanceList = Utility.calcDistance(_points);
//    JavaPlot javaplot = new JavaPlot();
//
//    double[][] graph = new double[distanceList.size()][2];
//
//    for (int i=0; i<=distanceList.size()-1; i++){
//      graph[i][0] = _points.get(i).time();
//      graph[i][1] = distanceList.get(i);
//    }
//
//    javaplot.addPlot(graph);
//    javaplot.set("xlabel","'parameter'");
//    javaplot.set("ylabel","'distance'");
//    javaplot.plot();


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
      public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        m_points.clear();
      }

      @Override
      public void mouseReleased(MouseEvent e) {

        Search search = Search.create(m_points);

        repaint();

        m_controlPoints.clear();

        m_controlPoints.add(search.m_controlPoints.get(0));
        m_controlPoints.add(search.m_controlPoints.get(1));
        m_controlPoints.add(search.m_controlPoints.get(2));
        m_w = search.m_w;

        //求めた制御点からBezier曲線の評価点を求めていて、m_evaluatePointsにその評価点の値が入る
        calculate();

        repaint();
    }

      @Override
      //マウスをクリックしたときの処理(MouseAdapterクラスにあるmouseClickedメソッドをオーバーライドしている)
      public void mouseClicked(MouseEvent e) {

        // repaintメソッドを用いてpaintメソッドを呼び出す
        repaint();
      }

    });
    MyMouseAdapter myMouseAdapter = new MyMouseAdapter();
    this.addMouseListener(myMouseAdapter);
    this.addMouseMotionListener(
            new MouseMotionAdapter() {
              @Override
              public void mouseDragged(MouseEvent e) {
                Point point = Point.createXYT(e.getX(), e.getY(),System.currentTimeMillis() * 0.001);
                m_points.add(point);
                drawPoint(point,Color.GRAY,getGraphics(),3.0);
              }
            }
    );
  }


  /**
   * 点列の時刻パラメータの正規化をします.
   * m_points全体の時刻パラメータが_range区間に収まるように正規化します.
   *
   * @param _range 正規化後の時刻パラメータの範囲
   */
  public List<Point> normalizePoints(Range _range) {
    double startTime = m_points.get(0).time();
    double timeLength = m_points.get(m_points.size() - 1).time() - startTime;
    double rangeLength = _range.length();
    List<Point> points = new ArrayList<>();
    for (Point point : m_points) {
      points.add(Point.createXYT(point.getX(), point.getY()
              , _range.start() + (point.time() - startTime) * (rangeLength / timeLength)));
    }

    return points;
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

  private List<Point> m_evaluatePointsXYT = new ArrayList<>();


  /** ドラッグで打たれた点列を保持するリスト */
  private List<Point> m_points = new ArrayList<>();
  /** 重み　*/
  private double m_w;
}
