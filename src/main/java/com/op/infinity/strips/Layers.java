package com.op.infinity.strips;

import com.jhlabs.image.ShadowFilter;
import com.op.infinity.Base;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;

public class Layers extends Base {

    private static Layers layers = new Layers();
    private String dir = host + "strips/";
    private String opFile = "Flowers";
    private int wmm = 210;
    private int hmm = 297;
    private double mm2in = 25.4;
    private double dpi = 300;
    private int w = (int) ((((double) wmm) / mm2in) * dpi);
    private int h = (int) ((((double) hmm) / mm2in) * dpi);

    private BufferedImage obi;
    private Graphics2D opG;
    private float outerStroke = 3;
    private double numMin = ((double) wmm) * 0.5;
    private double numMax = wmm * 15;
    private Random random = new Random(1);
    private double flowerMinRad = 25;
    private double flowerMaxRad = 100;
    private double numLayers = 10;

    private double shadow = 20;
    private double shadowX = 0;
    private double shadowY = -shadow / 2.0;
    private double filterShadowRad = shadow / 2.0;
    private float filterShadowAlpha = 0.75f;

    private String colStr = "78C4BD,345BA1,41A332,A5333B,A67D33,070B5D,222222,88AA99,AA79F2,00A898,667755,558811,118877,552299,881122";
    private ArrayList<Color> colors = new ArrayList<Color>();

    public static void main(String[] args) throws Exception {
        layers.run();
    }

    private void run() throws Exception {
        setup();

        drawAll();

        save();
    }

    private void drawAll() {

        for (double i = 0; i < numLayers; i++) {
            double totN = numMin + (numMax - numMin) * i / numLayers;
            BufferedImage bi = createAlphaBufferedImage(w, h);
            Graphics2D big = (Graphics2D) bi.getGraphics();
            for (double n = 0; n < totN; n++) {
                drawFlowers(i, n, big);
            }
            ShadowFilter filter = new ShadowFilter((int) filterShadowRad, (int) shadowX, (int) shadowY, filterShadowAlpha);
            filter.filter(bi, obi);
        }
    }

    private void drawFlowers(double i, double num, Graphics2D big) {
        System.out.println(i + ":" + num);

        int aInc = 1;

        double cw = w / 2;
        double ch = h / 2;
//        double crx = cx-cw;
//        double cry = cy-ch;
//        double crr = Math.sqrt(crx*crx + cry*cry);
//        double maxcr = Math.sqrt((w*w/4) + (h*h/4));
//        double pass = 0.5 + 0.5 * (crr / maxcr);

        double iFr = (i / numLayers);
        double pass = (i + 1) / numLayers;
        double radPos = w * 0.1 * random.nextDouble() + w * 0.33 * iFr + (0.66 * iFr * w * random.nextDouble());
        double angPos = random.nextDouble() * 360;

        double yOff = (1 - iFr) * w * 0.15;

        double cx = cw + radPos * Math.cos(Math.toRadians(angPos));
        double cy = yOff + ch + radPos * Math.sin(Math.toRadians(angPos));
        double rad = (flowerMinRad) + (random.nextDouble() * (flowerMaxRad - flowerMinRad));

        double n = 1 + (int) (random.nextDouble() * 7);
        double d = 1 + (int) (random.nextDouble() * 2);
        double k = n / d;

        double radInF = 0.5;
        Stroke stroke = new BasicStroke(outerStroke, CAP_ROUND, BasicStroke.JOIN_ROUND);

        Path2D pathOuter = new Path2D.Double();
        Path2D pathInner = new Path2D.Double();
        ArrayList<Path2D> lines = new ArrayList<>();
        double aRnd = Math.toRadians(random.nextDouble() * 360);
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

        big.setColor(getPastel(pass));
        big.fill(area);

        big.setColor(BLACK);
        big.setStroke(stroke);
        big.draw(area);

    }

    private Color getGrey(double pass) {
        int g = (int) (pass * 255.0);
        return new Color(g, g, g);
    }

    private Color getPastel(double pass) {
        Color col = colors.get((int) ((double) (colors.size()) * random.nextDouble()));
        int g = (int) (pass * 4.0);
        Color col2 = new Color((int) (((double) col.getRed()) * pass),
                (int) (((double) col.getGreen()) * pass),
                (int) (((double) col.getBlue()) * pass));
        for (int gg = 0; gg < g; gg++) {
            col2 = col2.brighter();
        }
        //return new Color(g, g, g);
        return col2;
    }

    void setup() throws IOException {
        obi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        opG = (Graphics2D) obi.getGraphics();
        opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
                RenderingHints.VALUE_ANTIALIAS_ON);
        opG.setColor(WHITE);
        opG.fillRect(0, 0, w, h);
        initColors();
    }

    private void save() throws Exception {
        File op1 = new File(dir + opFile + ".png");
        savePNGFile(obi, op1, dpi);
    }


    private void initColors() {
        StringTokenizer st = new StringTokenizer(colStr, ",");
        int i = 0;

        while (i <= colStr.length()) {
            while (st.hasMoreElements()) {
                String hex = st.nextElement().toString();
                colors.add(getLighter(Color.decode("#" + hex)));
                i++;
            }
            st = new StringTokenizer(colStr, ",");
        }
    }

    private Color getLighter(Color color) {
        double bgSat = 1;
        double bgLight = 0.25;
        float sc = 1 / 255f;
        float r = ((float) color.getRed()) * sc;
        float g = ((float) color.getGreen()) * sc;
        float b = ((float) color.getBlue()) * sc;
        float gr = (float) bgSat;
        float a = (float) bgLight;
        return new Color(r * gr, g * gr, b * gr, a);
    }

}
