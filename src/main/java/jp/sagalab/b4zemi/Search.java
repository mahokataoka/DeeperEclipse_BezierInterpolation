package jp.sagalab.b4zemi;

import jp.sagalab.jftk.optimizer.NelderMead;
import jp.sagalab.jftk.curve.Range;

import java.util.ArrayList;
import java.util.List;

/**
 * 補間に最適なfとwの探索と、入力点列のパラメータを修正するクラス
 */
public class Search implements NelderMead.ObjectiveFunction {
    /**
     * NelderMeadのインスタンス生成を行うファクトリーメソッド
     * @param _points 入力点列
     * @return インスタンス
     */
    public static Search create(List<Point> _points){
        return new Search(_points);
    }

    /**
     * 補間後の制御点列を求めるメソッド
     * @param _points 入力点列
     */
    public Search(List<Point> _points){
        //入力点列
        m_points = _points;

        double[] search = NelderMead.search(createRange(), 0.01, 1000, this::calc);

        Point.setW(2 / ( 1 + Math.exp(-search[2]) ) - 1);
        System.out.println("far_x:"+search[0]+" far_y:"+search[1]+" w:"+ Point.getW() + " sig_x"+ search[2]);

        //最遠点
        Point farPoint = Point.create(search[0], search[1]);
        List<Point> fPoints = new ArrayList<>();

        //モデルの最遠点を含む制御点のリストを作成
        fPoints.add(Point.create(0,0));
        fPoints.add(farPoint);
        fPoints.add(Point.create(1,0));


        m_controlPoints.clear();

        m_controlPoints.add(0,fPoints.get(0));
        m_controlPoints.add(1,fPoints.get(1));
        m_controlPoints.add(2,fPoints.get(2));

        //補間の計算
        //入力点列のパラメータを修正
        List<Point> modifiedPoints = new ArrayList<>(calculateT(_points));

        //BのMatrixを求める
        double[][] elements = new double[modifiedPoints.size()][];
        for (int i=0; i<=modifiedPoints.size()-1; i++){
            elements[i] = BezierCurve.Brow(modifiedPoints.get(i).time(), Point.getW());
        }
        Matrix B = Matrix.create(elements);

        //Bの転置をします
        Matrix Btrans = B.transpose();

        double[][] xy = new double[modifiedPoints.size()][2];
        for (int i=0; i<=modifiedPoints.size()-1; i++){
            xy[i][0] = modifiedPoints.get(i).getX();
            xy[i][1] = modifiedPoints.get(i).getY();
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

    /**
     * エラーを計算するメソッド
     * @param values 最遠点のx座標、最遠点のy座標、重みw
     * @return 誤差
     */
    public double calc(double[] values){
        double error=0;
        List<Point> fPoints = new ArrayList<>();    //規格化楕円弧モデルの制御点列

        fPoints.add(Point.create(0,0));
        fPoints.add(Point.create(values[0],values[1]));
        fPoints.add(Point.create(1.0,0));

        Point.setW( 2 / ( 1 + Math.exp(-values[2]) ) - 1 );
        System.out.println("values:"+values[2]);

        m_controlPoints.clear();

        m_controlPoints.add(fPoints.get(0));
        m_controlPoints.add(fPoints.get(1));
        m_controlPoints.add(fPoints.get(2));

        //入力点列のパラメータを修正
        List<Point> modified_points = new ArrayList<>(calculateT(m_points));

        //最小二乗法の計算をする
        //BのMatrixを求める
        double[][] elements = new double[modified_points.size()][];
        for (int i=0; i<=modified_points.size()-1; i++){
            elements[i] = BezierCurve.Brow(modified_points.get(i).time(), Point.getW());
        }
        Matrix B = Matrix.create(elements);

        //Bの転置をします
        Matrix Btrans = B.transpose();

        double[][] xy = new double[modified_points.size()][2];
        for (int i=0; i<=modified_points.size()-1; i++){
            xy[i][0] = m_points.get(i).getX();
            xy[i][1] = m_points.get(i).getY();
        }

        //pをMatrixにする
        Matrix p = Matrix.create(xy);

        //Bの転置とBをかける
        Matrix a = Btrans.product(B);
        Matrix b = Btrans.product(p);

        //制御点を求める
        Matrix x = a.solve(b);
        //errorを求める
        Matrix  Nd = B.product(x);
        Matrix Ndp = Nd.minus(p);
        Matrix Ndptrans = Ndp.transpose();
        Matrix NdpSquare = Ndptrans.product(Ndp);
        error = NdpSquare.get(0,0) + NdpSquare.get(1,1);
//        error = Math.pow(Ndp.get(0,0),  2);
//        for(int i=0; i<Ndp.rowSize(); i++){
//            for(int j=0; j< Nd.columnSize(); j++){
//                error += Math.pow(Ndp.get(i,j),  2);
//            }
//        }

        return error;

    }

//  範囲を生成するメソッド
    public static jp.sagalab.jftk.curve.Range[] createRange(){
        Range[] range = new Range[3];
        //最遠点のx座標の定義域
        range[0] = Range.create(-10, 10);
        //最遠点のy座標の定義域
        range[1] = Range.create(-10, 10);
        //重みwの定義域(シグモイドのxの定義域)
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

    /**
     * ポイントの正規化する
     * @param _points 入力点列
     * @return パラメータを計算し直した入力点列
     */
    public List<Point> calculateT(List<Point> _points){
        //規格化楕円弧モデルの生成
        BezierCurve bezierCurve = BezierCurve.create(m_controlPoints);
        List<Point> evaluateNEAList = new ArrayList<>();    //NEAはNormalized Ellipse Arc

        for(double t=0; t<=1; t+=0.001) {
            Point points = bezierCurve.evaluate(t, Point.getW());
            evaluateNEAList.add(points);
        }
        //規格化楕円弧モデルの距離パラメータを計算
        List<Double> distanceNEAlist = new ArrayList<>();
        double distanceNEALength=0;
        distanceNEAlist.add(0.0);
        for(int i=0; i<evaluateNEAList.size()-1; i++){
            double x = evaluateNEAList.get(i+1).getX()-evaluateNEAList.get(i).getX();
            double y = evaluateNEAList.get(i+1).getY()-evaluateNEAList.get(i).getY();
            distanceNEALength += Math.sqrt( x*x + y*y );
            distanceNEAlist.add(distanceNEALength);
        }

        //規格化楕円弧モデルから求めた距離パラメータを規格化
        for(int i=0; i<distanceNEAlist.size(); i++){
            if(i==0){
            }
            distanceNEAlist.set(i,distanceNEAlist.get(i)/distanceNEALength);
        }

        //入力点列の距離パラメータを計算
        List<Double> distanceT = new ArrayList<>();
        distanceT.add(0.0);
        double d = 0;
        List<Double> dList = new ArrayList<>();
//    dList.add(0.0);
        //入力点列の距離を計算する
        for (int i=0; i<_points.size()-1; i++) {
            double x = _points.get(i + 1).getX() - _points.get(i).getX();
            double y = _points.get(i + 1).getY() - _points.get(i).getY();
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
            for (int j = 1; j < distanceNEAlist.size(); j++) {
                if (dList.get(i) >= distanceNEAlist.get(j-1) && dList.get(i) < distanceNEAlist.get(j)) {
                    //線分abをm:nに内分する
                    double m = dList.get(i)-distanceNEAlist.get(j-1);
                    double n = distanceNEAlist.get(j)-dList.get(i);
                    double t = (n * (j-1)/999 + m * j/999) / (m + n);
                    distanceT.add(t);
                    break;
                }else if(dList.get(i) == distanceNEAlist.get(j)){
                    distanceT.add(1.0);
                    break;
                }
            }
        }

        List<Point> points = new ArrayList<>();

        for (int i=0;i<_points.size()-2;i++) {
            points.add(Point.createXYT(_points.get(i).getX(), _points.get(i).getY(), distanceT.get(i)));
        }

        return points;
    }

    public void setEvaluatePoints(List<Point> _evaluatePoints) {

        m_evaluatePoints = _evaluatePoints;

    }

    /**制御点列 */
    public List<Point> m_controlPoints = new ArrayList<>();

    /** 入力点列 */
    public final List<Point> m_points;

    /** 評価点列 */
    public List<Point> m_evaluatePoints = new ArrayList<>();

}
