package smoke;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import static smoke.RFFTWLibrary.*;

/** Usage: Drag with the mouse to add smoke to the fluid. This will also move a "rotor" that disturbs
 *        the velocity field at the mouse location. Press the indicated keys to change options
 */
class Smoke {
    static RFFTWLibrary RFFTW = (RFFTWLibrary)Native.loadLibrary("rfftw",RFFTWLibrary.class);
    
//--- SIMULATION PARAMETERS ------------------------------------------------------------------------
    final int DIM = 50;		//size of simulation grid
    double dt = 0.4;		//simulation time step
    double visc = 0.001;	//fluid viscosity
    double/*fftw_real*/ vx [], vy  [];        //(vx,vy)   = velocity field at the current moment
    double/*fftw_real*/ vx0[], vy0 [];        //(vx0,vy0) = velocity field at the previous moment
    double/*fftw_real*/ fx [], fy  [];	//(fx,fy)   = user-controlled simulation forces, steered with the mouse
    double/*fftw_real*/ rho[], rho0[];	//smoke density at the current (rho) and previous (rho0) moment
    /*rfftwnd_plan*/ Pointer plan_rc, plan_cr;  //simulation domain discretization
    
    
//--- VISUALIZATION PARAMETERS ---------------------------------------------------------------------
    int   winWidth, winHeight;     //size of the graphics window, in pixels
    int   color_dir = 0;           //use direction color-coding or not
    float vec_scale = 1000;        //scaling of hedgehogs
    boolean   draw_smoke = false;  //draw the smoke or not
    boolean   draw_vecs = true;    //draw the vector field or not
    final int COLOR_BLACKWHITE=0;  //different types of color mapping: black-and-white, rainbow, banded
    final int COLOR_RAINBOW=1;
    final int COLOR_BANDS=2;
    int   scalar_col = 0;           //method for scalar coloring
    boolean frozen = false;         //toggles on/off the animation
    
    
//------ SIMULATION CODE STARTS HERE -----------------------------------------------------------------
    
    /**init_simulation: Initialize simulation data structures as a function of the grid size 'n'.
     *                 Although the simulation takes place on a 2D grid, we allocate all data structures as 1D arrays,
     *                 for compatibility with the FFTW numerical library.
     */
    void init_simulation(int n) {
        int dim     = n * 2*(n/2+1);              //Allocate data structures
        vx       = new double/*fftw_real*/[dim]; 
        vy       = new double/*fftw_real*/[dim]; 
        vx0      = new double/*fftw_real*/[dim]; 
        vy0      = new double/*fftw_real*/[dim]; 
        for (int i = 0; i < dim; i++)                      //Initialize data structures to 0
        { vx[i] = vy[i] = vx0[i] = vy0[i] =0.0; } // float or double??
        
        dim     = n * n; // * sizeof(double/*fftw_real*/);
        fx      = new double/*fftw_real*/[dim]; 
        fy      = new double/*fftw_real*/[dim]; 
        rho     = new double/*fftw_real*/[dim]; 
        rho0    = new double/*fftw_real*/[dim]; 
        plan_rc = RFFTW.rfftw2d_create_plan(n, n, FFTW_REAL_TO_COMPLEX, FFTW_IN_PLACE);
        plan_cr = RFFTW.rfftw2d_create_plan(n, n, FFTW_COMPLEX_TO_REAL, FFTW_IN_PLACE);
        
        for (int i = 0; i < dim; i++)               //Initialize data structures to 0
        { fx[i] = fy[i] = rho[i] = rho0[i] = 0.0; } // float or double??
    }
    
    
//FFT: Execute the Fast Fourier Transform on the dataset 'vx'.
//     'direction' indicates if we do the direct (1) or inverse (-1) Fourier Transform
    void FFT(int direction,double/*fftw_real*/[] vx) {
        if(direction==1) RFFTW.rfftwnd_one_real_to_complex_in_place(plan_rc,(double[])vx);
        else             RFFTW.rfftwnd_one_complex_to_real_in_place(plan_cr,(double[])vx);
    }
    
    int clamp(double x) {
        return ((x)>=0.0?((int)(x)):(-((int)(1-(x))))); }
    
//solve: Solve (compute) one step of the fluid flow simulation
    void solve(int n, double/*fftw_real*/[] vx, double/*fftw_real*/[] vy, double/*fftw_real*/[] vx0, double/*fftw_real*/[] vy0, double/*fftw_real*/ visc, double dt) {
        double/*fftw_real*/ x, y, x0, y0, f, r, U[]=new double/*fftw_real*/[2], V[]=new double/*fftw_real*/[2], s, t;
        int i, j, i0, j0, i1, j1;
        
        for (i=0;i<n*n;i++) {
            vx[i] += dt*vx0[i]; vx0[i] = vx[i]; vy[i] += dt*vy0[i]; vy0[i] = vy[i]; }
        
        for ( x=0.5/n,i=0 ; i<n ; i++,x+=1.0/n ) {
            for ( y=0.5/n,j=0 ; j<n ; j++,y+=1.0/n ) {
                x0 = n*(x-dt*vx0[i+n*j])-0.5f;
                y0 = n*(y-dt*vy0[i+n*j])-0.5f;
                i0 = clamp(x0); s = x0-i0;
                i0 = (n+(i0%n))%n;
                i1 = (i0+1)%n;
                j0 = clamp(y0); t = y0-j0;
                j0 = (n+(j0%n))%n;
                j1 = (j0+1)%n;
                vx[i+n*j] = (1-s)*((1-t)*vx0[i0+n*j0]+t*vx0[i0+n*j1])+s*((1-t)*vx0[i1+n*j0]+t*vx0[i1+n*j1]);
                vy[i+n*j] = (1-s)*((1-t)*vy0[i0+n*j0]+t*vy0[i0+n*j1])+s*((1-t)*vy0[i1+n*j0]+t*vy0[i1+n*j1]);
            }
        }
        
        for(i=0; i<n; i++) {
            for(j=0; j<n; j++) {
                vx0[i+(n+2)*j] = vx[i+n*j]; vy0[i+(n+2)*j] = vy[i+n*j]; }
        }
        
        FFT(1,vx0);
        FFT(1,vy0);
        
        for (i=0;i<=n;i+=2) {
            x = 0.5*i;
            for (j=0;j<n;j++) {
                y = j<=n/2 ? (double)j : (double)(j-n);
                r = x*x+y*y;
                if ( r==0.0f ) continue;
                f = Math.exp(-r*dt*visc);
                U[0] = vx0[i  +(n+2)*j]; V[0] = vy0[i  +(n+2)*j];
                U[1] = vx0[i+1+(n+2)*j]; V[1] = vy0[i+1+(n+2)*j];
                
                vx0[i  +(n+2)*j] = f*((1-x*x/r)*U[0]     -x*y/r *V[0]);
                vx0[i+1+(n+2)*j] = f*((1-x*x/r)*U[1]     -x*y/r *V[1]);
                vy0[i+  (n+2)*j] = f*(  -y*x/r *U[0] + (1-y*y/r)*V[0]);
                vy0[i+1+(n+2)*j] = f*(  -y*x/r *U[1] + (1-y*y/r)*V[1]);
            }
        }
        
        FFT(-1,vx0);
        FFT(-1,vy0);
        
        f = 1.0/(n*n);
        for (i=0;i<n;i++) {
            for (j=0;j<n;j++) {
                vx[i+n*j] = f*vx0[i+(n+2)*j]; vy[i+n*j] = f*vy0[i+(n+2)*j];
            }
        }
    }
    
    
// diffuse_matter: This function diffuses matter that has been placed in the velocity field. It's almost identical to the
// velocity diffusion step in the function above. The input matter densities are in rho0 and the result is written into rho.
    void diffuse_matter(int n, double/*fftw_real*/[] vx, double/*fftw_real*/[] vy, double/*fftw_real*/[] rho, double/*fftw_real*/[] rho0, double dt) {
        double/*fftw_real*/ x, y, x0, y0, s, t;
        int i, j, i0, j0, i1, j1;
        
        for ( x=0.5/n,i=0 ; i<n ; i++,x+=1.0/n ) {
            for ( y=0.5/n,j=0 ; j<n ; j++,y+=1.0/n ) {
                x0 = n*(x-dt*vx[i+n*j])-0.5;
                y0 = n*(y-dt*vy[i+n*j])-0.5;
                i0 = clamp(x0);
                s = x0-i0;
                i0 = (n+(i0%n))%n;
                i1 = (i0+1)%n;
                j0 = clamp(y0);
                t = y0-j0;
                j0 = (n+(j0%n))%n;
                j1 = (j0+1)%n;
                rho[i+n*j] = (1-s)*((1-t)*rho0[i0+n*j0]+t*rho0[i0+n*j1])+s*((1-t)*rho0[i1+n*j0]+t*rho0[i1+n*j1]);
            }
        }
    }
    
//set_forces: copy user-controlled forces to the force vectors that are sent to the solver.
//            Also dampen forces and matter density to get a stable simulation.
    void set_forces() {
        int i;
        for (i = 0; i < DIM * DIM; i++) {
            rho0[i]  = 0.995f * rho[i];
            fx[i] *= 0.85f;
            fy[i] *= 0.85f;
            vx0[i]    = fx[i];
            vy0[i]    = fy[i];
        }
    }
    
    
//do_one_simulation_step: Do one complete cycle of the simulation:
//      - set_forces:
//      - solve:            read forces from the user
//      - diffuse_matter:   compute a new set of velocities
//      - gluPostRedisplay: draw a new visualization frame
    void do_one_simulation_step() {
        if (!frozen) {
            set_forces();
            solve(DIM, vx, vy, vx0, vy0, visc, dt);
            
            diffuse_matter(DIM, vx, vy, rho, rho0, dt);
            panel.repaint(50);
        }
    }
    
    
//------ VISUALIZATION CODE STARTS HERE -----------------------------------------------------------------
    
    
    //rainbow: Implements a color palette, mapping the scalar 'value' to a rainbow color RGB
    void rainbow(float value,float[] color) {
        final float dx=0.8f;
        if (value<0) value=0; if (value>1) value=1;
        value = (6-2*dx)*value+dx;
        color[0] = Math.max(0.0f,(3-Math.abs(value-4)-Math.abs(value-5))/2);
        color[1] = Math.max(0.0f,(4-Math.abs(value-2)-Math.abs(value-4))/2);
        color[2] = Math.max(0.0f,(3-Math.abs(value-1)-Math.abs(value-2))/2);
    }
    
    //set_colormap: Sets three different types of colormaps
    void set_colormap(GL gl, float vy) {
        float[] rgb = new float[3];
        
        if (scalar_col==COLOR_BLACKWHITE)
            rgb[0]=rgb[1]=rgb[2] = vy;
        else if (scalar_col==COLOR_RAINBOW)
            rainbow(vy,rgb);
        else if (scalar_col==COLOR_BANDS) {
            final int NLEVELS = 7;
            vy *= NLEVELS; vy = (int)(vy); vy/= NLEVELS;
            rainbow(vy,rgb);
        }
        
        gl.glColor3f(rgb[0], rgb[1], rgb[2]);
    }
    
    
    //direction_to_color: Set the current color by mapping a direction vector (x,y), using
    //                    the color mapping method 'method'. If method==1, map the vector direction
    //                    using a rainbow colormap. If method==0, simply use the white color
    void direction_to_color(GL gl, float x, float y, int method) {
        float r,g,b;
        if (method==1) {
            float f = (float)(Math.atan2(y,x) / Math.PI + 1);
            r = f;
            if(r > 1) r = 2 - r;
            g = f + .66667f;
            if(g > 2) g -= 2;
            if(g > 1) g = 2 - g;
            b = f + 2 * .66667f;
            if(b > 2) b -= 2;
            if(b > 1) b = 2 - b;
        } else { // method==0
            r = g = b = 1; }
        gl.glColor3f(r,g,b);
    }
    
    //visualize: This is the main visualization function
    void visualize(GL gl) {
        int        i, j, idx; double px,py;
        double/*fftw_real*/  wn = winWidth / (double)(DIM + 1);   // Grid cell width
        double/*fftw_real*/  hn = winHeight / (double)(DIM + 1);  // Grid cell heigh
        
        if (draw_smoke) {
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
            for (j = 0; j < DIM - 1; j++)			//draw smoke
            {
                gl.glBegin(GL.GL_TRIANGLE_STRIP);
                
                i = 0;
                px = wn + (float)i * wn;
                py = hn + (float)j * hn;
                idx = (j * DIM) + i;
                gl.glColor3d(rho[idx],rho[idx],rho[idx]);
                gl.glVertex2d(px,py);
                
                for (i = 0; i < DIM - 1; i++) {
                    px = wn + i * wn;
                    py = hn + (j + 1) * hn;
                    idx = ((j + 1) * DIM) + i;
                    set_colormap(gl, (float)rho[idx]);
                    gl.glVertex2d(px, py);
                    px = wn + (i + 1) * wn;
                    py = hn + j * hn;
                    idx = (j * DIM) + (i + 1);
                    set_colormap(gl, (float)rho[idx]);
                    gl.glVertex2d(px, py);
                }
                
                px = wn + (float)(DIM - 1) * wn;
                py = hn + (float)(j + 1) * hn;
                idx = ((j + 1) * DIM) + (DIM - 1);
                set_colormap(gl,(float)rho[idx]);
                gl.glVertex2d(px, py);
                gl.glEnd();
            }
        }
        
        if (draw_vecs) {
            gl.glBegin(GL.GL_LINES);				//draw velocities
            for (i = 0; i < DIM; i++)
                for (j = 0; j < DIM; j++) {
                idx = (j * DIM) + i;
                direction_to_color(gl, (float)(double)vx[idx],(float)(double)vy[idx],color_dir);
                gl.glVertex2d(wn + i * wn, hn + j * hn);
                gl.glVertex2d((wn + i * wn) + vec_scale * vx[idx], (hn + j * hn) + vec_scale * vy[idx]);
                }
            gl.glEnd();
        }
        gl.glFlush(); // forces all opengl commands to complete. Blocking!!
    }
    
    

    
    //display: Handle window redrawing events. Simply delegates to visualize().
    void display(GL gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        Polygonizer polygonizer = new Polygonizer();
        ArrayList<Triangle> triangles = polygonizer.GetPolygons();
        
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
        gl.glTranslatef(0, 0, -6.0f);
        //gl.glScalef(1/winWidth, 1/winHeight, 1/200);

        gl.glBegin( GL.GL_TRIANGLES );

        for (Triangle triangle : triangles) {
            
              gl.glColor3d(1, 0, 0); // Sets current primary color to red
              gl.glVertex3d(triangle.p[0].x/winWidth, triangle.p[0].y/winHeight, triangle.p[0].z/200); // Specify three vertices
              gl.glVertex3d(triangle.p[1].x/winWidth, triangle.p[1].y/winHeight, triangle.p[1].z/200); // Specify three vertices
              gl.glVertex3d(triangle.p[2].x/winWidth, triangle.p[2].y/winHeight, triangle.p[2].z/200); // Specify three vertices
        }
     
        gl.glEnd();   
        gl.glFlush();
        
         
        //visualize(gl);
        
//        /** draw white diagonal. */
//        gl.glBegin(GL.GL_LINES);
//        gl.glColor3f(1,1,1);
//        gl.glVertex2f(0,0);
//        gl.glVertex2f(100,100);
//        gl.glEnd();
//        gl.glFlush();
//
        //glutSwapBuffers();
    }
    
    //reshape: Handle window resizing (reshaping) events
    void reshape(GL gl, int w, int h) {
        GLU glu = new GLU();
//        gl.glViewport(0, 0,w, h);
//        gl.glMatrixMode(GL.GL_PROJECTION);
//        gl.glLoadIdentity();
        //glu.gluOrtho2D(0.0, (double)w, 0.0, (double)h);
        //glu.gluLookAt(0.0, 25.0, -200.0, 0.0f, 0.0f, 0.0f, 0, 1, 0);
        
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, w/h, 1.0, 200.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        winWidth = w; winHeight = h;
    }
    
//------ INTERACTION CODE STARTS HERE -----------------------------------------------------------------
    
//keyboard: Handle key presses
    void keyboard(char key, int x, int y) {
        switch (key) {
            case 't': dt -= 0.001; break;
            case 'T': dt += 0.001; break;
            case 'c': color_dir = 1 - color_dir; break;
            case 'S': vec_scale *= 1.2; break;
            case 's': vec_scale *= 0.8; break;
            case 'V': visc *= 5; break;
            case 'v': visc *= 0.2; break;
            case 'x': draw_smoke = !draw_smoke;
            if (!draw_smoke) draw_vecs = true; break;
            case 'y': draw_vecs = !draw_vecs;
            if (!draw_vecs) draw_smoke = true; break;
            case 'm': scalar_col++; if (scalar_col>COLOR_BANDS) scalar_col=COLOR_BLACKWHITE; break;
            case 'a': frozen = !frozen; break;
            case 'q': System.exit(0);
        }
    }
    
    
    
// drag: When the user drags with the mouse, add a force that corresponds to the direction of the mouse
//       cursor movement. Also inject some new matter into the field at the mouse location.
    int lmx=0,lmy=0;				//remembers last mouse location
    void drag(int mx, int my) {
        int xi,yi,X,Y; double  dx, dy, len;
        
        // Compute the array index that corresponds to the cursor location
        xi = (int)clamp((double)(DIM + 1) * ((double)mx / (double)winWidth));
        yi = (int)clamp((double)(DIM + 1) * ((double)(winHeight - my) / (double)winHeight));
        
        X = xi; Y = yi;
        
        if (X > (DIM - 1))  X = DIM - 1; if (Y > (DIM - 1))  Y = DIM - 1;
        if (X < 0) X = 0; if (Y < 0) Y = 0;
        
        // Add force at the cursor location
        my = winHeight - my;
        dx = mx - lmx; dy = my - lmy;
        len = Math.sqrt(dx * dx + dy * dy);
        if (len != 0.0) {  dx *= 0.1 / len; dy *= 0.1 / len; }
        fx[Y * DIM + X] += (float)dx;
        fy[Y * DIM + X] += (float)dy;
        rho[Y * DIM + X] = 10.0d;
        lmx = mx; lmy = my;
    }
    
    
    //main: The main program
    public static void main(String[] argv) {        
        System.out.printf("Fluid Flow Simulation and Visualization\n");
        System.out.printf("=======================================\n");
        System.out.printf("Click and drag the mouse to steer the flow!\n");
        System.out.printf("T/t:   increase/decrease simulation timestep\n");
        System.out.printf("S/s:   increase/decrease hedgehog scaling\n");
        System.out.printf("c:     toggle direction coloring on/off\n");
        System.out.printf("V/v:   increase decrease fluid viscosity\n");
        System.out.printf("x:     toggle drawing matter on/off\n");
        System.out.printf("y:     toggle drawing hedgehogs on/off\n");
        System.out.printf("m:     toggle thru scalar coloring\n");
        System.out.printf("a:     toggle the animation on/off\n");
        System.out.printf("q:     quit\n\n");
        
        new Smoke();
    }
    
    GLJPanel panel;
    
    public Smoke() {        
        init_simulation(DIM);	//initialize the simulation data structures
        
        // initialize GUI
        JFrame frame = new JFrame("Real-time smoke simulation and visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GLCapabilities caps = new GLCapabilities();
        caps.setDoubleBuffered(true);
        
        // initialize opengl Panel
        panel = new GLJPanel(caps);
        panel.addGLEventListener(new MyGLEventListener());
        panel.addMouseMotionListener(new MouseListener());
        panel.addKeyListener(new MyKeyListener());
        panel.setFocusable(true);
        
        // add panel to window
        frame.setLayout(new BorderLayout());
        frame.add(panel,BorderLayout.CENTER);
        frame.setSize(500,500);
        
        // show window
        frame.setVisible(true);
    }
    
    class MyKeyListener extends KeyAdapter {
        public void keyTyped(KeyEvent e) {
            char key = e.getKeyChar();
            Smoke.this.keyboard(key, 0, 0);
        }
    }
    
    class MouseListener extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent e) {
            drag(e.getX(), e.getY());
            //Flow.this.panel.repaint();
        }
    }
    
    class MyGLEventListener implements GLEventListener {
        public void init(GLAutoDrawable drawable) {
            GL gl = drawable.getGL();
            gl.glShadeModel(GL.GL_SMOOTH);              // Enable Smooth Shading
            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);    // Black Background
            gl.glClearDepth(1.0f);                      // Depth Buffer Setup
            gl.glEnable(GL.GL_DEPTH_TEST);              // Enables Depth Testing
            gl.glDepthFunc(GL.GL_LEQUAL);               // The Type Of Depth Testing To Do
            // Really Nice Perspective Calculations
            gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  
        
        }
        
        public void display(GLAutoDrawable drawable) {
            //Smoke.this.do_one_simulation_step();
            GL gl = drawable.getGL();
            Smoke.this.display(gl);
            //gl.glFlush();
        }
        
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            Smoke.this.reshape(drawable.getGL(),width,height);
        }
        
        public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) { }
    }
}