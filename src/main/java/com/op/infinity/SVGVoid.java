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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class SVGVoid extends Base {

    private static SVGVoid generate = new SVGVoid();

    // use gmp -> edge detect ->shrinking paths
    private String file = "WH3A";
    private String dir = "map/WallHole";
    private String svg = ".svg";
    private String opF = file + "_VOID_OUT.png";
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
        initCols();
        //initColsFromRnd();
        int i = 0;
        for (String path : paths) {
            drawPath(path, 19 - i);
            i++;
        }
        save();

    }

    private void initColsFromRnd() {
        Random rnd = new Random(seed);
        Random rnd1 = new Random(seed + 1);
        Random rnd2 = new Random(seed + 2);
        Random rnd3 = new Random(seed + 3);
        for (int i = 0; i < paths.size(); i++) {
            int r = (int) (rnd1.nextDouble() * 255);
            int g = (int) (rnd2.nextDouble() * 255);
            int b = (int) (rnd3.nextDouble() * 255);
            Color col = new Color(r, g, b);
            cols.add(col);
        }

        if (sortCols) {
            Collections.sort(cols, new Comparator<Color>() {

                @Override
                public int compare(Color o1, Color o2) {
                    Integer g1 = (o1.getRed() + o1.getGreen() + o1.getBlue()) / 3;
                    Integer g2 = (o2.getRed() + o2.getGreen() + o2.getBlue()) / 3;
                    if (sortColsDarkTop) {
                        return g2.compareTo(g1);
                    } else {
                        return g1.compareTo(g2);
                    }
                }
            });
        }
    }

    private void initCols2() throws Exception {
        BufferedImage bi = ImageIO.read(new File(dir + "/" + file + "COLS.png"));
        double n = paths.size();
        for (int x = 0; x < n * 100; x = x + 100) {
            int rgb = bi.getRGB(x, 50);
            Color col = new Color(rgb);
            cols.add(col);
        }
    }

    private void initCols() {

        cols.add(st);
        double r1 = st.getRed();
        double g1 = st.getGreen();
        double b1 = st.getBlue();

        double r2 = en.getRed();
        double g2 = en.getGreen();
        double b2 = en.getBlue();

        double n = paths.size();
        for (double i = 0; i < n; i++) {

            double fac = (i + 1) / (n + 1);
            fac = Math.pow(fac, 0.5);
            double r3 = r1 + (r2 - r1) * fac;
            double g3 = g1 + (g2 - g1) * fac;
            double b3 = b1 + (b2 - b1) * fac;

            Color col = new Color((int) r3, (int) g3, (int) b3);
            cols.add(col);
        }

        cols.add(en);
    }

    private void drawPath(String path, int svgNum) throws Exception {
        Shape shape = parsePathShape(path);
        Area inner = new Area(shape);
        double scale = Math.pow(((double) (cols.size() - svgNum)) / ((double) cols.size()), 0.33);
        scale = 1;
        AffineTransform tr1 = new AffineTransform();
        tr1.translate(w / 2, h / 2);
        tr1.scale(scale, scale);
        tr1.translate(-w / 2, -h / 2);
        inner.transform(tr1);

        Shape rect = new Rectangle2D.Double(0, 0, w, h);
        Area outer = new Area(rect);
        outer.subtract(inner);

        outer = inner;

        offX = offX - 2;
        offY = offY - 2;
        //double f = 10;
        //offX = f * Math.pow(offX + 1, 0.6);
        //offY = f * Math.pow(offY + 1, 0.3);
//        AffineTransform tr = AffineTransform.getTranslateInstance(offX * svgNum, offY * svgNum);
//        outer.transform(tr);

        Color col = cols.get(svgNum);
        opG.setBackground(new Color(0, 0, 0, 255));
        if (svgNum == 0) {
            opG.setColor(col);
            opG.fill(outer);
        } else {
            double tot = paths.size();
            double part = 1 / tot;
            for (double i = 1; i < tot + 1; i++) {
                double fr = 1 - (i / tot);
                int alpha = (int) (127.0 * fr);
                int g = (int) (127 * fr);
                Color shadow = new Color(g, g, g, alpha);
                AffineTransform t = AffineTransform.getTranslateInstance(shadowX * part, shadowY * part);
                outer.transform(t);
                // opG.setColor(shadow);
                GradientPaint gp = getGradientPaint(shadow);
                opG.setPaint(gp);

                opG.fill(outer);
            }
            AffineTransform t = AffineTransform.getTranslateInstance(-shadowX, -shadowY);
            outer.transform(t);

            opG.setColor(col);

            GradientPaint gp = getGradientPaint(col);
            opG.setPaint(gp);

            opG.fill(outer);
        }
        System.out.println(svgNum);
    }

    private GradientPaint getGradientPaint(Color col) {
        Color col1 = col.brighter();
        Color col2 = col.darker();
        GradientPaint gp = new GradientPaint(w, 0, col1, 0, h, col2);
        return gp;
    }

    private void initImage() {
        obi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        opG = (Graphics2D) obi.getGraphics();
        opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        opG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        opG.setColor(Color.BLACK);
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