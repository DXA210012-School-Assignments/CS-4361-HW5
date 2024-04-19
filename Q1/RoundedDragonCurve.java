import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class RoundedDragonCurve extends JFrame {
    private int level = 1;
    private String axiom = "";
    private String strF = "";

    private double rotation = 0.0;
    private double dirStart = 0.0;
    private double fxStart = 0.5;
    private double fyStart = 0.5;
    private double lengthFract = 0.0;
    private double reductFact = 0.0;
    private double xLast, yLast, dir;
    private Canvas canvas;

    public RoundedDragonCurve(String fileName) {
        super("Click left or right mouse button to change the level");
        canvas = new Canvas();
        canvas.setSize(800, 600);
        add(canvas);
        loadFile(fileName);

        canvas.addMouseListener(new MouseAdapter() {
            @SuppressWarnings("deprecation")
            @Override
            public void mousePressed(MouseEvent evt) {
                // Increase level on left mouse button, decrease on right mouse button
                if ((evt.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
                    level--;
                    if (level < 1) level = 1;
                } else {
                    level++;
                }
                redraw();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Load configuration from file
    private void loadFile(String fileName) {
        try {
            String[] lines = Files.readAllLines(Paths.get(fileName)).toArray(new String[0]);
            axiom = lines[0].trim();
            strF = lines[1].trim();
            strf = lines[2].trim();
            strX = lines[3].trim();
            strY = lines[4].trim();
            rotation = Double.parseDouble(lines[5].trim());
            dirStart = Double.parseDouble(lines[6].trim());
            fxStart = Double.parseDouble(lines[7].trim());
            fyStart = Double.parseDouble(lines[8].trim());
            lengthFract = Double.parseDouble(lines[9].trim());
            reductFact = Double.parseDouble(lines[10].trim());
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // Increase the level
    private void increaseLevel() {
        level++;
        redraw();
    }

    // Decrease the level, but not below 1
    private void decreaseLevel() {
        level = Math.max(1, level - 1);
        redraw();
    }

    // Convert double to rounded integer
    private int iX(double x) {
        return(int) Math.round(x);
    }

    // Convert double to rounded integer and adjust for canvas height
    private int iY(double y) {
        return(int) Math.round( y);
    }

    // Draw a line from the current position to a new position
    private void drawTo(double x, double y) {
        Graphics g = canvas.getGraphics();
        g.drawLine(iX(xLast), iY(yLast), iX(x), iY(y));
        xLast = x;
        yLast = y;
    }

    // Move to a new position without drawing
    private void moveTo(double x, double y) {
        xLast = x;
        yLast = y;
    }

    // Recursive turtle graphics interpretation
    private void turtleGraphics(String instruction, int depth, double len) {
        double xMark = 0, yMark = 0, dirMark = 0;
        for (int i = 0; i < instruction.length(); i++) {
            char ch = instruction.charAt(i);
            switch (ch) {
                case 'F':
                    // Draw forward if at the maximum depth, otherwise recursively interpret strF
                    if (depth == 0) {
                        double rad = Math.toRadians(dir);
                        double dx = len * Math.cos(rad);
                        double dy = len * Math.sin(rad);
                        drawTo(xLast + dx, yLast + dy);
                    } else {
                        turtleGraphics(strF, depth - 1, reductFact * len);
                    }
                    break;
            }
        }
    }

    // Redraw the canvas
    private void redraw() {
        Graphics g = canvas.getGraphics();
        // Clear the canvas
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        // Initialize starting position and direction
        xLast = fxStart * canvas.getWidth();
        yLast = fyStart * canvas.getHeight();
        dir = dirStart;
        // Interpret the axiom using turtle graphics
        turtleGraphics(axiom, level, lengthFract * canvas.getHeight());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RoundedDragonCurve("dragon.txt");
        });
    }
}