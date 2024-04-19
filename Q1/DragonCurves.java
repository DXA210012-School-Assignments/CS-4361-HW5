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


   public void paint(Graphics g) {
      Dimension d = getSize();
      maxX = d.width - 1;
      maxY = d.height - 1;
      xLast = fxStart * maxX;
      yLast = fyStart * maxY;
      dir = dirStart; // Initial direction in degrees
      lastDir = dirStart;
      String instructions = axiom;
      double finalLen = lengthFract * maxY;

      // We need to have the final instruction string
      // instead of recursively interpreting it
      // Because we need the context of previous and next
      // characters which isn't available when you do it recursively

      // So just expand the string for however many levels we need
      for(int k=0;k<level;k++)
      {
         String newInstructions = "";
         for(int j=0;j<instructions.length();j++)
         {
            char c = instructions.charAt(j);
            switch(c)
            {
               case 'F':
                  newInstructions += strF;
                  break;
               case 'X':
                  newInstructions += strX;
                  break;
               case 'Y':
                  newInstructions += strY;
                  break;
               default:
                  newInstructions += c;
                  break;
            }
         }
         instructions = newInstructions;
         finalLen *= lengthFract;
      }
      // Remove anything that's not F, f, +, -, [, or ]
      instructions = instructions.replaceAll("[^F\\+\\-]", "");
      // Remove angles that cancel each other out, makes coding corners easier
      instructions = instructions.replaceAll("\\+\\-|\\-\\+", "");

      turtleGraphics(g, instructions, level, finalLen);
   }


   public void turtleGraphics(Graphics g, String instruction, int depth, double len) {
      // Variables to remember the position and direction if we need to backtrack
      double rad, dx, dy;

      for (int i = 0; i < instruction.length(); i++) {
         char ch = instruction.charAt(i);
         char nextCh = (i + 1 < instruction.length()) ? instruction.charAt(i + 1) : '_';
         char prevCh = (i > 0) ? instruction.charAt(i - 1) : '_';

         // Determine the current direction in radians.
         rad = Math.toRadians(dir);
         dx = len * Math.cos(rad);
         dy = len * Math.sin(rad);

         boolean isCorner = (prevCh == 'F' || nextCh == 'F') && (rotation == 90 || rotation == -90);

         switch (ch) {
            case 'F': // Step forward and draw
                  if (isCorner) {
                     // Draw line with reduced length to simulate a corner rounding
                     drawTo(g, dx * (1 - corner), dy * (1 - corner));
                  } else {
                     // Draw full-length line
                     drawTo(g, dx, dy);
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
               if((rotation == 90 || rotation == -90) && prevCh != '_' && prevCh == 'F' && nextCh != '_' && nextCh == 'F'){
                  double cornerLen = len * Math.sqrt(corner*corner + corner*corner);
                  rad = Math.PI/180 * ((dir-rotation/2) % 360); // Degrees -> radians
                  dx = cornerLen * Math.cos(rad);
                  dy = cornerLen * Math.sin(rad);
                  drawTo(g, dx, dy);
               }
               dir -= rotation; break;
            case '-': // Turn left
               if((rotation == 90 || rotation == -90) && prevCh != '_' && prevCh == 'F' && nextCh != '_' && nextCh == 'F'){
                  double cornerLen = len * Math.sqrt(corner*corner + corner*corner);
                  rad = Math.PI/180 * ((dir+rotation/2) % 360); // Degrees -> radians
                  dx = cornerLen * Math.cos(rad);
                  dy = cornerLen * Math.sin(rad);
                  drawTo(g, dx, dy);
               }
               dir += rotation; break;
         }
      }
   }
}




