package com.op.infinity;

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
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

public class SVGTopoStripes {

    private static final int DIR_X = 1;
    private static final int DIR_Y = 2;
    private static final int DIR_Z = 3;
    private static final int DIR_NX = -1;
    private static final int DIR_NY = -2;
    private static final int DIR_NZ = 3;
    private static SVGTopoStripes generate = new SVGTopoStripes();

    // use gmp -> edge detect ->shrinking paths
    private String host = "../host/infinity/void/";

    private String file = "VirgaPlain";
    private String dir = "topo";
    private String svg = ".svg";
    private String opF = file + "_TOPO_OUT.png";
    private BufferedImage obi;
    private Graphics2D opG;
    private int w = -1;
    private int h = -1;
    private DocumentBuilder builder;
    private ArrayList<Color> cols = new ArrayList<Color>();
    private Color st = Color.decode("#FFFFFF");
    private Color en = Color.decode("#140000");
    private ArrayList<String> paths = new ArrayList();
    private int shrinkage = 5;
    private double shadowX = -shrinkage;
    private double shadowY = shrinkage;
    private double offX = 0; //shrinkage;
    private double offY = 0; //shrinkage;

    private int seed = 3;
    private boolean sortCols = true;
    private boolean sortColsDarkTop = true;

    public static void main(String[] args) throws Exception {
        generate.doGeneration();
    }

    private void doGeneration() throws Exception {
        init();
        initImage();
        drawAll();
        save();

    }

    private void drawAll() {
        double x = 0;
        double y = h;
        double angRad = Math.toRadians(30);
        boolean loop = true;
        double len = 1;

        ArrayList<Shape> inners = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            String path = paths.get(i);
            Shape shape = parsePathShape(path);
            inners.add(shape);
        }

        int dir = DIR_X;
        opG.setColor(Color.RED);
        double xx = x;
        double yy = y;
        double line = 1;
        while (loop) {
            if (dir == DIR_X) {
                xx = x + len * Math.cos(angRad);
                yy = y - len * Math.sin(angRad);
            } else if (dir == DIR_Y) {
                xx = x + len * Math.cos(Math.PI - angRad);
                yy = y - len * Math.sin(Math.PI - angRad);
            } else if (dir == DIR_Z) {
                xx = x + len * Math.cos(Math.PI / 2);
                yy = y - len * Math.sin(Math.PI / 2);
            }
            for (int i = 0; i < inners.size(); ) {
                Shape inner = inners.get(i);
                if (inner.contains(new Point2D.Double(xx, yy))) {
                    opG.fillRect((int) xx, (int) yy, 1, 1);
                    x = (int) xx;
                    y = (int) yy;
                    len = line;
                    dir = newDir(dir);
                    i++;
                } else if (xx > w || yy > h || xx < 0 || yy < 0) {
                    loop = false;
                    i++;
                } else {
                    opG.fillRect((int) xx, (int) yy, 1, 1);
                    len = (len + 1) * line;
                    i++;
                }

            }
        }
    }

    private int newDir(int dir) {
        int newDir = DIR_X;
        if (dir == DIR_X) {
            newDir = DIR_Z;
        } else if (dir == DIR_Z) {
            newDir = DIR_X;
        } else if (dir == DIR_Y) {
            newDir = DIR_Z;
        }

        return newDir;
    }

    private void drawPath(int svgNum) throws Exception {
        String path = paths.get(svgNum);
        Shape shape = parsePathShape(path);
        Area inner = new Area(shape);
        AffineTransform tr1 = new AffineTransform();
        double off = svgNum * 25;
        tr1.translate(off, -off);
        inner.transform(tr1);

        Shape rect = new Rectangle2D.Double(0, 0, w, h);
        Area outer = new Area(rect);
        outer.subtract(inner);

        double tot = paths.size();

        if (svgNum == 0) {
            int c = svgNum * 10;
            opG.setColor(new Color(c, c, c));
            opG.fill(outer);
        }

        int c2 = 20 + svgNum * 20;
        opG.setColor(new Color(c2, c2, c2));
        opG.fill(inner);
        int c3 = c2 + 30;
        opG.setColor(new Color(c3, c3, c3));
        opG.draw(inner);
        System.out.println(svgNum);
    }

    private void initImage() {
        obi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        opG = (Graphics2D) obi.getGraphics();
        opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        opG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        opG.setColor(Color.WHITE);
        opG.fillRect(0, 0, w, h);
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

        String xpathExpression = "//path";
        XPathExpression expression = xpath.compile(xpathExpression);
        NodeList svgPaths = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
        TreeMap<String, String> id2Path = new TreeMap();
        for (int i = 0; i < svgPaths.getLength(); i++) {
            String id = svgPaths.item(i).getAttributes().getNamedItem("id").getNodeValue();
            String path = svgPaths.item(i).getAttributes().getNamedItem("d").getNodeValue();
            id2Path.put(id, path);
            paths.add(path);
        }

        String viewBox = document.getElementsByTagName("svg").item(0).getAttributes().getNamedItem("viewBox")
                .getNodeValue().substring(4);
        w = Integer.parseInt(viewBox.substring(0, viewBox.indexOf(" ")));
        h = Integer.parseInt(viewBox.substring(viewBox.indexOf(" ") + 1));
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

}