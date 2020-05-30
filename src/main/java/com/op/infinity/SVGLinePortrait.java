package com.op.infinity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class SVGLinePortrait extends Base {

    private static SVGLinePortrait generate = new SVGLinePortrait();


    private String dir = host + "Hershey/";
    private String svg = ".svg";
    private String ip = dir + "Andrew.png";
    private BufferedImage ibi;
    private String opF = dir + "AndrewSpiralOut" + svg;
    private int w = 1000;
    private int h = 1000;
    private PrintWriter writer;

    public static void main(String[] args) throws Exception {
        generate.doGeneration();
    }

    private void doGeneration() throws Exception {
        init();

        //drawAllLines();

        drawAllSpiral();
        save();

    }

    private void drawAllSpiral() throws Exception {
        int cx = w / 2;
        int cy = h / 2;

        int dx = 10;
        int dy = 10;


        double r = 1;
        double ang = 0;
        String line1 = "<path id=\"path\" d=\"";
        writer.println(line1);
        boolean start = true;
        while (r < w / 2) {
            double xx = cx + r * Math.cos((Math.toRadians(ang)));
            double yy = cy + r * Math.sin((Math.toRadians(ang)));
            double xVar = (dx * Math.random());
            double yVar = (dy * Math.random());


            int x = (int) xx;
            int y = (int) yy;

            int rgb = ibi.getRGB(x, y);
            Color col = new Color(rgb);
            int add = ((col.getRed() + col.getGreen() + col.getBlue()) / 3);
            double whiteness = ((double) add) / 255.0;
            double sc = (1 - whiteness);
            double dxx = xVar * Math.cos((Math.toRadians(ang)));
            double dyy = yVar * Math.sin((Math.toRadians(ang)));

            int yyy = y - (int) (sc * yVar);
            int xxx = x - (int) (sc * xVar);

            if (start) {
                writer.println("M " + xxx + "," + yyy + " ");
                start = false;
            } else {
                writer.println("L " + xxx + "," + yyy + " ");
            }

            double c = 2 * Math.PI * r;
            double circleAng = 5;
            double num = c / circleAng;
            ang = ang + (360 / num);
            r = r + 10.0 / num;
        }
        writer.println("\" style=\"fill:none;stroke:#000000\" />");

    }

    private void drawAllLines() throws Exception {
        int dx = 2;
        int dy = 20;
        int xVar = 2 * dx;
        String line1 = "<path id=\"path\" d=\"";
        writer.println(line1);

        int lastyy = 0;
        for (int y = 0; y < h; y = y + dy) {
            int xSt = (int) (dx * xVar * Math.random());
            int xEn = (int) (dx * xVar * Math.random());
            //String line1 = "<path id=\"" + xSt + "-" + y + "\" d=\"";
            for (int x = xSt; x < w - xEn; x = x + dx) {
                double yVar = (dy * Math.random());
                int rgb = ibi.getRGB(x, y);
                Color col = new Color(rgb);
                int add = ((col.getRed() + col.getGreen() + col.getBlue()) / 3);
                double whiteness = ((double) add) / 255.0;
                double sc = (1 - whiteness);

                int yy = y - (int) (sc * yVar);
                if (x == xSt) {
                    writer.println("M " + x + "," + yy + " ");
                } else {
                    if (Math.abs(lastyy - yy) < 0.01 * yVar) {
                        //writer.println("M " + x + "," + yy + " ");
                    } else {
                        writer.println("L " + x + "," + yy + " ");
                    }
                }
                lastyy = yy;
            }
        }
        writer.println("\" style=\"fill:none;stroke:#000000\" />");
    }

    private void init() throws Exception {

        writer = new PrintWriter(opF, "UTF-8");
        writer.println("<svg width=\"" + w + "\" height=\"" + h + "\" xmlns=\"http://www.w3.org/2000/svg\">");

        ibi = ImageIO.read(new File(ip));
        w = ibi.getWidth();
        h = ibi.getHeight();
    }


    private void save() throws IOException {
        writer.println("</svg>");
        writer.close();
    }

}