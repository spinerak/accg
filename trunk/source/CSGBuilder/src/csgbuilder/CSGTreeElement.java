package csgbuilder;

public interface CSGTreeElement {
    public BoundingBox getBoundingBox();
    public double getFunctionValue(double x, double y, double z);
    @Override public String toString();
}

abstract class CSGTreeOperation implements CSGTreeElement {
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
        
        box.p[4].x = Math.min(left.getBoundingBox().p[4].x, right.getBoundingBox().p[4].x);
        box.p[4].y = Math.min(left.getBoundingBox().p[4].y, right.getBoundingBox().p[4].y);
        box.p[4].z = Math.max(left.getBoundingBox().p[4].z, right.getBoundingBox().p[4].z);

        box.p[5].x = Math.max(left.getBoundingBox().p[5].x, right.getBoundingBox().p[5].x);
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
        return "UNION(" + right.toString() + "," + left.toString() + ")";
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
        
        box.p[0].x = Math.max(left.getBoundingBox().p[0].x, right.getBoundingBox().p[0].x);
        box.p[0].y = Math.max(left.getBoundingBox().p[0].y, right.getBoundingBox().p[0].y);
        box.p[0].z = Math.min(left.getBoundingBox().p[0].z, right.getBoundingBox().p[0].z);

        box.p[1].x = Math.min(left.getBoundingBox().p[1].x, right.getBoundingBox().p[1].x);
        box.p[1].y = Math.max(left.getBoundingBox().p[1].y, right.getBoundingBox().p[1].y);
        box.p[1].z = Math.min(left.getBoundingBox().p[1].z, right.getBoundingBox().p[1].z);   

        box.p[2].x = Math.min(left.getBoundingBox().p[2].x, right.getBoundingBox().p[2].x);
        box.p[2].y = Math.min(left.getBoundingBox().p[2].y, right.getBoundingBox().p[2].y);
        box.p[2].z = Math.min(left.getBoundingBox().p[2].z, right.getBoundingBox().p[2].z);  

        box.p[3].x = Math.max(left.getBoundingBox().p[3].x, right.getBoundingBox().p[3].x);
        box.p[3].y = Math.min(left.getBoundingBox().p[3].y, right.getBoundingBox().p[3].y);
        box.p[3].z = Math.min(left.getBoundingBox().p[3].z, right.getBoundingBox().p[3].z);  
        
        box.p[4].x = Math.max(left.getBoundingBox().p[4].x, right.getBoundingBox().p[4].x);
        box.p[4].y = Math.max(left.getBoundingBox().p[4].y, right.getBoundingBox().p[4].y);
        box.p[4].z = Math.max(left.getBoundingBox().p[4].z, right.getBoundingBox().p[4].z);

        box.p[5].x = Math.min(left.getBoundingBox().p[5].x, right.getBoundingBox().p[5].x);
        box.p[5].y = Math.max(left.getBoundingBox().p[5].y, right.getBoundingBox().p[5].y);
        box.p[5].z = Math.max(left.getBoundingBox().p[5].z, right.getBoundingBox().p[5].z);   

        box.p[6].x = Math.min(left.getBoundingBox().p[6].x, right.getBoundingBox().p[6].x);
        box.p[6].y = Math.min(left.getBoundingBox().p[6].y, right.getBoundingBox().p[6].y);
        box.p[6].z = Math.max(left.getBoundingBox().p[6].z, right.getBoundingBox().p[6].z);  

        box.p[7].x = Math.max(left.getBoundingBox().p[7].x, right.getBoundingBox().p[7].x);
        box.p[7].y = Math.min(left.getBoundingBox().p[7].y, right.getBoundingBox().p[7].y);
        box.p[7].z = Math.max(left.getBoundingBox().p[7].z, right.getBoundingBox().p[7].z);  
        
        return box;
    }
        
    public double getFunctionValue(double x, double y, double z) {
        return Math.max(left.getFunctionValue(x, y, z), 
                        right.getFunctionValue(x,y,z));
    }
    
    @Override public String toString() {
        return "INTERSECTION(" + right.toString() + "," + left.toString() + ")";
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
                        right.getFunctionValue(x,y,z));
    }
    
    @Override public String toString() {
        return "DIFFERENCE(" + right.toString() + "," + left.toString() + ")";
    }
}

abstract class CSGObject implements CSGTreeElement {
    protected double[] pos  = new double[3];
    protected double[] size = new double[3];
    
    public CSGObject(double[] pos, double[] size) {
        this.pos  = pos;
        this.size = size;
    }
    
    public BoundingBox getBoundingBox() {
        BoundingBox box = new BoundingBox();
        box.p[0] = new Vertex(pos[0] - size[0], pos[1] - size[1], pos[2] - size[2]);
        box.p[1] = new Vertex(pos[0] + size[0], pos[1] - size[1], pos[2] - size[2]);
        box.p[2] = new Vertex(pos[0] + size[0], pos[1] + size[1], pos[2] - size[2]);
        box.p[3] = new Vertex(pos[0] - size[0], pos[1] + size[1], pos[2] - size[2]);
        box.p[4] = new Vertex(pos[0] - size[0], pos[1] - size[1], pos[2] + size[2]);
        box.p[5] = new Vertex(pos[0] + size[0], pos[1] - size[1], pos[2] + size[2]);
        box.p[6] = new Vertex(pos[0] + size[0], pos[1] + size[1], pos[2] + size[2]);
        box.p[7] = new Vertex(pos[0] - size[0], pos[1] + size[1], pos[2] + size[2]);
        return box;
    }
    
    public abstract double getFunctionValue(double x, double y, double z);
    @Override public abstract String toString();
}

class CSGEllipsoid extends CSGObject {
    public CSGEllipsoid(double[] pos, double[] size) {
        super(pos, size);
    }
    
    public double getFunctionValue(double x, double y, double z) {
        return Math.pow(pos[0] + x / size[0], 2) + 
               Math.pow(pos[1] + y / size[1], 2) +
               Math.pow(pos[2] + z / size[2], 2) - 1;
    }
    
    public String toString() {
        return "ELIPSOID(" + 
                pos[0] + "," +
                pos[1] + "," +
                pos[2] + "," + 
                size[0] + "," + 
                size[1] + "," + 
                size[2] + ")";
    }
}

class CSGCubeoid extends CSGObject {
    public CSGCubeoid(double[] pos, double[] size) {
        super(pos, size);
    }
    
    public double getFunctionValue(double x, double y, double z) {
        return Math.pow(pos[0] + x / size[0], 9) + 
               Math.pow(pos[1] + y / size[1], 9) +
               Math.pow(pos[2] + z / size[2], 9) - 1;
    }
    
    public String toString() {
        return "CSGCUBEOID(" + 
                pos[0] + "," +
                pos[1] + "," +
                pos[2] + "," + 
                size[0] + "," + 
                size[1] + "," + 
                size[2] + ")";
    }
}