import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class TreeBonus extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int MAX_DEPTH = 7;
    private static final double INITIAL_LENGTH = 100.0;
    private static final double ANGLE_RANGE = 60.0;
    private static final double LENGTH_RATIO = 0.7;
    @SuppressWarnings("unused")
    private static final double THICKNESS_RATIO = 0.7;

    private Random random;

    public TreeBonus() {
        random = new Random();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTree(g, WIDTH / 2, HEIGHT, -90, INITIAL_LENGTH, MAX_DEPTH);
    }

    private void drawTree(Graphics g, int x, int y, double angle, double length, int depth) {
        if (depth == 0) {
            return; // Base case: reached maximum depth, stop recursion
        }

        // Calculate the endpoint coordinates based on the given angle and length
        int x2 = x + (int) (Math.cos(Math.toRadians(angle)) * length);
        int y2 = y + (int) (Math.sin(Math.toRadians(angle)) * length);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke((float) length / 10)); // Set thickness based on length

        g2d.drawLine(x, y, x2, y2); // Draw the line segment

        // Calculate angles and lengths for the next branches
        double angle1 = angle + getRandomAngle();
        double angle2 = angle - getRandomAngle();

        double length1 = length * LENGTH_RATIO;
        double length2 = length * LENGTH_RATIO;

        // Recursively draw the next branches
        drawTree(g, x2, y2, angle1, length1, depth - 1);
        drawTree(g, x2, y2, angle2, length2, depth - 1);
    }

    private double getRandomAngle() {
        return random.nextDouble() * ANGLE_RANGE - ANGLE_RANGE / 2; // Generate a random angle within the specified range
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Random Bush Tree");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new TreeBonus());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}