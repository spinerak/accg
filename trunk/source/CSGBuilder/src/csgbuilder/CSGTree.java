package csgbuilder;

/**
 *
 * @author s040379
 */
public class CSGTree implements CSGTreeElement {
    private CSGTreeElement root;
    
    public CSGTree(CSGTreeElement root) {
        this.root = root;
    }
    
    public void union(CSGTreeElement object) {
        root = new CSGTreeUnion(root, object);
        normalize(root);
        prune();
    }
    
    public void intersect(CSGTreeElement object) {
        root = new CSGTreeIntersection(root, object);
        normalize(root);
        prune();        
    }
    
    public void difference(CSGTreeElement object) {
        root = new CSGTreeDifference(root, object);
        normalize(root);
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
    private void normalize(CSGTreeElement element) {  
        /**/
        return;
        
        /*/
        
        if (element instanceof CSGObject) {
            return;
        }
        
        CSGTreeOperation currentNode = (CSGTreeOperation)element;
        boolean transformed;
        
        do {
            transformed = false;
            
            if (currentNode instanceof CSGTreeDifference) {
                if (currentNode.left instanceof CSGTreeUnion) {
                    // (1)
                    CSGTreeOperation node = (CSGTreeOperation)currentNode.left;
                    currentNode.left = new CSGTreeDifference(node.left, node.right);
                }
                else if (currentNode.left instanceof CSGTreeIntersection) {
                    // (3)
                    CSGTreeOperation node = (CSGTreeOperation)currentNode.left;
                    // TODO
                }
            }
            else if (currentNode instanceof CSGTreeIntersection) {
                
            }
            
            if (currentNode.left instanceof CSGTreeOperation) {
                normalize((CSGTreeOperation)currentNode.left);    
            }
            
            if (currentNode.right instanceof CSGTreeOperation) {
                normalize((CSGTreeOperation)currentNode.right);    
            }            
        }
        while(!transformed);
        
        /**/
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
    
    @Override public String toString() {
        return root.toString();
    }
    
    public double getFunctionValue(double x, double y, double z) {
        return root.getFunctionValue(x, y, z);
    }
    
    public BoundingBox getBoundingBox() {
        return root.getBoundingBox();
    }
}