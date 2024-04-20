import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import javax.imageio.*;

/**
 * The LetterA class extends Frame to demonstrate text effects such as gradients,
 * transparency, and textures by drawing the letter 'A' with different visual styles.
 */
public class LetterA extends Frame {

    /**
     * The main method instantiates the LetterA Frame.
     * @param args Not used in this application.
     */
    public static void main(String[] args) {
        new LetterA();
    }

    /**
     * Constructs the LetterA Frame setting up the window for displaying the text effects.
     */
    LetterA() {
        super("A with different effects.");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });
        setSize(400, 400);
        add("Center", new CvTextEffects());
        setVisible(true);
    }
}

/**
 * The CvTextEffects class extends Canvas and is responsible for applying different effects
 * to the letter 'A' when it is drawn on the screen.
 */
class CvTextEffects extends Canvas {

    /**
     * Paints the letter 'A' three times, each with a different effect: gradient, texture, and a combination of both.
     * @param g The graphics object to be painted on.
     */
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Font font = new Font("Arial", Font.BOLD, 200);
        g2.setFont(font);

        // Apply a yellow to blue gradient and semi-transparency, then draw the first 'A'
        GradientPaint gp = new GradientPaint(20, 300, Color.YELLOW, 20, 360, Color.BLUE, true);
        g2.setPaint(gp);
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f);
        g2.setComposite(ac);
        g2.drawString("A", 50, 250);

        // Apply a texture loaded from an image file with medium transparency, then draw the second 'A'
        URL url = getClass().getClassLoader().getResource("texture.png");
        BufferedImage textureImage;
        try { 
            textureImage = ImageIO.read(url);
            TexturePaint tp1 = new TexturePaint(textureImage, new Rectangle2D.Double(0, 0, 
                    textureImage.getWidth(), textureImage.getHeight()));
            g2.setPaint(tp1);
            AlphaComposite ac3 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
            g2.setComposite(ac3);
            g2.drawString("A", 100, 250);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Apply a green to magenta gradient with increased transparency, then draw the third 'A'
        GradientPaint gp2 = new GradientPaint(100, 300, Color.GREEN, 100, 360, Color.MAGENTA, true);
        g2.setPaint(gp2);
        AlphaComposite ac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f);
        g2.setComposite(ac2);
        g2.drawString("A", 150, 250);
    }
}
