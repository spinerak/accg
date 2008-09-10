package csgbuilder;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import java.util.ArrayList;
/**
 *
 * @author s040379
 */
public class gljPanel1EventListener implements GLEventListener {
    private int winWidth = 1;
    private int winHeight = 1;
    
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glShadeModel(GL.GL_SMOOTH);              // Enable Smooth Shading
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.5f);    // Black Background
        gl.glClearDepth(1.0f);                      // Depth Buffer Setup
        gl.glEnable(GL.GL_DEPTH_TEST);              // Enables Depth Testing
        gl.glDepthFunc(GL.GL_LEQUAL);               // The Type Of Depth Testing To Do
        // Really Nice Perspective Calculations
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        MarchingCubesPolygonizer polygonizer = new MarchingCubesPolygonizer();
        ArrayList<Triangle> triangles = polygonizer.GetPolygons();
        
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
        gl.glTranslatef(0, 0, -6.0f);
        //gl.glScalef(1/winWidth, 1/winHeight, 1/200);

        gl.glBegin( GL.GL_TRIANGLES );

        for (Triangle triangle : triangles) {
            
              gl.glColor3d(1, 0, 0); // Sets current primary color to red
              gl.glVertex3d(triangle.p[0].x/winWidth, triangle.p[0].y/winHeight, triangle.p[0].z/200); // Specify three vertices
              gl.glVertex3d(triangle.p[1].x/winWidth, triangle.p[1].y/winHeight, triangle.p[1].z/200); // Specify three vertices
              gl.glVertex3d(triangle.p[2].x/winWidth, triangle.p[2].y/winHeight, triangle.p[2].z/200); // Specify three vertices
        }
     
        gl.glEnd();   
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) 
    {        
        GL gl = drawable.getGL();
        GLU glu = new GLU();
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, width/height, 1.0, 200.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        winWidth = width;
        winHeight = height;
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) { }    
}