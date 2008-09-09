package csgbuilder;

import javax.media.opengl.*;
/**
 *
 * @author s040379
 */
public class gljPanel1EventListener implements GLEventListener {
    public void init(GLAutoDrawable drawable) {  }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) { }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) { }    
}