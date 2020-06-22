package com.op.infinity.strips;

import com.op.infinity.Base;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;

public class Strips extends Base {

    private static Strips strips = new Strips();
    private String dir = host + "strips/";
    private String opFile = "Strips";
    private int wmm = 210;
    private int hmm = 297;
    private int strmm = 5;
    private int shadmm = 2;
    private double mm2in = 25.4;
    private double dpi = 300;
    private int w = (int) ((((double) wmm) / mm2in) * dpi);
    private int h = (int) ((((double) hmm) / mm2in) * dpi);

    private int shaddow = (int) ((((double) shadmm) / mm2in) * dpi);
    private BufferedImage obi;
    private Graphics2D opG;
    private float outerStroke = (float) ((((double) strmm) / mm2in) * dpi);
    private float innerStroke = outerStroke * 0.9f;
    private int tot = 400;
    private Random random = new Random(0);
    private double flowerMinRad = 50;
    private double flowerMaxRad = 200;


    public static void main(String[] args) throws Exception {
        strips.run();
    }

    private void run() throws Exception {
        setup();

        drawAll();

        save();
    }

    private void drawAll() {
        for (double n = 0; n < tot; n++) {
            drawLines(n);
            //drawCircles();
            System.out.println("line=" + n);
        }
    }

    private void drawLines(double n) {
        double nn = n / (double) tot;
        double tblr = random.nextDouble();
        int rx1 = 0;
        int ry1 = 0;
        int rx2 = 0;
        int ry2 = 0;

        double ww = (double) w;
        double hh = (double) h;

        if (tblr > 0.833) {
            //t2b
            rx1 = (int) (random.nextDouble() * ww);
            ry1 = (int) (-hh * 0.1);
            rx2 = (int) (random.nextDouble() * ww);
            ry2 = h;
        } else if (tblr > 0.666) {
            //ltr
            rx1 = (int) (-ww * 0.1);
            ry1 = (int) (random.nextDouble() * hh);
            rx2 = (int) (ww * 1.1);
            ry2 = (int) (random.nextDouble() * hh);
        } else if (tblr > 0.499) {
            //l2t
            rx1 = (int) (-ww * 0.1);
            ry1 = (int) (random.nextDouble() * hh);
            rx2 = (int) (random.nextDouble() * ww);
            ry2 = (int) (-hh * 0.1);
        } else if (tblr > 0.33) {
            //ltb
            rx1 = (int) (-ww * 0.1);
            ry1 = (int) (random.nextDouble() * hh);
            rx2 = (int) (random.nextDouble() * ww);
            ry2 = (int) (hh * 1.1);
        } else if (tblr > 0.166) {
            //r2t
            rx1 = (int) (ww * 1.1);
            ry1 = (int) (random.nextDouble() * hh);
            rx2 = (int) (random.nextDouble() * ww);
            ry2 = (int) (-hh * 0.1);
        } else {
            //rtb
            rx1 = (int) (ww * 1.1);
            ry1 = (int) (random.nextDouble() * hh);
            rx2 = (int) (random.nextDouble() * ww);
            ry2 = (int) (hh * 1.1);
        }

        float rndTh = (float) (outerStroke * 0.5 * (random.nextDouble() - 0.5));
        Stroke outer = new BasicStroke(outerStroke + rndTh, CAP_ROUND, BasicStroke.JOIN_ROUND);
        Stroke inner = new BasicStroke(innerStroke + rndTh, CAP_ROUND, BasicStroke.JOIN_ROUND);

        Path2D path = new Path2D.Double();
        path.moveTo(rx1, ry1);
        double dy = ry2 - ry1;
        double dx = rx2 - rx1;
        double radiusMistakeMax = outerStroke * 0.25;
        double len = Math.sqrt(dy * dy + dx * dx);
        double ang = Math.atan2(dy, dx);
        double lStep = 10;
        for (double l = 0; l < len; l = l + lStep) {
            double xn = rx1 + dx * (l / len);
            double yn = ry1 + dy * (l / len);
            double dRnd = (random.nextDouble() * radiusMistakeMax) - radiusMistakeMax * 0.5;
            path.lineTo(xn + dRnd * Math.cos(ang - 90), yn + dRnd * Math.sin(ang - 90));
        }

        path.transform(AffineTransform.getTranslateInstance(shaddow, shaddow));
        opG.setColor(new Color(0.15f, 0.15f, 0.15f, 0.5f));
        opG.setStroke(outer);
        opG.draw(path);

        path.transform(AffineTransform.getTranslateInstance(-shaddow, -shaddow));
        int g = (int) (nn * 255);
        opG.setColor(new Color(g, g, g));
        opG.setStroke(outer);
        opG.draw(path);

        opG.setColor(getRandomColor(nn));
        opG.setStroke(inner);
        opG.draw(path);
    }

    private void drawCircles() {
        int rad = (int) ((((double) (w)) * 0.1) + random.nextDouble() * ((double) (w)) * 0.5);

        int cx1 = -(int) (((double) (w)) * 0.1) + (int) (random.nextDouble() * ((double) (w)));
        int cy1 = -(int) (((double) (h)) * 0.1) + (int) (random.nextDouble() * ((double) (h)));

        int shaddowX = 5;
        int shaddowY = 5;
        Stroke outer = new BasicStroke(outerStroke, CAP_ROUND, BasicStroke.JOIN_ROUND);
        Stroke inner = new BasicStroke(innerStroke, CAP_ROUND, BasicStroke.JOIN_ROUND);

//        opG.setColor(new Color(0.5f, 0.5f, 0.5f, 0.5f));
//        opG.setStroke(outer);
//        opG.drawArc(rx1 + shaddowX, ry1 + shaddowY, rad + shaddowX, rad + shaddowY, 0, 360);

        Shape circle = getRoughCircle(rad, cx1, cy1);

        opG.setColor(BLACK);
        opG.setStroke(outer);
        opG.draw(circle);
        //opG.drawArc(rx1, ry1, rad, rad, 0, 360);

        opG.setColor(getRandomColor(1));
        opG.setStroke(inner);
        opG.draw(circle);
        //opG.drawArc(rx1, ry1, rad, rad, 0, 360);
    }

    private Shape getRoughCircle(int rad1, int cx, int cy) {
        double rad = (double) rad1;

        double radiusMistakeMax = 5;
        double aa = 90;
        Path2D path = new Path2D.Double();
        double radiusWobbble = 10;
        double aOff = -aa + (aa * 2) * random.nextDouble();
        for (double a = 0; a < 360 + aOff; a++) {
            int x = (int) (cx + rad * Math.cos(Math.toRadians(a)));
            int y = (int) (cy + rad * Math.sin(Math.toRadians(a)));
            int xOff = (int) (random.nextDouble() * radiusMistakeMax);
            int yOff = (int) (random.nextDouble() * radiusMistakeMax);
            if (a == 0) {
                path.moveTo(x + xOff, y + yOff);
            } else {
                path.lineTo(x + xOff, y + yOff);
            }
            double radIncPerStep = -radiusWobbble * 0.5 + radiusWobbble * random.nextDouble();
            rad = rad + radIncPerStep;
        }
        return path;
    }

    private Color getRandomColor(double lightness) {
        int r = (int) (random.nextDouble() * 255 * lightness);
        int g = (int) (random.nextDouble() * 255 * lightness);
        int b = (int) (random.nextDouble() * 255 * lightness);
        return new Color(r, g, b);
    }

    void setup() throws IOException {
        obi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        opG = (Graphics2D) obi.getGraphics();
        opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
                RenderingHints.VALUE_ANTIALIAS_ON);
        opG.setColor(WHITE);
        opG.fillRect(0, 0, w, h);
    }

    private void save() throws Exception {
        File op1 = new File(dir + opFile + ".png");
        savePNGFile(obi, op1, dpi);
    }


}
