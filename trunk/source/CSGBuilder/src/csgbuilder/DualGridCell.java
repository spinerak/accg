/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csgbuilder;

import java.util.ArrayList;

/**
 *
 * @author s031407
 */
public class DualGridCell {
    public Vertex[] p;
    public float[] val;
    public ArrayList normals;
    ArrayList vertices;
    
    public DualGridCell() {
        p = new Vertex[8];
        val = new float[8];   
        vertices = new ArrayList<Vertex>();
        normals = new ArrayList<Vertex>();
    }
}
