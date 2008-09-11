package csgbuilder;

/**
 *
 * @author s040379
 */
public class CSGTree {
    private CSGTreeElement root;
    
    public CSGTree(CSGTreeElement root) {
        this.root = root;
    }
    
    public void union(CSGTreeElement object) {
        root = new CSGTreeUnion(root, object);
        normalize();
        prune();
    }
    
    public void intersect(CSGTreeElement object) {
        root = new CSGTreeIntersection(root, object);
        normalize();
        prune();        
    }
    
    public void difference(CSGTreeElement object) {
        root = new CSGTreeDifference(root, object);
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
    private void normalize() {
        // TODO
    }
    
    /**
     *  Applies the following transformations on the CSGTree:
     * (1) A /\ B -> [] if !A.intersects(B)
     * (2) A - B  ->  A if !A.intersects(B)
     * See: T. F. Wiegand, Interactive Rendering of CSG Models.
     */
    private void prune() {
        // TODO
    }
}