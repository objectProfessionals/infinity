package com.op.infinity;

import org.apache.batik.ext.awt.geom.PathLength;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

public class SVGScribble {

    private static SVGScribble generate = new SVGScribble();

    private String host = "../host/infinity/void/";

    private String dir = "scribble";
    private String file = "VirgaPlain";
    private String svg = ".svg";
    private String opF = file + "_VOID_OUT.png";
    private BufferedImage obi;
    private BufferedImage ibi;
    private Graphics2D opG;
    private int w = -1;
    private int h = -1;
    private DocumentBuilder builder;
    private ArrayList<Color> cols = new ArrayList<Color>();
    private Color st = Color.decode("#FFFFFF");
    private Color en = Color.decode("#140000");
    private ArrayList<Ellipse> paths = new ArrayList();

    public static void main(String[] args) throws Exception {
        generate.doGeneration();
    }

    private void doGeneration() throws Exception {
        init();
        initImage();
        Path2D p2d = new Path2D.Double();
        for (int i = 1; i < paths.size(); i++) {
            Ellipse path0 = paths.get(i - 1);
            Ellipse path1 = paths.get(i);
            getPath(path0, path1, p2d, i);
        }
        //drawPaths(p2ds);
        //opG.draw(p2d);
        draw(p2d);
        save();

    }

    private void draw(Path2D p1) {
        Path2D p = (Path2D) p1.clone();
        PathIterator pi = (p.getPathIterator(null));
        int c = 0;
        float str = 3f;
        double[] coords0 = {0, 0, 0, 0, 0, 0};
        while (!pi.isDone()) {
            double[] coords = {0, 0, 0, 0, 0, 0};
            int segm = pi.currentSegment(coords);
            if (segm == PathIterator.SEG_MOVETO) {
                coords0 = coords;
            } else {
                int x0 = (int) coords0[0];
                int y0 = (int) coords0[1];
                int x1 = (int) coords[0];
                int y1 = (int) coords[1];
                if (x1 >= w || y1 >= h || x1 < 0 || y1 < 0) {
                    coords0 = coords;
                    pi.next();
                    continue;
                }
                //println("x0,y0=" + x0 + "," + y0);
                int rgb = ibi.getRGB(x1, y1);
                Color col = new Color(rgb);
                float grey = 1 - (((col.getRed() + col.getGreen() + col.getBlue()) / 3) / 255f);
                opG.setStroke(new BasicStroke(grey * str));
                opG.drawLine(x0, y0, x1, y1);
                coords0 = coords;
            }
            println("c=" + c);
            c++;
            pi.next();
        }
    }

    private void drawPaths(ArrayList<Path2D> p2ds) {
        for (int i = 0; i < p2ds.size(); i++) {
            opG.draw(p2ds.get(i));
        }
    }

    private Path2D getPath(Ellipse path0, Ellipse path1, Path2D p2d, int svgNum) throws Exception {
        Area a0 = getArea(path0);
        Area a1 = getArea(path1);

        float numPoints = 1000;
        float pf = 1;
        float numPaths = pf * (1 + (float) (path1.num - path0.num));

        PathLength pl0 = new PathLength(a0);

        float seg = pl0.lengthOfPath() / numPoints;

        PathLength pl1 = new PathLength(a1);

        for (float n = 1; n <= numPaths; n++) {
            float p0f = pl0.lengthOfPath() / pl1.lengthOfPath();
            float s = 1;
            Path2D pSingle = new Path2D.Double();
            for (float f1 = 0; f1 < pl1.lengthOfPath(); f1 = f1 + seg) {
                float f0 = f1 * p0f;
                Point2D p0 = pl0.pointAtLength(f0);
                Point2D p1 = pl1.pointAtLength(f1);
                if (p0 == null || p1 == null) {
                    println("f0=" + f0 + " f1=" + f1);
                    continue;
                }
                //double nf = (n / numPaths);
                double nf = (n / numPaths) + ((s / numPoints) / (numPaths));
                double x = p0.getX() + ((p1.getX() - p0.getX()) * nf);
                double y = p0.getY() + ((p1.getY() - p0.getY()) * nf);
                if (s == 1) {
                    pSingle.moveTo(x, y);
                    //println("M " + x + "," + y);
                } else {
                    pSingle.lineTo(x, y);
                    //println("L " + x + "," + y);

//                    double rx1 = random();
//                    double ry1 = random();
//                    double rx2 = random();
//                    double ry2 = random();
//                    p2d.curveTo(x + rx1, y + ry1, x + rx2, y + ry2, x, y);
                }
                s++;
            }

            PathIterator pi = (pSingle.getPathIterator(pathTransform(path1)));

            while (!pi.isDone()) {
                double[] coords = {0, 0, 0, 0, 0, 0};
                int segm = pi.currentSegment(coords);
                if (svgNum == 1 && n == 1 && segm == PathIterator.SEG_MOVETO) {
                    p2d.moveTo(coords[0], coords[1]);
                } else {
                    p2d.lineTo(coords[0], coords[1]);
                }
                pi.next();
            }

        }
        return p2d;
    }

    private AffineTransform pathTransform(Ellipse p) {
        double r = 0.025;
        double rr = -r * 0.5 + Math.random() * Math.PI * r;

        double s = 0.25;
        double ss = 1 + s * Math.random();

        AffineTransform at = new AffineTransform();
        AffineTransform ro = AffineTransform.getRotateInstance(rr, p.cx, p.cx);
        AffineTransform sc = AffineTransform.getScaleInstance(ss, ss);
        AffineTransform tr1 = AffineTransform.getTranslateInstance(-p.cx, -p.cy);
        AffineTransform tr2 = AffineTransform.getTranslateInstance(p.cx, p.cy);
        at.concatenate(tr2);
        at.concatenate(sc);
        at.concatenate(ro);
        at.concatenate(tr1);
        return at;
    }

    private void println(String s) {
        System.out.println(s);
    }

    private double random() {
        if (Math.random() < 0.95) {
            return 0;
        }
        double r = 10 * Math.random();
        double range = -0.5 + Math.random();

        return range * r;
    }

    private Area getArea(Ellipse path) {
        Shape shape1 = new Ellipse2D.Double(path.cx - path.rx, path.cy - path.ry, path.rx * 2, path.ry * 2);
        Area a1 = new Area(shape1);
        if (path.m1 == 0 && path.m2 == 0 && path.m3 == 0 && path.m4 == 0 && path.m5 == 0 && path.m6 == 0) {

        } else {
            AffineTransform tr1 = affineTransform(path.m1, path.m2, path.m3, path.m4, path.m5, path.m6);
            double rot = Math.PI * 0.0 * Math.random();
            AffineTransform ro = AffineTransform.getRotateInstance(rot);
            double sca = 1 + 0.0 * Math.random();
            AffineTransform sc = AffineTransform.getScaleInstance(sca, sca);
            AffineTransform tr = AffineTransform.getTranslateInstance(-path.cx, -path.cy);
            AffineTransform tr2 = AffineTransform.getTranslateInstance(path.cx, path.cy);
            AffineTransform at = new AffineTransform();
            at.concatenate(tr2);
            at.concatenate(sc);
            at.concatenate(ro);
            at.concatenate(tr);
            at.concatenate(tr1);

            a1.transform(at);
        }
        return a1;
    }

    private AffineTransform affineTransform(double m1, double m2, double m3, double m4, double m5, double m6) {
        double r = 0.25;
        double r1 = 1; //1 + Math.random() * r;
        double r2 = 1; //1 + Math.random() * r;
        double r3 = 1; //1 + Math.random() * r;
        double r4 = 1; //1 + Math.random() * r;
        double r5 = 1; //1 + Math.random() * r;
        double r6 = 1; //1 + Math.random() * r;

        return new AffineTransform(m1 * r1, m2 * r2, m3 * r3, m4 * r4, m5 * r5, m6 * r6);
    }

    private void initImage() {
        obi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        opG = (Graphics2D) obi.getGraphics();
        opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        opG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        opG.setColor(Color.WHITE);
        opG.fillRect(0, 0, w, h);
        opG.setColor(Color.BLACK);


        try {
            ibi = ImageIO.read(new File((host + dir + "/" + file + ".jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void init() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(false);
        factory.setValidating(false);
        factory.setFeature("http://xml.org/sax/features/namespaces", false);
        factory.setFeature("http://xml.org/sax/features/validation", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        builder = factory.newDocumentBuilder();

        Document document = builder.parse(host + dir + "/" + file + svg);
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();

        String viewBox = document.getElementsByTagName("svg").item(0).getAttributes().getNamedItem("viewBox")
                .getNodeValue().substring(4);
        w = Integer.parseInt(viewBox.substring(0, viewBox.indexOf(" ")));
        h = Integer.parseInt(viewBox.substring(viewBox.indexOf(" ") + 1));

        String xpathExpression = "//ellipse";
        XPathExpression expression = xpath.compile(xpathExpression);
        NodeList svgPaths = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
        TreeMap<String, String> id2Path = new TreeMap();
        for (int i = 0; i < svgPaths.getLength(); i++) {
            String id = svgPaths.item(i).getAttributes().getNamedItem("id").getNodeValue();
            String cx = svgPaths.item(i).getAttributes().getNamedItem("cx").getNodeValue();
            String cy = svgPaths.item(i).getAttributes().getNamedItem("cy").getNodeValue();
            String rx = svgPaths.item(i).getAttributes().getNamedItem("rx").getNodeValue();
            String ry = svgPaths.item(i).getAttributes().getNamedItem("ry").getNodeValue();
            String tr = "";
            Ellipse el = new Ellipse();
            el.num = Double.parseDouble(id);
            el.cx = Double.parseDouble(cx);
            el.cy = Double.parseDouble(cy);
            el.rx = Double.parseDouble(rx);
            el.ry = Double.parseDouble(ry);
            if (svgPaths.item(i).getAttributes().getNamedItem("transform") != null) {
                double darr[] = {0, 0, 0, 0, 0, 0};
                tr = svgPaths.item(i).getAttributes().getNamedItem("transform").getNodeValue();
                //matrix(0.80037199,-0.59950369,0.60413665,0.79688074,0,0)
                tr = tr.substring(7);
                tr = tr.substring(0, tr.length() - 1);
                String arr[] = tr.split(",");
                darr[0] = Double.parseDouble(arr[0]);
                darr[1] = Double.parseDouble(arr[1]);
                darr[2] = Double.parseDouble(arr[2]);
                darr[3] = Double.parseDouble(arr[3]);
                darr[4] = Double.parseDouble(arr[4]);
                darr[5] = Double.parseDouble(arr[5]);

                el.m1 = darr[0];
                el.m2 = darr[1];
                el.m3 = darr[2];
                el.m4 = darr[3];
                el.m5 = darr[4];
                el.m6 = darr[5];
            }
            paths.add(el);
        }

    }

    public static Shape parsePathShape(String svgPathShape) {
        try {
            AWTPathProducer pathProducer = new AWTPathProducer();
            PathParser pathParser = new PathParser();
            pathParser.setPathHandler(pathProducer);
            pathParser.parse(svgPathShape);
            return pathProducer.getShape();
        } catch (ParseException ex) {
            // Fallback to default square shape if shape is incorrect
            return new Rectangle2D.Float(0, 0, 1, 1);
        }
    }

    private void save() throws IOException {
        File f = new File(host + dir + "/" + opF);
        FileOutputStream fos = new FileOutputStream(f);
        ImageIO.write(obi, "png", fos);

        System.out.println("saved: " + f.getAbsolutePath());
    }

    private class Ellipse {
        double num;
        double cx, cy, rx, ry;
        double m1;
        double m2;
        double m3;
        double m4;
        double m5;
        double m6;
    }
}