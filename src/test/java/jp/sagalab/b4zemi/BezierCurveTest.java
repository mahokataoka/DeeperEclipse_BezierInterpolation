//package jp.sagalab.b4zemi;
//
//import org.junit.jupiter.api.Test;
//
//import java.util.Arrays;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public final class BezierCurveTest {
//
//  @Test
//  public void testGetControlPoints() {
//    System.out.println("GetControlPoints");
//    BezierCurve b = BezierCurve.create(Arrays.asList(
//            Point.create(-2.0, 0.0), Point.create(-1.0, 0.0),Point.create(0.0, 2.0), Point.create(1.0, 0.0), Point.create(2.0, 0.0)));
//    assertEquals(-2.0, b.getControlPoints().get(0).getX(), 1.0e-10);
//    assertEquals(-1.0, b.getControlPoints().get(1).getX(), 1.0e-10);
//    assertEquals(0.0, b.getControlPoints().get(2).getX(), 1.0e-10);
//    assertEquals(1.0, b.getControlPoints().get(3).getX(), 1.0e-10);
//    assertEquals(2.0, b.getControlPoints().get(4).getX(), 1.0e-10);
//    assertEquals(0.0, b.getControlPoints().get(0).getY(), 1.0e-10);
//    assertEquals(0.0, b.getControlPoints().get(1).getY(), 1.0e-10);
//    assertEquals(2.0, b.getControlPoints().get(2).getY(), 1.0e-10);
//    assertEquals(0.0, b.getControlPoints().get(3).getY(), 1.0e-10);
//    assertEquals(0.0, b.getControlPoints().get(4).getY(), 1.0e-10);
//    assertEquals(5, b.getControlPoints().size());
//  }
//
//  @Test
//  public void testGetDegree() {
//    System.out.println("GetControlPoints");
//    BezierCurve b = BezierCurve.create(Arrays.asList(
//            Point.create(-2.0, 0.0), Point.create(-1.0, 0.0), Point.create(0.0, 2.0), Point.create(1.0, 0.0), Point.create(2.0, 0.0)));
//
//    assertEquals(4, b.getDegree());
//  }
//
//  @Test
//  public void testEvaluate() {
//    System.out.println("Evaluate");
//    BezierCurve b = BezierCurve.create(Arrays.asList(
//            Point.create(-2.0, 0.0), Point.create(-1.0, 0.0), Point.create(0.0, 2.0), Point.create(1.0, 0.0), Point.create(2.0, 0.0)));
//
//    assertEquals(-2.0, b.evaluate(0.0).getX(), 1.0e-10);
//    assertEquals(-1.0, b.evaluate(0.25).getX(), 1.0e-10);
//    assertEquals(0.0, b.evaluate(0.5).getX(), 1.0e-10);
//    assertEquals(1.0, b.evaluate(0.75).getX(), 1.0e-10);
//    assertEquals(2.0, b.evaluate(1.0).getX(), 1.0e-10);
//    assertEquals(0.0, b.evaluate(0.0).getY(), 1.0e-10);
//    assertEquals(27 / 64.0, b.evaluate(0.25).getY(), 1.0e-10);
//    assertEquals(0.75, b.evaluate(0.5).getY(), 1.0e-10);
//    assertEquals(27 / 64.0, b.evaluate(0.75).getY(), 1.0e-10);
//    assertEquals(0.0, b.evaluate(1.0).getY(), 1.0e-10);
//  }
//}
