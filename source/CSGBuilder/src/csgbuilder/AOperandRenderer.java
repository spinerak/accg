/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csgbuilder;

import java.awt.Color;
import javax.media.opengl.GLAutoDrawable;

/**
 *
 * @author s031407
 */
public class AOperandRenderer extends OperandViewerRenderer {
    private boolean isUnion = false;
    private boolean isDifference = false;
    
    private OperandMesh activeCSGMesh = null;
    
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
    
    public void startUnion(OperandMesh pvMesh) {
        if (pvMesh == null) {
            return;
        }
        
        isUnion = true;
        isDifference = false;
        CSGMode = true;
        activeCSGMesh = pvMesh;
    }
    
    public void startDifference(OperandMesh pvMesh) {
        if (pvMesh == null) {
            return;
        }
        
        isUnion = false;
        isDifference = true;
        CSGMode = true;
        activeCSGMesh = pvMesh;        
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
        
        if (doCSG()) {
            // We know activeCSGMesh is not null because of the checkes in
            // startUnion and startDifference
            activeCSGMesh.render(drawable.getGL(), false, new float[]{0, 1, 0});
        }
    }
}
