package jp.sagalab.b4zemi;

/**
 * 平面上の点を表し、平面上の点のｘ座標とy座標を保持するクラスです。
 * @author yusa
 */
public class Point {
    /**
     * 平面上の点を表し、平面上の点のｘ座標とy座標を保持します。
     * @param _x ｘ座標
     * @param _y y座標
     * @return 内分点
     */

    /** x座標 */
    private final double m_x;
    /** y座標 */
    private final double m_y;

    /**
     * 指定した座標にある点を生成する
     * @param _x x座標
     * @param _y y座標
     */
    private Point(double _x,double _y){
        m_x = _x;
        m_y = _y;
    }
    /**
     * 点の生成を行うためのstaticファクトリーメソッドです
     * @param _x x座標
     * @param _y y座標
     * @return 点のインスタンス
     */
    static Point create(double _x,double _y){
        return new Point(_x,_y);
    }

    /**
     *点のx座標を取得するメソッド
     * @return x座標
     */
    public double getX(){
        return m_x;
    }

    /**
     * 点のy座標を取得する
     * @return y座標
     */
    public double getY(){
        return m_y;
    }

    /**
     * この点ともう一つの点を t : ( 1 - t ) で内分 ( t < 0 または t > 1 の場合は外分 ) するメソッド
     * @param _other もう一つの点
     * @param t 比のパラメータ
     * @return 内分点 (外分点)
     */
    public Point divide(Point _other,double t){
        double divx = ((1-t) * m_x + t * _other.getX());
        double divy = ((1-t) * m_y + t * _other.getY());

        return create(divx,divy);
    }

}
