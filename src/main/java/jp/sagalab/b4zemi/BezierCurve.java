package jp.sagalab.b4zemi;

import java.util.ArrayList;
import java.util.List;

public class BezierCurve {
    //フィールド 制御点列
    private final List<Point> m_controlPoints;
    //コンストラクタ 制御点列を指定してBezier曲線オブジェクトを生成する．
    private BezierCurve(List<Point> _controlPoints){
        m_controlPoints = _controlPoints;
    }
    //ファクトリーメソッド
    public static BezierCurve create(List<Point> _controlPoints){
        return new BezierCurve(_controlPoints);
    }
    //getControlPoints制御点列のコピーを取得する
    public List<Point> getControlPoints(){
        return m_controlPoints;
    }

    //getDegree次数を取得する．
    public int getDegree(){
        return getControlPoints().size() - 1;
    }
    //evaluate パラメータ t に対応する評価点を De Casteljau のアルゴリズムで評価する．
    public Point evaluate(double _t){
        List<Point> bezierPoints  = new ArrayList<Point>();
        if(m_controlPoints.size() == 1){
            return m_controlPoints.get(0);
        }
        for(int i = 0;i < getDegree();i++){
            bezierPoints.add(m_controlPoints.get(i).divide(m_controlPoints.get(i+1),_t));
        }
        BezierCurve b = BezierCurve.create(bezierPoints);
        return b.evaluate(_t);

    }
}
