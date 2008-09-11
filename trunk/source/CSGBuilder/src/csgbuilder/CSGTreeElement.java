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
        // TODO
        return new BoundingBox();
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
        // TODO
        return new BoundingBox();
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
        // TODO
        return new BoundingBox();
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
        box.p[1] = new Vertex(pos[0] + size[0], pos[1] + size[1], pos[2] - size[2]);
        box.p[1] = new Vertex(pos[0] - size[0], pos[1] + size[1], pos[2] - size[2]);
        box.p[0] = new Vertex(pos[0] - size[0], pos[1] - size[1], pos[2] + size[2]);
        box.p[1] = new Vertex(pos[0] + size[0], pos[1] - size[1], pos[2] + size[2]);
        box.p[1] = new Vertex(pos[0] + size[0], pos[1] + size[1], pos[2] + size[2]);
        box.p[1] = new Vertex(pos[0] - size[0], pos[1] + size[1], pos[2] + size[2]);
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
        return "CSGCubeoid(" + 
                pos[0] + "," +
                pos[1] + "," +
                pos[2] + "," + 
                size[0] + "," + 
                size[1] + "," + 
                size[2] + ")";
    }
}