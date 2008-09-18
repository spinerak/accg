/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csgbuilder;

import com.sun.opengl.util.Animator;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;

/**
 *
 * @author s031407
 */
public class OperandViewer {
	// Mesh of the operand being shown (can be null if there is no operand loaded)
	private OperandMesh mMesh = null;
	
	// The GLCanvas used to render the mesh
	private GLCanvas mCanvas;
	
	private OperandViewerRenderer mRenderer;
	
	/* Constructor */
	public OperandViewer() {
        GLCapabilities lvCaps = new GLCapabilities();
        lvCaps.setDoubleBuffered(true);
		
		mCanvas = new GLCanvas(lvCaps);
		
		mRenderer = new OperandViewerRenderer(this);
		OperandViewerMIA lvListener = new OperandViewerMIA(mRenderer);
		
        mCanvas.addGLEventListener(mRenderer);
        mCanvas.addMouseListener(lvListener);
        mCanvas.addMouseMotionListener(lvListener);
		mCanvas.addMouseWheelListener(lvListener);
		mCanvas.addKeyListener(lvListener);

		mCanvas.setFocusable(true);
	}
	
	/* Getters/Setters */
	public void setMesh(OperandMesh pMesh) {
		mMesh = pMesh;
	}

	public OperandMesh getMesh() {
		return mMesh;
	}

	public GLCanvas getCanvas() {
		return mCanvas;
	}
	
	public void start() {
		mRenderer.start();
	}
}
