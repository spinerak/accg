package csgbuilder;

/**
 *
 * @author s031407
 */
public class Types {

}
    class Vertex implements java.io.Serializable {
        public float x;
        public float y;
        public float z;
        
        public Vertex() {
            x = y = z = 0;
        }
        
        public Vertex(float px, float py, float pz) {
            x = px;
            y = py;
            z = pz;
        }
        
        

		public Vertex(double px, double py, double pz) {
            x = (float)px;
            y = (float)py;
            z = (float)pz;
		}
		
		public void normalize() {
            float l = length();
			x = x / l;
			y = y / l;
			z = z / l;
		}
        
        public Vertex clone() {
            return new Vertex(x, y, z);
        }
		
		public void subtract(Vertex pvVertex) {
			x = x - pvVertex.x;
			y = y - pvVertex.y;
			z = z - pvVertex.z;
		}

		public float length() {
			return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
		}
        
        public float dot(Vertex v) {
            return x * v.x + y * v.y + z * v.z;
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
    
    class Cube {
        public Vertex p;
        
        public Vertex dim;
        
        public Cube() {
            p = new Vertex();
            dim = new Vertex();
        }
    }

    class GridCell {
        public Vertex[] p = new Vertex[8];
        public float[] val = new float[8];

        public GridCell () {
			for (int i = 0; i < 8; i++) {
				p[i] = new Vertex();
			}
		}
    }
    
    class BoundingBox implements java.io.Serializable {
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