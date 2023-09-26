package jp.sagalab.b4zemi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utility {
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
    public static void saveEvaluatePoints(List<Point> m_points){
        File output = new File("output");
        if(!output.exists()){
            output.mkdir();
        }
        try {
            PrintWriter pw = new PrintWriter(new File(output, "points_" + System.currentTimeMillis() + ".csv"));
            pw.println("評価点の出力");
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
}
