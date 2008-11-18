package csgbuilder;

import com.sun.opengl.util.BufferUtil;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * http://local.wasp.uwa.edu.au/~pbourke/geometry/polygonise/index.html
 */
public class CSGTreePolygoniser extends Thread {
    private CSGTree mTree;
    private OperandViewer mViewer;
    
	// Whether or not to do marching cubes
	static final boolean ADAPTIVE = true;
	
	// Maximum number of recursions when doing adaptive marching cubes
	static final int MAX_RECURSIONS = 3;
	
	// Number of cubes when not doing adaptive marching cubes
	static final int NUM_CUBES = 10000;
	
	// Whether or not to polygonise cube using tetrahedrons
	static final boolean TETRAHEDRONS = false;
	
	// Delta used when calculating vertex normal
	static final float NORMAL_DELTA = 0.001f;
	
    // To visualize the marching cubes algorithm
    private ArrayList<OcCell> marchingCubes;
    
    private int[] edgeTable = {
    0x0  , 0x109, 0x203, 0x30a, 0x406, 0x50f, 0x605, 0x70c,
    0x80c, 0x905, 0xa0f, 0xb06, 0xc0a, 0xd03, 0xe09, 0xf00,
    0x190, 0x99 , 0x393, 0x29a, 0x596, 0x49f, 0x795, 0x69c,
    0x99c, 0x895, 0xb9f, 0xa96, 0xd9a, 0xc93, 0xf99, 0xe90,
    0x230, 0x339, 0x33 , 0x13a, 0x636, 0x73f, 0x435, 0x53c,
    0xa3c, 0xb35, 0x83f, 0x936, 0xe3a, 0xf33, 0xc39, 0xd30,
    0x3a0, 0x2a9, 0x1a3, 0xaa , 0x7a6, 0x6af, 0x5a5, 0x4ac,
    0xbac, 0xaa5, 0x9af, 0x8a6, 0xfaa, 0xea3, 0xda9, 0xca0,
    0x460, 0x569, 0x663, 0x76a, 0x66 , 0x16f, 0x265, 0x36c,
    0xc6c, 0xd65, 0xe6f, 0xf66, 0x86a, 0x963, 0xa69, 0xb60,
    0x5f0, 0x4f9, 0x7f3, 0x6fa, 0x1f6, 0xff , 0x3f5, 0x2fc,
    0xdfc, 0xcf5, 0xfff, 0xef6, 0x9fa, 0x8f3, 0xbf9, 0xaf0,
    0x650, 0x759, 0x453, 0x55a, 0x256, 0x35f, 0x55 , 0x15c,
    0xe5c, 0xf55, 0xc5f, 0xd56, 0xa5a, 0xb53, 0x859, 0x950,
    0x7c0, 0x6c9, 0x5c3, 0x4ca, 0x3c6, 0x2cf, 0x1c5, 0xcc ,
    0xfcc, 0xec5, 0xdcf, 0xcc6, 0xbca, 0xac3, 0x9c9, 0x8c0,
    0x8c0, 0x9c9, 0xac3, 0xbca, 0xcc6, 0xdcf, 0xec5, 0xfcc,
    0xcc , 0x1c5, 0x2cf, 0x3c6, 0x4ca, 0x5c3, 0x6c9, 0x7c0,
    0x950, 0x859, 0xb53, 0xa5a, 0xd56, 0xc5f, 0xf55, 0xe5c,
    0x15c, 0x55 , 0x35f, 0x256, 0x55a, 0x453, 0x759, 0x650,
    0xaf0, 0xbf9, 0x8f3, 0x9fa, 0xef6, 0xfff, 0xcf5, 0xdfc,
    0x2fc, 0x3f5, 0xff , 0x1f6, 0x6fa, 0x7f3, 0x4f9, 0x5f0,
    0xb60, 0xa69, 0x963, 0x86a, 0xf66, 0xe6f, 0xd65, 0xc6c,
    0x36c, 0x265, 0x16f, 0x66 , 0x76a, 0x663, 0x569, 0x460,
    0xca0, 0xda9, 0xea3, 0xfaa, 0x8a6, 0x9af, 0xaa5, 0xbac,
    0x4ac, 0x5a5, 0x6af, 0x7a6, 0xaa , 0x1a3, 0x2a9, 0x3a0,
    0xd30, 0xc39, 0xf33, 0xe3a, 0x936, 0x83f, 0xb35, 0xa3c,
    0x53c, 0x435, 0x73f, 0x636, 0x13a, 0x33 , 0x339, 0x230,
    0xe90, 0xf99, 0xc93, 0xd9a, 0xa96, 0xb9f, 0x895, 0x99c,
    0x69c, 0x795, 0x49f, 0x596, 0x29a, 0x393, 0x99 , 0x190,
    0xf00, 0xe09, 0xd03, 0xc0a, 0xb06, 0xa0f, 0x905, 0x80c,
    0x70c, 0x605, 0x50f, 0x406, 0x30a, 0x203, 0x109, 0x0   };
    
    // Maps edges to edges in neighbouring cubes
    int[][][] edgeMapping = {
        /* Cube 0 */
        {
        /* Edge 0 */{-1},
        /* Edge 1 */{3, 1, -1},
        /* Edge 2 */{0, 3, -1},
        /* Edge 3 */{-1},
        /* Edge 4 */{-1},
        /* Edge 5 */{7, 1, 1, 4, 3, 5, -1},
        /* Edge 6 */{4, 3, 0, 7, 2, 4, -1},
        /* Edge 7 */{-1},
        /* Edge 8 */{-1},
        /* Edge 9 */{8, 1, -1},
        /* Edge 10 */{11, 1, 8, 2, 2, 3, -1},
        /* Edge 11 */{8, 3, -1}
        },
        /* Cube 1 */
        {
        /* Edge 0 */{-1},
        /* Edge 1 */{-1},
        /* Edge 2 */{0, 2, -1},
        /* Edge 3 */{1, 0, -1},
        /* Edge 4 */{-1},
        /* Edge 5 */{-1},
        /* Edge 6 */{4, 2, 0, 6, 2, 5, -1},
        /* Edge 7 */{5, 0, 3, 5, 1, 4, -1},
        /* Edge 8 */{9, 1, -1},
        /* Edge 9 */{-1},
        /* Edge 10 */{9, 2, -1},
        /* Edge 11 */{10, 0, 8, 2, 2, 3, -1}
        },
        /* Cube 2 */
        {
        /* Edge 0 */{-1},
        /* Edge 1 */{-1},
        /* Edge 2 */{-1},
        /* Edge 3 */{-1},
        /* Edge 4 */{-1},
        /* Edge 5 */{-1},
        /* Edge 6 */{-1},
        /* Edge 7 */{-1},
        /* Edge 8 */{-1},
        /* Edge 9 */{-1},
        /* Edge 10 */{-1},
        /* Edge 11 */{-1}
        },
         /* Cube 3 */
        {
        /* Edge 0 */{-1},
        /* Edge 1 */{-1},
        /* Edge 2 */{-1},
        /* Edge 3 */{-1},
        /* Edge 4 */{-1},
        /* Edge 5 */{-1},
        /* Edge 6 */{-1},
        /* Edge 7 */{-1},
        /* Edge 8 */{-1},
        /* Edge 9 */{-1},
        /* Edge 10 */{-1},
        /* Edge 11 */{-1}
        },       
         /* Cube 4 */
        {
        /* Edge 0 */{-1},
        /* Edge 1 */{-1},
        /* Edge 2 */{-1},
        /* Edge 3 */{-1},
        /* Edge 4 */{-1},
        /* Edge 5 */{-1},
        /* Edge 6 */{-1},
        /* Edge 7 */{-1},
        /* Edge 8 */{-1},
        /* Edge 9 */{-1},
        /* Edge 10 */{-1},
        /* Edge 11 */{-1}
        },       
         /* Cube 5 */
        {
        /* Edge 0 */{-1},
        /* Edge 1 */{-1},
        /* Edge 2 */{-1},
        /* Edge 3 */{-1},
        /* Edge 4 */{-1},
        /* Edge 5 */{-1},
        /* Edge 6 */{-1},
        /* Edge 7 */{-1},
        /* Edge 8 */{-1},
        /* Edge 9 */{-1},
        /* Edge 10 */{-1},
        /* Edge 11 */{-1}
        },       
         /* Cube 6 */
        {
        /* Edge 0 */{-1},
        /* Edge 1 */{-1},
        /* Edge 2 */{-1},
        /* Edge 3 */{-1},
        /* Edge 4 */{-1},
        /* Edge 5 */{-1},
        /* Edge 6 */{-1},
        /* Edge 7 */{-1},
        /* Edge 8 */{-1},
        /* Edge 9 */{-1},
        /* Edge 10 */{-1},
        /* Edge 11 */{-1}
        },       
         /* Cube 7 */
        {
        /* Edge 0 */{-1},
        /* Edge 1 */{-1},
        /* Edge 2 */{-1},
        /* Edge 3 */{-1},
        /* Edge 4 */{-1},
        /* Edge 5 */{-1},
        /* Edge 6 */{-1},
        /* Edge 7 */{-1},
        /* Edge 8 */{-1},
        /* Edge 9 */{-1},
        /* Edge 10 */{-1},
        /* Edge 11 */{-1}
        }       
    };

    public CSGTreePolygoniser () {
        marchingCubes = new ArrayList<OcCell>();
    }
    
    public CSGTreePolygoniser (OperandViewer pvViewer, CSGTree pvTree) {
        marchingCubes = new ArrayList<OcCell>();
        
        setViewer(pvViewer);
        setTree(pvTree);
    }
    
    public ArrayList<OcCell> getMarchingCubes() {
        return this.marchingCubes;
    }
    
    public ArrayList<Vertex> getPolygonsAdaptive(CSGTree tree) {
        // Construct the octree
        OcTree ocTree = new OcTree(tree);
        
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		polygonsAdaptiveRecursive(vertices, ocTree.root);
		return vertices;
    }
    
    public void setTree(CSGTree pvTree) {
        mTree = pvTree;
    }
    
    public void setViewer(OperandViewer pvViewer) {
        mViewer = pvViewer;
    }
    
    public void run() {
        mViewer.setMesh(getMesh(mTree));
    }
	
	public OperandMesh getMesh(CSGTree pvTree) {
		ArrayList<Vertex> lvVertexArray;
		
        lvVertexArray = this.getPolygonsAdaptive(pvTree);
		
		OperandMesh lvMesh = new OperandMesh(lvVertexArray.size());

		for (Vertex v : lvVertexArray) {
			lvMesh.addVertex(v);
			
			Vertex n = new Vertex();
			float d = NORMAL_DELTA;
			n.x = (float) ((pvTree.getFunctionValue(v.x + d, v.y, v.z) - pvTree.getFunctionValue(v.x, v.y, v.z)) / d);
			n.y = (float) ((pvTree.getFunctionValue(v.x, v.y + d, v.z) - pvTree.getFunctionValue(v.x, v.y, v.z)) / d);
			n.z = (float) ((pvTree.getFunctionValue(v.x, v.y, v.z + d) - pvTree.getFunctionValue(v.x, v.y, v.z)) / d);
			n.subtract(v);
			n.normalize();
			lvMesh.addNormal(n);
		}
		
		lvMesh.flipVertices();
		
		// SET DEBUG DATA
		lvMesh.setDebugBB(pvTree.getBoundingBox());
		lvMesh.setDebugMCCells(this.getMarchingCubes());
			
		return lvMesh;
    }
    
    private void polygonsAdaptiveRecursive(ArrayList<Vertex> vertices, OcCell cell) {
        Polygonise(cell, 0);
        
        Vertex[] mapVertices = new Vertex[12];

        if (cell.hasChildren) {
            // Recurse on all children
            boolean notRecursed = false;
            boolean recursed = false;
            for (int i = 0; i < 8; i++) {
                OcCell c = cell.child[i];
                polygonsAdaptiveRecursive(vertices, c);
                
                if (!recursed) recursed = c.hasChildren;
                if (!notRecursed) notRecursed = !c.hasChildren;
                
//                // update parent coords
//                for (int j = 0; c.edges[j] != -1; j++) {
//                    // check if the edge can be mapped on a parent edge
//                    int e = c.edges[j];
//                    int e1 = i, e2 = -1, e3 = -1;
//                    if (i < 4) {
//                        e2 = (i + 3) % 4;
//                        e3 = i + 8;
//                    } else {
//                        e2 = (i + 7) % 8;
//                        e3 = i + 4;
//                    }
//                    
//                    if ((e == e1) || (e == e2) || (e == e3)) {
//                        // edge e can be mapped to edge e on the parent
//                        mapVertices[e] = (Vertex) c.vertices.get(j);
//                    }
//                }
//                
//                // do the mapping
//                for (int j = 0; cell.edges[j] != -1; j++) {
//                    if (mapVertices[j] != null) {
//                        Vertex v = (Vertex) cell.vertices.get(j);
//                        v.x = mapVertices[j].x;
//                        v.y = mapVertices[j].y;
//                        v.z = mapVertices[j].z;
//                    }
//                }
            }
            
//            if (recursed && notRecursed) {
//                for (int i = 0; i < 8; i++) {
//                    OcCell c = cell.child[i];
//                    if (c.hasChildren) continue;
//                    
//                     for (int j = 0; c.edges[j] != -1; j++) {
//                        int[] edgeMap = edgeMapping[i][j];
//                        
//                        for (int k = 0; edgeMap[k] != -1; i += 2) {
//                            int mapEdge = edgeMap[k];
//                            int mapCube = edgeMap[k+1];
//                            // edge j maps to mapEdge in mapCube
//                            
//                            // If it also has no children we cant copy vertices
//                            if (cell.child[mapCube].hasChildren == false) {
//                                continue;
//                            }
//                            
//                            // Search for the correct edge in the neighbouring edges
//                            for (int l = 0; cell.child[mapCube].edges[l] != -1; l++) {
//                                if (cell.child[mapCube].edges[l] == mapEdge) {
//                                    Vertex v = (Vertex) c.vertices.get(j);
//                                    Vertex w = (Vertex) cell.child[mapCube].vertices.get(l);
//                                    v.x = w.x;
//                                    v.y = w.y;
//                                    v.z = w.z;
//                                    break;
//                                }
//                            }
//                        }                        
//                     }
//                   
//                }
//            }
            
            // If we encounter a cell that has no children, but it's neighbouring
            // cells do have children we have to fix cracks caused by the difference
            // in cell resolution.
//            if (false && recursed && notRecursed) {
//                for (int i = 0; i < 8; i++) {
//                    OcCell c = cell.child[i];
//                    if (c.hasChildren) continue;
//                    
//                    // Fix cracks
//                    // For all edges take the interpolated vertice from a neighbouring
//                    // cell (if this cell is recursed), else do nothing.
//                    for (int j = 0; c.edges[j] != -1; j++) {
//                        int[] edgeMap = edgeMapping[i][j];
//                        
//                        for (int k = 0; edgeMap[k] != -1; i += 2) {
//                            int mapEdge = edgeMap[k];
//                            int mapCube = edgeMap[k+1];
//                            // edge j maps to mapEdge in mapCube
//                            
//                            // If it also has no children we cant copy vertices
//                            if (cell.child[mapCube].hasChildren == false) {
//                                continue;
//                            }
//                        }
//                    }
//                }                
//            }
        }
        else {
            // It's a leaf -> polgonize
            vertices.addAll(cell.vertices);
        }
        
        this.marchingCubes.add(cell);
    }

    private GridCell BuildCell(Vertex p, Vertex dim, CSGTree tree) {
        return BuildCell(p.x, p.y, p.z, dim, tree);
    }
    
    private GridCell BuildCell(float x, float y, float z, Vertex dim, CSGTree tree) {
        GridCell cell = new GridCell();
        
        // Set vertices
        cell.p[0].x = x; cell.p[0].y = y; cell.p[0].z = z;
        cell.p[1].x = x + dim.x; cell.p[1].y = y; cell.p[1].z = z;
        cell.p[2].x = x + dim.x; cell.p[2].y = y; cell.p[2].z = z + dim.z;
        cell.p[3].x = x; cell.p[3].y = y; cell.p[3].z = z + dim.z;
        cell.p[4].x = x; cell.p[4].y = y + dim.y; cell.p[4].z = z;
        cell.p[5].x = x + dim.x; cell.p[5].y = y + dim.y; cell.p[5].z = z;
        cell.p[6].x = x + dim.x; cell.p[6].y = y + dim.y; cell.p[6].z = z + dim.z;
        cell.p[7].x = x; cell.p[7].y = y + dim.y; cell.p[7].z = z + dim.z;
        
        // Set iso values on all vertices
                    
        for (int i = 0; i < cell.p.length; i++) {

            //cell.val[i] = (float) Difference(cell.p[i].x + 0.5f, cell.p[i].y + 0.5f, cell.p[i].z + 0.5f);
            //cell.val[i] = tree.approachFunctionValue(cell.p[i].x, cell.p[i].y, cell.p[i].z);
            cell.val[i] = (float) tree.getFunctionValue(cell.p[i].x, cell.p[i].y, cell.p[i].z);
        }
        
        return cell;
    }
    
//    public ArrayList<Vertex> getPolygons(CSGTree tree) {
//		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
//        
//        Vertex start = new Vertex(); start.x = -1f; start.y = -1f; start.z = -1f;
//        Vertex end = new Vertex(); end.x = 1f; end.y = 1f; end.z = 1f;
//
//		int div = (int) Math.pow((double)NUM_CUBES, 1.0/3.0);
//		if (div == 0) {
//			return vertices;
//		}
//		
//		Vertex step = new Vertex();
//		step.x = (end.x - start.x)/div;
//		step.y = (end.y - start.y)/div;
//		step.z = (end.z - start.z)/div;
//
//        for (float x = start.x; x < end.x; x += step.x) {
//             for (float y = start.y; y < end.y; y += step.y) {
//                 for (float z = start.z; z < end.z; z += step.z) {
//                    GridCell cell = BuildCell(x, y, z, step, tree);                    
//                    this.marchingCubes.add(cell);
//                    
//					if (TETRAHEDRONS) {
//						PolygoniseCubeTri(vertices, cell, 0);
//					}
//					else
//					{
//						Polygonise(vertices, cell, 0);
//					}
//                    //ArrayList<Triangle> newTriangles = PolygoniseCubeTri(cell, 0);
//                 }
//             }
//        }
//        
//        return vertices;
//    }


    /*
       Given a grid cell and an isolevel, calculate the triangular
       facets required to represent the isosurface through the cell.
       Return the number of triangular facets, the array "triangles"
       will be loaded up with the vertices at most 5 triangular facets.
            0 will be returned if the grid cell is either totally above
       of totally below the isolevel.
    */
    public void Polygonise(OcCell cell, float isolevel)
    {
       int i,ntriang;
       int cubeindex;
       Vertex[] vertlist = new Vertex[12];

    int[][] triTable =
    {{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {0, 1, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {1, 8, 3, 9, 8, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {0, 8, 3, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {9, 2, 10, 0, 2, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {2, 8, 3, 2, 10, 8, 10, 9, 8, -1, -1, -1, -1, -1, -1, -1},
    {3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {0, 11, 2, 8, 11, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {1, 9, 0, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {1, 11, 2, 1, 9, 11, 9, 8, 11, -1, -1, -1, -1, -1, -1, -1},
    {3, 10, 1, 11, 10, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {0, 10, 1, 0, 8, 10, 8, 11, 10, -1, -1, -1, -1, -1, -1, -1},
    {3, 9, 0, 3, 11, 9, 11, 10, 9, -1, -1, -1, -1, -1, -1, -1},
    {9, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {4, 3, 0, 7, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {0, 1, 9, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {4, 1, 9, 4, 7, 1, 7, 3, 1, -1, -1, -1, -1, -1, -1, -1},
    {1, 2, 10, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {3, 4, 7, 3, 0, 4, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1},
    {9, 2, 10, 9, 0, 2, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1},
    {2, 10, 9, 2, 9, 7, 2, 7, 3, 7, 9, 4, -1, -1, -1, -1},
    {8, 4, 7, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {11, 4, 7, 11, 2, 4, 2, 0, 4, -1, -1, -1, -1, -1, -1, -1},
    {9, 0, 1, 8, 4, 7, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1},
    {4, 7, 11, 9, 4, 11, 9, 11, 2, 9, 2, 1, -1, -1, -1, -1},
    {3, 10, 1, 3, 11, 10, 7, 8, 4, -1, -1, -1, -1, -1, -1, -1},
    {1, 11, 10, 1, 4, 11, 1, 0, 4, 7, 11, 4, -1, -1, -1, -1},
    {4, 7, 8, 9, 0, 11, 9, 11, 10, 11, 0, 3, -1, -1, -1, -1},
    {4, 7, 11, 4, 11, 9, 9, 11, 10, -1, -1, -1, -1, -1, -1, -1},
    {9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {9, 5, 4, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {0, 5, 4, 1, 5, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {8, 5, 4, 8, 3, 5, 3, 1, 5, -1, -1, -1, -1, -1, -1, -1},
    {1, 2, 10, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {3, 0, 8, 1, 2, 10, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1},
    {5, 2, 10, 5, 4, 2, 4, 0, 2, -1, -1, -1, -1, -1, -1, -1},
    {2, 10, 5, 3, 2, 5, 3, 5, 4, 3, 4, 8, -1, -1, -1, -1},
    {9, 5, 4, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {0, 11, 2, 0, 8, 11, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1},
    {0, 5, 4, 0, 1, 5, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1},
    {2, 1, 5, 2, 5, 8, 2, 8, 11, 4, 8, 5, -1, -1, -1, -1},
    {10, 3, 11, 10, 1, 3, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1},
    {4, 9, 5, 0, 8, 1, 8, 10, 1, 8, 11, 10, -1, -1, -1, -1},
    {5, 4, 0, 5, 0, 11, 5, 11, 10, 11, 0, 3, -1, -1, -1, -1},
    {5, 4, 8, 5, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1},
    {9, 7, 8, 5, 7, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {9, 3, 0, 9, 5, 3, 5, 7, 3, -1, -1, -1, -1, -1, -1, -1},
    {0, 7, 8, 0, 1, 7, 1, 5, 7, -1, -1, -1, -1, -1, -1, -1},
    {1, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {9, 7, 8, 9, 5, 7, 10, 1, 2, -1, -1, -1, -1, -1, -1, -1},
    {10, 1, 2, 9, 5, 0, 5, 3, 0, 5, 7, 3, -1, -1, -1, -1},
    {8, 0, 2, 8, 2, 5, 8, 5, 7, 10, 5, 2, -1, -1, -1, -1},
    {2, 10, 5, 2, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1},
    {7, 9, 5, 7, 8, 9, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1},
    {9, 5, 7, 9, 7, 2, 9, 2, 0, 2, 7, 11, -1, -1, -1, -1},
    {2, 3, 11, 0, 1, 8, 1, 7, 8, 1, 5, 7, -1, -1, -1, -1},
    {11, 2, 1, 11, 1, 7, 7, 1, 5, -1, -1, -1, -1, -1, -1, -1},
    {9, 5, 8, 8, 5, 7, 10, 1, 3, 10, 3, 11, -1, -1, -1, -1},
    {5, 7, 0, 5, 0, 9, 7, 11, 0, 1, 0, 10, 11, 10, 0, -1},
    {11, 10, 0, 11, 0, 3, 10, 5, 0, 8, 0, 7, 5, 7, 0, -1},
    {11, 10, 5, 7, 11, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {0, 8, 3, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {9, 0, 1, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {1, 8, 3, 1, 9, 8, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1},
    {1, 6, 5, 2, 6, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {1, 6, 5, 1, 2, 6, 3, 0, 8, -1, -1, -1, -1, -1, -1, -1},
    {9, 6, 5, 9, 0, 6, 0, 2, 6, -1, -1, -1, -1, -1, -1, -1},
    {5, 9, 8, 5, 8, 2, 5, 2, 6, 3, 2, 8, -1, -1, -1, -1},
    {2, 3, 11, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {11, 0, 8, 11, 2, 0, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1},
    {0, 1, 9, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1},
    {5, 10, 6, 1, 9, 2, 9, 11, 2, 9, 8, 11, -1, -1, -1, -1},
    {6, 3, 11, 6, 5, 3, 5, 1, 3, -1, -1, -1, -1, -1, -1, -1},
    {0, 8, 11, 0, 11, 5, 0, 5, 1, 5, 11, 6, -1, -1, -1, -1},
    {3, 11, 6, 0, 3, 6, 0, 6, 5, 0, 5, 9, -1, -1, -1, -1},
    {6, 5, 9, 6, 9, 11, 11, 9, 8, -1, -1, -1, -1, -1, -1, -1},
    {5, 10, 6, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {4, 3, 0, 4, 7, 3, 6, 5, 10, -1, -1, -1, -1, -1, -1, -1},
    {1, 9, 0, 5, 10, 6, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1},
    {10, 6, 5, 1, 9, 7, 1, 7, 3, 7, 9, 4, -1, -1, -1, -1},
    {6, 1, 2, 6, 5, 1, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1},
    {1, 2, 5, 5, 2, 6, 3, 0, 4, 3, 4, 7, -1, -1, -1, -1},
    {8, 4, 7, 9, 0, 5, 0, 6, 5, 0, 2, 6, -1, -1, -1, -1},
    {7, 3, 9, 7, 9, 4, 3, 2, 9, 5, 9, 6, 2, 6, 9, -1},
    {3, 11, 2, 7, 8, 4, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1},
    {5, 10, 6, 4, 7, 2, 4, 2, 0, 2, 7, 11, -1, -1, -1, -1},
    {0, 1, 9, 4, 7, 8, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1},
    {9, 2, 1, 9, 11, 2, 9, 4, 11, 7, 11, 4, 5, 10, 6, -1},
    {8, 4, 7, 3, 11, 5, 3, 5, 1, 5, 11, 6, -1, -1, -1, -1},
    {5, 1, 11, 5, 11, 6, 1, 0, 11, 7, 11, 4, 0, 4, 11, -1},
    {0, 5, 9, 0, 6, 5, 0, 3, 6, 11, 6, 3, 8, 4, 7, -1},
    {6, 5, 9, 6, 9, 11, 4, 7, 9, 7, 11, 9, -1, -1, -1, -1},
    {10, 4, 9, 6, 4, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {4, 10, 6, 4, 9, 10, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1},
    {10, 0, 1, 10, 6, 0, 6, 4, 0, -1, -1, -1, -1, -1, -1, -1},
    {8, 3, 1, 8, 1, 6, 8, 6, 4, 6, 1, 10, -1, -1, -1, -1},
    {1, 4, 9, 1, 2, 4, 2, 6, 4, -1, -1, -1, -1, -1, -1, -1},
    {3, 0, 8, 1, 2, 9, 2, 4, 9, 2, 6, 4, -1, -1, -1, -1},
    {0, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {8, 3, 2, 8, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1},
    {10, 4, 9, 10, 6, 4, 11, 2, 3, -1, -1, -1, -1, -1, -1, -1},
    {0, 8, 2, 2, 8, 11, 4, 9, 10, 4, 10, 6, -1, -1, -1, -1},
    {3, 11, 2, 0, 1, 6, 0, 6, 4, 6, 1, 10, -1, -1, -1, -1},
    {6, 4, 1, 6, 1, 10, 4, 8, 1, 2, 1, 11, 8, 11, 1, -1},
    {9, 6, 4, 9, 3, 6, 9, 1, 3, 11, 6, 3, -1, -1, -1, -1},
    {8, 11, 1, 8, 1, 0, 11, 6, 1, 9, 1, 4, 6, 4, 1, -1},
    {3, 11, 6, 3, 6, 0, 0, 6, 4, -1, -1, -1, -1, -1, -1, -1},
    {6, 4, 8, 11, 6, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {7, 10, 6, 7, 8, 10, 8, 9, 10, -1, -1, -1, -1, -1, -1, -1},
    {0, 7, 3, 0, 10, 7, 0, 9, 10, 6, 7, 10, -1, -1, -1, -1},
    {10, 6, 7, 1, 10, 7, 1, 7, 8, 1, 8, 0, -1, -1, -1, -1},
    {10, 6, 7, 10, 7, 1, 1, 7, 3, -1, -1, -1, -1, -1, -1, -1},
    {1, 2, 6, 1, 6, 8, 1, 8, 9, 8, 6, 7, -1, -1, -1, -1},
    {2, 6, 9, 2, 9, 1, 6, 7, 9, 0, 9, 3, 7, 3, 9, -1},
    {7, 8, 0, 7, 0, 6, 6, 0, 2, -1, -1, -1, -1, -1, -1, -1},
    {7, 3, 2, 6, 7, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {2, 3, 11, 10, 6, 8, 10, 8, 9, 8, 6, 7, -1, -1, -1, -1},
    {2, 0, 7, 2, 7, 11, 0, 9, 7, 6, 7, 10, 9, 10, 7, -1},
    {1, 8, 0, 1, 7, 8, 1, 10, 7, 6, 7, 10, 2, 3, 11, -1},
    {11, 2, 1, 11, 1, 7, 10, 6, 1, 6, 7, 1, -1, -1, -1, -1},
    {8, 9, 6, 8, 6, 7, 9, 1, 6, 11, 6, 3, 1, 3, 6, -1},
    {0, 9, 1, 11, 6, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {7, 8, 0, 7, 0, 6, 3, 11, 0, 11, 6, 0, -1, -1, -1, -1},
    {7, 11, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {3, 0, 8, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {0, 1, 9, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {8, 1, 9, 8, 3, 1, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1},
    {10, 1, 2, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {1, 2, 10, 3, 0, 8, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1},
    {2, 9, 0, 2, 10, 9, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1},
    {6, 11, 7, 2, 10, 3, 10, 8, 3, 10, 9, 8, -1, -1, -1, -1},
    {7, 2, 3, 6, 2, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {7, 0, 8, 7, 6, 0, 6, 2, 0, -1, -1, -1, -1, -1, -1, -1},
    {2, 7, 6, 2, 3, 7, 0, 1, 9, -1, -1, -1, -1, -1, -1, -1},
    {1, 6, 2, 1, 8, 6, 1, 9, 8, 8, 7, 6, -1, -1, -1, -1},
    {10, 7, 6, 10, 1, 7, 1, 3, 7, -1, -1, -1, -1, -1, -1, -1},
    {10, 7, 6, 1, 7, 10, 1, 8, 7, 1, 0, 8, -1, -1, -1, -1},
    {0, 3, 7, 0, 7, 10, 0, 10, 9, 6, 10, 7, -1, -1, -1, -1},
    {7, 6, 10, 7, 10, 8, 8, 10, 9, -1, -1, -1, -1, -1, -1, -1},
    {6, 8, 4, 11, 8, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {3, 6, 11, 3, 0, 6, 0, 4, 6, -1, -1, -1, -1, -1, -1, -1},
    {8, 6, 11, 8, 4, 6, 9, 0, 1, -1, -1, -1, -1, -1, -1, -1},
    {9, 4, 6, 9, 6, 3, 9, 3, 1, 11, 3, 6, -1, -1, -1, -1},
    {6, 8, 4, 6, 11, 8, 2, 10, 1, -1, -1, -1, -1, -1, -1, -1},
    {1, 2, 10, 3, 0, 11, 0, 6, 11, 0, 4, 6, -1, -1, -1, -1},
    {4, 11, 8, 4, 6, 11, 0, 2, 9, 2, 10, 9, -1, -1, -1, -1},
    {10, 9, 3, 10, 3, 2, 9, 4, 3, 11, 3, 6, 4, 6, 3, -1},
    {8, 2, 3, 8, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1},
    {0, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {1, 9, 0, 2, 3, 4, 2, 4, 6, 4, 3, 8, -1, -1, -1, -1},
    {1, 9, 4, 1, 4, 2, 2, 4, 6, -1, -1, -1, -1, -1, -1, -1},
    {8, 1, 3, 8, 6, 1, 8, 4, 6, 6, 10, 1, -1, -1, -1, -1},
    {10, 1, 0, 10, 0, 6, 6, 0, 4, -1, -1, -1, -1, -1, -1, -1},
    {4, 6, 3, 4, 3, 8, 6, 10, 3, 0, 3, 9, 10, 9, 3, -1},
    {10, 9, 4, 6, 10, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {4, 9, 5, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {0, 8, 3, 4, 9, 5, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1},
    {5, 0, 1, 5, 4, 0, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1},
    {11, 7, 6, 8, 3, 4, 3, 5, 4, 3, 1, 5, -1, -1, -1, -1},
    {9, 5, 4, 10, 1, 2, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1},
    {6, 11, 7, 1, 2, 10, 0, 8, 3, 4, 9, 5, -1, -1, -1, -1},
    {7, 6, 11, 5, 4, 10, 4, 2, 10, 4, 0, 2, -1, -1, -1, -1},
    {3, 4, 8, 3, 5, 4, 3, 2, 5, 10, 5, 2, 11, 7, 6, -1},
    {7, 2, 3, 7, 6, 2, 5, 4, 9, -1, -1, -1, -1, -1, -1, -1},
    {9, 5, 4, 0, 8, 6, 0, 6, 2, 6, 8, 7, -1, -1, -1, -1},
    {3, 6, 2, 3, 7, 6, 1, 5, 0, 5, 4, 0, -1, -1, -1, -1},
    {6, 2, 8, 6, 8, 7, 2, 1, 8, 4, 8, 5, 1, 5, 8, -1},
    {9, 5, 4, 10, 1, 6, 1, 7, 6, 1, 3, 7, -1, -1, -1, -1},
    {1, 6, 10, 1, 7, 6, 1, 0, 7, 8, 7, 0, 9, 5, 4, -1},
    {4, 0, 10, 4, 10, 5, 0, 3, 10, 6, 10, 7, 3, 7, 10, -1},
    {7, 6, 10, 7, 10, 8, 5, 4, 10, 4, 8, 10, -1, -1, -1, -1},
    {6, 9, 5, 6, 11, 9, 11, 8, 9, -1, -1, -1, -1, -1, -1, -1},
    {3, 6, 11, 0, 6, 3, 0, 5, 6, 0, 9, 5, -1, -1, -1, -1},
    {0, 11, 8, 0, 5, 11, 0, 1, 5, 5, 6, 11, -1, -1, -1, -1},
    {6, 11, 3, 6, 3, 5, 5, 3, 1, -1, -1, -1, -1, -1, -1, -1},
    {1, 2, 10, 9, 5, 11, 9, 11, 8, 11, 5, 6, -1, -1, -1, -1},
    {0, 11, 3, 0, 6, 11, 0, 9, 6, 5, 6, 9, 1, 2, 10, -1},
    {11, 8, 5, 11, 5, 6, 8, 0, 5, 10, 5, 2, 0, 2, 5, -1},
    {6, 11, 3, 6, 3, 5, 2, 10, 3, 10, 5, 3, -1, -1, -1, -1},
    {5, 8, 9, 5, 2, 8, 5, 6, 2, 3, 8, 2, -1, -1, -1, -1},
    {9, 5, 6, 9, 6, 0, 0, 6, 2, -1, -1, -1, -1, -1, -1, -1},
    {1, 5, 8, 1, 8, 0, 5, 6, 8, 3, 8, 2, 6, 2, 8, -1},
    {1, 5, 6, 2, 1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {1, 3, 6, 1, 6, 10, 3, 8, 6, 5, 6, 9, 8, 9, 6, -1},
    {10, 1, 0, 10, 0, 6, 9, 5, 0, 5, 6, 0, -1, -1, -1, -1},
    {0, 3, 8, 5, 6, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {10, 5, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {11, 5, 10, 7, 5, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {11, 5, 10, 11, 7, 5, 8, 3, 0, -1, -1, -1, -1, -1, -1, -1},
    {5, 11, 7, 5, 10, 11, 1, 9, 0, -1, -1, -1, -1, -1, -1, -1},
    {10, 7, 5, 10, 11, 7, 9, 8, 1, 8, 3, 1, -1, -1, -1, -1},
    {11, 1, 2, 11, 7, 1, 7, 5, 1, -1, -1, -1, -1, -1, -1, -1},
    {0, 8, 3, 1, 2, 7, 1, 7, 5, 7, 2, 11, -1, -1, -1, -1},
    {9, 7, 5, 9, 2, 7, 9, 0, 2, 2, 11, 7, -1, -1, -1, -1},
    {7, 5, 2, 7, 2, 11, 5, 9, 2, 3, 2, 8, 9, 8, 2, -1},
    {2, 5, 10, 2, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1},
    {8, 2, 0, 8, 5, 2, 8, 7, 5, 10, 2, 5, -1, -1, -1, -1},
    {9, 0, 1, 5, 10, 3, 5, 3, 7, 3, 10, 2, -1, -1, -1, -1},
    {9, 8, 2, 9, 2, 1, 8, 7, 2, 10, 2, 5, 7, 5, 2, -1},
    {1, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {0, 8, 7, 0, 7, 1, 1, 7, 5, -1, -1, -1, -1, -1, -1, -1},
    {9, 0, 3, 9, 3, 5, 5, 3, 7, -1, -1, -1, -1, -1, -1, -1},
    {9, 8, 7, 5, 9, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {5, 8, 4, 5, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1},
    {5, 0, 4, 5, 11, 0, 5, 10, 11, 11, 3, 0, -1, -1, -1, -1},
    {0, 1, 9, 8, 4, 10, 8, 10, 11, 10, 4, 5, -1, -1, -1, -1},
    {10, 11, 4, 10, 4, 5, 11, 3, 4, 9, 4, 1, 3, 1, 4, -1},
    {2, 5, 1, 2, 8, 5, 2, 11, 8, 4, 5, 8, -1, -1, -1, -1},
    {0, 4, 11, 0, 11, 3, 4, 5, 11, 2, 11, 1, 5, 1, 11, -1},
    {0, 2, 5, 0, 5, 9, 2, 11, 5, 4, 5, 8, 11, 8, 5, -1},
    {9, 4, 5, 2, 11, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {2, 5, 10, 3, 5, 2, 3, 4, 5, 3, 8, 4, -1, -1, -1, -1},
    {5, 10, 2, 5, 2, 4, 4, 2, 0, -1, -1, -1, -1, -1, -1, -1},
    {3, 10, 2, 3, 5, 10, 3, 8, 5, 4, 5, 8, 0, 1, 9, -1},
    {5, 10, 2, 5, 2, 4, 1, 9, 2, 9, 4, 2, -1, -1, -1, -1},
    {8, 4, 5, 8, 5, 3, 3, 5, 1, -1, -1, -1, -1, -1, -1, -1},
    {0, 4, 5, 1, 0, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {8, 4, 5, 8, 5, 3, 9, 0, 5, 0, 3, 5, -1, -1, -1, -1},
    {9, 4, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {4, 11, 7, 4, 9, 11, 9, 10, 11, -1, -1, -1, -1, -1, -1, -1},
    {0, 8, 3, 4, 9, 7, 9, 11, 7, 9, 10, 11, -1, -1, -1, -1},
    {1, 10, 11, 1, 11, 4, 1, 4, 0, 7, 4, 11, -1, -1, -1, -1},
    {3, 1, 4, 3, 4, 8, 1, 10, 4, 7, 4, 11, 10, 11, 4, -1},
    {4, 11, 7, 9, 11, 4, 9, 2, 11, 9, 1, 2, -1, -1, -1, -1},
    {9, 7, 4, 9, 11, 7, 9, 1, 11, 2, 11, 1, 0, 8, 3, -1},
    {11, 7, 4, 11, 4, 2, 2, 4, 0, -1, -1, -1, -1, -1, -1, -1},
    {11, 7, 4, 11, 4, 2, 8, 3, 4, 3, 2, 4, -1, -1, -1, -1},
    {2, 9, 10, 2, 7, 9, 2, 3, 7, 7, 4, 9, -1, -1, -1, -1},
    {9, 10, 7, 9, 7, 4, 10, 2, 7, 8, 7, 0, 2, 0, 7, -1},
    {3, 7, 10, 3, 10, 2, 7, 4, 10, 1, 10, 0, 4, 0, 10, -1},
    {1, 10, 2, 8, 7, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {4, 9, 1, 4, 1, 7, 7, 1, 3, -1, -1, -1, -1, -1, -1, -1},
    {4, 9, 1, 4, 1, 7, 0, 8, 1, 8, 7, 1, -1, -1, -1, -1},
    {4, 0, 3, 7, 4, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {4, 8, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {9, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {3, 0, 9, 3, 9, 11, 11, 9, 10, -1, -1, -1, -1, -1, -1, -1},
    {0, 1, 10, 0, 10, 8, 8, 10, 11, -1, -1, -1, -1, -1, -1, -1},
    {3, 1, 10, 11, 3, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {1, 2, 11, 1, 11, 9, 9, 11, 8, -1, -1, -1, -1, -1, -1, -1},
    {3, 0, 9, 3, 9, 11, 1, 2, 9, 2, 11, 9, -1, -1, -1, -1},
    {0, 2, 11, 8, 0, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {3, 2, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {2, 3, 8, 2, 8, 10, 10, 8, 9, -1, -1, -1, -1, -1, -1, -1},
    {9, 10, 2, 0, 9, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {2, 3, 8, 2, 8, 10, 0, 1, 8, 1, 10, 8, -1, -1, -1, -1},
    {1, 10, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {1, 3, 8, 9, 1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {0, 9, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {0, 3, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}};

       /*
          Determine the index into the edge table which
          tells us which vertices are inside of the surface
       */
       cubeindex = 0;
       if (cell.val[0] < isolevel) cubeindex |= 1;
       if (cell.val[1] < isolevel) cubeindex |= 2;
       if (cell.val[2] < isolevel) cubeindex |= 4;
       if (cell.val[3] < isolevel) cubeindex |= 8;
       if (cell.val[4] < isolevel) cubeindex |= 16;
       if (cell.val[5] < isolevel) cubeindex |= 32;
       if (cell.val[6] < isolevel) cubeindex |= 64;
       if (cell.val[7] < isolevel) cubeindex |= 128;
       
       cell.edges = triTable[cubeindex];

       /* Cube is entirely in/out of the surface */
       if (edgeTable[cubeindex] == 0)
          return;

       /* Find the vertices where the surface intersects the cube */
       if ((edgeTable[cubeindex] & 1) == 1)
          vertlist[0] =
             VertexInterp(isolevel,cell.p[0],cell.p[1],cell.val[0],cell.val[1]);
       if ((edgeTable[cubeindex] & 2) == 2)
          vertlist[1] =
             VertexInterp(isolevel,cell.p[1],cell.p[2],cell.val[1],cell.val[2]);
       if ((edgeTable[cubeindex] & 4) == 4)
          vertlist[2] =
             VertexInterp(isolevel,cell.p[2],cell.p[3],cell.val[2],cell.val[3]);
       if ((edgeTable[cubeindex] & 8) == 8)
          vertlist[3] =
             VertexInterp(isolevel,cell.p[3],cell.p[0],cell.val[3],cell.val[0]);
       if ((edgeTable[cubeindex] & 16) == 16)
          vertlist[4] =
             VertexInterp(isolevel,cell.p[4],cell.p[5],cell.val[4],cell.val[5]);
       if ((edgeTable[cubeindex] & 32) == 32)
          vertlist[5] =
             VertexInterp(isolevel,cell.p[5],cell.p[6],cell.val[5],cell.val[6]);
       if ((edgeTable[cubeindex] & 64) == 64)
          vertlist[6] =
             VertexInterp(isolevel,cell.p[6],cell.p[7],cell.val[6],cell.val[7]);
       if ((edgeTable[cubeindex] & 128) == 128)
          vertlist[7] =
             VertexInterp(isolevel,cell.p[7],cell.p[4],cell.val[7],cell.val[4]);
       if ((edgeTable[cubeindex] & 256) == 256)
          vertlist[8] =
             VertexInterp(isolevel,cell.p[0],cell.p[4],cell.val[0],cell.val[4]);
       if ((edgeTable[cubeindex] & 512) == 512)
          vertlist[9] =
             VertexInterp(isolevel,cell.p[1],cell.p[5],cell.val[1],cell.val[5]);
       if ((edgeTable[cubeindex] & 1024) == 1024)
          vertlist[10] =
             VertexInterp(isolevel,cell.p[2],cell.p[6],cell.val[2],cell.val[6]);
       if ((edgeTable[cubeindex] & 2048) == 2048)
          vertlist[11] =
             VertexInterp(isolevel,cell.p[3],cell.p[7],cell.val[3],cell.val[7]);

       /* Create the triangle */
       for (i=0;triTable[cubeindex][i]!=-1;i++) {
		   //vertices.add(vertlist[triTable[cubeindex][i]]);
		   cell.vertices.add(vertlist[triTable[cubeindex][i]]);
	   }
    }

    /*
       Linearly interpolate the position where an isosurface cuts
       an edge between two vertices, each with their own scalar value
    */
    Vertex VertexInterp(float isolevel, Vertex p1, Vertex p2, float valp1, float valp2)
    {
       double mu;
       Vertex v = new Vertex();
	   
       if (Math.abs(isolevel-valp1) < 0.00001)
          return(p1);
       if (Math.abs(isolevel-valp2) < 0.00001)
          return(p2);
       if (Math.abs(valp1-valp2) < 0.00001)
          return(p1);
       mu = (isolevel - valp1) / (valp2 - valp1);

       v.x = (float) (p1.x + mu * (p2.x - p1.x));
       v.y = (float) (p1.y + mu * (p2.y - p1.y));
       v.z = (float) (p1.z + mu * (p2.z - p1.z));

       return(v);
    }
    
    
    public void PolygoniseCubeTri(ArrayList<Vertex> vertices, OcCell g, float iso) {
        // Split cube into five tetrahedrons and use the PolygoniseTri to polygonise
        PolygoniseTri(vertices, g, iso, 0, 1, 2, 5);
        PolygoniseTri(vertices, g, iso, 0, 2, 3, 7);
        PolygoniseTri(vertices, g, iso, 5, 6, 7, 2);
        PolygoniseTri(vertices, g, iso, 4, 5, 7, 0);
        PolygoniseTri(vertices, g, iso, 0, 2, 5, 7);
    }
    
     public void PolygoniseTri(ArrayList<Vertex>vertices, OcCell g,float iso,int v0,int v1,int v2,int v3)
    {
       int ntri = 0;
       int triindex;

       /*
          Determine which of the 16 cases we have given which vertices
          are above or below the isosurface
       */
       triindex = 0;
       if (g.val[v0] < iso) triindex |= 1;
       if (g.val[v1] < iso) triindex |= 2;
       if (g.val[v2] < iso) triindex |= 4;
       if (g.val[v3] < iso) triindex |= 8;

       /* Form the vertices of the triangles for each case */
           Triangle tri = new Triangle();
           Triangle tri2 = new Triangle();

       switch (triindex) {
       case 0x00:
       case 0x0F:
          break;
       case 0x0E:
       case 0x01:
           tri.p[0] = VertexInterp(iso,g.p[v0],g.p[v1],g.val[v0],g.val[v1]);
           tri.p[1] = VertexInterp(iso,g.p[v0],g.p[v2],g.val[v0],g.val[v2]);
           tri.p[2] = VertexInterp(iso,g.p[v0],g.p[v3],g.val[v0],g.val[v3]);
           vertices.add(tri.p[0]); vertices.add(tri.p[1]); vertices.add(tri.p[2]);
          break;
       case 0x0D:
       case 0x02:
          tri.p[0] = VertexInterp(iso,g.p[v1],g.p[v0],g.val[v1],g.val[v0]);
          tri.p[1] = VertexInterp(iso,g.p[v1],g.p[v3],g.val[v1],g.val[v3]);
          tri.p[2] = VertexInterp(iso,g.p[v1],g.p[v2],g.val[v1],g.val[v2]);
           vertices.add(tri.p[0]); vertices.add(tri.p[1]); vertices.add(tri.p[2]);
          break;
       case 0x0C:
       case 0x03:
          tri.p[0] = VertexInterp(iso,g.p[v0],g.p[v3],g.val[v0],g.val[v3]);
          tri.p[1] = VertexInterp(iso,g.p[v0],g.p[v2],g.val[v0],g.val[v2]);
          tri.p[2] = VertexInterp(iso,g.p[v1],g.p[v3],g.val[v1],g.val[v3]);
           vertices.add(tri.p[0]); vertices.add(tri.p[1]); vertices.add(tri.p[2]);

          tri2.p[0] = tri.p[2];
          tri2.p[1] = VertexInterp(iso,g.p[v1],g.p[v2],g.val[v1],g.val[v2]);
          tri2.p[2] = tri.p[1];
           vertices.add(tri2.p[0]); vertices.add(tri2.p[1]); vertices.add(tri2.p[2]);
          break;
       case 0x0B:
       case 0x04:
          tri.p[0] = VertexInterp(iso,g.p[v2],g.p[v0],g.val[v2],g.val[v0]);
          tri.p[1] = VertexInterp(iso,g.p[v2],g.p[v1],g.val[v2],g.val[v1]);
          tri.p[2] = VertexInterp(iso,g.p[v2],g.p[v3],g.val[v2],g.val[v3]);
           vertices.add(tri.p[0]); vertices.add(tri.p[1]); vertices.add(tri.p[2]);
          break;
       case 0x0A:
       case 0x05:
          tri.p[0] = VertexInterp(iso,g.p[v0],g.p[v1],g.val[v0],g.val[v1]);
          tri.p[1] = VertexInterp(iso,g.p[v2],g.p[v3],g.val[v2],g.val[v3]);
          tri.p[2] = VertexInterp(iso,g.p[v0],g.p[v3],g.val[v0],g.val[v3]);
           vertices.add(tri.p[0]); vertices.add(tri.p[1]); vertices.add(tri.p[2]);

          tri2.p[0] = tri.p[0];
          tri2.p[1] = VertexInterp(iso,g.p[v1],g.p[v2],g.val[v1],g.val[v2]);
          tri2.p[2] = tri.p[1];
           vertices.add(tri2.p[0]); vertices.add(tri2.p[1]); vertices.add(tri2.p[2]);
          break;
       case 0x09:
       case 0x06:
          tri.p[0] = VertexInterp(iso,g.p[v0],g.p[v1],g.val[v0],g.val[v1]);
          tri.p[1] = VertexInterp(iso,g.p[v1],g.p[v3],g.val[v1],g.val[v3]);
          tri.p[2] = VertexInterp(iso,g.p[v2],g.p[v3],g.val[v2],g.val[v3]);
           vertices.add(tri.p[0]); vertices.add(tri.p[1]); vertices.add(tri.p[2]);

           tri2.p[0] = tri.p[0];
          tri2.p[1] = VertexInterp(iso,g.p[v0],g.p[v2],g.val[v0],g.val[v2]);
          tri2.p[2] = tri.p[2];
           vertices.add(tri2.p[0]); vertices.add(tri2.p[1]); vertices.add(tri2.p[2]);
          break;
       case 0x07:
       case 0x08:
          tri.p[0] = VertexInterp(iso,g.p[v3],g.p[v0],g.val[v3],g.val[v0]);
          tri.p[1] = VertexInterp(iso,g.p[v3],g.p[v2],g.val[v3],g.val[v2]);
          tri.p[2] = VertexInterp(iso,g.p[v3],g.p[v1],g.val[v3],g.val[v1]);
            vertices.add(tri.p[0]); vertices.add(tri.p[1]); vertices.add(tri.p[2]);
          break;
       }

    }
}
