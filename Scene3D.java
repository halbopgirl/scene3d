//Name: Haleigh Jayde Doetschman
//Date: 10/16/2019
//Class: CMSC 405
//Purpose: Creates a unique 3D scene

package scene3d;

//imports
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;

/*
Strongly Based off The Book Examples, I also based the code for the torus 
off an example I found online
 */
public class Scene3D extends GLJPanel implements
        GLEventListener, KeyListener, ActionListener {

    //initial rotation values, which can be edited on the main page through 
    //using the keyboard
    double rotateX = -75;
    double rotateY = 10;
    double rotateZ = 0;
    
    //panel elements
    String fillerString = "Choose one!";
    String congrats = "Congratulations! May you live happily ever after.";
    String condolences = "I'm so sorry. There are plenty of fish in the sea.";
    private final JLabel question = new JLabel("What did she say?");
    private final JRadioButton yesButton = new JRadioButton("Yes");
    private final JRadioButton noButton = new JRadioButton("No");
    private JLabel result = new JLabel(fillerString);
    GLJPanel drawable = new GLJPanel();

    //create button group so only one radio button can be selected
    private final ButtonGroup group = new ButtonGroup();
    {
        group.add(yesButton);
        group.add(noButton);
    }//end adding buttons

    
    public static void main(String[] args) {
        JFrame window = new JFrame("Marry me?");
        Scene3D panel = new Scene3D();
        window.setContentPane(panel);
        window.pack();
        window.setLocation(50, 50);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        panel.requestFocusInWindow();
    }//end main

    //Set up GUI
    public Scene3D() {
        super(new GLCapabilities(null));
        JPanel bottom = new JPanel();
        JPanel nested = new JPanel();
        bottom.setLayout(new BorderLayout(20, 20));
        nested.setLayout(new BorderLayout());
        nested.add(yesButton, BorderLayout.NORTH);
        nested.add(noButton, BorderLayout.SOUTH);
        bottom.add(question, BorderLayout.WEST);
        bottom.add(nested, BorderLayout.CENTER);
        bottom.add(result, BorderLayout.EAST);
        bottom.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        drawable.setPreferredSize(new Dimension(800, 800));  //display size
        drawable.addGLEventListener(this);
        yesButton.addActionListener(this);
        noButton.addActionListener(this);
        setLayout(new BorderLayout());
        add(drawable, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        // enable keyboard event handling
        drawable.requestFocus();
        addKeyListener(this);
    }//end Scene3D

    //initializes the OpenGL drawing context.
    public void init(GLAutoDrawable drawable) {
        // called when the panel is created
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0, 0, 0, 1);
        gl.glEnable(GL2.GL_DEPTH_TEST);

        // initialization for lighting and materials.
        gl.glEnable(GL2.GL_LIGHTING);        
        gl.glEnable(GL2.GL_LIGHT0);       
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-10, 10, -10, 10, -10, 10);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLineWidth(2);
        gl.glPolygonOffset(1, 2);
    }//end init

    //called when the OpenGL display needs to be redrawn.
    public void display(GLAutoDrawable drawablee) {
        // called when the panel needs to be drawn
        GL2 gl = drawablee.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glPushMatrix();
        //set initial rotation to view inside box
        gl.glRotated(rotateZ, 0, 0, 1);
        gl.glRotated(rotateY, 0, 1, 0);
        gl.glRotated(rotateX, 1, 0, 0);
        //draw diamond
        gl.glScalef(0.5f, 0.5f, 0.5f);
        drawShape(gl, Shapes.diamond, 1.3f, 1.3f, 2f, true);
        //draw torus and position against diamond
        gl.glRotatef(-90, 1, 1, 0);
        gl.glTranslatef(-3.8f, 4.4f, 0);
        gl.glColor3f(.9f, .93f, .7f);
        drawTorus(gl, 3.1f, .2f);
        //draw box
        gl.glColor3f(.9f, .93f, .7f);
        gl.glScalef(5, 5, 5);
        makeBox(gl, Shapes.square);
        
        //if she says yes to proposal, repaint drawing area with a heart
        if (yesButton.isSelected()) {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity();
            gl.glPushMatrix();
            gl.glScalef(3, 3, 3);
            gl.glRotatef(-90, 1, 0, 0);
            gl.glTranslatef(0, 3f, 0);
            drawShape(gl, Shapes.heart, 4f, 2.8f, 2.8f, true);
            result.setText(congrats);
        } //end if
        
        //if she says no to proposal, repaint drawing area with two fish
        else if (noButton.isSelected()) {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity();
            gl.glPushMatrix();
            gl.glScalef(3, 3, 3);
            gl.glTranslatef(1f, 0, 0);
            drawShape(gl, Shapes.fish, 0f, 0f, 2f, false);
            gl.glPopMatrix();
            gl.glPushMatrix();
            gl.glScalef(1, 1, 1);
            gl.glTranslatef(-1f, -1.2f, 1.5f);
            drawShape(gl, Shapes.longFish, 0f, 2f, 2f, false);
            result.setText(condolences);
        } //end else if
        gl.glPopMatrix();

    } //end display

    //Called when the size of the GLJPanel changes
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }//end reshape

    //This is called before the GLJPanel is destroyed to release OpenGL resources
    public void dispose(GLAutoDrawable drawable) {
    }//end dispose

//Method to create shapes from polygonal mesh shape arrays in Shapes class
    //input includes information about color and whether to draw polygon edges
    public void drawShape(GL2 gl, Shapes shape, float r, float g, float b, boolean lineLoop) {
        //draw shape in specified color
        gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        for (int i = 0; i < shape.faces.length; i++) {
            gl.glColor3f(r, g, b);
            gl.glBegin(GL2.GL_TRIANGLE_FAN);
            for (int j = 0; j < shape.faces[i].length; j++) {
                int vertexNum = shape.faces[i][j];
                gl.glVertex3dv(shape.vertices[vertexNum], 0);
            }//end nested for loop
            gl.glEnd();
        }//end for loop
        gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
        //if lineLoop is true, draws polygon edges in black
        if (lineLoop == true) {
            gl.glColor3f(0, 0, 0);
            for (int i = 0; i < shape.faces.length; i++) {
                gl.glBegin(GL2.GL_LINE_LOOP);
                for (int j = 0; j < shape.faces[i].length; j++) {
                    int vertexNum = shape.faces[i][j];
                    gl.glVertex3dv(shape.vertices[vertexNum], 0);
                }//end nested for loop
                gl.glEnd();
            }//end for loop
        }//end if
    }//end drawShape

    //draws torus 
    public void drawTorus(GL2 gl, float innerRadius, float bandRadius) {
        int precision = 100; // precision
        float centerToEdge = 1.5f * bandRadius; //
        double dv = 2 * Math.PI / precision;
        double dw = 2 * Math.PI / precision;
        double torusBodyRotation = 0.0f;
        double axisRotation = 0.0f; 
        // outer loop
        while (axisRotation < 2 * Math.PI + dw) {
            torusBodyRotation = 0.0f;
            gl.glBegin(GL.GL_TRIANGLE_STRIP);
            // inner loop
            while (torusBodyRotation < 2 * Math.PI + dv) {
                gl.glNormal3d(
                        (innerRadius + centerToEdge * Math.cos(torusBodyRotation)) * Math.cos(axisRotation) - (innerRadius + bandRadius * Math.cos(torusBodyRotation)) * Math.cos(axisRotation),
                        (innerRadius + centerToEdge * Math.cos(torusBodyRotation)) * Math.sin(axisRotation) - (innerRadius + bandRadius * Math.cos(torusBodyRotation)) * Math.sin(axisRotation),
                        (centerToEdge * Math.sin(torusBodyRotation) - bandRadius * Math.sin(torusBodyRotation)));
                gl.glVertex3d((innerRadius + bandRadius * Math.cos(torusBodyRotation)) * Math.cos(axisRotation),
                        (innerRadius + bandRadius * Math.cos(torusBodyRotation)) * Math.sin(axisRotation),
                        bandRadius * Math.sin(torusBodyRotation));
                gl.glNormal3d(
                        (innerRadius + centerToEdge * Math.cos(torusBodyRotation + dv)) * Math.cos(axisRotation + dw) - (innerRadius + bandRadius * Math.cos(torusBodyRotation + dv)) * Math.cos(axisRotation + dw),
                        (innerRadius + centerToEdge * Math.cos(torusBodyRotation + dv)) * Math.sin(axisRotation + dw) - (innerRadius + bandRadius * Math.cos(torusBodyRotation + dv)) * Math.sin(axisRotation + dw),
                        centerToEdge * Math.sin(torusBodyRotation + dv) - bandRadius * Math.sin(torusBodyRotation + dv));
                gl.glVertex3d((innerRadius + bandRadius * Math.cos(torusBodyRotation + dv)) * Math.cos(axisRotation + dw),
                        (innerRadius + bandRadius * Math.cos(torusBodyRotation + dv)) * Math.sin(axisRotation + dw),
                        bandRadius * Math.sin(torusBodyRotation + dv));
                torusBodyRotation += dv;
            } //end nested while loop
            gl.glEnd();
            axisRotation += dw;
        } //end while loop
    }//end drawTorus

    static int sides = 0;
    
    //makes box by rotating squares
    public void makeBox(GL2 gl, Shapes square) {
        while (sides < 5) {
            gl.glPushMatrix();
            gl.glRotatef(90, 0, 1, 0);
            gl.glRotatef(-120, 1, 0, 0);
            switch (sides) {
                case 0:
                    //bottom
                    gl.glTranslated(0, 1, 0);
                    gl.glRotatef(90, -1, 0, 0);
                    gl.glColor3f(0f, 0f, 1f);
                    break;
                case 1:
                    //side 1
                    gl.glTranslated(0, -1, 0);
                    gl.glRotatef(-90, -1, 0, 0);
                    gl.glColor3f(1f, 1f, 0f);
                    break;
                case 2:
                    //side 2
                    gl.glTranslated(1f, 0, 0);
                    gl.glRotatef(90, 0, 1, 0);
                    gl.glColor3f(0f, 1f, 1f);
                    break;
                case 3:
                    //side 3
                    gl.glTranslated(0, 0, 1);
                    gl.glRotatef(180, 0, 1, 0);
                    gl.glColor3f(1f, 0f, 0f);
                    break;
                case 4:
                    //side 4
                    gl.glTranslated(-1f, 0, 0);
                    gl.glRotatef(270, 0, 1, 0);
                    gl.glColor3f(0f, 1f, 0f);
                    break;

            }//end switch

            gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
            for (int i = 0; i < square.faces.length; i++) {
                gl.glColor3f(1.3f, 1.3f, 1.3f);
                gl.glBegin(GL2.GL_QUADS);
                for (int j = 0; j < square.faces[i].length; j++) {
                    int vertexNum = square.faces[i][j];
                    gl.glVertex3dv(square.vertices[vertexNum], 0);
                }//end nested for
                gl.glEnd();
            }//end for
            gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
            gl.glPopMatrix();
            sides++;
            Shapes newSquare = Shapes.square;
            makeBox(gl, newSquare);
        }//end while
    }//end makeBox

    // ----------------  Methods from the KeyListener interface --------------
    public void keyPressed(KeyEvent evt) {
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            rotateY -= 15;
        } else if (key == KeyEvent.VK_RIGHT) {
            rotateY += 15;
        } else if (key == KeyEvent.VK_DOWN) {
            rotateX += 15;
        } else if (key == KeyEvent.VK_UP) {
            rotateX -= 15;
        } else if (key == KeyEvent.VK_PAGE_UP) {
            rotateZ += 15;
        } else if (key == KeyEvent.VK_PAGE_DOWN) {
            rotateZ -= 15;
        } else if (key == KeyEvent.VK_HOME) {
            rotateX = rotateY = rotateZ = 0;
        }
        sides = 0;
        repaint();
    }//end keyPressed

    //necessary overrides
    public void keyReleased(KeyEvent evt) {
        //do nothing
    }//end keyReleased

    public void keyTyped(KeyEvent evt) {
        //do nothing
    }//end keyTyped
    
    // ----------------  Method from the ActionListener interface --------------

    //repaint scene when radioButton is selected
    public void actionPerformed(ActionEvent e) {
        repaint();
    }//end actionPerformed

}//end Scene3D
