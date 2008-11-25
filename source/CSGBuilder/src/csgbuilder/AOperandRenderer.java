/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csgbuilder;

import java.awt.Color;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

/**
 *
 * @author s031407
 */
public class AOperandRenderer extends OperandViewerRenderer {
    private boolean isUnion = false;
    private boolean isDifference = false;
    
    private OperandMesh activeCSGMesh = null;
    private CSGTree activeCSGTree = null;
    
    public AOperandRenderer () {
        super();
    }
    
    public boolean doCSGUnion() {
        return isUnion;
    }
    
    public boolean doCSGDifference() {
        return isDifference;
    }
    
    public boolean doCSG() {
        return CSGMode;
    }
    
    public void startUnion(OperandMesh pvMesh, CSGTree pvTree) {
        if (pvMesh == null) {
            return;
        }
        
        isUnion = true;
        isDifference = false;
        CSGMode = true;
        activeCSGMesh = pvMesh;
        activeCSGTree = pvTree;
    }
    
    public void startDifference(OperandMesh pvMesh, CSGTree pvTree) {
        if (pvMesh == null) {
            return;
        }
        
        isUnion = false;
        isDifference = true;
        CSGMode = true;
        activeCSGMesh = pvMesh; 
        activeCSGTree = pvTree;
    }
    
    public void stopUnion() {
        isUnion = false;
        CSGMode = false;
    }
    
    public void stopDifference() {
        isDifference = false;
        CSGMode = false;
    }
    
    public void display(GLAutoDrawable drawable) {
        super.display(drawable);
        
        if (doCSG() && !getViewer().isPolygonising) {
            // We know activeCSGMesh is not null because of the checkes in
            // startUnion and startDifference
            GL gl = drawable.getGL();
//            double[] pos = activeCSGTree.getPosition();
//            double[] pos2 = new double[]{pos[0], pos[1], pos[2]};
//            System.out.printf("%f %f %f\n", pos[0], pos[1], pos[2]);
            activeCSGMesh.render(gl, false, new float[]{0, 1, 0}, new double[]{0,0,0});
        }
    }
}
