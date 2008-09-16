/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csgbuilder;

import com.sun.opengl.util.BufferUtil;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import javax.media.opengl.GL;

/**
 *
 * @author s031407
 */
public class OperandMesh {
	// Contains the array of vertices
	private FloatBuffer mVertices;
	
	private FloatBuffer mDebugMCCells = null;
	private BoundingBox mDebugBB = null;

	public void setDebugBB(BoundingBox pDebugBB) {
		mDebugBB = pDebugBB;
	}
	
	private int mVertexCount;
	
	public OperandMesh(int pVertexCount) {
		mVertices = BufferUtil.newFloatBuffer(pVertexCount * 3);
		mVertexCount = pVertexCount;
	}
	
	public void render(GL gl) {
            gl.glEnableClientState(GL.GL_VERTEX_ARRAY);  // Enable Vertex Arrays

			// Drawmode: wireframe
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);

			gl.glVertexPointer(3, GL.GL_FLOAT, 0, mVertices); 
			
			// Draw all at once
            gl.glColor3f(1.0f, 0.0f, 0.0f);
			gl.glDrawArrays(GL.GL_TRIANGLES, 0, mVertexCount);  

            // Disable Vertex Arrays
            gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
			
			// Debug axis
			drawAxis(gl);
			
			// Debug bounding box
			drawBB(gl);

			// Debug marching cubes
			drawMC(gl);
	}

	public int getVertexCount() {
		return mVertexCount;
	}

	public void addVertex(Vertex pVertex) {
		mVertices.put(pVertex.x);
		mVertices.put(pVertex.y);
		mVertices.put(pVertex.z);
	}
	
	public void flipVertices() {
		mVertices.flip();
	}

	// START DEBUG FUNCTIONS
	
	private void drawMC(GL gl) {
			if (mDebugMCCells == null) {
				return;
			}
			
				gl.glEnableClientState(GL.GL_VERTEX_ARRAY);  // Enable Vertex Arrays

				gl.glVertexPointer(3, GL.GL_FLOAT, 0, mDebugMCCells); 
				gl.glColor4f(0.0f, 1.0f, 0.0f, 0.07f);
				gl.glDrawArrays(GL.GL_QUADS, 0, mDebugMCCells.limit() / 3);
				gl.glDisableClientState(GL.GL_VERTEX_ARRAY);				
	}
	
    private void drawBB(GL gl) {
		if (mDebugBB == null) {
			return;
		}
		
        BoundingBox box = mDebugBB;
        
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
	
	public void setDebugMCCells(ArrayList<GridCell> pCells) {
                        mDebugMCCells = BufferUtil.newFloatBuffer(pCells.size() * 4 * 4 * 3);
                        for (GridCell cell : pCells) {
                                // Bottom
                                mDebugMCCells.put(new float[]{cell.p[0].x, cell.p[0].y, cell.p[0].z});
                                mDebugMCCells.put(new float[]{cell.p[1].x, cell.p[1].y, cell.p[1].z});
                                mDebugMCCells.put(new float[]{cell.p[2].x, cell.p[2].y, cell.p[2].z});
                                mDebugMCCells.put(new float[]{cell.p[3].x, cell.p[3].y, cell.p[3].z});


                                // Top
                                mDebugMCCells.put(new float[]{cell.p[4].x, cell.p[4].y, cell.p[4].z});
                                mDebugMCCells.put(new float[]{cell.p[5].x, cell.p[5].y, cell.p[5].z});
                                mDebugMCCells.put(new float[]{cell.p[6].x, cell.p[6].y, cell.p[6].z});
                                mDebugMCCells.put(new float[]{cell.p[7].x, cell.p[7].y, cell.p[7].z});


                                // Front
                                mDebugMCCells.put(new float[]{cell.p[0].x, cell.p[0].y, cell.p[0].z});
                                mDebugMCCells.put(new float[]{cell.p[1].x, cell.p[1].y, cell.p[1].z});
                                mDebugMCCells.put(new float[]{cell.p[5].x, cell.p[5].y, cell.p[5].z});
                                mDebugMCCells.put(new float[]{cell.p[4].x, cell.p[4].y, cell.p[4].z});


                                // Back
                                mDebugMCCells.put(new float[]{cell.p[2].x, cell.p[2].y, cell.p[2].z});
                                mDebugMCCells.put(new float[]{cell.p[3].x, cell.p[3].y, cell.p[3].z});
                                mDebugMCCells.put(new float[]{cell.p[7].x, cell.p[7].y, cell.p[7].z});
                                mDebugMCCells.put(new float[]{cell.p[6].x, cell.p[6].y, cell.p[6].z});
                        }
						
						mDebugMCCells.flip();
	}

    private void drawAxis(GL gl) {
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
}

