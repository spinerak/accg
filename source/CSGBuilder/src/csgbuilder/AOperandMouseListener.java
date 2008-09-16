/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csgbuilder;

import com.sun.opengl.util.BufferUtil;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLUquadric;
import javax.media.opengl.glu.GLU;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.nio.FloatBuffer;
import java.util.ArrayList;



/**
 *
 * @author s031407
 */
class AOperandMouseListener extends MouseInputAdapter {
    private Renderer renderer;
    
    public AOperandMouseListener(Renderer renderer/*, GLAutoDrawable drawable*/) {
        this.renderer = renderer;
//        GL glDisplay = drawable.getGL();
//        glDisplay.registerMouseEventForHelp(
//                MouseEvent.MOUSE_CLICKED, MouseEvent.BUTTON1_DOWN_MASK,
//                "Toggle display mode"
//        );
    }
    
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            renderer.reset();
        }
    }

    public void mousePressed(MouseEvent mouseEvent) {
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
            renderer.startDrag(mouseEvent.getPoint());
        }
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
            renderer.drag(mouseEvent.getPoint());
        }
    }
}
/**
 * Created by IntelliJ IDEA.
 * User: pepijn
 * Date: Aug 7, 2005
 * Time: 5:18:33 PM
 * To change this template use File | Settings | File Templates.
 */
class ArcBall {
    private static final float Epsilon = 1.0e-5f;

    Vector3f StVec;          //Saved click vector
    Vector3f EnVec;          //Saved drag vector
    float adjustWidth;       //Mouse bounds width
    float adjustHeight;      //Mouse bounds height

    public ArcBall(float NewWidth, float NewHeight) {
        StVec = new Vector3f();
        EnVec = new Vector3f();
        setBounds(NewWidth, NewHeight);
    }

    public void mapToSphere(Point point, Vector3f vector) {
        // Copy paramter into temp point
        Point2f tempPoint = new Point2f(point.x, point.y);

        // Adjust point coords and scale down to range of [-1 ... 1]
        tempPoint.x = (tempPoint.x * this.adjustWidth) - 1.0f;
        tempPoint.y = 1.0f - (tempPoint.y * this.adjustHeight);

        // Compute the square of the length of the vector to the point from the center
        float length = (tempPoint.x * tempPoint.x) + (tempPoint.y * tempPoint.y);

        // If the point is mapped outside of the sphere... (length > radius squared)
        if (length > 1.0f) {
            // Compute a normalizing factor (radius / sqrt(length))
            float norm = (float) (1.0 / Math.sqrt(length));

            // Return the "normalized" vector, a point on the sphere
            vector.x = tempPoint.x * norm;
            vector.y = tempPoint.y * norm;
            vector.z = 0.0f;
        } else    //Else it's on the inside
        {
            // Return a vector to a point mapped inside the sphere 
            // sqrt(radius squared - length)
            vector.x = tempPoint.x;
            vector.y = tempPoint.y;
            vector.z = (float) Math.sqrt(1.0f - length);
        }

    }

    public void setBounds(float NewWidth, float NewHeight) {
        assert((NewWidth > 1.0f) && (NewHeight > 1.0f));

        // Set adjustment factor for width/height
        adjustWidth = 1.0f / ((NewWidth - 1.0f) * 0.5f);
        adjustHeight = 1.0f / ((NewHeight - 1.0f) * 0.5f);
    }

    // Mouse down
    public void click(Point NewPt) {
        mapToSphere(NewPt, this.StVec);

    }

    // Mouse drag, calculate rotation
    public void drag(Point NewPt, Quat4f NewRot) {
        // Map the point to the sphere
        this.mapToSphere(NewPt, EnVec);

        // Return the quaternion equivalent to the rotation
        if (NewRot != null) {
            Vector3f Perp = new Vector3f();

            // Compute the vector perpendicular to the begin and end vectors
            Vector3f.cross(Perp, StVec, EnVec);

            // Compute the length of the perpendicular vector
            if (Perp.length() > Epsilon)    //if its non-zero
            {
                // We're ok, so return the perpendicular vector as the transform 
                // after all
                NewRot.x = Perp.x;
                NewRot.y = Perp.y;
                NewRot.z = Perp.z;
                // In the quaternion values, w is cosine (theta / 2), 
                // where theta is rotation angle
                NewRot.w = Vector3f.dot(StVec, EnVec);
            } else                                    //if its zero
            {
                // The begin and end vectors coincide, so return an identity transform
                NewRot.x = NewRot.y = NewRot.z = NewRot.w = 0.0f;
            }
        }
    }

}


/**
 * Created by IntelliJ IDEA.
 * User: pepijn
 * Date: Aug 7, 2005
 * Time: 6:01:31 PM
 * To change this template use File | Settings | File Templates.
 */
class Matrix4f {
    float M00;
    float M10;
    float M20;
    float M30;
    float M01;
    float M11;
    float M21;
    float M31;
    float M02;
    float M12;
    float M22;
    float M32;
    float M03;
    float M13;
    float M23;
    float M33;

    public Matrix4f() {
        setIdentity();
    }

    void get(float[] dest) {
        dest[0] = M00;
        dest[1] = M10;
        dest[2] = M20;
        dest[3] = M30;
        dest[4] = M01;
        dest[5] = M11;
        dest[6] = M21;
        dest[7] = M31;
        dest[8] = M02;
        dest[9] = M12;
        dest[10] = M22;
        dest[11] = M32;
        dest[12] = M03;
        dest[13] = M13;
        dest[14] = M23;
        dest[15] = M33;
    }

    void setZero() {
        M00 = M01 = M02 = M03 = M10 = M11 = M12 = M13 = M20 = M21 = M22 = 
                M23 = M30 = M31 = M32 = M33 = 0.0f;
    }

    void setIdentity() {
        setZero();
        M00 = M11 = M22 = M33 = 1.0f;
    }

    void setRotation(Quat4f q1) {
        float n, s;
        float xs, ys, zs;
        float wx, wy, wz;
        float xx, xy, xz;
        float yy, yz, zz;

        n = (q1.x * q1.x) + (q1.y * q1.y) + (q1.z * q1.z) + (q1.w * q1.w);
        s = (n > 0.0f) ? (2.0f / n) : 0.0f;

        xs = q1.x * s;
        ys = q1.y * s;
        zs = q1.z * s;
        wx = q1.w * xs;
        wy = q1.w * ys;
        wz = q1.w * zs;
        xx = q1.x * xs;
        xy = q1.x * ys;
        xz = q1.x * zs;
        yy = q1.y * ys;
        yz = q1.y * zs;
        zz = q1.z * zs;

        M00 = 1.0f - (yy + zz);
        M01 = xy - wz;
        M02 = xz + wy;
        M03 = 0f;
        M10 = xy + wz;
        M11 = 1.0f - (xx + zz);
        M12 = yz - wx;
        M13 = 0f;
        M20 = xz - wy;
        M21 = yz + wx;
        M22 = 1.0f - (xx + yy);
        M23 = 0f;
        M30 = 0f;
        M31 = 0f;
        M32 = 0f;
        M33 = 1f;
    }

    public final void set(Matrix4f m1) {
        M00 = m1.M00; M01 = m1.M01; M02 = m1.M02; M03 = m1.M03;
        M10 = m1.M10; M11 = m1.M11; M12 = m1.M12; M13 = m1.M13;
        M20 = m1.M20; M21 = m1.M21; M22 = m1.M22; M23 = m1.M23;
        M30 = m1.M30; M31 = m1.M31; M32 = m1.M32; M33 = m1.M33;
    }

    /**
     * Sets the value of this matrix to the result of multiplying
     * the two argument matrices together.
     *
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void mul(Matrix4f m1, Matrix4f m2) {
        // alias-safe way.
        set(
                m1.M00 * m2.M00 + m1.M01 * m2.M10 + m1.M02 * m2.M20 + m1.M03 * m2.M30,
                m1.M00 * m2.M01 + m1.M01 * m2.M11 + m1.M02 * m2.M21 + m1.M03 * m2.M31,
                m1.M00 * m2.M02 + m1.M01 * m2.M12 + m1.M02 * m2.M22 + m1.M03 * m2.M32,
                m1.M00 * m2.M03 + m1.M01 * m2.M13 + m1.M02 * m2.M23 + m1.M03 * m2.M33,

                m1.M10 * m2.M00 + m1.M11 * m2.M10 + m1.M12 * m2.M20 + m1.M13 * m2.M30,
                m1.M10 * m2.M01 + m1.M11 * m2.M11 + m1.M12 * m2.M21 + m1.M13 * m2.M31,
                m1.M10 * m2.M02 + m1.M11 * m2.M12 + m1.M12 * m2.M22 + m1.M13 * m2.M32,
                m1.M10 * m2.M03 + m1.M11 * m2.M13 + m1.M12 * m2.M23 + m1.M13 * m2.M33,

                m1.M20 * m2.M00 + m1.M21 * m2.M10 + m1.M22 * m2.M20 + m1.M23 * m2.M30,
                m1.M20 * m2.M01 + m1.M21 * m2.M11 + m1.M22 * m2.M21 + m1.M23 * m2.M31,
                m1.M20 * m2.M02 + m1.M21 * m2.M12 + m1.M22 * m2.M22 + m1.M23 * m2.M32,
                m1.M20 * m2.M03 + m1.M21 * m2.M13 + m1.M22 * m2.M23 + m1.M23 * m2.M33,

                m1.M30 * m2.M00 + m1.M31 * m2.M10 + m1.M32 * m2.M20 + m1.M33 * m2.M30,
                m1.M30 * m2.M01 + m1.M31 * m2.M11 + m1.M32 * m2.M21 + m1.M33 * m2.M31,
                m1.M30 * m2.M02 + m1.M31 * m2.M12 + m1.M32 * m2.M22 + m1.M33 * m2.M32,
                m1.M30 * m2.M03 + m1.M31 * m2.M13 + m1.M32 * m2.M23 + m1.M33 * m2.M33
        );
    }

    /**
     * Sets 16 values
     */
    private void set(float m00, float m01, float m02, float m03,
                     float m10, float m11, float m12, float m13,
                     float m20, float m21, float m22, float m23,
                     float m30, float m31, float m32, float m33) {
        this.M00 = m00;
        this.M01 = m01;
        this.M02 = m02;
        this.M03 = m03;
        this.M10 = m10;
        this.M11 = m11;
        this.M12 = m12;
        this.M13 = m13;
        this.M20 = m20;
        this.M21 = m21;
        this.M22 = m22;
        this.M23 = m23;
        this.M30 = m30;
        this.M31 = m31;
        this.M32 = m32;
        this.M33 = m33;
    }
}


/**
 * Created by IntelliJ IDEA.
 * User: pepijn
 * Date: Aug 7, 2005
 * Time: 5:46:24 PM
 * To change this template use File | Settings | File Templates.
 */
class Point2f {
    public float x, y;

    public Point2f(float x, float y) {
        this.x = x;
        this.y = y;
    }
}


/**
 * Created by IntelliJ IDEA.
 * User: pepijn
 * Date: Aug 7, 2005
 * Time: 5:50:25 PM
 * To change this template use File | Settings | File Templates.
 */
class Quat4f {
    public float x, y, z, w;
}





/**
 * Created by IntelliJ IDEA.
 * User: pepijn
 * Date: Aug 7, 2005
 * Time: 5:45:22 PM
 * To change this template use File | Settings | File Templates.
 */
class Vector3f {
    public float x, y, z;

    public static void cross(Vector3f Result, Vector3f v1, Vector3f v2) {
        Result.x = (v1.y * v2.z) - (v1.z * v2.y);
        Result.y = (v1.z * v2.x) - (v1.x * v2.z);
        Result.z = (v1.x * v2.y) - (v1.y * v2.x);
    }

    public static float dot(Vector3f v1, Vector3f v2) {
        return (v1.x * v2.x) + (v1.y * v2.y) + (v1.z + v2.z);
    }

    public float length() {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }
}
class Renderer implements GLEventListener {
    // TEMP
    private GLCanvas panel;
    
    // User Defined Variables
    private GLUquadric quadratic;   // Used For Our Quadric
    private GLU glu = new GLU();

    private Matrix4f LastRot = new Matrix4f();
    private Matrix4f ThisRot = new Matrix4f();
    private final Object matrixLock = new Object();
    private float[] matrix = new float[16];

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
	
	private BoundingBox mBoundingBox = null;
    
    public Renderer(javax.media.opengl.GLCanvas pvPanel) {
        panel = pvPanel;
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
        GL gl = drawable.getGL();
		
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
//        gl.glEnable(GL.GL_LIGHTING);   // Enable Lighting

        gl.glEnable(GL.GL_COLOR_MATERIAL);  // Enable Color Material
        
        gl.glEnable (GL.GL_BLEND);
        gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		loadMesh(gl);
    }
	

	
	 private void buildVBOs(GL gl) {
            // Generate And Bind The Vertex Buffer
            gl.glGenBuffersARB(1, VBOVertices, 0);  // Get A Valid Name
            gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, VBOVertices[0]);  // Bind The Buffer
 
            gl.glGenBuffersARB(1, VBOCells, 0);  // Get A Valid Name
            gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, VBOCells[0]);  // Bind The Buffer
			
			// Load The Data
            gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, vertexCount * 3 * 
                    BufferUtil.SIZEOF_FLOAT, vertices, GL.GL_STATIC_DRAW_ARB);

             gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, cellCount * 4 * 3 * 
                    BufferUtil.SIZEOF_FLOAT, cells, GL.GL_STATIC_DRAW_ARB);

			
			// Our Copy Of The Data Is No Longer Necessary, It Is Safe In The Graphics Card
            vertices = null;
			cells = null;
			VBOUsed = true;
        }

    void reset() {
        synchronized(matrixLock) {
            LastRot.setIdentity();   // Reset Rotation
            ThisRot.setIdentity();   // Reset Rotation
        }
    }

    void startDrag( Point MousePt ) {
        synchronized(matrixLock) {
            LastRot.set( ThisRot );  // Set Last Static Rotation To Last Dynamic One
        }
        arcBall.click( MousePt );    // Update Start Vector And Prepare For Dragging
    }

    void drag( Point MousePt )       // Perform Motion Updates Here
    {
        Quat4f ThisQuat = new Quat4f();

        // Update End Vector And Get Rotation As Quaternion
        arcBall.drag( MousePt, ThisQuat); 
        synchronized(matrixLock) {
            ThisRot.setRotation(ThisQuat);  // Convert Quaternion Into Matrix3fT
            ThisRot.mul( ThisRot, LastRot); // Accumulate Last Rotation Into This One
        }
    }

    void torus(GL gl, float MinorRadius, float MajorRadius) // Draw A Torus With Normals
    {
        int i, j;
        gl.glBegin(GL.GL_TRIANGLE_STRIP); // Start A Triangle Strip
        for (i = 0; i < 20; i++)          // Stacks
        {
            for (j = -1; j < 20; j++)     // Slices
            {
                float wrapFrac = (j % 20) / (float) 20;
                double phi = Math.PI * 2.0 * wrapFrac;
                float sinphi = (float) (Math.sin(phi));
                float cosphi = (float) (Math.cos(phi));

                float r = MajorRadius + MinorRadius * cosphi;

                gl.glNormal3d(
                        (Math.sin(Math.PI * 2.0 * (i % 20 + wrapFrac) / 
                        (float) 20)) * cosphi,
                        sinphi,
                        (Math.cos(Math.PI * 2.0 * (i % 20 + wrapFrac) / 
                        (float) 20)) * cosphi);
                gl.glVertex3d(
                        (Math.sin(Math.PI * 2.0 * (i % 20 + wrapFrac) / 
                        (float) 20)) * r,
                        MinorRadius * sinphi,
                        (Math.cos(Math.PI * 2.0 * (i % 20 + wrapFrac) / 
                        (float) 20)) * r);

                gl.glNormal3d(
                        (Math.sin(Math.PI * 2.0 * (i + 1 % 20 + wrapFrac) / 
                        (float) 20)) * cosphi,
                        sinphi,
                        (Math.cos(Math.PI * 2.0 * (i + 1 % 20 + wrapFrac) / 
                        (float) 20)) * cosphi);
                gl.glVertex3d(
                        (Math.sin(Math.PI * 2.0 * (i + 1 % 20 + wrapFrac) / 
                        (float) 20)) * r,
                        MinorRadius * sinphi,
                        (Math.cos(Math.PI * 2.0 * (i + 1 % 20 + wrapFrac) / 
                        (float) 20)) * r);
            }
        }
        gl.glEnd();  // Done Torus
    }

    public void loadMesh(GL gl)
    {
		// Do Marching Cubes only once
        tree = new CSGTree(new CSGEllipsoid(new double[]{0.0,0.0,0.0}, new double[]{1.0,1.0,1.0}));
            tree.difference(new CSGEllipsoid(new double[]{0.6,0.6,0.0}, new double[]{1.0,1.0,1.0}));
			
            //tree.difference(new CSGEllipsoid(new double[]{0.0,0.5,1.0}, new double[]{0.8,0.8,0.8}));
            //tree.union(new CSGCuboid(new double[]{-1.5,0.6,0.2}, new double[]{0.3,0.6,0.2}));
            //tree.union(new CSGCuboid(new double[]{1.5,0.6,0.2}, new double[]{0.3,0.6,0.2}));
            //tree.union(new CSGCuboid(new double[]{-1.0,2.5,0.2}, new double[]{0.2,0.6,0.2}));
            //tree.union(new CSGCuboid(new double[]{1.0,2.5,0.2}, new double[]{0.2,0.6,0.2}));
			
			
			mBoundingBox = tree.getBoundingBox();

			MarchingCubesPolygonizer polygonizer = new MarchingCubesPolygonizer();
            ArrayList<Vertex> vertexArray = polygonizer.GetPolygonsAdaptive(tree);
            ArrayList<GridCell> cellArray = polygonizer.getMarchingCubes();
            //System.out.println(tree);
            System.out.println("Triangle count: " + MCTriangles.size());
            System.out.println("Cell count:     " + MCCells.size());
			
			vertexCount = vertexArray.size();
			cellCount = cellArray.size();
			
			System.out.println("Vertex count: " + Integer.toString(vertexCount));
			System.out.println("MC Vertex count: " + Integer.toString(cellCount * 4 * 4));
			vertices = BufferUtil.newFloatBuffer(vertexCount * 3);
			
			for (Vertex v : vertexArray) {
				vertices.put(v.x);
				vertices.put(v.y);
				vertices.put(v.z);
			}
			
			cells = BufferUtil.newFloatBuffer(cellCount * 4 * 4 * 3);
			for (GridCell cell : cellArray) {
				// Bottom
				cells.put(new float[]{cell.p[0].x, cell.p[0].y, cell.p[0].z});
				cells.put(new float[]{cell.p[1].x, cell.p[1].y, cell.p[1].z});
				cells.put(new float[]{cell.p[2].x, cell.p[2].y, cell.p[2].z});
				cells.put(new float[]{cell.p[3].x, cell.p[3].y, cell.p[3].z});

				// Top
				cells.put(new float[]{cell.p[4].x, cell.p[4].y, cell.p[4].z});
				cells.put(new float[]{cell.p[5].x, cell.p[5].y, cell.p[5].z});
				cells.put(new float[]{cell.p[6].x, cell.p[6].y, cell.p[6].z});
				cells.put(new float[]{cell.p[7].x, cell.p[7].y, cell.p[7].z});

				// Front
				cells.put(new float[]{cell.p[0].x, cell.p[0].y, cell.p[0].z});
				cells.put(new float[]{cell.p[1].x, cell.p[1].y, cell.p[1].z});
				cells.put(new float[]{cell.p[5].x, cell.p[5].y, cell.p[5].z});
				cells.put(new float[]{cell.p[4].x, cell.p[4].y, cell.p[4].z});

				// Back
				cells.put(new float[]{cell.p[2].x, cell.p[2].y, cell.p[2].z});
				cells.put(new float[]{cell.p[3].x, cell.p[3].y, cell.p[3].z});
				cells.put(new float[]{cell.p[7].x, cell.p[7].y, cell.p[7].z});
				cells.put(new float[]{cell.p[6].x, cell.p[6].y, cell.p[6].z});
			}
			
			
			vertices.flip();
			cells.flip();
			
			if (VBOSupported) {
				buildVBOs(gl);
			}
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
    
    private void visualizeMarchingCubes(GL gl) {
            gl.glEnableClientState(GL.GL_VERTEX_ARRAY);  // Enable Vertex Arrays
			//gl.glEnableClientState(GL.GL_COLOR_ARRAY);

			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);

			// Render
            // Draw All Of The Triangles At Once
			gl.glVertexPointer(3, GL.GL_FLOAT, 0, cells); 
			gl.glColor4f(0.0f, 1.0f, 0.0f, 0.07f);
            gl.glDrawArrays(GL.GL_QUADS, 0, cellCount * 4 * 4);
            gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
    }
	
	private void renderMesh(GL gl) {
// Enable Pointers
            gl.glEnableClientState(GL.GL_VERTEX_ARRAY);  // Enable Vertex Arrays
			//gl.glEnableClientState(GL.GL_COLOR_ARRAY);

			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);

			if (VBOUsed)
			{
                gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, VBOCells[0]);
                // Set The Vertex Pointer To The Vertex Buffer
                gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);    
				
				
                gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, VBOVertices[0]);
                // Set The Vertex Pointer To The Vertex Buffer
                gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);    
			}
			else
			{
				gl.glVertexPointer(3, GL.GL_FLOAT, 0, vertices); 
			}

			
            gl.glColor3f(1.0f, 0.0f, 0.0f);
			gl.glDrawArrays(GL.GL_TRIANGLES, 0, vertexCount);  

            // Disable Pointers
            // Disable Vertex Arrays
			//gl.glDisableClientState(GL.GL_COLOR_ARRAY);
            gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
	}
    
    public void display(GLAutoDrawable drawable) {
        synchronized(matrixLock) {
            ThisRot.get(matrix);
        }
        panel.repaint(50);
        GL gl = drawable.getGL();

        // Clear Screen And Depth Buffer
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);                
        gl.glLoadIdentity();                  // Reset The Current Modelview Matrix
        
        
        // Go Into The Screen 7.0
        gl.glTranslatef(0.0f, 0.0f, -6.0f);  

        gl.glPushMatrix();                  // NEW: Prepare Dynamic Transform
        gl.glMultMatrixf(matrix, 0);        // NEW: Apply Dynamic Transform
        gl.glColor3f(1.0f, 0.75f, 0.75f);

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
        drawGrid(gl);
        drawBoundingBox(gl);
        
		renderMesh(gl);
        
        visualizeMarchingCubes(gl);
        gl.glPopMatrix();                   // NEW: Unapply Dynamic Transform

        gl.glFlush();                       // Flush The GL Rendering Pipeline
    }
}