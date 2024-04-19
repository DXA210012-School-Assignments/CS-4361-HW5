import java.awt.*;
import javax.swing.*;
import java.util.Random;

public class TreeBonus extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private final Random random = new Random();

    public TreeBonus() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    private void drawTree(Graphics2D g2d, int x, int y, double angle, double length, int depth, double thickness) {
        if (depth == 0) return;

        double rad = Math.toRadians(angle);
        int x2 = x + (int) (Math.cos(rad) * length);
        int y2 = y + (int) (Math.sin(rad) * length);
        double nextThickness = thickness * 0.7;

        drawBranch(g2d, x, y, x2, y2, thickness, nextThickness);

        double newLength = length * 0.7;
        double randomAngle = 45.0;
        double angleChange = randomAngle * (random.nextDouble() - 0.5) * 2;

        drawTree(g2d, x2, y2, angle + angleChange, newLength, depth - 1, nextThickness);
        drawTree(g2d, x2, y2, angle - angleChange, newLength, depth - 1, nextThickness);
    }

    private void drawBranch(Graphics2D g2d, int x1, int y1, int x2, int y2, double startThickness, double endThickness) {
        double[] unitVector = getUnitVector(x1, y1, x2, y2);
        double[] normalVector = new double[] {-unitVector[1], unitVector[0]};
        int[] xPoints = calculatePoints(x1, x2, normalVector[0] * startThickness, normalVector[0] * endThickness);
        int[] yPoints = calculatePoints(y1, y2, normalVector[1] * startThickness, normalVector[1] * endThickness);

        g2d.fillPolygon(xPoints, yPoints, 4);
    }

    private double[] getUnitVector(int x1, int y1, int x2, int y2) {
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        double length = Math.hypot(deltaX, deltaY);
        return new double[] {deltaX / length, deltaY / length};
    }

    private int[] calculatePoints(int p1, int p2, double thicknessStart, double thicknessEnd) {
        return new int[] {
            (int) (p1 - thicknessStart), (int) (p1 + thicknessStart),
            (int) (p2 + thicknessEnd), (int) (p2 - thicknessEnd)
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTree((Graphics2D) g, WIDTH / 2, HEIGHT, -90, HEIGHT / 4, 10, 20);
    }

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
