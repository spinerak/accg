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
    ArrayList normals;
    float[] val;
    int[] edges;
    float[] mDim;
    int mDepth;
    int childDepth;
    CSGTree mTree;
    ArrayList vertices;
    public OcCell parent;
    public boolean hasChildren;
    
    public void upParentChildDepth() {
        childDepth++;
        if (parent != null) {
            parent.upParentChildDepth();
        }
    }
    
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
        childDepth = 0;
        
        // Set vertices
        p[0].x = x; p[0].y = y; p[0].z = z;
        p[1].x = x + dim[0]; p[1].y = y; p[1].z = z;
        p[2].x = x + dim[0]; p[2].y = y; p[2].z = z + dim[2];
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
        
        // Set vertices
        CSGTreePolygoniser polygoniser = new CSGTreePolygoniser();
        polygoniser.Polygonise(this, 0);
        
        // Calculate normals
        normals = new ArrayList<Vertex>(this.vertices.size());

        float d = 0.001f;
        for (int i = 0; i < this.vertices.size(); i++) {
            Vertex v = (Vertex) this.vertices.get(i);
            Vertex n = new Vertex();
            n.x = (float) ((tree.getFunctionValue(v.x + d, v.y, v.z) - tree.getFunctionValue(v.x, v.y, v.z)) / d);
            n.y = (float) ((tree.getFunctionValue(v.x, v.y + d, v.z) - tree.getFunctionValue(v.x, v.y, v.z)) / d);
            n.z = (float) ((tree.getFunctionValue(v.x, v.y, v.z + d) - tree.getFunctionValue(v.x, v.y, v.z)) / d);
            n.normalize();
            normals.add(n);
        }
        
        // Calculate new dimensions
        float ndim[] = new float[3];
        ndim[0] = dim[0] / 2f; ndim[1] = dim[1] / 2f; ndim[2] = dim[2] / 2f;

        boolean recurse = parent == null;
        
        int maxDepth = 7;
        
        // -> hack
        if (depth < 4) {
            recurse = parent == null;
            for (Vertex v : p) {
                if (tree.getFunctionValue(v.x, v.y, v.z) < 0) {
                    recurse = true;
                    break;
                }
            }
        }
        else 
        {
            float delta = 0.3f;

            for (int i = 0; i < normals.size(); i++) {
                Vertex v = (Vertex) normals.get(i);
                for (int j = 0; j < normals.size(); j++) {
                    Vertex w = (Vertex) normals.get(j);
                    if (v.equals(w)) {
                        continue;
                    }

                    if (Math.acos(v.dot(w)) > delta) {
                        recurse = true;
                        break;
                    }
                }
                
                if (recurse) break;
            }
        }

        if (recurse && (depth < maxDepth)) {
            recurse(0); // recurse on this cell once
            
            int recurseDepth = 0;
            for (int i = 0; i < 8; i++) {
                if (child[i].hasChildren) {
                    recurseDepth = 1;

//                    OcCell c = child[i];
//                    if ((dim[0] > 0.3) && (c.vertices.size() > 0)) {
//                    while (c.hasChildren) {
//                        recurseDepth++;
//                        c = c.child[0];
//                    }
//                    }
                    break;
                }
            }
            
            // -> hack
            // If one of the children has children and the current child has content -> recurse
            if (recurseDepth > 0) {
                for (OcCell c : child) {
                    if (c.hasChildren) continue;
                    
                    for (Vertex v : c.p) {
                        if (tree.getFunctionValue(v.x, v.y, v.z) < 0) {
                            c.recurse(recurseDepth);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public void recurse(int numRecursions) {
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
            
            if (numRecursions > 1) {
                for (OcCell c : child) {
                    c.recurse(numRecursions - 1);
                }
            }
    }
}
