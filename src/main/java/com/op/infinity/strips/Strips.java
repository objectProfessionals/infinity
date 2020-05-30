package com.op.infinity.strips;

import com.op.infinity.Base;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;

public class Strips extends Base {

    private static Strips strips = new Strips();
    private String dir = host + "strips/";
    private String opFile = "Strips";
    private int wmm = 200;
    private int hmm = 200;
    private double mm2in = 25.4;
    private double dpi = 300;
    private int w = (int) ((((double) wmm) / mm2in) * dpi);
    private int h = (int) ((((double) hmm) / mm2in) * dpi);

    private int shaddow = w / 200;
    private BufferedImage obi;
    private Graphics2D opG;
    private float outerStroke = 50;
    private float innerStroke = 46;
    private int tot = 200;
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
        for (int n = 0; n < tot; n++) {
            //drawLines();
            //drawCircles();
            drawFlowers(n);
        }
    }

    private void drawFlowers(int i) {
        int aInc = 1;
        double cx = (random.nextDouble() * ((double) (w)));
        double cy = (random.nextDouble() * ((double) (h)));
        double cw = w / 2;
        double ch = h / 2;
        double rad = (flowerMinRad) + (random.nextDouble() * (flowerMaxRad - flowerMinRad));
        double crx = cx - cw;
        double cry = cy - ch;
        double crr = Math.sqrt(crx * crx + cry * cry);
        double maxcr = Math.sqrt((w * w / 4) + (h * h / 4));

        double pass = 0.5 + 0.5 * (crr / maxcr);
        double n = 3 + (int) (random.nextDouble() * 9);
        double d = 1 + (int) (random.nextDouble() * 2);
        double k = n / d;

        double radInF = 0.5;
        Stroke stroke = new BasicStroke(outerStroke * 0.1f, CAP_ROUND, BasicStroke.JOIN_ROUND);
        Stroke strokeLine = new BasicStroke(outerStroke * 0.05f, CAP_ROUND, BasicStroke.JOIN_ROUND);

        Path2D pathOuter = new Path2D.Double();
        Path2D pathInner = new Path2D.Double();
        ArrayList<Path2D> lines = new ArrayList<>();
        double aRnd = Math.toRadians(random.nextDouble() * 360);
        System.out.println(i);
        for (double a = 0; a <= 720; a = a + aInc) {
            double ang = Math.toRadians(a);
            double x = cx + rad * Math.cos(k * ang) * Math.cos(ang + aRnd);
            double y = cy + rad * Math.cos(k * ang) * Math.sin(ang + aRnd);
            double xi = cx + rad * radInF * Math.cos(k * ang) * Math.cos(ang + aRnd);
            double yi = cy + rad * radInF * Math.cos(k * ang) * Math.sin(ang + aRnd);
            if (a == 0) {
                pathOuter.moveTo(x, y);
                pathInner.moveTo(xi, yi);
            } else {
                pathOuter.lineTo(x, y);
                pathInner.lineTo(xi, yi);
            }

            if (a % 30 == 0) {
                Path2D line = new Path2D.Double();
                line.moveTo(cx + rad * radInF * Math.cos(ang + aRnd), cy + rad * radInF * Math.sin(ang + aRnd));
                line.lineTo(cx + rad * Math.cos(ang + aRnd), cy + rad * Math.sin(ang + aRnd));
                lines.add(line);
            }
        }

        Area area = new Area(pathOuter);
        area.subtract(new Area(pathInner));

        //pass = 0.5*((cx+(double)w/2)/(double)w);
        opG.setColor(getGrey(pass));
        opG.fill(area);

        opG.setColor(BLACK);
        opG.setStroke(stroke);
        opG.draw(area);

//        opG.setColor(getRandomColor(pass));
//        opG.setStroke(strokeLine);
//        for (Path2D line : lines) {
//            opG.draw(line);
//        }
    }

    private Color getGrey(double pass) {
        int g = (int) (pass * 255.0);
        return new Color(g, g, g);
    }

    private void drawLines() {
        boolean t2b = random.nextDouble() > 0.5;
        int rx1 = 0;
        int ry1 = 0;
        int rx2 = 0;
        int ry2 = 0;

        if (t2b) {
            rx1 = (int) (random.nextDouble() * ((double) (w)));
            ry1 = 0;
            rx2 = (int) (random.nextDouble() * ((double) (w)));
            ry2 = h;
        } else {
            rx1 = 0;
            ry1 = (int) (random.nextDouble() * ((double) (h)));
            rx2 = w;
            ry2 = (int) (random.nextDouble() * ((double) (h)));
        }

        Stroke outer = new BasicStroke(outerStroke);
        Stroke inner = new BasicStroke(innerStroke);

        opG.setColor(new Color(0.5f, 0.5f, 0.5f, 0.5f));
        opG.setStroke(outer);
        opG.drawLine(rx1 + shaddow, ry1 + shaddow, rx2 + shaddow, ry2 + shaddow);

        opG.setColor(BLACK);
        opG.setStroke(outer);
        opG.drawLine(rx1, ry1, rx2, ry2);

        opG.setColor(getRandomColor(1));
        opG.setStroke(inner);
        opG.drawLine(rx1, ry1, rx2, ry2);
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
