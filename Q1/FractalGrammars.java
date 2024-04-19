// FractalGrammars.java
// Extended with U and V strings, file format needs to include U and V strings too.
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;


public class FractalGrammars extends Frame {
   public static void main(String[] args) {
      if (args.length == 0)
         System.out.println("Use filename as program argument.");
      else
         new FractalGrammars(args[0]);
   }

   FractalGrammars(String fileName) {
      super("Click left or right mouse button to change the level");
      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {System.exit(0);}
      });
      setSize(800, 600);
      add("Center", new CvFractalGrammars(fileName));
      setVisible(true);
   }
}

class CvFractalGrammars extends Canvas {
   String fileName, axiom, strF, strf, strX, strY;
   
   String strU, strV;
   
   int maxX, maxY, level = 1;
   double xLast, yLast, dir, rotation, dirStart, fxStart, fyStart,
          lengthFract, reductFact;

   void error(String str) {
      System.out.println(str);
      System.exit(1);
   }

   CvFractalGrammars(String fileName) {
      Input inp = new Input(fileName);
      if (inp.fails())
         error("Cannot open input file.");
      axiom = inp.readString(); inp.skipRest();
      strF = inp.readString(); inp.skipRest();
      strf = inp.readString(); inp.skipRest();
      strX = inp.readString(); inp.skipRest();
      strY = inp.readString(); inp.skipRest();
      
      strU = inp.readString(); inp.skipRest();
      strV = inp.readString(); inp.skipRest();

      rotation = inp.readFloat(); inp.skipRest();
      dirStart = inp.readFloat(); inp.skipRest();
      fxStart = inp.readFloat(); inp.skipRest();
      fyStart = inp.readFloat(); inp.skipRest();
      lengthFract = inp.readFloat(); inp.skipRest();
      reductFact = inp.readFloat();
      if (inp.fails()) error("Input file incorrect.");

      addMouseListener(new MouseAdapter() {
         @SuppressWarnings("deprecation")
         public void mousePressed(MouseEvent evt) {
            if ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
               level--; // Right mouse button decreases level
               if (level < 1) level = 1;
            } else
               level++; // Left mouse button increases level
            repaint();
         }
      });

   }

   Graphics g;

   int iX(double x) {return (int) Math.round(x);}
   int iY(double y) {return (int) Math.round(maxY - y);}


   void drawRoundedLine(Graphics2D g2, double x1, double y1, double x2, double y2, double radius) {
      // Ensure that radius is small relative to the length of the segment
      radius = Math.min(radius, Math.hypot(x2 - x1, y2 - y1) / 2);
      // Calculate direction from (x1, y1) to (x2, y2)
      double angle = Math.atan2(y2 - y1, x2 - x1);
      // Starting point for the line considering the radius
      double xStart = x1 + radius * Math.cos(angle);
      double yStart = y1 + radius * Math.sin(angle);
      // Ending point for the line considering the radius
      double xEnd = x2 - radius * Math.cos(angle);
      double yEnd = y2 - radius * Math.sin(angle);
      // Draw line from start to end points
      g2.draw(new Line2D.Double(xStart, yStart, xEnd, yEnd));
      // Draw arcs at the end points
      g2.draw(new Arc2D.Double(x2 - radius, y2 - radius, 2 * radius, 2 * radius,
              Math.toDegrees(-angle) - 90, 180, Arc2D.OPEN));
      g2.draw(new Arc2D.Double(x1 - radius, y1 - radius, 2 * radius, 2 * radius,
              Math.toDegrees(-angle) + 90, 180, Arc2D.OPEN));
  }
  


  public void paint(Graphics g) {
   // Overriding the paint method to use Graphics2D for better control over geometry
   Graphics2D g2 = (Graphics2D) g;
   // Set rendering hints for better drawing quality
   g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
   Dimension d = getSize();
   maxX = d.width - 1; maxY = d.height - 1;
   xLast = fxStart * maxX; yLast = fyStart * maxY;
   dir = dirStart; // Initial direction in degrees
   turtleGraphics(g2, axiom, level, lengthFract * maxY); // Use g2 instead of g
}

   public void turtleGraphics(Graphics g2, String instruction,
         int depth, double len) {
      //double xMark = 0, yMark = 0, dirMark = 0;
      for (int i = 0; i < instruction.length(); i++) {
         char ch = instruction.charAt(i);
         switch (ch) {


            case 'F': // Step forward and draw
            if (depth == 0) {
               double rad = Math.PI / 180 * dir; // Convert direction to radians
               double newX = xLast + len * Math.cos(rad); // Calculate the new x position
               double newY = yLast + len * Math.sin(rad); // Calculate the new y position
   
               drawRoundedLine(g2, xLast, yLast, newX, newY, len * 0.1); // Draw with rounded corners
               xLast = newX; // Update current x position
               yLast = newY; // Update current y position
            } else {
               turtleGraphics(g2, strF, depth - 1, reductFact * len);
            }
            break;
        


         case 'X':
            if (depth > 0)
               turtleGraphics(g, strX, depth - 1, reductFact * len);
            break;
         case 'Y':
            if (depth > 0)
               turtleGraphics(g, strY, depth - 1, reductFact * len);
            break;

            
         case '+': // Turn right
            dir -= rotation;
            break;
         case '-': // Turn left
            dir += rotation;
            break;

         }
      }
   }
}