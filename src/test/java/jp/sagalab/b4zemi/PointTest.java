package jp.sagalab.b4zemi;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by jumpaku on 2017/05/23.
 * Made a little change by inagaki on 2023/05/07
 */
public class PointTest {
  @Test
  public void testGetX() {
    System.out.println("GetX");

    Point instance1 = Point.create(2.0, 4.3);
    assertEquals(2.0, instance1.getX(), 1.0e-10);

    Point instance2 = Point.create(-0.04, 6.3);
    assertEquals(-0.04, instance2.getX(), 1.0e-10);
  }

  @Test
  public void testGetY() {
    System.out.println("GetY");

    Point instance1 = Point.create(2.0, 4.3);
    assertEquals(4.3, instance1.getY(), 1.0e-10);

    Point instance2 = Point.create(-0.04, 6.3);
    assertEquals(6.3, instance2.getY(), 1.0e-10);
  }

  @Test
  public void testDivide() {
    System.out.println("Divide");

    Point p0 = Point.create(10.0, 1.0);
    Point p2 = Point.create(20.0, 2.0);

    assertEquals(13.0, p0.divide(p2, 0.3).getX(), 1.0e-10);
    assertEquals(0.0, p0.divide(p2, -1.0).getX(), 1.0e-10);
    assertEquals(30.0, p0.divide(p2, 2.0).getX(), 1.0e-10);
    assertEquals(10.0, p0.divide(p2, 0.0).getX(), 1.0e-10);
    assertEquals(20.0, p0.divide(p2, 1.0).getX(), 1.0e-10);

    assertEquals(1.3, p0.divide(p2, 0.3).getY(), 1.0e-10);
    assertEquals(0.0, p0.divide(p2, -1.0).getY(), 1.0e-10);
    assertEquals(3.0, p0.divide(p2, 2.0).getY(), 1.0e-10);
    assertEquals(1.0, p0.divide(p2, 0.0).getY(), 1.0e-10);
    assertEquals(2.0, p0.divide(p2, 1.0).getY(), 1.0e-10);
  }

}