package csgbuilder;

import java.io.Serializable;

/**
 *
 * @author s040379
 */
public class CSGTree extends CSGTreeElement {
    protected CSGTreeElement root;
    
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
    
    public boolean isResizable() {
        return root.isResizable();
    }
    
    public boolean isMovable() {
        return root.isMovable();
    }
    
    public boolean isRotatable() {
        return root.isRotatable();
    }
    
    public double[] getRotation() {
        return root.getRotation();
    }
    
    public double[] getPosition() {
        return root.getPosition();
    }
    
    public double[] getDimensions() {
        return root.getDimensions();
    }
    
 
    public void resize(double[] size) {
        root.resize(size);
    }
    
    public void move(double[] pos) {
        root.move(pos);
    }
    
    public void rotate(double[] rot) {
        root.rotate(rot);
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
        // TODO: implement cases (1)..(9)
        
        boolean changed;
        
        do {
            changed = false;
            
            if (element instanceof CSGTreeOperation) {
                CSGTreeOperation node = (CSGTreeOperation)element;
                normalize(node.left);
                normalize(node.right);
            }                

            if (element instanceof CSGTreeDifference) {
                // (1) (3) (5) (8)
                CSGTreeOperation node = (CSGTreeOperation)element;

                if (node.right instanceof CSGTreeUnion) {
                    // (1)
                    CSGTreeOperation right = (CSGTreeOperation)node.right;
                    changed = true;
                }
                else if (node.right instanceof CSGTreeIntersection) {
                    // (3)
                    CSGTreeOperation right = (CSGTreeOperation)node.right;
                    changed = true;
                }
                else if (node.right instanceof CSGTreeDifference) {
                    // (5)
                    CSGTreeOperation right = (CSGTreeOperation)node.right;
                    changed = true;
                }
                else if (node.left instanceof CSGTreeUnion) {
                    // (8)
                    CSGTreeOperation left = (CSGTreeOperation)node.left;
                    changed = true;
                }
            }
            else if (element instanceof CSGTreeIntersection) {
                // (2) (4) (6) (7) (9)
                CSGTreeOperation node = (CSGTreeOperation)element;

                if (node.right instanceof CSGTreeUnion) {
                    // (2)
                    CSGTreeOperation right = (CSGTreeOperation)node.right;
                    changed = true;             
                }
                else if (node.right instanceof CSGTreeIntersection) {
                    // (4)
                    CSGTreeOperation right = (CSGTreeOperation)node.right;
                    changed = true;
                }
                else if (node.right instanceof CSGTreeDifference) {
                    // (6)
                    CSGTreeOperation right = (CSGTreeOperation)node.right;
                    changed = true;
                }
                else if (node.left instanceof CSGTreeDifference) {
                    // (7)
                    CSGTreeOperation left = (CSGTreeOperation)node.left;
                    changed = true;
                }
                else if (node.left instanceof CSGTreeUnion) {
                    // (9)
                    CSGTreeOperation left = (CSGTreeOperation)node.left;
                    changed = true;
                }
            }
        } while (changed);
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
    
    public javax.swing.tree.DefaultMutableTreeNode CSGTree2TreeNode() {
        return root.CSGTree2TreeNode();
    }
}