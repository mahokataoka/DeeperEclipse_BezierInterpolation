package jp.sagalab.b4zemi;

import jp.sagalab.jftk.optimizer.NelderMead;
import jp.sagalab.jftk.curve.Range;

import java.util.ArrayList;
import java.util.List;

public class Search implements NelderMead.ObjectiveFunction {

    public static Search create(List<Point> _points){
        return new Search(_points);
    }

    public Search(List<Point> _points){

        m_points = _points;

        double[] search = NelderMead.search(createRange(), 0.1, 1000, this::calc);

        double w = 2 / ( 1 + Math.exp(-search[2]) ) - 1;
        System.out.println("x:"+search[0]+" y:"+search[1]+" w:"+ w + " x"+ search[2]);

        //最遠点
        Point farPoint = Point.create(search[0], search[1]);
        List<Point> fPoints = new ArrayList<>();

        //モデルの最遠点と制御点のリストを作成
        fPoints.add(Point.create(0,0));
        fPoints.add(farPoint);
        fPoints.add(Point.create(1,0));

        //制御点を求める
        Point.setW(2 / ( 1 + Math.exp(-search[2]) ) - 1);

        m_controlPoints.add(0,fPoints.get(0));
        m_controlPoints.add(1,fPoints.get(1));
        m_controlPoints.add(2,fPoints.get(2));

        List<Point> t = new ArrayList<>(calculateT(_points));

        //BのMatrixを求める
        double[][] elements = new double[t.size()][];
        for (int i=0; i<=t.size()-1; i++){
            elements[i] = BezierCurve.Brow(t.get(i).time(), Point.getW());
        }
        Matrix B = Matrix.create(elements);

        //Bの転置をします
        Matrix Btrans = B.transpose();

        double[][] xy = new double[t.size()][2];
        for (int i=0; i<=t.size()-1; i++){
            xy[i][0] = t.get(i).getX();
            xy[i][1] = t.get(i).getY();
        }

        //pをMatrixにする
        Matrix p = Matrix.create(xy);

        //Bの転置とBをかける
        Matrix a = Btrans.product(B);
        Matrix b = Btrans.product(p);

        //制御点を求める
        Matrix x = a.solve(b);

        //m_controlpointsに入れる
        Point point0  = Point.create(x.get(0,0), x.get(0, 1));
        Point point1  = Point.create(x.get(1,0), x.get(1, 1));
        Point point2  = Point.create(x.get(2,0), x.get(2, 1));

        m_controlPoints.clear();

        m_controlPoints.add(point0);
        m_controlPoints.add(point1);
        m_controlPoints.add(point2);

    }


//  エラーを計算するメソッド
    public double calc(double[] values){
        double error =  0;
        List<Point> fPoints = new ArrayList<>();
        Point farPoint = Point.create(values[0],values[1]);

        fPoints.add(Point.create(0,0));
        fPoints.add(farPoint);
        fPoints.add(Point.create(1.0,1.0));

        Point.setW( 2 / ( 1 + Math.exp(-values[2]) ) - 1 );

        m_controlPoints.clear();

        m_controlPoints.add(fPoints.get(0));
        m_controlPoints.add(fPoints.get(1));
        m_controlPoints.add(fPoints.get(2));

        List<Point> t = new ArrayList<>(calculateT(m_points));

        //BのMatrixを求める
        double[][] elements = new double[t.size()][];
        for (int i=0; i<=t.size()-1; i++){
            elements[i] = BezierCurve.Brow(t.get(i).time(), Point.getW());
        }
        Matrix B = Matrix.create(elements);
//        System.out.println(B);
//        System.out.println(m_points);
//        System.out.println(t);

        //Bの転置をします
        Matrix Btrans = B.transpose();

        double[][] xy = new double[t.size()][2];
        for (int i=0; i<=t.size()-1; i++){
            xy[i][0] = m_points.get(i).getX();
            xy[i][1] = m_points.get(i).getY();
        }

        //pをMatrixにする
        Matrix p = Matrix.create(xy);

//        System.out.println(p);

        //Bの転置とBをかける
        Matrix a = Btrans.product(B);
        Matrix b = Btrans.product(p);

        //制御点を求める
        Matrix x = a.solve(b);
//        System.out.println(x);

        //errorを求める
        Matrix  Nd = B.product(x);
        Matrix Ndp = Nd.minus(p);
        error = Math.pow(Ndp.get(0,0),  2);


//        //m_controlpointsに入れる
//        Point point0  = Point.create(x.get(0,0), x.get(0, 1));
//        Point point1  = Point.create(x.get(1,0), x.get(1, 1));
//        Point point2  = Point.create(x.get(2,0), x.get(2, 1));
//
//        m_controlPoints.clear();
//
//        m_controlPoints.add(point0);
//        m_controlPoints.add(point1);
//        m_controlPoints.add(point2);
//
//        calculate();

        return error;

    }

//  範囲を生成するメソッド
    public static jp.sagalab.jftk.curve.Range[] createRange(){
        Range[] range = new Range[3];
        //最遠点のx座標の定義域
        range[0] = Range.create(-10, 10);
        //最遠点のy座標の定義域
        range[1] = Range.create(-10, 10);
        //重みwの定義域
        range[2] = Range.create(-5, 5);

        return range;
    }

//
    public void calculate() {
    /*
      ここにBezierCurveのインスタンスを生成し評価点列を求める処理を記述する．
     */

        BezierCurve bezierCurve = BezierCurve.create(m_controlPoints);
        List<Point> evaluatelist = new ArrayList<>();
//        List<Point> evaluatelist2 = new ArrayList<>();

        for(double t=0; t<=1; t+=0.001) {
            Point points = bezierCurve.evaluate(t,Point.getW());
            evaluatelist.add(points);
        }

        // 求めた評価点列をm_evaluatePointsに設定します．
        setEvaluatePoints(evaluatelist);

//        for(double t=0; t<=1; t+=0.001) {
//            Point points2 = bezierCurve.evaluate(t,Point.getW());
//            evaluatelist2.add(points2);
//        }
//        setEvaluatePoints2(evaluatelist2);
    }

    //ポイントの正規化する
    public List<Point> calculateT(List<Point> m_points){

        BezierCurve bezierCurve = BezierCurve.create(m_controlPoints);
        List<Point> evaluatelist = new ArrayList<>();

        //規格化楕円弧モデルの有理二次ベジェ曲線を計算
        for(double t=0; t<=1; t+=0.001) {
            Point points = bezierCurve.evaluate(t, Point.getW());
            evaluatelist.add(points);
        }
        //規格化楕円弧モデルの距離パラメータを計算
        List<Double> distancelist = new ArrayList<>();  //距離が入っている、要素番号がt
        double distanceLength=0;
        distancelist.add(0.0);
        for(int i=0; i<evaluatelist.size()-1; i++){
            double x = evaluatelist.get(i+1).getX()-evaluatelist.get(i).getX();
            double y = evaluatelist.get(i+1).getY()-evaluatelist.get(i).getY();
            distanceLength += Math.sqrt( x*x + y*y );
            distancelist.add(distanceLength);
        }

        //規格化楕円弧モデルから求めた距離パラメータを全長が1になるように規格化
        for(int i=0; i<distancelist.size(); i++){
            if(i==0){
            }
            distancelist.set(i,distancelist.get(i)/distanceLength);
        }

        //入力点列の距離パラメータを計算
        List<Double> distanceT = new ArrayList<>();
        distanceT.add(0.0);
        double d = 0;
        List<Double> dList = new ArrayList<>();
//    dList.add(0.0);
        //入力点列の距離を計算する
        for (int i=0; i<m_points.size()-1; i++) {
            double x = m_points.get(i + 1).getX() - m_points.get(i).getX();
            double y = m_points.get(i + 1).getY() - m_points.get(i).getY();
            d += Math.sqrt(x * x + y * y);
            dList.add(d);
        }
        //全長を1に規格化
        for(int i=0; i<dList.size(); i++){
            if(dList.get(i)==0){
            }
            dList.set(i,dList.get(i)/d);
        }

        //規格化楕円弧モデルと入力点列の距離を比較
        for (int i=0; i<dList.size(); i++) {
            for (int j = 1; j < distancelist.size(); j++) {
                if (dList.get(i) >= distancelist.get(j-1) && dList.get(i) < distancelist.get(j)) {
                    //線分abをm:nに内分する
                    double m = dList.get(i)-distancelist.get(j-1);
                    double n = distancelist.get(j)-dList.get(i);
                    double t = (n * (j-1)/999 + m * j/999) / (m + n);
                    distanceT.add(t);
                    break;
                }else if(dList.get(i) == distancelist.get(j)){
                    distanceT.add(1.0);
                    break;
                }
            }
        }

        List<Point> points = new ArrayList<>();

        for (int i=0;i<m_points.size()-2;i++) {
            points.add(Point.createXYT(m_points.get(i).getX(), m_points.get(i).getY(), distanceT.get(i)));
        }

        System.out.println(dList);
        System.out.println(points);

        return points;
    }

    public void setEvaluatePoints(List<Point> _evaluatePoints) {

        m_evaluatePoints = _evaluatePoints;

    }

    public List<Point> m_controlPoints = new ArrayList<>();

    public final List<Point> m_points;

    /** 評価点列 */
    public List<Point> m_evaluatePoints = new ArrayList<>();

}
