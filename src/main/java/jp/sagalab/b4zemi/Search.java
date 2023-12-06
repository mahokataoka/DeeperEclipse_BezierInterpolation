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
        m_count=0;

        double[] search = NelderMead.search(createRange(), 0.001, 1000, this::calc);

        System.out.println("count calc:"+m_count);
        double theta = search[2];
        double w = Math.cos(theta/2);
//        double w = search[2];
        m_w=w;

        System.out.println("far_x:"+search[0]+" far_y:"+search[1]+" w:"+ w + " theta:"+ search[2]);

        //最遠点
        Point farPoint = Point.create(search[0], search[1]);
        List<Point> fPoints = new ArrayList<>();

        //モデルの最遠点を含む制御点のリストを作成
        fPoints.add(Point.create(0,0));
        fPoints.add(farPoint);
        fPoints.add(Point.create(1,0));

        //補間の計算
        //入力点列のパラメータを修正
        List<Point> modifiedPoints = new ArrayList<>(calculateT(_points,fPoints,w));

        //BのMatrixを求める
        double[][] elements = new double[modifiedPoints.size()][];
        if(m_w>=0) {
            for (int i = 0; i <= modifiedPoints.size() - 1; i++) {
                elements[i] = BezierCurve.Brow(modifiedPoints.get(i).time(), w);
            }
        }else{
            BezierCurve bc = BezierCurve.create(fPoints);
            for (int i = 0; i <= modifiedPoints.size() - 1; i++) {
                elements[i] = bc.Qrow(modifiedPoints.get(i).time(), w);
            }
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
        m_count++;
        double error;
        List<Point> fPoints = new ArrayList<>();    //規格化楕円弧モデルの制御点列

        fPoints.add(Point.create(0,0));
        fPoints.add(Point.create(values[0],values[1]));
        fPoints.add(Point.create(1.0,0));

        if(values[2]<0){
            return Double.POSITIVE_INFINITY;
        }
        if(values[2]>Math.PI*2){
            return Double.POSITIVE_INFINITY;
        }

        double theta = values[2];
        double w = Math.cos(theta/2);

//        double w = values[2];
//
//        if(w<-1){
//            return Double.POSITIVE_INFINITY;
//        }
//        if(w>1){
//            return Double.POSITIVE_INFINITY;
//        }

        //入力点列のパラメータを修正
        List<Point> modified_points = new ArrayList<>(calculateT(m_points,fPoints,w));

        //最小二乗法の計算をする
        //BのMatrixを求める
        double[][] elements = new double[modified_points.size()][];
        if(w>=0) {
            for (int i = 0; i <= modified_points.size() - 1; i++) {
                elements[i] = BezierCurve.Brow(modified_points.get(i).time(), w);
            }
        }else{
            BezierCurve bc = BezierCurve.create(fPoints);
            for (int i = 0; i <= modified_points.size() - 1; i++) {
                elements[i] = bc.Qrow(modified_points.get(i).time(), w);
            }
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
        Matrix Nd = B.product(x);
        Matrix Ndp = Nd.minus(p);
        Matrix Ndptrans = Ndp.transpose();
        Matrix NdpSquare = Ndptrans.product(Ndp);
        error = NdpSquare.get(0,0) + NdpSquare.get(1,1);

        return error;
    }

//  範囲を生成するメソッド
    public static jp.sagalab.jftk.curve.Range[] createRange(){
        Range[] range = new Range[3];
        //最遠点のx座標の定義域
        range[0] = Range.create(0, 1);
        //最遠点のy座標の定義域
        range[1] = Range.create(0, 1);
        //重みwの定義域
        range[2] = Range.create(Math.PI*(2.0/3.0), Math.PI*(4.0/3.0));
//        range[2] = Range.create(-0.5, 0.5);

        return range;
    }


    /**
     * ポイントの正規化する
     * @param _points 入力点列
     * @return パラメータを計算し直した入力点列
     */
    public List<Point> calculateT(List<Point> _points, List<Point> _controlPoints ,double _w){
        //規格化楕円弧モデルの生成
        BezierCurve bezierCurve = BezierCurve.create(_controlPoints);
        List<Point> evaluateNEAList = new ArrayList<>();    //NEAはNormalized Ellipse Arc

        if(_w>=0) {
            for (double t = 0; t <= 1; t += 0.001) {
                Point points = bezierCurve.evaluate(t, _w);
                evaluateNEAList.add(points);
            }
        }else{
            for (double t = 0; t <= 1; t += 0.001) {
                Point points = bezierCurve.evaluateEx(t, _w);
                evaluateNEAList.add(points);
            }
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
        //入力点列の距離を計算する
        List<Double> dList = new ArrayList<>();
        dList.add(0.0);
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
                    //m:nに内分する
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

    /** 重み　*/
    public double m_w;

    int m_count;
}
