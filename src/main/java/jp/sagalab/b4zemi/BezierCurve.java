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


    public Point evaluate(double _t,double _w1){
        double p0 = bernstein(0,_t, getDegree())-bernstein(1,_t, getDegree())/2;
        double p1 = bernstein(1,_t, getDegree());
        double p2 = bernstein(2,_t, getDegree())-bernstein(1,_t, getDegree())/2;

        double topx = (m_controlPoints.get(0).getX()*p0+m_controlPoints.get(1).getX()*p1*(_w1+1)+m_controlPoints.get(2).getX()*p2);
        double topy = (m_controlPoints.get(0).getY()*p0+m_controlPoints.get(1).getY()*p1*(_w1+1)+m_controlPoints.get(2).getY()*p2);
        double bottom = (p0+(_w1+1)*p1+p2);

        return Point.create(topx/bottom,topy/bottom);

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
