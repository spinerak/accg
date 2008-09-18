/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csgbuilder;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

/**
 *
 * @author s031407
 */
public class OperandViewerMIA extends MouseInputAdapter implements KeyListener {
    private OperandViewerRenderer mRenderer;
	
	private boolean mTranslate = false;
    
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
			if (mTranslate)	{
				mRenderer.startTranslate(mouseEvent.getPoint());
			}
			else {
				mRenderer.startRotate(mouseEvent.getPoint());
			}
         }
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
			if (mTranslate) {
				mRenderer.translate(mouseEvent.getPoint());
			}
			else {
	            mRenderer.rotate(mouseEvent.getPoint());
			}
        }
    }
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		mRenderer.zoom((float)e.getWheelRotation());
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		// When ctrl is pressed do translation instead of ratation
		if (e.getKeyCode() == 17) {
			mTranslate = true;
		}
	}

	public void keyReleased(KeyEvent e) {
		// When ctrl is pressed do translation instead of ratation
		if (e.getKeyCode() == 17) {
			mTranslate = false;
		}
	}
}
