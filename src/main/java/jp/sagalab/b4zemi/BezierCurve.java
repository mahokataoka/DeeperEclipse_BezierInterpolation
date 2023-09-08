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

//    public Point evaluate(double _t){
//        double sumx = 0;
//        double sumy = 0;
//
//        for(int i=0;i<=getDegree();i++){
//
//            sumx += m_controlPoints.get(i).getX()*bernstein(i,_t);
//            sumy += m_controlPoints.get(i).getY()*bernstein(i,_t);
//
//        }
//        return Point.create(sumx,sumy);
//
//    }
//    public  Point evaluate(double _t,double _w1){
//        double w0 = 1;
//        double w2 = 1;
//        double topx = 0;
//        double topy = 0;
//        double bottom = 0;
//
//        topx = (w0*m_controlPoints.get(0).getX()*bernstein(0,_t))+(_w1*m_controlPoints.get(1).getX()*bernstein(1,_t))+(w2*m_controlPoints.get(2).getX()*bernstein(2,_t));
//        topy = (w0*m_controlPoints.get(0).getY()*bernstein(0,_t))+(_w1*m_controlPoints.get(1).getY()*bernstein(1,_t))+(w2*m_controlPoints.get(2).getY()*bernstein(2,_t));
//        bottom = (w0*bernstein(0,_t))+(_w1*bernstein(1,_t))+(w2*bernstein(2,_t));
//
//        return Point.create(topx/bottom,topy/bottom);
//
//    }
    public Point evaluate(double _t,double _w1){
        double p0 = bernstein(0,_t, getDegree())-bernstein(1,_t, getDegree())/2;
        double p1 = bernstein(1,_t, getDegree());
        double p2 = bernstein(2,_t, getDegree())-bernstein(1,_t, getDegree())/2;

        double topx = (m_controlPoints.get(0).getX()*p0+m_controlPoints.get(1).getX()*p1*(_w1+1)+m_controlPoints.get(2).getX()*p2);
        double topy = (m_controlPoints.get(0).getY()*p0+m_controlPoints.get(1).getY()*p1*(_w1+1)+m_controlPoints.get(2).getY()*p2);
        double bottom = (p0+(_w1+1)*p1+p2);
        if(topx/bottom==Double.POSITIVE_INFINITY) {
            System.out.println("x:" + topx + " y:" + topy + " bottom:" + bottom);
            System.out.println("p0:"+p0+" p1:"+p1+" p2:"+p2);
            System.out.println("_t:"+_t);
        }
        return Point.create(topx/bottom,topy/bottom);


//        double x = m_controlPoints.get(0).getX() * bernstein(0, _t, getDegree()) +
//                m_controlPoints.get(1).getX() * bernstein(1, _t, getDegree()) +
//                m_controlPoints.get(2).getX() * bernstein(2, _t, getDegree());
//
//        double y= m_controlPoints.get(0).getY() * bernstein(0, _t, getDegree()) +
//                m_controlPoints.get(1).getY() * bernstein(1, _t, getDegree()) +
//                m_controlPoints.get(2).getY() * bernstein(2, _t, getDegree());
//
//        return Point.create(x, y);
    }


    public static double[] Brow(double _t, double _w1){

        double p0 = bernstein(0,_t, 2)-bernstein(1,_t, 2)/2;
        double p1 = bernstein(1,_t, 2);
        double p2 = bernstein(2,_t, 2)-bernstein(1,_t, 2)/2;

        double Brow[] = new double[3];

        double onecol = p0 / (p0 + (_w1 + 1) * p1 + p2);
        double twocol = ((_w1 + 1) * p1) / (p0 + (_w1 + 1) * p1 + p2);
        double threecol = p2 / (p0 + (_w1 + 1) * p1 + p2);

        Brow[0] = onecol;
        Brow[1] = twocol;
        Brow[2] = threecol;

//        Brow[0] = bernstein(0, _t, 2);
//        Brow[1] = bernstein(1, _t, 2);
//        Brow[2] = bernstein(2, _t, 2);

        return Brow;

    }

    //fにかける部分のみの基底関数
    public static double[] rowf(double _t, double _w1){
        double p0 = bernstein(0,_t, 2)-bernstein(1,_t, 2)/2;
        double p1 = bernstein(1,_t, 2);
        double p2 = bernstein(2,_t, 2)-bernstein(1,_t, 2)/2;

        double Brow[] = new double[1];
        double twocol = ((_w1 + 1) * p1) / (p0 + (_w1 + 1) * p1 + p2);
        Brow[0] = twocol;

        return Brow;
    }



    //階乗
     public static int recursion(int _n){
        if(_n==0){
            return 1;
        }
        int result = 1;
        for(int i=1; i<=_n; i++){
            result*=i;
        }
        return  result;
    }

    //(n,i)=nCi
    public static double combination(int _i, int _degree){
        return recursion(_degree)/(double)(recursion(_i)*recursion(_degree - _i));
    }

    static double bernstein(int _i,double _t, int _degree){
        return (combination(_i, _degree)*Math.pow(_t,_i)*Math.pow((1-_t),_degree - _i));
    }

}
