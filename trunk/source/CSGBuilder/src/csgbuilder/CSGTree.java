package csgbuilder;

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
    }
    
    public void intersect(CSGTreeElement object) {
        root = new CSGTreeIntersection(root, object);
        normalize(root);
    }
    
    public void difference(CSGTreeElement object) {
        root = new CSGTreeDifference(root, object);
        normalize(root);
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
    
    public CSGTreeElement clone() {
        return new CSGTree(this.root.clone());
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
        if (!(element instanceof CSGTreeOperation)) {
	    // return if element is a primitive.
	    return;
	}
	
	CSGTreeOperation node = (CSGTreeOperation)element;
	
	do {
	    boolean changed;
	    
	    do {
		changed = false;
		
		if (node.right instanceof CSGTreeUnion) {
		    if (node instanceof CSGTreeDifference) {
			// 1. X - (Y \/ Z) -> (X - Y) - Z
			CSGTreeDifference newLeft  = new CSGTreeDifference(node.left, ((CSGTreeOperation)node.right).left);
			CSGTreeElement    newRight = ((CSGTreeOperation)node.right).right;
			node = new CSGTreeDifference(newLeft, newRight);
			changed = true;			
		    } 
		    else if (node instanceof CSGTreeIntersection) {
			// 2. X /\ (Y \/ Z) -> (X /\ Y) \/ (X /\ Z)
			CSGTreeIntersection newLeft  = new CSGTreeIntersection(node.left, ((CSGTreeOperation)node.right).left);
			CSGTreeIntersection newRight = new CSGTreeIntersection(node.left, ((CSGTreeOperation)node.right).right);
			node = new CSGTreeUnion(newLeft, newRight);
			changed = true;
		    }
		}		
		else if (node.right instanceof CSGTreeIntersection) {
		    if (node instanceof CSGTreeDifference) {
			// 3. X - (Y /\ Z) -> (X - Y) \/ (X - Z)
			CSGTreeDifference newLeft  = new CSGTreeDifference(node.left, ((CSGTreeOperation)node.right).left);
			CSGTreeDifference newRight = new CSGTreeDifference(node.left, ((CSGTreeOperation)node.right).right);
			node = new CSGTreeUnion(newLeft, newRight);
			changed = true;
		    } 
		    else if (node instanceof CSGTreeIntersection) {
			// 4. X /\ (Y /\ Z) -> (X /\ Y) /\ Z
			CSGTreeIntersection newLeft  = new CSGTreeIntersection(node.left, ((CSGTreeOperation)node.right).left);
			CSGTreeElement      newRight = ((CSGTreeOperation)node.right).right;
			node = new CSGTreeIntersection(newLeft, newRight);
			changed = true;
		    }
		}
		else if (node.right instanceof CSGTreeDifference) {
		    if (node instanceof CSGTreeDifference) {
			// 5. X - (Y - Z) -> (X - Y) \/ (X /\ Z)
			CSGTreeDifference   newLeft  = new CSGTreeDifference(node.left, ((CSGTreeOperation)node.right).left);
			CSGTreeIntersection newRight = new CSGTreeIntersection(node.left, ((CSGTreeOperation)node.right).right);
			node = new CSGTreeUnion(newLeft, newRight);
			changed = true;
		    } 
		    else if (node instanceof CSGTreeIntersection) {
			// 6. X /\ (Y - Z) -> (X /\ Y) - Z
			CSGTreeIntersection newLeft  = new CSGTreeIntersection(node.left, ((CSGTreeOperation)node.right).left);
			CSGTreeElement      newRight = ((CSGTreeOperation)node.right).right;
			node = new CSGTreeDifference(newLeft, newRight);
			changed = true;
		    }		    
		}
		else if (node.left instanceof CSGTreeDifference) {
		    if (node instanceof CSGTreeIntersection) {
			// 7. (X - Y) /\ Z -> (X /\ Z) - Y
			CSGTreeIntersection newLeft  = new CSGTreeIntersection(node.left, ((CSGTreeOperation)node.right).left);
			CSGTreeElement      newRight = ((CSGTreeIntersection)node.right).right;
			node = new CSGTreeDifference(newLeft, newRight);
			changed = true;
		    }
		}
		else if (node.left instanceof CSGTreeIntersection) {
		    if (node instanceof CSGTreeDifference) {
			// 8. (X \/ Y) - Z -> (X - Z) \/ (Y - Z)
			CSGTreeDifference newLeft  = new CSGTreeDifference(node.left, ((CSGTreeOperation)node.right).right);
			CSGTreeDifference newRight = new CSGTreeDifference(((CSGTreeOperation)node.right).left, ((CSGTreeOperation)node.right).right);
			node = new CSGTreeUnion(newLeft, newRight);
			changed = true;
		    }
		    else if (node instanceof CSGTreeIntersection) {
			// 9. (X \/ Y) /\ Z -> (X /\ Z) \/(Y /\ Z)
			CSGTreeIntersection newLeft  = new CSGTreeIntersection(node.left,((CSGTreeOperation)node.right).right);
			CSGTreeIntersection newRight = new CSGTreeIntersection(((CSGTreeOperation)node.right).left,((CSGTreeOperation)node.right).right);
			node = new CSGTreeUnion(newLeft, newRight);
			changed = true;
		    }
		    
		    prune(node);
		}
	    }
	    while (changed);
	    
	    normalize(node.left);
	}
	while(!(node instanceof CSGTreeUnion) &&
	       (!(node.right instanceof CSGTreeOperation) ||
	         (node.left  instanceof CSGTreeUnion)));
    }
    
    /**
     *  Applies the following transformations on the CSGTree:
     * (1) A /\ B -> [] if !A.intersects(B)
     * (2) A - B  ->  A if !A.intersects(B)
     * See: T. F. Wiegand, Interactive Rendering of CSG Models.
     */
    private void prune(CSGTreeElement element) {
	if (!(element instanceof CSGTreeOperation)) {
	    // Return if element is a primitive.
	    return;
	}
	
	CSGTreeOperation node = (CSGTreeOperation)element;
	
	if (node instanceof CSGTreeIntersection) {
	    if (!node.left.getBoundingBox().intersects(node.right.getBoundingBox())) {
		// TODO: null is incorrect, change!
		// element = null;
	    }
	}
	else if (node instanceof CSGTreeDifference) {
	    if (!node.left.getBoundingBox().intersects(node.right.getBoundingBox())) {
		element = node.left;
	    }
	}
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