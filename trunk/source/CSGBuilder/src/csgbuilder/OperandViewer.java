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
    private CSGTree mTree = null;
	
	// The GLCanvas used to render the mesh
	private GLCanvas mCanvas;
	
	private OperandViewerRenderer mRenderer;
	CSGTreePolygoniser mPolygoniser;

    /* Constructor */
	public OperandViewer(OperandViewerRenderer pRenderer, OperandViewerMIA inputAdapter) {
        GLCapabilities lvCaps = new GLCapabilities();
           lvCaps.setRedBits(8);
           lvCaps.setBlueBits(8);
           lvCaps.setGreenBits(8);
           lvCaps.setAlphaBits(8);
           lvCaps.setDoubleBuffered(true);
		
        mPolygoniser = new CSGTreePolygoniser(this);

		mCanvas = new GLCanvas(lvCaps);
		
		mRenderer = pRenderer;
        mRenderer.setViewer(this);
		OperandViewerMIA lvListener = inputAdapter;
		
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
    
    public CSGTree getTree() {
        return mTree;
    }
    
    public void setTree(CSGTree pvTree) {
        mTree = pvTree;
    }
    
    public void startPolygonisation() {
        // Clear mesh
        mMesh = null;
        
        if (mPolygoniser.isAlive()) {
            mPolygoniser.stop();
        }
        
        mPolygoniser = new CSGTreePolygoniser(this);
        mPolygoniser.start();
    }

	public GLCanvas getCanvas() {
		return mCanvas;
	}
	
	public void start() {
		mRenderer.start();
	}
}
