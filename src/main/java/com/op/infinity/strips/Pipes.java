package com.op.infinity.strips;

import com.op.infinity.Base;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Pipes extends Base {

    private static Pipes strips = new Pipes();
    private String dir = host + "pipes/";
    private int wmm = 200;
    private int hmm = 200;
    private double mm2in = 25.4;
    private double dpi = 300;
    private int w = (int) ((((double) wmm) / mm2in) * dpi);
    private int h = (int) ((((double) hmm) / mm2in) * dpi);

    private BufferedImage obi;
    private Graphics2D opG;
    private BufferedImage pipeBI;
    private BufferedImage revPipeBI;
    private double bF = 0.1; //0.1
    private double pipeRad = 50;
    private double minLen = 25; //pipeRad * 1;
    private double maxLenF = 5;
    private Random random = new Random(0);
    private double totPipes = 200;
    private double pipeAngInc = 0.5;

    private double stroke = 5;
    private double pipeDrawInc = 0.25;
    private double pipeEdgeStrokeF = 0.05;
    private boolean pipeByImage = true;
    private String stripFile = "pipeStrip";
    private String opFile = (pipeByImage ? stripFile : "Pipes") + (int) pipeRad + "x" + (int) totPipes;

    public static void main(String[] args) throws Exception {
        strips.run();
    }

    private void run() throws Exception {
        setup();

        drawAll();

        save();
    }

    private void drawAll() {
        double x1a = (double) w * 0.5 - pipeRad * 0.5;
        double y1a = (double) h * bF * 1.5;
        double x2a = 0;
        double y2a = 0;
        double x1b = x1a + pipeRad;
        double y1b = y1a;
        double x2b = 0;
        double y2b = 0;
        double lastAng = 270;
        double lastPos = 5;

        opG.setColor(BLACK);
        int tries = 0;
        int resets = 0;
        for (double n = 0; n < totPipes; ) {
            double[] angs = getAngle(lastAng, x1a, y1a, x1b, y1b, bF);
            double angN = angs[0];
            double pos = angs[1];
            double nF = n / totPipes;
            if (angN != -1) {
                System.out.println("n=" + (int) n + " angN=" + angN);
                double len = calcLen(x1a, y1a);
                if (!isInside(len, angN, x1a, y1a, bF)) {
                    if (tries < 20) {
//                        angN = (360 + lastAng - 180) % 360;
//                        len = 2 * maxLenF * minLen;
                        tries++;
                    } else {
                        System.out.println("RESETTING: n=" + (int) n + " angN=" + angN);
                        resets++;
                        x1a = (w * bF * 2) + random.nextDouble() * ((double) w) * (1 - bF * 2);
                        y1a = (h * bF * 2) + random.nextDouble() * ((double) h) * (1 - bF * 2);
                        x1b = x1a + pipeRad;
                        y1b = y1a;
                        lastAng = 270;
                        lastPos = 5;
                        tries = 0;
                    }
                    continue;
                }
                if (n == totPipes - 1) {
                    double l = 1;
                    double border = bF * 2;
                    while (x2a > w * border && x2a < w * (1 - border) && y2a > h * border && y2a < h * (1 - border)) {
                        x2a = x1a + l * cos(Math.toRadians(lastAng));
                        y2a = y1a - l * sin(Math.toRadians(lastAng));

                        x2b = x1b + l * cos(Math.toRadians(lastAng));
                        y2b = y1b - l * sin(Math.toRadians(lastAng));

                        drawPipeSection(x2a, y2a, x2b, y2b, nF, false);
                        l++;
                    }
                    System.out.println("reset=" + resets);
                    return;
                } else {
                    for (double l = 0; l < len; l++) {
                        x2a = x1a + len * (l / len) * cos(Math.toRadians(lastAng));
                        y2a = y1a - len * (l / len) * sin(Math.toRadians(lastAng));

                        x2b = x1b + len * (l / len) * cos(Math.toRadians(lastAng));
                        y2b = y1b - len * (l / len) * sin(Math.toRadians(lastAng));

                        drawPipeSection(x2a, y2a, x2b, y2b, nF, false);
                    }
                }

                if (lastPos > pos) {
                    if (lastPos - pos > 2) {
                        double cx = x2b;
                        double cy = y2b;
                        double nxa = x2a;
                        double nya = y2a;
                        for (double a = lastAng; a < angN + 360; a = a + pipeAngInc) {
                            nxa = cx + pipeRad * cos(Math.toRadians(a - 90));
                            nya = cy - pipeRad * sin(Math.toRadians(a - 90));
                            drawPipeSection(cx, cy, nxa, nya, nF, true);
                        }
                        x1b = cx;
                        y1b = cy;
                        x1a = nxa;
                        y1a = nya;
                    } else {
                        double cx = x2a;
                        double cy = y2a;
                        double nxb = x2b;
                        double nyb = y2b;
                        for (double a = lastAng; a > angN; a = a - pipeAngInc) {
                            nxb = cx + pipeRad * cos(Math.toRadians(a + 90));
                            nyb = cy - pipeRad * sin(Math.toRadians(a + 90));
                            drawPipeSection(cx, cy, nxb, nyb, nF, false);
                        }
                        x1a = cx;
                        y1a = cy;
                        x1b = nxb;
                        y1b = nyb;
                    }
                } else {
                    if (pos - lastPos > 2) {
                        double cx = x2a;
                        double cy = y2a;
                        double nxb = x2b;
                        double nyb = y2b;
                        for (double a = lastAng; a > angN - 360; a = a - pipeAngInc) {
                            nxb = cx + pipeRad * cos(Math.toRadians(a + 90));
                            nyb = cy - pipeRad * sin(Math.toRadians(a + 90));
                            drawPipeSection(cx, cy, nxb, nyb, nF, false);
                        }
                        x1a = cx;
                        y1a = cy;
                        x1b = nxb;
                        y1b = nyb;
                    } else {
                        double cx = x2b;
                        double cy = y2b;
                        double nxa = x2a;
                        double nya = y2a;
                        for (double a = lastAng; a < angN; a = a + pipeAngInc) {
                            nxa = cx + pipeRad * cos(Math.toRadians(a - 90));
                            nya = cy - pipeRad * sin(Math.toRadians(a - 90));
                            drawPipeSection(cx, cy, nxa, nya, nF, true);
                        }
                        x1b = cx;
                        y1b = cy;
                        x1a = nxa;
                        y1a = nya;
                    }
                }
                lastAng = angN;
                lastPos = pos;
                n++;
            }
        }

//        opG.setColor(RED);
//        opG.fillRect((int) x1a, (int) y1a, 10, 10);

    }

    private double calcLen(double x1a, double y1a) {
        double len = minLen + (int) (minLen * random.nextDouble() * maxLenF);

        return len;
    }

    private boolean isInside(double len, double angN, double x1, double y1, double bF) {
        double x2 = x1 + len * cos(Math.toRadians(angN));
        double y2 = y1 + len * sin(Math.toRadians(angN));
        int bw = (int) (((double) w) * bF);
        int bh = (int) (((double) h) * bF);

        if (x2 < bw || x2 > w - bw || y2 < bh || y2 > h - bh) {
            return false;
        }
        return true;
    }

    private void drawPipeSection(double x1, double y1, double x2, double y2, double nF, boolean reversePrint) {
        if (pipeByImage) {
            drawPipeSectionByImage(x1, y1, x2, y2, nF, reversePrint);
        } else {
            drawPipeSectionByLine(x1, y1, x2, y2, nF);
        }
    }

    private void drawPipeSectionByLine(double x1, double y1, double x2, double y2, double nF) {
        double th2 = stroke * 0.5;
        opG.setColor(BLACK);
        opG.fillRect((int) (x1 - th2), (int) (y1 - th2), (int) stroke, (int) stroke);
        opG.fillRect((int) (x2 - th2), (int) (y2 - th2), (int) stroke, (int) stroke);

        double dx = x2 - x1;
        double dy = y2 - y1;
        double len = Math.sqrt(dx * dx + dy * dy);
        double ang = Math.atan2(dy, dx);
        int g = (int) ((0.2 + nF * 0.8) * 255);
        Color colIn = new Color(g, g, g);
        for (double l = len * pipeEdgeStrokeF; l < len * (1 - pipeEdgeStrokeF); l = l + pipeDrawInc) {
            double xx = x1 + l * cos(ang);
            double yy = y1 + l * sin(ang);
            opG.setColor(colIn);
            opG.fillRect((int) xx, (int) yy, 1, 1);

        }
    }

    private void drawPipeSectionByImage(double x1, double y1, double x2, double y2, double nF, boolean reversePrint) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double ang = Math.atan2(dy, dx);
        double rev = (reversePrint ? Math.PI : 0);
        AffineTransform rot = AffineTransform.getRotateInstance(ang);
        AffineTransform trx = AffineTransform.getTranslateInstance(x1, y1);
        AffineTransform at = new AffineTransform();
        at.concatenate(trx);
        at.concatenate(rot);
        BufferedImage bi = pipeBI;
        if (reversePrint) {
            bi = revPipeBI;
        }
        opG.drawImage(bi, at, null);
    }

    private double[] getAngle(double lastAng, double x1a, double y1a, double x1b, double y1b, double bF) {
        double x1 = (x1a + x1b) / 2;
        double y1 = (y1a + y1b) / 2;
        double xx = (x1 - (w / 2));
        double yy = (y1 - (h / 2));
        double r = Math.sqrt(xx * xx + yy * yy);
        double rmax = w * (0.5 - 2 * bF);
        double posAng = (360 + Math.toDegrees(Math.atan2(-yy, xx))) % 360;
        double ang = 0;
        double pos = 0;
        if (r > rmax) {
            if (posAng < 60) {
                ang = 210;
                pos = 4;
            } else if (posAng < 120) {
                ang = 270;
                pos = 5;
            } else if (posAng < 180) {
                ang = 330;
                pos = 6;
            } else if (posAng < 240) {
                ang = 30;
                pos = 1;
            } else if (posAng < 300) {
                ang = 90;
                pos = 2;
            } else {
                ang = 150;
                pos = 3;
            }
        } else {
            double rnd = random.nextDouble();
            if (rnd >= 0.8333) {
                ang = 30;
                pos = 1;
            } else if (rnd >= 0.666) {
                ang = 90;
                pos = 2;
            } else if (rnd >= 0.499) {
                ang = 150;
                pos = 3;
            } else if (rnd >= 0.333) {
                ang = 210;
                pos = 4;
            } else if (rnd >= 0.166) {
                ang = 270;
                pos = 5;
            } else {
                ang = 330;
                pos = 6;
            }
//        double diffAng = Math.abs(ang - lastAng);
//        if (diffAng == 180 || diffAng == 0) {
//            double[] arr = {-1, pos};
//            return arr;
//        }
        }


        double[] arr = {ang, pos};
        return arr;
    }

    void setup() throws IOException {
        obi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        opG = (Graphics2D) obi.getGraphics();
        opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
                RenderingHints.VALUE_ANTIALIAS_ON);
        opG.setColor(WHITE);
        opG.fillRect(0, 0, w, h);

        BufferedImage bi = ImageIO.read(new File(dir + stripFile + ".png"));
        pipeBI = new BufferedImage((int) pipeRad, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D pipeG = (Graphics2D) pipeBI.getGraphics();
        double s = pipeRad / ((double) bi.getWidth());
        AffineTransform sc = AffineTransform.getScaleInstance(s, 1);
        pipeG.drawImage(bi, sc, null);

        revPipeBI = new BufferedImage((int) pipeRad, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D revG = (Graphics2D) revPipeBI.getGraphics();
        AffineTransform r = AffineTransform.getRotateInstance(Math.PI, pipeRad * 0.5, 0.5);
        revG.drawImage(pipeBI, r, null);


//        pipeG.setColor(BLACK);
//        pipeG.fillRect(0, 0, 1, 1);
//
//        double edge = pipeEdgeStrokeF * pipeRad;
//        for (int i = 0; i < pipeRad; i++) {
//            if ((i < edge) || (i > pipeRad - edge)) {
//                pipeG.setColor(BLACK);
//                pipeG.fillRect(i, 0, 1, 1);
//            } else {
//                pipeG.setColor(WHITE);
//                pipeG.fillRect(i, 0, 1, 1);
//            }
//        }

    }

    private void save() throws Exception {
        File op1 = new File(dir + opFile + ".png");
        savePNGFile(obi, op1, dpi);
    }


}
