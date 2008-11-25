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
	private boolean SHOW_BB = false;
	private boolean SHOW_MC = false;
	private boolean SHOW_NORMALS = false;
    private RenderMethod mRenderMethod = RenderMethod.Fill;
                
    public enum RenderMethod {
        WireFrame,
        Fill
    }
	
	// Contains the array of vertices
	private FloatBuffer mVertices;
	private FloatBuffer mNormals;
	
	private FloatBuffer mDebugMCCells = null;
	private BoundingBox mDebugBB = null;

	public void setDebugBB(BoundingBox pDebugBB) {
		mDebugBB = pDebugBB;
	}
	
	private int mVertexCount;
	
	public OperandMesh(int pVertexCount) {
		mVertices = BufferUtil.newFloatBuffer(pVertexCount * 3);
		mNormals = BufferUtil.newFloatBuffer(pVertexCount * 3);
		mVertexCount = pVertexCount;
	}
    
    public void setRenderMethod(RenderMethod method) {
        mRenderMethod = method;
    }
    
    public void toggleShowNormals() {
        SHOW_NORMALS = !SHOW_NORMALS;
    }
    
    public void toggleShowBB() {
        SHOW_BB = !SHOW_BB;
    }
	
    public void toggleShowMC() {
        SHOW_MC = !SHOW_MC;
    }
    
    public void toggleShowAxis() {
        SHOW_NORMALS = !SHOW_NORMALS;
    }
    
    public void render(GL gl) {
        render(gl, false, new float[]{1, 0, 0});
    }
    
    public void render(GL gl, boolean CSGMode, float[] color) {

        
        if (mVertexCount > 0) {
            gl.glEnableClientState(GL.GL_VERTEX_ARRAY);  // Enable Vertex Arrays
			gl.glVertexPointer(3, GL.GL_FLOAT, 0, mVertices); 

			gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
			gl.glNormalPointer(GL.GL_FLOAT, 0, mNormals);
			

			if (mRenderMethod == RenderMethod.WireFrame) {
                gl.glDisable(GL.GL_LIGHTING);   // Enable Lighting
                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
            }
            else {
                gl.glEnable(GL.GL_LIGHTING);   // Enable Lighting
                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
            }
                        
			// Draw all at once
            if (CSGMode) {
                gl.glColor4f(color[0], color[1], color[2], 0.2f);
            }
            else {
                gl.glColor3f(color[0], color[1], color[2]);
            }
            
            gl.glDrawArrays(GL.GL_TRIANGLES, 0, mVertexCount);  
			
			// Disable Vertex Arrays
			gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
            gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		}
        
		if (SHOW_MC) {
			// Debug marching cubes
			drawMC(gl);
		}

		if (SHOW_NORMALS) {
			// Draw normals
			drawNormals(gl);
		}
		
		if (SHOW_BB) {
			// Debug bounding box
			drawBB(gl);
		}


	}

	public int getVertexCount() {
		return mVertexCount;
	}

	public void addVertex(Vertex pVertex) {
		mVertices.put(pVertex.x);
		mVertices.put(pVertex.y);
		mVertices.put(pVertex.z);
	}
	
	public void addNormal(Vertex pvNormal) {
		mNormals.put(pvNormal.x);
		mNormals.put(pvNormal.y);
		mNormals.put(pvNormal.z);
	}
	
	public void flipVertices() {
		mVertices.flip();
		mNormals.flip();
	}

	// START DEBUG FUNCTIONS
	
	private void drawMC(GL gl) {
		if (mDebugMCCells == null) {
			return;
		}
			
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);  // Enable Vertex Arrays

				
		gl.glVertexPointer(3, GL.GL_FLOAT, 0, mDebugMCCells); 

        gl.glEnable(GL.GL_LIGHTING);   // Enable Lighting

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		gl.glColor4f(0.0f, 1.0f, 0.0f, 0.07f);
		gl.glDrawArrays(GL.GL_QUADS, 0, mDebugMCCells.limit() / 3);
				
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);				
	}
	
	private void drawNormals(GL gl) {
        gl.glColor3d(0.0, 0.0, 1.0);
		
		for (int i = 0; i < mVertices.capacity(); i += 3) {
			gl.glBegin(GL.GL_LINES);
			float x = mVertices.get(i);
			float y = mVertices.get(i + 1);
			float z = mVertices.get(i + 2);
			gl.glVertex3f(x, y, z);
			float ratio = 15f;
			gl.glVertex3f(x + mNormals.get(i)/ratio, y + mNormals.get(i + 1)/ratio, z + mNormals.get(i + 2)/ratio);
			gl.glEnd();
		}
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

 
	public void setDebugMCCells(ArrayList<OcCell> pCells) {
                        mDebugMCCells = BufferUtil.newFloatBuffer(pCells.size() * 4 * 4 * 3);
                        for (OcCell cell : pCells) {
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

	public void setDebugDGCells(ArrayList<DualGridCell> pCells) {
                        mDebugMCCells = BufferUtil.newFloatBuffer(pCells.size() * 4 * 4 * 3);
                        for (DualGridCell cell : pCells) {
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
    
}

