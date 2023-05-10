package jp.sagalab.b4zemi;

public class Point {
    //フィールド(変数)
    private final double m_x;
    private final double m_y;
    //コンストラクタ(初期化)
    private Point(double _x,double _y){
        m_x = _x;
        m_y = _y;
    }
    //点の生成を行うためのstaticファクトリーメソッド
    static Point create(double _x,double _y){
        return new Point(_x,_y);
    }
    //点のx座標を取得する
    public double getX(){
        return m_x;
    }
    //点のy座標を取得する．
    public double getY(){
        return m_y;
    }
    //この点ともう一つの点を t : ( 1 - t ) で内分 ( t < 0 または t > 1 の場合は外分 ) する.
    public Point divide(Point _other,double t){
        double divx = ((1-t) * m_x + t * _other.getX());
        double divy = ((1-t) * m_y + t * _other.getY());

        return create(divx,divy);
    }

}
