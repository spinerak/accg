/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csgbuilder;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

/**
 *
 * @author s031407
 */
public class AOperandMIA extends OperandViewerMIA {
    private CSGBuilderView mView;

    public AOperandMIA(AOperandRenderer pRenderer, CSGBuilderView pView/*, GLAutoDrawable drawable*/) {
        super(pRenderer);
        mView = pView;
    }
    
    public void keyPressed(KeyEvent e) {
		// When ctrl is pressed do translation instead of ratation
        AOperandRenderer lvRenderer = (AOperandRenderer) getRenderer();
        
		switch (e.getKeyCode()) {
            case 16: // shift
                lvRenderer.startUnion(mView.mBOperandViewer.getMesh());
                break;
            case 18: // alt
                lvRenderer.startDifference(mView.mBOperandViewer.getMesh());
                break;
        }
        
        super.keyPressed(e);
        
        //System.out.printf("%d\n", e.getKeyCode());
	}

	public void keyReleased(KeyEvent e) {
		// When ctrl is pressed do translation instead of ratation
        AOperandRenderer lvRenderer = (AOperandRenderer) getRenderer();

        switch (e.getKeyCode()) {
            case 16: // shift
                lvRenderer.stopUnion();
                break;
            case 18: // alt
                lvRenderer.stopDifference();
                break;           
		}
        
        super.keyReleased(e);
	}
    
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            AOperandRenderer lvRenderer = (AOperandRenderer) getRenderer();
            if (lvRenderer.doCSGUnion()) {
                mView.mAOperandViewer.getTree().union(mView.mBOperandViewer.getTree());
                mView.mAOperandViewer.startPolygonisation();
            }
            else if (lvRenderer.doCSGDifference()) {
                mView.mAOperandViewer.getTree().difference(mView.mBOperandViewer.getTree());
                mView.mAOperandViewer.startPolygonisation();
            }
        }
        
        super.mouseClicked(e);
    }
}
