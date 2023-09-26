package jp.sagalab.b4zemi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utility {
    //入力点をcsv形式で保存する
    public static void saveEnterPoints(List<Point> m_points){
        File output = new File("output");
        if(!output.exists()){
            output.mkdir();
        }
        try {
            PrintWriter pw = new PrintWriter(new File(output, "points_" + System.currentTimeMillis() + ".csv"));
            pw.println("入力点の出力");
            for(Point p : m_points){
                pw.print(p.getX());
                pw.print(",");
                pw.print(p.getY());
                pw.print(",");
                pw.println(p.time());
            }
            pw.close();
        } catch (IOException ignore){

        }
    }
    //評価点をcsv形式で保存する
    public static void saveEvaluatePoints(List<Point> _points){
        File output = new File("output");
        if(!output.exists()){
            output.mkdir();
        }
        List<Double> distanceList = calcDistance(_points);
        try {
            PrintWriter pw = new PrintWriter(new File(output, "points_" + System.currentTimeMillis() + ".csv"));
            pw.println("評価点の出力");
            for(int i=0; i< _points.size(); i++){
                pw.print(distanceList.get(i));
                pw.print(",");
                pw.println(_points.get(i).time());
            }
            pw.close();
        } catch (IOException ignore){

        }
    }


    public static List<Point> loadPoints(String filename){
        List<Point> m_points = new ArrayList<>();
        try{
            Scanner sc = new Scanner(new File(filename));
            while (sc.hasNext()){
                String line = sc.nextLine();
                String[] split = line.split(",");
                m_points.add(Point.createXYT(
                        Double.parseDouble(split[0]),
                        Double.parseDouble(split[1]),
                        Double.parseDouble(split[2])
                ));
            }
            sc.close();
        } catch (FileNotFoundException e){
            throw  new RuntimeException(e);
        }
        return m_points;
    }

    //入力点列の距離パラメータを計算
    public static List<Double> calcDistance(List<Point> _points){
        List<Double> distanceList = new ArrayList<>();
        distanceList.add(0.0);
        double distance = 0;

        for(int i=0; i< _points.size()-1; i++){
            double x = _points.get(i + 1).getX() - _points.get(i).getX();
            double y = _points.get(i + 1).getY() - _points.get(i).getY();
            distance += Math.sqrt(x * x + y * y);
            distanceList.add(distance);
        }

        distanceList = normalizedTimes(distanceList);

        return distanceList;
    }

    //正規化
    public static List<Double> normalizedTimes(List<Double> _distanceList){
        double parameterLast = _distanceList.get(_distanceList.size()-1);
        double parameter0 = _distanceList.get(0);

        List<Double> pointParameter = new ArrayList<>();

        for(double d : _distanceList){
            double denominator = parameterLast - parameter0;
            double numerator = d - parameter0;
            double result = numerator / denominator;
            pointParameter.add(result);
        }
        return pointParameter;
    }

}
