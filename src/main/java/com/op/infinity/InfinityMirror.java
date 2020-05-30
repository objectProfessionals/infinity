package com.op.infinity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class InfinityMirror extends Base {

    String dir = host + "mirror/";
    String fileName = "infinity";
    String patternName = "pattern2";
    double dpi = 600;
    double i2mm = 25.4;
    double wmm = 100;
    double hmm = 100;
    double w = dpi * wmm / i2mm;
    double h = dpi * hmm / i2mm;

    BufferedImage opImage;
    Graphics2D opG;
    BufferedImage ipImage;
    Graphics2D ipG;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        createImage();
    }


    private static void createImage() throws IOException, Exception {
        InfinityMirror mr = new InfinityMirror();
        mr.createImageFiles();
    }

    public void createImageFiles() throws Exception {
        init();
        loadPattern();
        //createPattern();
        drawAll();
        savePNG();
    }

    private void createPattern() {
        ipImage = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_ARGB);
        ipG = (Graphics2D) ipImage.getGraphics();
        ipG.setColor(Color.black);
        ipG.fill(new Rectangle2D.Double(0, 0, w, h));

        Color cols[] = {Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.WHITE};

        for (int i = 0; i < cols.length; i++) {
            ipG.setColor(cols[i]);
            ipG.setStroke(new BasicStroke(50));
            int n = i % 3;
            Polygon p = createPolygon(3 + n, 1000, 1000, 2000, 2000);
            ipG.draw(p);
        }
    }

    private Polygon createPolygon(int numberOfSides, int l, int b, int x, int y) {
        Polygon polygon = new Polygon();
        for (int i = 0; i < numberOfSides; i++) {
            int radius = (int) (Math.min(l, b) * 0.4);
            double angle = 2 * Math.PI / numberOfSides;
            int calcX = (int) (x + radius * Math.cos(i * angle));
            int calcY = (int) (y - radius * Math.sin(i * angle));
            polygon.addPoint(calcX, calcY);
        }
        return polygon;
    }


    private void drawAll() {
        float num = 10;
        for (float d = 1; d > 0; d = d - 1 / num) {
            draw(d);
        }
    }

    private void draw(float opacity) {
        opG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        double depth = 0.2;
        int b = (int) (w * depth * (1 - opacity));
        opG.drawImage(ipImage, b, b, (int) (w - 2 * b), (int) (h - 2 * b), null);
    }

    private void init() throws Exception {
        int ww = (int) w;
        int hh = (int) h;
        opImage = createBufferedImage(ww, hh);
        opG = (Graphics2D) opImage.getGraphics();
        opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        opG.setColor(Color.DARK_GRAY);
        opG.fillRect(0, 0, ww, hh);
    }

    private void loadPattern() throws IOException {
        ipImage = ImageIO.read(new File(dir + patternName + ".png"));
        ipG = (Graphics2D) ipImage.getGraphics();
    }

    private void savePNG() throws Exception {
        File fFile1 = new File(dir + fileName + "_" + patternName + ".png");
        savePNGFile(opImage, fFile1, dpi);
        opG.dispose();
        System.out.println("PNG image created : " + dir);

    }
}