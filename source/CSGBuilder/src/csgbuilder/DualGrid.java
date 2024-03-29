/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csgbuilder;

import java.util.ArrayList;
import java.util.Collections;


/**
 *
 * @author s031407
 */
public class DualGrid {
    public ArrayList cells;
    
    int[][] faceAdjacent = {
        /*0*/{1, 3, 4, -1},
        /*1*/{0, 2, 5, -1},
        /*2*/{1, 3, 6, -1},
        /*3*/{0, 2, 7, -1},
        /*4*/{0, 5, 7, -1},
        /*5*/{1, 4, 6, -1},
        /*6*/{2, 5, 7, -1},
        /*7*/{3, 4, 6, -1}
    };
    
    int[][][] faceAdjacentChild = {
        {/*0-0*/{-1},
         /*0-1*/{1, 0, 2, 3, 5, 4, 6, 7, -1},
         /*0-2*/{-1},
         /*0-3*/{2, 1, 3, 0, 6, 5, 7, 4, -1},
         /*0-4*/{4, 0, 5, 1, 6, 2, 7, 3, -1},
         /*0-5*/{-1},
         /*0-6*/{-1},
         /*0-7*/{-1}},  

        {/*1-0*/{0, 1, 3, 2, 4, 5, 7, 6, -1},
         /*1-1*/{-1},
         /*1-2*/{2, 1, 3, 0, 6, 5, 7, 4, -1},
         /*1-3*/{-1},
         /*1-4*/{-1},
         /*1-5*/{4, 0, 5, 1, 6, 2, 7, 3, -1},
         /*1-6*/{-1},
         /*1-7*/{-1}},

        {/*2-0*/{-1},
         /*2-1*/{0, 3, 1, 2, 4, 7, 5, 6, -1},
         /*2-2*/{-1},
         /*2-3*/{0, 1, 3, 2, 4, 5, 7, 6, -1},
         /*2-4*/{-1},
         /*2-5*/{-1},
         /*2-6*/{4, 0, 5, 1, 6, 2, 7, 3, -1},
         /*2-7*/{-1}},

        {/*3-0*/{0, 3, 1, 2, 4, 7, 5, 6, -1},
         /*3-1*/{-1},
         /*3-2*/{1, 0, 2, 3, 5, 4, 6, 7, -1},
         /*3-3*/{-1},
         /*3-4*/{-1},
         /*3-5*/{-1},
         /*3-6*/{-1},
         /*3-7*/{4, 0, 5, 1, 6, 2, 7, 3, -1}},

        {/*4-0*/{0, 4, 1, 5, 2, 6, 3, 7, -1},
         /*4-1*/{-1},
         /*4-2*/{-1},
         /*4-3*/{-1},
         /*4-4*/{-1},
         /*4-5*/{1, 0, 2, 3, 5, 4, 6, 7, -1},
         /*4-6*/{-1},
         /*4-7*/{2, 1, 3, 0, 6, 5, 7, 4, -1}},

        {/*5-0*/{-1},
         /*5-1*/{0, 4, 1, 5, 2, 6, 3, 7, -1},
         /*5-2*/{-1},
         /*5-3*/{-1},
         /*5-4*/{0, 1, 3, 2, 4, 5, 7, 6, -1},
         /*5-5*/{-1},
         /*5-6*/{2, 1, 3, 0, 6, 5, 7, 4, -1},
         /*5-7*/{-1}},

        {/*6-0*/{-1},
         /*6-1*/{-1},
         /*6-2*/{0, 4, 1, 5, 2, 6, 3, 7, -1},
         /*6-3*/{-1},
         /*6-4*/{-1},
         /*6-5*/{0, 3, 1, 2, 4, 7, 5, 6, -1},
         /*6-6*/{-1},
         /*6-7*/{0, 1, 3, 2, 4, 5, 7, 6, -1}},
        
        {/*7-0*/{-1},
         /*7-1*/{-1},
         /*7-2*/{-1},
         /*7-3*/{0, 4, 1, 5, 2, 6, 3, 7, -1},
         /*7-4*/{0, 3, 1, 2, 4, 7, 5, 6, -1},
         /*7-5*/{-1},
         /*7-6*/{1, 0, 2, 3, 5, 4, 6, 7, -1},
         /*7-7*/{-1}} 
    };
    
    int[][] cases = {{0, 1}, {2, 3}, {4, 5}, {6, 7}, {1, 2}, {3, 0}, {5, 6}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};

    public DualGrid(OcTree ocTree) {
        cells = new ArrayList<DualGridCell>();
        cubeProc(ocTree.root);
    }
    
    public void cubeProc(OcCell cell) {
        int[] indexes = new int[8];
        for (int i = 0; i < 8; i++) {
            indexes[i] = i;
            OcCell c = cell.child[i];
            
//            for (int j = 0; faceAdjacent[i][j] != -1; j++) {
//                edgeProc(i, j, c, cell.child[faceAdjacent[i][j]]);
//            }
            
            if (!c.hasChildren) continue;
            
            cubeProc(c);
        }
        
        for (int i = 0; i < 4; i++) {
            int l,r;
            l = i; r = (i + 1) % 4;
            edgeProc(l, r, cell.child[l], cell.child[r]);
            l = i + 4; r = ((i + 1) % 4) + 4;
            edgeProc(l, r, cell.child[l], cell.child[r]);
            l = i; r = i + 4;
            edgeProc(l, r, cell.child[l], cell.child[r]);
        }
        
        vertProc(0, indexes, cell.child);
    }
    
    public void edgeProc(int i1, int i2, OcCell c1, OcCell c2) {
        if (!c1.hasChildren || !c2.hasChildren) return;
        
//        if (i1 > i2) {
//            int h = i1; i1 = i2; i2 = h;
//            OcCell h2 = c1; c1 = c2; c2 = h2;
//        }
        
        // We collect 8 cells
        int[] indexes = new int[8];
        OcCell[] cells = new OcCell[8];
        int total = 0;
        
        for (int i = 0; faceAdjacentChild[i1][i2][i] != -1; i++) {
            indexes[i] = faceAdjacentChild[i1][i2][i];

            if (i % 2 == 1) {
                cells[i] = c2.child[indexes[i]];
                edgeProc(indexes[i-1], indexes[i], cells[i-1], cells[i]);
            }
            else
            {
                cells[i] = c1.child[indexes[i]];
            }
            
            total++;
        }
        
        for (int i = 0; i < 8; i++) {
            boolean contains = false;
            for (int j = 0; j < 8; j++ ) {
                if (indexes[j] == i) {
                    contains = true;
                }
            }
            
            if (!contains) {
                System.out.println("Case " + i1 + "x" + i2 + " missing.");
                break;
            }
        }
        
        if (total < 7) return; // TODO: fix
        
        int[][] cases = {{0, 1}, {2, 3}, {4, 5}, {6, 7}, {1, 2}, {3, 0}, {5, 6}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
        
        int c = 0;
        for (int i = 0; i < 12; i++) {
             if ((i1 == cases[i][0] && i2 == cases[i][1]) || (i1 == cases[i][1] && i2 == cases[i][0])) {
                 c = (int) Math.floor(i/4);
                 break;
             }
        }
        
        vertProc(c, indexes, cells);
    }
    
    int[][] focusPoint = {
        {7, 6, 5, 4, 3, 2, 1, 0}, // yz
        {5, 4, 7, 6, 1, 0, 3, 2}, // xy
        {2, 3, 0, 1, 6, 7, 4, 5} // xz
    };
    
    public void vertProc(int c, int[] indexes, OcCell[] cells) {
        // Recurse into the 8 children that all touch the same vertex
        boolean allLeaves = true;
        for (int i = 0; i < 8; i++) {
            if (!cells[i].hasChildren) continue; // leave untouched
            
//            indexes[i] = focusPoint[c][indexes[i]];
//            cells[i] = cells[i].child[indexes[i]];
            cells[i] = cells[i].child[focusPoint[c][indexes[i]]];
            
            allLeaves = false;
        }
        
        if (allLeaves) {
            //Collections.sort(cells);
            DualGridCell gc = new DualGridCell();
            for (int j = 0; j < 8; j++) {
                //System.out.println(indexes[j] + "-" + indexes.length);
                gc.p[indexes[j]] = cells[j].getCenter();
                gc.val[indexes[j]] = cells[j].getCenterVal();
            }

            for (int j = 0; j < 8; j++) {
                if (gc.p[j] == null) {
                    System.out.println("missing");
                    break;
                }
            }
            
            this.cells.add(gc);
        }
        else {
            vertProc(c, indexes, cells);
        }
    }
}
