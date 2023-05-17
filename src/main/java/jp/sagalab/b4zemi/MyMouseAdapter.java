package jp.sagalab.b4zemi;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyMouseAdapter extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        System.out.println("クリック");
    }

}
