package jp.sagalab.b4zemi;

import java.util.ArrayList;
import java.util.List;

/**
 * n次のBezier曲線を表すクラスです。
 * @author yusa
 */
public class BezierCurve {


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
    public Point evaluate(double _t,double _w1) {
        double p0 = bernstein(0, _t, getDegree()) - bernstein(1, _t, getDegree()) / 2;
        double p1 = bernstein(1, _t, getDegree());
        double p2 = bernstein(2, _t, getDegree()) - bernstein(1, _t, getDegree()) / 2;

        double topx = (m_controlPoints.get(0).getX() * p0 + m_controlPoints.get(1).getX() * p1 * (_w1 + 1) + m_controlPoints.get(2).getX() * p2);
        double topy = (m_controlPoints.get(0).getY() * p0 + m_controlPoints.get(1).getY() * p1 * (_w1 + 1) + m_controlPoints.get(2).getY() * p2);
        double bottom = (p0 + (_w1 + 1) * p1 + p2);

        return Point.create(topx / bottom, topy / bottom);
    }

    //-1<w<0のときに呼び出されるメソッド
    Point evaluateEx(double _t, double _w){
        double paramaterSe = parameterEx(_w);
        List<Double> q = new ArrayList<>();

        if(_t>=0 && _t<1/(2-paramaterSe)){
            q = basisfuncThreequqrters(_t,paramaterSe);
        }
        if(_t>1/(2-paramaterSe) && _t<=1){
            q = basisfuncRound(_t,paramaterSe);
        }

        double x_top = q.get(0)*m_controlPoints.get(0).getX() + q.get(1)*m_controlPoints.get(1).getX() + q.get(2)*m_controlPoints.get(2).getX();
        double y_top = q.get(0)*m_controlPoints.get(0).getY() + q.get(1)*m_controlPoints.get(1).getY() + q.get(2)*m_controlPoints.get(2).getY();
        double bottom = q.get(3);

        return Point.create(x_top/bottom,y_top/bottom);
    }


    List<Double> basisfuncThreequqrters(double _t, double _Se){
        List<Double> q = new ArrayList<>();
        List<Double> p = p((2-_Se)*_t);

        q.add(0,p.get(0));
        q.add(1,p.get(1));
        q.add(2,p.get(2));

        q.add(p.get(0)+p.get(1)+p.get(2));

        return q;
    }

    List<Double> basisfuncRound(double _t, double _Se){
        List<Double> q = new ArrayList<>();
        List<Double> p = p((2-_Se)*_t -1);

        q.add(0,p.get(2) + p.get(1));
        q.add(1,-1* p.get(1));
        q.add(2,p.get(0) + p.get(1));

        q.add(p.get(0)+p.get(1)+p.get(2));

        return q;
    }

    //Seを求める
    double parameterEx(double _w){
        double denominator = _w*Math.sqrt(1+_w)*Math.sqrt(1-_w) + _w*_w -1;
        double numerator = 2*_w*Math.sqrt(1+_w)*Math.sqrt(1-_w) -1;

        return denominator/numerator;
    }

    //Pを計算
    List<Double> p(double _t){
        List<Double> p = new ArrayList<>();
        List<Double> b = new ArrayList<>();

        for(int i = 0; i<=getDegree(); i++) {
            //Bをもとめる
            long first = factorial(getDegree()) / (factorial(i) * factorial(getDegree()-i));
            double second = Math.pow(_t, i);
            double third = Math.pow(1 - _t, getDegree()-i);
            //重みB
            b.add(first * second * third);
        }

        p.add(0,b.get(0) - b.get(1)/2);
        p.add(1,b.get(1));
        p.add(2,b.get(2) - b.get(1)/2);

        return p;
    }


    /**
     * 階乗の計算
     */
    long factorial (int _x){
        if(_x==0){
            return 1;
        }

        long y = 1;
        for(int i=_x; i>=1; i--){
            y*=i;
        }
        return y;
    }

    public static double[] Brow(double _t, double _w1){
        double p0 = bernstein(0, _t, 2) - bernstein(1, _t, 2) / 2;
        double p1 = bernstein(1, _t, 2);
        double p2 = bernstein(2, _t, 2) - bernstein(1, _t, 2) / 2;

        double Brow[] = new double[3];

        double onecol = p0 / (p0 + (_w1 + 1) * p1 + p2);
        double twocol = ((_w1 + 1) * p1) / (p0 + (_w1 + 1) * p1 + p2);
        double threecol = p2 / (p0 + (_w1 + 1) * p1 + p2);

        Brow[0] = onecol;
        Brow[1] = twocol;
        Brow[2] = threecol;

        return Brow;
    }

    //w<0
    public double[] Qrow(double _t, double _w){
        double paramaterSe = parameterEx(_w);
        double Qrow[] = new double[3];
        List<Double> q = new ArrayList<>();
        if(_t>=0 && _t<1/(2-paramaterSe)){
            q = basisfuncThreequqrters(_t,paramaterSe);
        }
        if(_t>1/(2-paramaterSe) && _t<=1) {
            q = basisfuncRound(_t, paramaterSe);
        }
            Qrow[0] = q.get(0)/q.get(3);
            Qrow[1] = q.get(1)/q.get(3);
            Qrow[2] = q.get(2)/q.get(3);
        return Qrow;
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

    /**
     * 制御点列を指定してBezier曲線オブジェクトを生成するコンストラクタ
     * @param _controlPoints 制御点列
     */
    private BezierCurve(List<Point> _controlPoints){
        m_controlPoints = _controlPoints;
    }
    /** 制御点列 */
    private final List<Point> m_controlPoints;
}
