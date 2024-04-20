import java.awt.*;
import javax.swing.*;
import java.util.Random;

/**
 * The TreeBonus class extends JPanel to draw a fractal tree structure using recursive methods.
 * The class uses Java 2D graphics to render the tree on the panel.
 */
public class TreeBonus extends JPanel {
    private static final int WIDTH = 800; // Width of the drawing panel
    private static final int HEIGHT = 600; // Height of the drawing panel
    private final Random random = new Random(); // Random number generator for branch angles

    /**
     * Constructor that sets the preferred size of the panel.
     */
    public TreeBonus() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    /**
     * Draws the fractal tree on the graphics object provided.
     * @param g2d The Graphics2D object used for drawing.
     * @param x The x-coordinate of the start of the branch.
     * @param y The y-coordinate of the start of the branch.
     * @param angle The angle of the branch from the vertical.
     * @param length The length of the branch.
     * @param depth The recursive depth of tree generation.
     * @param thickness The starting thickness of the branch.
     */
    private void drawTree(Graphics2D g2d, int x, int y, double angle, double length, int depth, double thickness) {
        if (depth == 0) return; // Base case for recursion

        // Convert the angle to radians and calculate the endpoint of the branch
        double rad = Math.toRadians(angle);
        int x2 = x + (int) (Math.cos(rad) * length);
        int y2 = y + (int) (Math.sin(rad) * length);
        
        // Calculate the thickness of the next branches
        double nextThickness = thickness * 0.7;

        // Draw the current branch
        drawBranch(g2d, x, y, x2, y2, thickness, nextThickness);

        // Calculate the new length and the angle change for recursive branches
        double newLength = length * 0.7;
        double randomAngle = 45.0;
        double angleChange = randomAngle * (random.nextDouble() - 0.5) * 2;

        // Recursively draw the two new branches
        drawTree(g2d, x2, y2, angle + angleChange, newLength, depth - 1, nextThickness);
        drawTree(g2d, x2, y2, angle - angleChange, newLength, depth - 1, nextThickness);
    }

    /**
     * Draws a single branch of the tree.
     * @param g2d The Graphics2D object used for drawing.
     * @param x1 The x-coordinate of the start of the branch.
     * @param y1 The y-coordinate of the start of the branch.
     * @param x2 The x-coordinate of the end of the branch.
     * @param y2 The y-coordinate of the end of the branch.
     * @param startThickness The thickness at the start of the branch.
     * @param endThickness The thickness at the end of the branch.
     */
    private void drawBranch(Graphics2D g2d, int x1, int y1, int x2, int y2, double startThickness, double endThickness) {
        // Calculate the unit vector for the direction of the branch
        double[] unitVector = getUnitVector(x1, y1, x2, y2);
        // Calculate the normal vector for the branch thickness
        double[] normalVector = new double[] {-unitVector[1], unitVector[0]};
        
        // Calculate the coordinates for the polygon representing the branch
        int[] xPoints = calculatePoints(x1, x2, normalVector[0] * startThickness, normalVector[0] * endThickness);
        int[] yPoints = calculatePoints(y1, y2, normalVector[1] * startThickness, normalVector[1] * endThickness);

        // Draw the branch as a filled polygon
        g2d.fillPolygon(xPoints, yPoints, 4);
    }

    /**
     * Calculates the unit vector for a line defined by two points.
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @return An array containing the unit vector components.
     */
    private double[] getUnitVector(int x1, int y1, int x2, int y2) {
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        double length = Math.hypot(deltaX, deltaY);
        return new double[] {deltaX / length, deltaY / length};
    }

    /**
     * Calculates the coordinates for the corners of the branch polygon.
     * @param p1 The first coordinate of the branch's start or end.
     * @param p2 The second coordinate of the branch's start or end.
     * @param thicknessStart The thickness at the start of the branch.
     * @param thicknessEnd The thickness at the end of the branch.
     * @return An array containing the calculated coordinates.
     */
    private int[] calculatePoints(int p1, int p2, double thicknessStart, double thicknessEnd) {
        return new int[] {
            (int) (p1 - thicknessStart), (int) (p1 + thicknessStart),
            (int) (p2 + thicknessEnd), (int) (p2 - thicknessEnd)
        };
    }

    /**
     * Overrides the paintComponent method to draw the fractal tree.
     * @param g The graphics context provided by the Swing framework.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the tree with the specified parameters
        drawTree((Graphics2D) g, WIDTH / 2, HEIGHT, -90, HEIGHT / 4, 10, 20);
    }

    /**
     * Main method to create the JFrame and set up the program.
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Random Fractal Tree");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new TreeBonus());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
