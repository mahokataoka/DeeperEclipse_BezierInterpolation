package jp.sagalab.b4zemi;

import java.util.ArrayList;
import java.util.List;

/**
 * n次のBezier曲線を表すクラスです。
 * @author yusa
 */
public class BezierCurve {
    //フィールド 制御点列
    /**
     * Bezier曲線を生成します。
     * @param _controlPoints 制御点列
     * @return Bezier曲線
     */

    /** 制御点列 */
    private final List<Point> m_controlPoints;

    /**
     * 制御点列を指定してBezier曲線オブジェクトを生成するコンストラクタ
     * @param _controlPoints 制御点列
     */
    private BezierCurve(List<Point> _controlPoints){

        m_controlPoints = _controlPoints;
    }

    /**
     * Bezier曲線の生成を行うためのstaticファクトリーメソッド
     * @param _controlPoints 制御点列
     * @return Bezier曲線のインスタンス
     */
    public static BezierCurve create(List<Point> _controlPoints){

        return new BezierCurve(_controlPoints);
    }

    /**
     * 制御点列のコピーを取得するメソッド
     * @return 制御点列のコピー
     */

    public List<Point> getControlPoints(){

        return m_controlPoints;
    }

    /**
     * getDegree次数を取得するメソッド
     * @return 次数
     */

    public int getDegree(){

        return getControlPoints().size() - 1;
    }

    /**
     * //evaluate パラメータ t に対応する評価点を De Casteljau のアルゴリズムで評価するメソッドです。
     * @param _t 閉区間 [ 0, 1 ] 内のパラメータ
     * @return パラメータ t に対応する評価点
     */

//    public Point evaluate(double _t){
//
//        List<Point> bezierPoints  = new ArrayList<Point>();
//
//        if(m_controlPoints.size() == 1){
//            return m_controlPoints.get(0);
//        }
//        for(int i = 0;i < getDegree();i++){
//            bezierPoints.add(m_controlPoints.get(i).divide(m_controlPoints.get(i+1),_t));
//        }
//        BezierCurve b = BezierCurve.create(bezierPoints);
//        return b.evaluate(_t);
//
//    }
    //Bernstein 多項式表現

    public Point evaluate(double _t){
        double sumx = 0;
        double sumy = 0;

        for(int i=0;i<getDegree();i++){

            sumx += m_controlPoints.get(i).getX()*bernstein(i,_t);
            sumy += m_controlPoints.get(i).getY()*bernstein(i,_t);

        }
        return Point.create(sumx,sumy);

    }
    int recursion(int n){
        if (n == 0){
            return 1;
        }
        return n * recursion(n-1);
    }

    int ni(int i){
        return  recursion(getDegree())/recursion(i)*recursion(getDegree()-i);
    }

    double bernstein(int i,double t){
        return (ni(i)*Math.pow(t,i)*Math.pow((1-t),getDegree()-i));
    }
}
