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
public class OcCell {
    OcCell[] child;
    Vertex[] p;
    float[] val;
    int[] edges;
    float[] mDim;
    int mDepth;
    CSGTree mTree;
    ArrayList vertices;
    public OcCell parent;
    public boolean hasChildren;
    
    public OcCell(float x, float y, float z, float[] dim, CSGTree tree, int depth, OcCell pvParent) {
        p = new Vertex[8];
        for (int i = 0; i < 8; i++) {
            p[i] = new Vertex();
        }
        val = new float[8];
        edges = new int[16];
        hasChildren = false;
        parent = pvParent;
        vertices = new ArrayList<Vertex>();
        mDim = dim;
        mDepth = depth;
        mTree = tree;
        
        // Set vertices
        p[0].x = x; p[0].y = y; p[0].z = z;
        p[1].x = x + dim[0]; p[1].y = y; p[1].z = z;
        p[2].x = x + dim[0]; p[2].y = y; p[2].z = z + dim[0];
        p[3].x = x; p[3].y = y; p[3].z = z + dim[2];
        p[4].x = x; p[4].y = y + dim[1]; p[4].z = z;
        p[5].x = x + dim[0]; p[5].y = y + dim[1]; p[5].z = z;
        p[6].x = x + dim[0]; p[6].y = y + dim[1]; p[6].z = z + dim[2];
        p[7].x = x; p[7].y = y + dim[1]; p[7].z = z + dim[2];
        
        // Check if we need to recurse
        
        // Calculate values on all points
        for (int i = 0; i < 8; i++) {
            val[i] = (float) tree.getFunctionValue(p[i].x, p[i].y, p[i].z);
        }
        
        
        // Calculate new dimensions
        float ndim[] = new float[3];
        ndim[0] = dim[0] / 2f; ndim[1] = dim[1] / 2f; ndim[2] = dim[2] / 2f;

        boolean recurse = parent == null;
        
//        Vertex[] samplePoints = new Vertex[20];
//        for (int i = 0; i < 8; i++) samplePoints[i] = p[i];
//        samplePoints[8] = new Vertex(p[0].x + dim[0]/2, p[0].y, p[0].z);
//        samplePoints[9] = new Vertex(p[0].x, p[0].y + dim[1]/2, p[0].z);
//        samplePoints[10] = new Vertex(p[0].x, p[0].y, p[0].z + dim[2]/2);
//        samplePoints[11] = new Vertex(p[0].x + dim[0], p[0].y + dim[1]/2, p[0].z);
//        samplePoints[12] = new Vertex(p[0].x + dim[0], p[0].y, p[0].z + dim[2]/2);
//        samplePoints[13] = new Vertex(p[0].x + dim[0]/2, p[0].y + dim[1], p[0].z);
//        samplePoints[14] = new Vertex(p[0].x, p[0].y + dim[1], p[0].z+dim[2]/2);
//        samplePoints[15] = new Vertex(p[0].x + dim[0]/2, p[0].y, p[0].z + dim[2]);
//        samplePoints[16] = new Vertex(p[0].x, p[0].y + dim[1]/2, p[0].z + dim[2]);
//        samplePoints[17] = new Vertex(p[0].x + dim[0], p[0].y + dim[1], p[0].z + dim[2]/2);
//        samplePoints[18] = new Vertex(p[0].x + dim[0], p[0].y + dim[1]/2, p[0].z + dim[2]);
//        samplePoints[19] = new Vertex(p[0].x + dim[0]/2, p[0].y + dim[1], p[0].z + dim[2]);
        

        // If any of the cube's corner points is inside the volume -> recurse
//        if (!recurse) {
//            for (Vertex v : p) {
//                if (tree.getFunctionValue(v.x, v.y, v.z) < 0) {
//                    recurse = true;
//                    break;
//                }
//            }
//        }
        
        CSGTreePolygoniser polygoniser = new CSGTreePolygoniser();
        polygoniser.Polygonise(this, 0);
        Vertex[] normals = new Vertex[this.vertices.size()];
        
        float d = 0.001f;
        
        
        float delta = 0.3f;
        int maxDepth = 8;
        
        for (int i = 0; i < this.vertices.size(); i++) {
            Vertex v = (Vertex) this.vertices.get(i);
            normals[i] = new Vertex();
			normals[i].x = (float) ((tree.getFunctionValue(v.x + d, v.y, v.z) - tree.getFunctionValue(v.x, v.y, v.z)) / d);
			normals[i].y = (float) ((tree.getFunctionValue(v.x, v.y + d, v.z) - tree.getFunctionValue(v.x, v.y, v.z)) / d);
			normals[i].z = (float) ((tree.getFunctionValue(v.x, v.y, v.z + d) - tree.getFunctionValue(v.x, v.y, v.z)) / d);
            normals[i].normalize();
        }
        
        for (Vertex v : normals) {
            for (Vertex w : normals) {
                if (v.equals(w)) {
                    continue;
                }
                
                if (Math.acos(v.dot(w)) > delta) {
                    recurse = true;
                    break;
                }
//                System.out.printf("%f\n", Math.acos(v.x * w.x + v.y * w.y + v.z * w.z));
//                System.out.printf("%f\n", Math.acos(v.dot(w)));
            }
        }
        
//        if (!recurse) {
//            for (Vertex v : p) {
//                if (tree.getFunctionValue(v.x, v.y, v.z) < 0) {
//                    recurse = true;
//                    break;
//                }
//            }
//        }
        
//        boolean recurse = false;
//        Vertex[] samplePoints = p;
//        Vertex c = new Vertex(x + dim[0]/2, y + dim[1]/2, z + dim[2]/2);
//
//        // w = f(x, y, z)
//        float w = (float) tree.getFunctionValue(c.x, c.y, c.z);
//        
//        // E(w, x, y, z) = Sum (w - Ti(x, y, z))^2/(1 + |df(xi, yi, zi)|^2
//        float E = 0;
//        
//        
//        for (Vertex v : samplePoints) {
//            // Ti(x, y, z) = df(xi, yi, zi) * ((x, y, z) - (xi, yi, zi))
//        
//			float d = 0.001f;
//            Vertex df = new Vertex();
//			df.x = (float) ((tree.getFunctionValue(v.x + d, v.y, v.z) - tree.getFunctionValue(v.x, v.y, v.z)) / d);
//			df.y = (float) ((tree.getFunctionValue(v.x, v.y + d, v.z) - tree.getFunctionValue(v.x, v.y, v.z)) / d);
//			df.z = (float) ((tree.getFunctionValue(v.x, v.y, v.z + d) - tree.getFunctionValue(v.x, v.y, v.z)) / d);
//
//            Vertex di = c.clone();
//            di.subtract(v);
//            float Ti = df.dot(di);
//        
//            E += Math.pow(w - Ti, 2)/(1 + Math.pow(df.length(), 2));
//        }
////        E = E / 8;
//        recurse = E > 0.3; 
        
        if (recurse && (depth < maxDepth)) {
            recurse();
            
            boolean oneHasChildren = false;
            for (int i = 0; i < 8; i++) { if (child[i].hasChildren) { oneHasChildren = true; } }
            
            // -> hack
            // If one of the children has children and the current child has content -> recurse
            if (oneHasChildren) {
                for (OcCell c : child) {
                    if (c.hasChildren) continue;
                    
                    for (Vertex v : c.p) {
                        if (tree.getFunctionValue(v.x, v.y, v.z) < 0) {
                            c.recurse();
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public void recurse() {
            float x = p[0].x;
            float y = p[0].y;
            float z = p[0].z;
            
            // Calculate new dimensions
            float ndim[] = new float[3];
            ndim[0] = mDim[0] / 2f; ndim[1] = mDim[1] / 2f; ndim[2] = mDim[2] / 2f;

            
            child = new OcCell[8];
            hasChildren = true;

            int depth = mDepth + 1;

            // Bottom 4 cubes
            child[0] = new OcCell(x, y, z, ndim, mTree, depth, this);
            child[1] = new OcCell(x + ndim[0], y, z, ndim, mTree, depth, this);
            child[2] = new OcCell(x + ndim[0], y, z + ndim[2], ndim, mTree, depth, this);
            child[3] = new OcCell(x, y, z + ndim[2], ndim, mTree, depth, this);

            // Top 4 cubes
            child[4] = new OcCell(x, y + ndim[1], z, ndim, mTree, depth, this);
            child[5] = new OcCell(x + ndim[0], y + ndim[1], z, ndim, mTree, depth, this);
            child[6] = new OcCell(x + ndim[0], y + ndim[1], z + ndim[2], ndim, mTree, depth, this);
            child[7] = new OcCell(x, y + ndim[1], z + ndim[2], ndim, mTree, depth, this);        
    }
}
