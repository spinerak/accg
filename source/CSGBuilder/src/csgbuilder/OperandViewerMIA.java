/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csgbuilder;

import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

/**
 *
 * @author s031407
 */
public class OperandViewerMIA extends MouseInputAdapter {
    private OperandViewerRenderer mRenderer;
    
    public OperandViewerMIA(OperandViewerRenderer pRenderer/*, GLAutoDrawable drawable*/) {
        mRenderer = pRenderer;
    }
    
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            mRenderer.reset();
        }
    }

    public void mousePressed(MouseEvent mouseEvent) {
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
            mRenderer.startDrag(mouseEvent.getPoint());
        }
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
            mRenderer.drag(mouseEvent.getPoint());
        }
    }
}
