// curves.java
import java.awt.*;
import java.awt.event.*;

public class DragonCurves extends Frame {
   public static void main(String[] args) {
      if (args.length == 0) {
            System.out.println("Use filename as program argument.");
      } else {
            new DragonCurves(args[0]);
      }
   }

   DragonCurves(String fileName) {
      super("DragonCURVES Click left or right mouse button to change the level");
      addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
               System.exit(0);
            }
      });
      setSize(800, 600);
      add("Center", new CvDragonCurves(fileName));
      setVisible(true);
   }
}

   class CvDragonCurves extends Canvas {
      String fileName, axiom, strF, strX, strY;

      int maxX, maxY, level = 1;
      double xLast, yLast, dir, lastDir, rotation, dirStart, fxStart, fyStart,
         lengthFract, reductFact;

      void error(String str) {  
         System.out.println(str);
         System.exit(1);
      }

      CvDragonCurves(String fileName) {
         Input inp = new Input(fileName);
         if (inp.fails())
            error("Cannot open input file."); 
         axiom = inp.readString(); inp.skipRest();
         strF = inp.readString(); inp.skipRest();
         strX = inp.readString(); inp.skipRest();
         strY = inp.readString(); inp.skipRest();

         rotation = inp.readFloat(); inp.skipRest();
         dirStart = inp.readFloat(); inp.skipRest();
         fxStart = inp.readFloat(); inp.skipRest();
         fyStart = inp.readFloat(); inp.skipRest();
         lengthFract = inp.readFloat(); inp.skipRest();
         reductFact = inp.readFloat();
         if (inp.fails())
            error("Input file incorrect.");

         addMouseListener(new MouseAdapter(){
         @SuppressWarnings("deprecation")
         public void mousePressed(MouseEvent evt)
            {  if ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
               {  level--;       // Right mouse button decreases level
                  if (level < 1)
                     level = 1;
               }
               else
                  level++;     // Left mouse button increases level
               repaint();
            }
         });
   }

   Graphics g;

   private static final double corner = .5; // Corner half the length of original line

   int iX(double x){return (int)Math.round(x);}
   int iY(double y){return (int)Math.round(maxY-y);}

   void drawTo(Graphics g, double dx, double dy) {
      g.drawLine(iX(xLast), iY(yLast), iX(xLast + dx) ,iY(yLast + dy));
      xLast = xLast + dx;
      yLast = yLast + dy;
   }

/**
 * Overrides the paint method to draw the fractal based on generated instructions.
 * This method serves as the primary entry point for drawing operations initiated by the AWT painting system.
 * It calculates the dimensions and starting position based on component size and pre-defined start ratios.
 * Subsequently, it computes the total drawing length affected by the depth of the fractal recursion (level)
 * and invokes the turtleGraphics method to render the fractal on the canvas.
 *
 * @param g The Graphics object provided by the system which serves as the canvas on which the fractal is drawn.
 */

   @Override
   public void paint(Graphics g) {
      // Get the size of the canvas to calculate the maximum allowable dimensions
      Dimension d = getSize();
      maxX = d.width - 1;
      maxY = d.height - 1;

      // Calculate starting positions based on the proportions specified by fxStart and fyStart
      xLast = fxStart * maxX;
      yLast = fyStart * maxY;

      // Set the initial direction from which drawing will start
      dir = dirStart;

      // Generate the complete set of drawing instructions based on the current level
      String instructions = generateInstructions();

      // Calculate the length of each line segment in the drawing based on the reduction factor and level
      double finalLen = Math.pow(reductFact, level) * lengthFract * maxY;

      // Draw the fractal using the generated instructions and calculated line segment length
      turtleGraphics(g, instructions, finalLen);
   }


   /**
    * Renders the turtle graphics based on the given instruction string.
    * @param g The graphics context on which the drawing is performed.
    * @param instruction A string containing the drawing commands.
    * @param len The length of each step in the turtle graphics.
    */
   public void turtleGraphics(Graphics g, String instruction, double len) {
      double rad, dx, dy;

      for (int i = 0; i < instruction.length(); i++) {
         char ch = instruction.charAt(i);
         // Check for next and previous characters to determine context for corners.
         char nextCh = (i + 1 < instruction.length()) ? instruction.charAt(i + 1) : ' ';
         char prevCh = (i > 0) ? instruction.charAt(i - 1) : ' ';

         // Convert the current direction from degrees to radians.
         rad = Math.toRadians(dir);
         // Calculate the horizontal and vertical components of the movement.
         dx = len * Math.cos(rad);
         dy = len * Math.sin(rad);

         // Determine if the current position is a corner.
         boolean isCorner = (prevCh == 'F' || nextCh == 'F') && (rotation == 90 || rotation == -90);

         switch (ch) {
            case 'F': // Step forward and draw
                  if (isCorner) {
                     // If it's a corner, reduce the length of the line to create a rounded corner effect.
                     drawTo(g, dx * (1 - corner), dy * (1 - corner));
                  } else {
                     // Otherwise, draw the full length of the line.
                     drawTo(g, dx, dy);
                  }
                  break;

            case '+': // Turn right
                  // Handle rounded corners when a turn is immediately followed by another forward command.
                  if ((rotation == 90 || rotation == -90) && prevCh == 'F' && nextCh == 'F') {
                     double cornerLen = len * Math.sqrt(2 * corner * corner);
                     // Adjust direction for half the turn to smooth the corner.
                     rad = Math.toRadians(dir - rotation / 2);
                     drawTo(g, cornerLen * Math.cos(rad), cornerLen * Math.sin(rad));
                  }
                  // Update direction by subtracting the rotation angle.
                  dir -= rotation;
                  break;

            case '-': // Turn left
                  // Similar logic for left turns as for right turns.
                  if ((rotation == 90 || rotation == -90) && prevCh == 'F' && nextCh == 'F') {
                     double cornerLen = len * Math.sqrt(2 * corner * corner);
                     rad = Math.toRadians(dir + rotation / 2);
                     drawTo(g, cornerLen * Math.cos(rad), cornerLen * Math.sin(rad));
                  }
                  // Update direction by adding the rotation angle.
                  dir += rotation;
                  break;
         }
      }
   }

/**
 * Generates the expanded set of instructions for drawing the fractal.
 * This method iteratively expands the axiom based on predefined rules for each level of iteration.
 * It then cleans up the resulting instruction string by removing unnecessary characters and simplifying redundant commands.
 * 
 * @return A string containing the fully expanded and cleaned instructions for drawing the fractal.
 */

   private String generateInstructions() {
      // Start with the initial axiom
      StringBuilder newInstructions = new StringBuilder(axiom);

      // Expand the axiom for the specified number of levels
      for (int i = 0; i < level; i++) {
         // Use a new StringBuilder for the next level of instructions
         StringBuilder nextLevelInstructions = new StringBuilder();
         
         // Iterate over the current set of instructions
         for (int j = 0; j < newInstructions.length(); j++) {
            char c = newInstructions.charAt(j); // Get the current character to expand
            switch (c) {
                  case 'F':
                     nextLevelInstructions.append(strF);
                     break;
                  case 'X':
                     nextLevelInstructions.append(strX);
                     break;
                  case 'Y':
                     nextLevelInstructions.append(strY);
                     break;
                  default:
                     nextLevelInstructions.append(c);
                     break;
            }
         }
        // Set the newly expanded instructions for the next iteration
         newInstructions = nextLevelInstructions;
      }
      // Convert StringBuilder to String and remove any characters not involved in drawing commands
      return newInstructions.toString()
                           .replaceAll("[^F\\+\\-]", "") // Keep only 'F', '+', '-'
                           .replaceAll("\\+\\-|\\-\\+", ""); // Simplify '+-' and '-+' pairs
   }
}




