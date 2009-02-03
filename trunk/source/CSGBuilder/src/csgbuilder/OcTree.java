/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csgbuilder;

/**
 *
 * @author s031407
 */
public class OcTree {
    public OcCell root;
    
    // Constructs the OCTree based on a CSGTree
    public OcTree(CSGTree tree) {
        Vertex s = tree.getBoundingBox().p[0];
        Vertex e = tree.getBoundingBox().p[6];

        float[] dim = new float[3];
        
        dim[0] = Math.abs(e.x - s.x);
        dim[1] = Math.abs(e.y - s.y);
        dim[2] = Math.abs(e.z - s.z);
        
        double pos[] = new double[3];
        
        root = new OcCell(s.x - (float)pos[0], s.y - (float)pos[1], s.z - (float)pos[2], dim, tree, 0, null, true, null);
    }
}
