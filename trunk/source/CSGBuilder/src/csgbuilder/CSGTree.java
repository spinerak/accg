package csgbuilder;

/**
 *
 * @author s040379
 */
public class CSGTree {    
    private CSGTreeElement root;
    
    public void insert(CSGTreeOperations operator, CSGTreeElement object) {
        root = new CSGTreeNode(operator, root, object);
        normalize();
        prune();
    }
    
    /**
     * Applies the following transformations on the CSGTree:
     * (1) X -(Y \/ Z)   = (X - Y) - Z
     * (2) X /\ (Y \/ Z) = (X /\ Y) \/ (X /\ Z)
     * (3) X - (Y /\ Z)  = (X - Y) \/ (X - Z)
     * (4) X /\ (Y /\ Z) = (X /\ Y) /\ Z
     * (5) X - (Y - Z)   = (X - Y) \/ (X /\ Z)
     * (6) X /\ (Y - Z)  = (X /\ Y) - Z
     * (7) (X - Y) /\ Z  = (X /\ Z) - Y
     * (8) (X \/ Y) - Z  = (X - Z) \/ (Y - Z)
     * (9) (X \/ Y) /\ Z = (X /\ Z) \/ (Y /\ Z)
     * See: T. F. Wiegand, Interactive Rendering of CSG Models.
     */
    protected void normalize() {
        // TODO
    }
    
    /**
     *  Applies the following transformations on the CSGTree:
     * (1) A /\ B -> [] if !A.intersects(B)
     * (2) A - B  ->  A if !A.intersects(B)
     * See: T. F. Wiegand, Interactive Rendering of CSG Models.
     */
    protected void prune() {
        // TODO
    }
}

interface CSGTreeElement {
    public BoundingBox getBoundingBox(CSGTreeElement left, CSGTreeElement right);
    public double getFunctionValue(double x, double y, double z);
}

enum CSGTreeOperations {    
    UNION {
        public BoundingBox getBoundingBox(CSGTreeElement left, CSGTreeElement right) {
            // TODO
            return new BoundingBox();
        }
        
        public double getFunctionValue(CSGTreeElement left, CSGTreeElement right, 
                                       double x, double y, double z) {
            return Math.min(left.getFunctionValue(x, y, z), 
                            right.getFunctionValue(x,y,z));
        }
    },
    
    DIFFERENCE {
        public BoundingBox getBoundingBox(CSGTreeElement left, CSGTreeElement right) {
            // TODO
            return new BoundingBox();
        }
        
        public double getFunctionValue(CSGTreeElement left, CSGTreeElement right, 
                                       double x, double y, double z) {
            return Math.max(left.getFunctionValue(x, y, z),
                            -right.getFunctionValue(x,y,z));
        }
    },
    
    INTERSECTION {
        public BoundingBox getBoundingBox(CSGTreeElement left, CSGTreeElement right) {
            // TODO
            return new BoundingBox();
        }
        
        public double getFunctionValue(CSGTreeElement left, CSGTreeElement right, 
                                       double x, double y, double z) {
            return Math.max(left.getFunctionValue(x, y, z),
                            right.getFunctionValue(x,y,z));
        }
    };
    
    public abstract BoundingBox getBoundingBox(CSGTreeElement left, CSGTreeElement right);
    public abstract double getFunctionValue(CSGTreeElement left, CSGTreeElement right, 
                                            double x, double y, double z);
}

class CSGTreeNode implements CSGTreeElement {
    private CSGTreeOperations operator;
    private CSGTreeElement left;
    private CSGTreeElement right;
    
    public CSGTreeNode(CSGTreeOperations operation, CSGTreeElement left, CSGTreeElement right) {
        this.operator = operation;
    }
    
    public double getFunctionValue(double x, double y, double z) {
        return operator.getFunctionValue(left, right, x, y, z);
    }
    
    public BoundingBox getBoundingBox(CSGTreeElement left, CSGTreeElement right) {
        return operator.getBoundingBox(left, right);
    }
    
    public CSGTreeOperations getOperation() {
        return operator;
    }
}

abstract class CSGObject implements CSGTreeElement {
    protected double[] pos  = new double[3];
    protected double[] size = new double[3];
    
    public CSGObject(double[] pos, double[] size) {
        this.pos  = pos;
        this.size = size;
    }
    
    public abstract BoundingBox getBoundingBox();
    public abstract double getFuntionValue(double x, double y, double z);
}

abstract class CSGSphere extends CSGObject {
    public CSGSphere(double[] pos, double r) {
        super(pos, new double[]{r,r,r});
    }
    
    public BoundingBox getBoundingBox() {
        BoundingBox box = new BoundingBox();
        // TODO
        return box;
    }
    
    public double getFunctionValue(double x, double y, double z) {
        return Math.pow(pos[0] + x, 2) + 
               Math.pow(pos[1] + y, 2) +
               Math.pow(pos[2] + z, 2) - 
               Math.pow(size[0], 2);
    }
}