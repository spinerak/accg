package csgbuilder;

import java.util.Arrays;

public abstract class CSGTreeElement {
    public abstract BoundingBox getBoundingBox();
    public abstract double getFunctionValue(double x, double y, double z);
    @Override public abstract String toString();
    
    public double approachFunctionValue(double x, double y, double z) {
        BoundingBox box = getBoundingBox();
        
        if ((box.p[7].x <= x) && (x <= box.p[6].x) && 
            (box.p[5].y <= y) && (y <= box.p[7].y) &&
            (box.p[3].z <= z) && (z <= box.p[7].z)) {
            return getFunctionValue(x, y, z);
        }
        else {
            return -1; 
        }
    }
}

abstract class CSGTreeOperation extends CSGTreeElement {
    protected CSGTreeElement left;
    protected CSGTreeElement right;
    
    public abstract BoundingBox getBoundingBox();
    public abstract double getFunctionValue(double x, double y, double z);
    @Override public abstract String toString();
}

class CSGTreeUnion extends CSGTreeOperation {
    public CSGTreeUnion(CSGTreeElement left, CSGTreeElement right) {
        this.left = left;
        this.right = right;
    }
    
    public BoundingBox getBoundingBox() {
        // Bound(A) \/ Bound(B) = Bound(A \/ B)
        BoundingBox box = new BoundingBox();
        
        box.p[0].x = Math.min(left.getBoundingBox().p[0].x, right.getBoundingBox().p[0].x);
        box.p[0].y = Math.min(left.getBoundingBox().p[0].y, right.getBoundingBox().p[0].y);
        box.p[0].z = Math.min(left.getBoundingBox().p[0].z, right.getBoundingBox().p[0].z);

        box.p[1].x = Math.max(left.getBoundingBox().p[1].x, right.getBoundingBox().p[1].x);
        box.p[1].y = Math.min(left.getBoundingBox().p[1].y, right.getBoundingBox().p[1].y);
        box.p[1].z = Math.min(left.getBoundingBox().p[1].z, right.getBoundingBox().p[1].z);   

        box.p[2].x = Math.max(left.getBoundingBox().p[2].x, right.getBoundingBox().p[2].x);
        box.p[2].y = Math.max(left.getBoundingBox().p[2].y, right.getBoundingBox().p[2].y);
        box.p[2].z = Math.min(left.getBoundingBox().p[2].z, right.getBoundingBox().p[2].z);  

        box.p[3].x = Math.min(left.getBoundingBox().p[3].x, right.getBoundingBox().p[3].x);
        box.p[3].y = Math.max(left.getBoundingBox().p[3].y, right.getBoundingBox().p[3].y);
        box.p[3].z = Math.min(left.getBoundingBox().p[3].z, right.getBoundingBox().p[3].z);  
        
        box.p[4].x = Math.max(left.getBoundingBox().p[4].x, right.getBoundingBox().p[4].x);
        box.p[4].y = Math.min(left.getBoundingBox().p[4].y, right.getBoundingBox().p[4].y);
        box.p[4].z = Math.max(left.getBoundingBox().p[4].z, right.getBoundingBox().p[4].z);

        box.p[5].x = Math.min(left.getBoundingBox().p[5].x, right.getBoundingBox().p[5].x);
        box.p[5].y = Math.min(left.getBoundingBox().p[5].y, right.getBoundingBox().p[5].y);
        box.p[5].z = Math.max(left.getBoundingBox().p[5].z, right.getBoundingBox().p[5].z);   

        box.p[6].x = Math.max(left.getBoundingBox().p[6].x, right.getBoundingBox().p[6].x);
        box.p[6].y = Math.max(left.getBoundingBox().p[6].y, right.getBoundingBox().p[6].y);
        box.p[6].z = Math.max(left.getBoundingBox().p[6].z, right.getBoundingBox().p[6].z);  

        box.p[7].x = Math.min(left.getBoundingBox().p[7].x, right.getBoundingBox().p[7].x);
        box.p[7].y = Math.max(left.getBoundingBox().p[7].y, right.getBoundingBox().p[7].y);
        box.p[7].z = Math.max(left.getBoundingBox().p[7].z, right.getBoundingBox().p[7].z);  
        
        return box;
    }
        
    public double getFunctionValue(double x, double y, double z) {
        return Math.min(left.getFunctionValue(x, y, z), 
                        right.getFunctionValue(x,y,z));
    }
    
    @Override public String toString() {
        return "UNION(" + left.toString() + "," + right.toString() + ")";
    }
}

class CSGTreeIntersection extends CSGTreeOperation {
    public CSGTreeIntersection(CSGTreeElement left, CSGTreeElement right) {
        this.left = left;
        this.right = right;
    }
    
    public BoundingBox getBoundingBox() {
        // Bound(A) /\ Bound(B) = Bound(A /\ B)
        BoundingBox box = new BoundingBox();
        
        box.p[0].x = Math.min(left.getBoundingBox().p[0].x, right.getBoundingBox().p[0].x);
        box.p[0].y = Math.max(left.getBoundingBox().p[0].y, right.getBoundingBox().p[0].y);
        box.p[0].z = Math.min(left.getBoundingBox().p[0].z, right.getBoundingBox().p[0].z);

        box.p[1].x = Math.min(left.getBoundingBox().p[1].x, right.getBoundingBox().p[1].x);
        box.p[1].y = Math.max(left.getBoundingBox().p[1].y, right.getBoundingBox().p[1].y);
        box.p[1].z = Math.min(left.getBoundingBox().p[1].z, right.getBoundingBox().p[1].z);   

        box.p[2].x = Math.min(left.getBoundingBox().p[2].x, right.getBoundingBox().p[2].x);
        box.p[2].y = Math.min(left.getBoundingBox().p[2].y, right.getBoundingBox().p[2].y);
        box.p[2].z = Math.min(left.getBoundingBox().p[2].z, right.getBoundingBox().p[2].z);  

        box.p[3].x = Math.min(left.getBoundingBox().p[3].x, right.getBoundingBox().p[3].x);
        box.p[3].y = Math.min(left.getBoundingBox().p[3].y, right.getBoundingBox().p[3].y);
        box.p[3].z = Math.min(left.getBoundingBox().p[3].z, right.getBoundingBox().p[3].z);  
        
        box.p[4].x = Math.min(left.getBoundingBox().p[4].x, right.getBoundingBox().p[4].x);
        box.p[4].y = Math.max(left.getBoundingBox().p[4].y, right.getBoundingBox().p[4].y);
        box.p[4].z = Math.max(left.getBoundingBox().p[4].z, right.getBoundingBox().p[4].z);

        box.p[5].x = Math.min(left.getBoundingBox().p[5].x, right.getBoundingBox().p[5].x);
        box.p[5].y = Math.max(left.getBoundingBox().p[5].y, right.getBoundingBox().p[5].y);
        box.p[5].z = Math.max(left.getBoundingBox().p[5].z, right.getBoundingBox().p[5].z);   

        box.p[6].x = Math.min(left.getBoundingBox().p[6].x, right.getBoundingBox().p[6].x);
        box.p[6].y = Math.min(left.getBoundingBox().p[6].y, right.getBoundingBox().p[6].y);
        box.p[6].z = Math.max(left.getBoundingBox().p[6].z, right.getBoundingBox().p[6].z);  
        
        box.p[7].x = Math.min(left.getBoundingBox().p[7].x, right.getBoundingBox().p[7].x);
        box.p[7].y = Math.min(left.getBoundingBox().p[7].y, right.getBoundingBox().p[7].y);
        box.p[7].z = Math.max(left.getBoundingBox().p[7].z, right.getBoundingBox().p[7].z);  
        
        return box;
    }
        
    public double getFunctionValue(double x, double y, double z) {
        return Math.max(left.getFunctionValue(x, y, z), 
                        right.getFunctionValue(x,y,z));
    }
    
    @Override public String toString() {
        return "INTERSECTION(" + left.toString() + "," + right.toString() + ")";
    }
}

class CSGTreeDifference extends CSGTreeOperation {
    public CSGTreeDifference(CSGTreeElement left, CSGTreeElement right) {
        this.left = left;
        this.right = right;
    }
    
    public BoundingBox getBoundingBox() {
        return left.getBoundingBox();
    }
        
    public double getFunctionValue(double x, double y, double z) {
        return Math.max(left.getFunctionValue(x, y, z), 
                        -right.getFunctionValue(x, y, z));
    }
    
    @Override public String toString() {
        return "DIFFERENCE(" + left.toString() + "," + right.toString() + ")";
    }
}

abstract class CSGObject extends CSGTreeElement {
    protected double[] pos  = new double[3];
    protected double[] size = new double[3];
    protected double[] rot = new double[3];
    protected BoundingBox box;
    
    public CSGObject(double[] pos, double[] size, double[] rot) {
        this.pos  = pos;
        this.size = size;
        this.rot  = rot;
    }
    
    protected Vertex getRotatedVertex(Vertex v) {
        float cx = (float)Math.cos(rot[0]); float sx = (float)Math.sin(rot[0]);
        float cy = (float)Math.cos(rot[1]); float sy = (float)Math.sin(rot[1]);
        float cz = (float)Math.cos(rot[2]); float sz = (float)Math.sin(rot[2]);
        
        Vertex r = new Vertex();
        r.x = v.x*cy*cz + v.y*cy*sz - v.z*sy;
        r.y = v.x*(sx*sy*cz - cx*sz) + v.y*(sx*sy*sz + cx*cy) + v.z*sx*cy;
        r.z = v.x*(cx*sy*cz + sx*sz) + v.y*(cx*sy*sz - sx*cz) + v.z*cx*cy;

//        r.x = v.x;
//        r.y = v.y*cx + v.z*sx;
//        r.z = v.y*-sx + v.z*cx;
        
//        r.x = v.x*cy + v.z*-sy;
//        r.y = v.y;
//        r.z = v.x*sy + v.z*cy;

//        r.x = v.x*cz  + v.y*sz;
//        r.y = v.x*-sz + v.y*cz;
//        r.z = v.z;
        
        return r;
    }
    
    private void computeBoundingBox() {
        Vertex p0 = new Vertex(-size[0], -size[1], -size[2]);
        Vertex p1 = new Vertex(size[0],  -size[1], -size[2]);
        Vertex p2 = new Vertex(size[0],  size[1],  -size[2]);
        Vertex p3 = new Vertex(-size[0], size[1],  -size[2]);
        Vertex p4 = new Vertex(-size[0], -size[1], size[2]);
        Vertex p5 = new Vertex(size[0],  -size[1], size[2]);
        Vertex p6 = new Vertex(size[0],  size[1],  size[2]);
        Vertex p7 = new Vertex(-size[0],  size[1],  size[2]);
        
        Vertex p0R = getRotatedVertex(p0);
        Vertex p1R = getRotatedVertex(p1);
        Vertex p2R = getRotatedVertex(p2);
        Vertex p3R = getRotatedVertex(p3);
        Vertex p4R = getRotatedVertex(p4);
        Vertex p5R = getRotatedVertex(p5);
        Vertex p6R = getRotatedVertex(p6);
        Vertex p7R = getRotatedVertex(p7);

        float[] xs = {p0R.x, p1R.x, p2R.x, p3R.x, p4R.x, p5R.x, p6R.x, p7R.x};
        float[] ys = {p0R.y, p1R.y, p2R.y, p3R.y, p4R.y, p5R.y, p6R.y, p7R.y};
        float[] zs = {p0R.z, p1R.z, p2R.z, p3R.z, p4R.z, p5R.z, p6R.z, p7R.z};
        
        Arrays.sort(xs);
        Arrays.sort(ys);
        Arrays.sort(zs);
        
        float minX = xs[0] - (float)pos[0]; float maxX = xs[xs.length - 1] - (float)pos[0];
        float minY = ys[0] - (float)pos[1]; float maxY = ys[ys.length - 1] - (float)pos[1];
        float minZ = zs[0] - (float)pos[2]; float maxZ = zs[zs.length - 1] - (float)pos[2];
        
        box = new BoundingBox();   
        box.p[0] = new Vertex(minX, minY, minZ);        
        box.p[1] = new Vertex(maxX, minY, minZ);
        box.p[2] = new Vertex(maxX, maxY, minZ);
        box.p[3] = new Vertex(minX, maxY, minZ);
        box.p[4] = new Vertex(maxX, minY, maxZ);
        box.p[5] = new Vertex(minX, minY, maxZ);
        box.p[6] = new Vertex(maxX, maxY, maxZ);
        box.p[7] = new Vertex(minX, maxY, maxZ);
     }
    
    public BoundingBox getBoundingBox() {
        if (box == null) {
            computeBoundingBox();
        }
        
        return box;
    }
    
    public abstract double getFunctionValue(double x, double y, double z);
    @Override public abstract String toString();
}

class CSGEllipsoid extends CSGObject {
    public CSGEllipsoid(double[] pos, double[] size) {
		super(pos, size, new double[]{0.0, 0.0, 0.0});
	}
	
	public CSGEllipsoid(double[] pos, double[] size, double[] rot) {
        super(pos, size, rot);
    }
    
    public double getFunctionValue(double x, double y, double z) {
        return Math.pow((pos[0] + x) / size[0], 2) + 
               Math.pow((pos[1] + y) / size[1], 2) +
               Math.pow((pos[2] + z) / size[2], 2) - 1;
    }
    
    public String toString() {
        return "ELIPSOID(" + 
                pos[0] + "," +
                pos[1] + "," +
                pos[2] + "," + 
                size[0] + "," + 
                size[1] + "," + 
                size[2] + "," +
                rot[0] + "," +
                rot[1] + "," + 
                rot[2] + ")";
    }
}

class CSGCuboid extends CSGObject {
    public CSGCuboid(double[] pos, double[] size, double[] rot) {
        super(pos, size, rot);
    }
    
    public double getFunctionValue(double x, double y, double z) {
        Vertex v = getRotatedVertex(new Vertex(pos[0] + x, pos[1] + y, pos[2] + z));
        
        return Math.pow(v.x / size[0], 4) + 
               Math.pow(v.y / size[1], 4) +
               Math.pow(v.z / size[2], 4) - 1;
    }
    
    public String toString() {
        return "CSGCUBEOID(" + 
                pos[0] + "," +
                pos[1] + "," +
                pos[2] + "," + 
                size[0] + "," + 
                size[1] + "," + 
                size[2] + "," +
                rot[0] + "," +
                rot[1] + "," + 
                rot[2] + ")";
    }
}