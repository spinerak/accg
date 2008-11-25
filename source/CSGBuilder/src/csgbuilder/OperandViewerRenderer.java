/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csgbuilder;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.BufferUtil;
import java.awt.Point;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 *
 * @author s031407
 */
public class OperandViewerRenderer implements GLEventListener {
    // Initial zoom value
	private double mZoom = -6.0;
    
    private double polyAnimOffset = 0; 
    
    static final float[] defaultColor = new float[]{1, 0, 0};
	
	private Point mStartDrag = null;
	private Point mTranslation = new Point(0, 0);
	
	static final float ZOOM_FACTOR = 1f/3f;
	
    // User Defined Variables
    private GLUquadric quadratic;   // Used For Our Quadric
    private GLU glu = new GLU();
	private GL gl = null;

    private Matrix4f LastRot = new Matrix4f();
    private Matrix4f ThisRot = new Matrix4f();
    private final Object matrixLock = new Object();
    private float[] matrix = new float[16];
	private Animator mAnimator;


    private ArcBall arcBall = new ArcBall(640.0f, 480.0f);  // NEW: ArcBall Instance

    private CSGTree tree;
	
	private boolean VBOSupported = false; // Whether or not VBO's are supported
	private boolean VBOUsed = false; // Wether or not VBO's are actually used
	
	private int[] VBOVertices = new int[1];  // Vertex VBO Name
	private int[] VBOCells = new int[1];
	private FloatBuffer vertices;    // Vertex Data
	private int vertexCount;
	
	private FloatBuffer cells;	// Marching Cube debug cells data
	private int cellCount;
    
    public boolean CSGMode = false;
	
	private BoundingBox mBoundingBox = null;
	
	private OperandViewer mViewer;
    
    public OperandViewerRenderer() {
    }
    
    public void setViewer(OperandViewer pViewer) {
        mViewer = pViewer;
    }

    public OperandViewer getViewer() {
        return mViewer;
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();

        gl.glViewport(0, 0, width, height);  // Reset The Current Viewport
        gl.glMatrixMode(GL.GL_PROJECTION);   // Select The Projection Matrix
        gl.glLoadIdentity();                 // Reset The Projection Matrix
        
        // Calculate The Aspect Ratio Of The Window
        glu.gluPerspective(45.0f, (float) (width) / (float) (height),            
                1.0f, 100.0f);
        gl.glMatrixMode(GL.GL_MODELVIEW);  // Select The Modelview Matrix
        gl.glLoadIdentity();               // Reset The Modelview Matrix

        //*NEW* Update mouse bounds for arcball
        arcBall.setBounds((float) width, (float) height);                 
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, 
            boolean deviceChanged) {
        init(drawable);
    }

    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL();
		
		// Check For VBO support
        VBOSupported = gl.isFunctionAvailable("glGenBuffersARB") &&
                gl.isFunctionAvailable("glBindBufferARB") &&
                gl.isFunctionAvailable("glBufferDataARB") &&
                gl.isFunctionAvailable("glDeleteBuffersARB");
		
        // Start Of User Initialization
        LastRot.setIdentity();                                // Reset Rotation
        ThisRot.setIdentity();                                // Reset Rotation
        ThisRot.get(matrix);

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);              // Black Background
        gl.glClearDepth(1.0f);                                // Depth Buffer Setup
        gl.glDepthFunc(GL.GL_LEQUAL);  // The Type Of Depth Testing (Less Or Equal)
        gl.glEnable(GL.GL_DEPTH_TEST);                        // Enable Depth Testing
        gl.glShadeModel(GL.GL_FLAT);   // Select Flat Shading (Nice Definition Of Objects)
        
        // Set Perspective Calculations To Most Accurate
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);                

        quadratic = glu.gluNewQuadric(); // Create A Pointer To The Quadric Object
        glu.gluQuadricNormals(quadratic, GLU.GLU_SMOOTH); // Create Smooth Normals
        glu.gluQuadricTexture(quadratic, true); // Create Texture Coords

        gl.glEnable(GL.GL_LIGHT0);     // Enable Default Light
        gl.glEnable(GL.GL_LIGHTING);   // Enable Lighting
		gl.glShadeModel(GL.GL_SMOOTH);
        gl.glEnable(GL.GL_COLOR_MATERIAL);  // Enable Color Material
        
        gl.glEnable (GL.GL_BLEND);
        gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    }
	
	void zoom(float pvAmount) {
		mZoom += pvAmount*ZOOM_FACTOR;
	}

    void reset() {
        synchronized(matrixLock) {
            LastRot.setIdentity();   // Reset Rotation
            ThisRot.setIdentity();   // Reset Rotation
        }
    }
	
	void startTranslate(Point pvMousePt) {
		mStartDrag = pvMousePt;
	}
	
	private Vertex getProjectionCoords(Point p, GL gl, double mDepth) {
			double[] lvProjection = new double[16];
			double[] lvModelView = new double[16];
			int[] lvViewPort = new int[4];
			
			int realy = 0;// GL y coord pos
			double wcoord[] = new double[4];// wx, wy, wz;// returned xyz coords
    
			  gl.glGetIntegerv(GL.GL_VIEWPORT, lvViewPort, 0);
			  gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, lvModelView, 0);
			  gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, lvProjection, 0);

			  /* note viewport[3] is height of window in pixels */
			  realy = lvViewPort[3] - (int) p.y - 1;

			  glu.gluUnProject((double) p.x, (double) realy, 0.0, //
				  lvModelView, 0,
				  lvProjection, 0, 
				  lvViewPort, 0, 
				  wcoord, 0);
			  
			  return new Vertex(wcoord[0], wcoord[1], wcoord[2]);
	}
	
	void translate(Point pvMousePt) {
		if (mStartDrag != null) {
			mTranslation.x -= mStartDrag.x - pvMousePt.x;
			mTranslation.y -= mStartDrag.y - pvMousePt.y;
            
			mStartDrag = pvMousePt;
		}
	}

    void startRotate( Point MousePt ) {
        synchronized(matrixLock) {
            LastRot.set( ThisRot );  // Set Last Static Rotation To Last Dynamic One
        }
        arcBall.click( MousePt );    // Update Start Vector And Prepare For Dragging
    }

    void rotate( Point MousePt )       // Perform Motion Updates Here
    {
        Quat4f ThisQuat = new Quat4f();

        // Update End Vector And Get Rotation As Quaternion
        arcBall.drag( MousePt, ThisQuat); 
        synchronized(matrixLock) {
            ThisRot.setRotation(ThisQuat);  // Convert Quaternion Into Matrix3fT
            ThisRot.mul( ThisRot, LastRot); // Accumulate Last Rotation Into This One
        }
    }

    private void drawAxis(GL gl) {
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
        
        gl.glBegin(GL.GL_TRIANGLES);
        
        gl.glColor4d(1.0, 0.0, 0.0, 1);        
        gl.glVertex3d(-100, 0, 0);
        gl.glVertex3d(100, 0, 0);
        gl.glVertex3d(0, 0, 0);
        
        gl.glColor4d(0.0, 1.0, 0.0, 1);        
        gl.glVertex3d(0, -100, 0);
        gl.glVertex3d(0, 100, 0);
        gl.glVertex3d(0, 0, 0);
        
        gl.glColor4d(0.0, 0.0, 1.0, 1);
        gl.glVertex3d(0, 0, -100);
        gl.glVertex3d(0, 0, 100);
        gl.glVertex3d(0, 0, 0);
        
        gl.glEnd();
    }
    
    private void drawBoundingBox(GL gl) {
		BoundingBox box = mBoundingBox;
        
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
        gl.glColor3d(1.0, 1.0, 1.0);
        
        gl.glBegin(GL.GL_QUADS);
        
        // Top
        gl.glVertex3d(box.p[2].x, box.p[2].y, box.p[2].z);
        gl.glVertex3d(box.p[3].x, box.p[3].y, box.p[3].z);
        gl.glVertex3d(box.p[7].x, box.p[7].y, box.p[7].z);
        gl.glVertex3d(box.p[6].x, box.p[6].y, box.p[6].z);
        
        // Bottom
        gl.glVertex3d(box.p[0].x, box.p[0].y, box.p[0].z);
        gl.glVertex3d(box.p[1].x, box.p[1].y, box.p[1].z);
        gl.glVertex3d(box.p[4].x, box.p[4].y, box.p[4].z);
        gl.glVertex3d(box.p[5].x, box.p[5].y, box.p[5].z);
        
        // Front
        gl.glVertex3d(box.p[7].x, box.p[7].y, box.p[7].z);
        gl.glVertex3d(box.p[6].x, box.p[6].y, box.p[6].z);
        gl.glVertex3d(box.p[4].x, box.p[4].y, box.p[4].z);
        gl.glVertex3d(box.p[5].x, box.p[5].y, box.p[5].z);
        
        // Back
        gl.glVertex3d(box.p[0].x, box.p[0].y, box.p[0].z);
        gl.glVertex3d(box.p[1].x, box.p[1].y, box.p[1].z);
        gl.glVertex3d(box.p[2].x, box.p[2].y, box.p[2].z);
        gl.glVertex3d(box.p[3].x, box.p[3].y, box.p[3].z);
        
        gl.glEnd();
    }
    
    private void drawGrid(GL gl) {
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
        
        gl.glBegin(GL.GL_TRIANGLES);
        
        gl.glColor3d(1.0, 1.0, 0.0);        
        gl.glVertex3d(-100, 0, 0);
        gl.glVertex3d(100, 0, 0);
        gl.glVertex3d(0, 0, 0);
        
        gl.glColor3d(1.0, 0.5, 0.0);        
        gl.glVertex3d(0, -100, 0);
        gl.glVertex3d(0, 100, 0);
        gl.glVertex3d(0, 0, 0);
        
        gl.glColor3d(0.5, 0.5, 0.0);
        gl.glVertex3d(0, 0, -100);
        gl.glVertex3d(0, 0, 100);
        gl.glVertex3d(0, 0, 0);
        
        gl.glEnd();
    }
    
    public void display(GLAutoDrawable drawable) {
        synchronized(matrixLock) {
            ThisRot.get(matrix);
        }
        GL gl = drawable.getGL();

        // Clear Screen And Depth Buffer
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);                
        gl.glLoadIdentity();                  // Reset The Current Modelview Matrix
        
        
        // Convert translation to projection coords
		Vertex lvTranslation = getProjectionCoords(mTranslation, gl, mZoom);
		//Vertex lvTranslation = new Vertex(0, 0, 0);
        gl.glTranslatef(lvTranslation.x, lvTranslation.y, (float)mZoom);  

        gl.glPushMatrix();                  // NEW: Prepare Dynamic Transform
        gl.glMultMatrixf(matrix, 0);        // NEW: Apply Dynamic Transform
        gl.glColor3f(1.0f, 0.75f, 0.75f);

        //drawGrid(gl);
        //drawBoundingBox(gl);
        
        if (mViewer.isPolygonising) {
            gl.glColor3f(0.8f, 0.8f, 0.8f);
            
            double w = 1.5;
            double h = 0.05;
            
            double animw = 0.5;
            
            double animInc = 0.2;
            
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
            gl.glBegin(GL.GL_QUADS);
                gl.glVertex3d(-w/2, -h/2, 0);
                gl.glVertex3d(w/2, -h/2, 0);
                gl.glVertex3d(w/2, h/2, 0);
                gl.glVertex3d(-w/2, h/2, 0);
            gl.glEnd();
            
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
            
            double xOffset = -w/2 + polyAnimOffset;

            if (polyAnimOffset < 0) {
                animw = animw + polyAnimOffset;
                xOffset -= polyAnimOffset;
            }
            animw = Math.min(animw, w - polyAnimOffset);
            
            
            gl.glBegin(GL.GL_QUADS);
                gl.glVertex3d(xOffset, -h/2, 0);
                gl.glVertex3d(xOffset + animw, -h/2, 0);
                gl.glVertex3d(xOffset + animw, h/2, 0);
                gl.glVertex3d(xOffset, h/2, 0);
            gl.glEnd();

            polyAnimOffset += animInc;
            
            if (polyAnimOffset > w) polyAnimOffset = -animw;

        }
        else if (mViewer.getMesh() != null) {
			mViewer.getMesh().render(gl, CSGMode, defaultColor);
		}
        
        if (!mViewer.isPolygonising) {
            // Debug axis
            drawAxis(gl);
        }
        
        gl.glPopMatrix();                   // NEW: Unapply Dynamic Transform

        gl.glFlush();                       // Flush The GL Rendering Pipeline
    }
	
	public void start() {
		mAnimator = new Animator(mViewer.getCanvas());
		mAnimator.start();
	}
}
