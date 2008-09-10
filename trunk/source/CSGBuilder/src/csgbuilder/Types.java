package csgbuilder;

/**
 *
 * @author s031407
 */
public class Types {

}

    

    class Vertex {
        public double x;
        public double y;
        public double z;
        
        public Vertex() {
            x = y = z = 0;
        }
        
        public Vertex(double px, double py, double pz) {
            x = px;
            y = py;
            z = pz;
        }

    }
    class Triangle {
        public Vertex[] p;

        public Triangle() {
            p = new Vertex[3];
            for (int i = 0; i < 3; i++) {
                p[i] = new Vertex();
            }
        }
    }

    class GridCell {
        public Vertex[] p = new Vertex[8];
        public double[] val;

        public GridCell () {
            p = new Vertex[8];
            val = new double[8];
            
            for (int i = 0; i < 8; i++) {
                p[i] = new Vertex();
            }
        }
    }
    
    class BoundingBox {
        public Vertex[] p = new Vertex[8];
        /* p[0] = (0,0,0)
         * p[1] = (1,0,0)
         * p[2] = (1,1,0)
         * p[3] = (0,1,0)
         * p[4] = (0,0,1)
         * p[5] = (1,0,1)
         * p[6] = (1,1,1)
         * p[7] = (0,1,1)
         */
                
        public BoundingBox () {
            for (int i = 0; i < 8; i++) {
                p[i] = new Vertex();
            }
        }
        
        public boolean intersects(BoundingBox b) {
            // TODO
            return true;
        }
    }