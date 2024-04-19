
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import javax.imageio.*;
public class LetterA extends Frame {
    public static void main(String[] args) { new LetterA(); }

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

class CvTextEffects extends Canvas {
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Font font = new Font("Arial", Font.BOLD, 200);
        g2.setFont(font);

        // First 'A' with transparency and gradient
        GradientPaint gp = new GradientPaint(20, 300, Color.YELLOW, 20, 360, Color.BLUE, true);
        g2.setPaint(gp);
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f); // Least Transparent
        g2.setComposite(ac);
        g2.drawString("A", 50, 250);

        // Second 'A' with texture from image file
        URL url = getClass().getClassLoader().getResource("texture.png");
        BufferedImage textureImage;
        try { 
            textureImage = ImageIO.read(url);
            TexturePaint tp1 = new TexturePaint(textureImage, new Rectangle2D.Double(0, 0, 
                    textureImage.getWidth(), textureImage.getHeight()));
            g2.setPaint(tp1);
            AlphaComposite ac3 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);// Medium transparent
            g2.setComposite(ac3);
            g2.drawString("A", 100, 250);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Third 'A' with a different gradient and increased transparency
        GradientPaint gp2 = new GradientPaint(100, 300, Color.GREEN, 100, 360, Color.MAGENTA, true);
        g2.setPaint(gp2);
        AlphaComposite ac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f); // Most transparent
        g2.setComposite(ac2);
        g2.drawString("A", 150, 250);


    }
}
