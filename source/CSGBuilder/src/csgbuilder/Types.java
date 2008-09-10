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