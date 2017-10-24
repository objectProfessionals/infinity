package com.op.infinity;

import com.jhlabs.image.ShadowFilter;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class SVGVoidOLD {

    private static SVGVoidOLD generate = new SVGVoidOLD();

    private String host = "../host/infinity/images/out/misc/infinity/";
    private String dir = host + "/Virga/";
    private String file = "VirgaCol2";
    private String svg = ".svg";
    private String opF = file + "_VOID_OUT.png";
    private BufferedImage obi;
    private Graphics2D opG;
    private int w = -1;
    private int h = -1;
    private DocumentBuilder builder;
    private ArrayList<Color> cols = new ArrayList<Color>();
    private Color st = Color.decode("#333333");
    private Color en = Color.decode("#00FF77");
    private NodeList svgPaths;
    private double numSVGs = -1;
    private int seed = 2;
    private String[] colsStr = {"DDDDDD", "2cdfff", "224190", "7739d4", "c3031a", "803f26", "ffd651", "7486a2",
            "55298b", "0a1149"};
    private double shrinkage = 20;
    private double shadowX = -shrinkage / 2.0;
    private double shadowY = -shrinkage / 2.0;
    private double filterShadowRad = shrinkage / 2.0;
    private float filterShadowAlpha = 0.75f;
    private double offX = 0; // -shrinkage;
    private double offY = 0; // shrinkage;
    private double totDegsRot = 0.0;
    private boolean onlyInner = false;
    // lighter = grown
    private boolean lighterStack = true;

    public static void main(String[] args) throws Exception {
        generate.doGeneration();

    }

    private void doGeneration() throws Exception {
        init();
        initNodes();
        initImage();

        // initColsFromStr();
        // initColsFromRnd();
        initColsFromPalette();
        // initColsByGradient();
        drawPath();
        save();

    }

    private void initColsFromPalette() throws Exception {
        BufferedImage bi = ImageIO.read(new File(dir + file + "_PAL.jpg"));
        int n = (int) numSVGs;
        int www = bi.getWidth();
        int hhh = bi.getHeight();
        int d = www / n;
        ArrayList<Color> cs = new ArrayList<Color>();
        for (int x = 0; x < www; x = x + 1) {
            int rgb = bi.getRGB(x, hhh / 2);
            Color col = new Color(rgb);
            cs.add(col);
        }

        Comparator<Color> s = new Comparator<Color>() {

            @Override
            public int compare(Color o1, Color o2) {
                int g1 = (o1.getRed() + o1.getGreen() + o1.getBlue()) / 3;
                int g2 = (o2.getRed() + o2.getGreen() + o2.getBlue()) / 3;
                return g2 - g1;
            }
        };
        cs.sort(s);
        for (int x = 0; x < www; x = x + d) {
            cols.add(cs.get(x));
        }
    }

    private void initColsByGradient() {
        cols.add(st);
        double r1 = st.getRed();
        double g1 = st.getGreen();
        double b1 = st.getBlue();

        double r2 = en.getRed();
        double g2 = en.getGreen();
        double b2 = en.getBlue();

        double n = numSVGs - 2;
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

    private void initColsFromStr() {
        for (int i = 0; i < colsStr.length; i++) {
            Color col = Color.decode("#" + colsStr[i]);
            cols.add(col);
        }
    }

    private void initColsFromRnd() {
        Random rnd = new Random(seed);
        Random rnd1 = new Random(seed + 1);
        Random rnd2 = new Random(seed + 2);
        Random rnd3 = new Random(seed + 3);
        for (int i = 0; i < numSVGs; i++) {
            int r = (int) (rnd1.nextDouble() * 255);
            int g = (int) (rnd2.nextDouble() * 255);
            int b = (int) (rnd3.nextDouble() * 255);
            Color col = new Color(r, g, b);
            cols.add(col);
            Collections.sort(cols, new Comparator<Color>() {

                @Override
                public int compare(Color o1, Color o2) {
                    Integer g1 = (o1.getRed() + o1.getGreen() + o1.getBlue()) / 3;
                    Integer g2 = (o2.getRed() + o2.getGreen() + o2.getBlue()) / 3;
                    return g2.compareTo(g1);
                }
            });
        }

    }

    private void drawPath() throws Exception {
        for (int i = 0; i < svgPaths.getLength(); i++) {
            int c = svgPaths.getLength() - i - 1;
            if (lighterStack) {
                // c = i;
            } else {
                c = i;
            }

            drawSVGPath(svgPaths, c);
        }

    }

    private void initNodes() throws SAXException, IOException, XPathExpressionException {
        Document document = builder.parse(dir + file + svg);
        String xpathExpression = "//path/@d";
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        XPathExpression expression = xpath.compile(xpathExpression);
        svgPaths = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
        String viewBox = document.getElementsByTagName("svg").item(0).getAttributes().getNamedItem("viewBox")
                .getNodeValue().substring(4);
        w = Integer.parseInt(viewBox.substring(0, viewBox.indexOf(" ")));
        h = Integer.parseInt(viewBox.substring(viewBox.indexOf(" ") + 1));

        numSVGs = svgPaths.getLength();
        System.out.println(numSVGs);
    }

    private void drawSVGPath(NodeList svgPaths, int fileNum) throws NoninvertibleTransformException {
        Node svgPath = svgPaths.item(fileNum);
        String path = svgPath.getNodeValue();

        Shape shape = parsePathShape(path);
        // double www = shape.getBounds().getWidth();
        // double hhh = shape.getBounds().getHeight();
        // double ar = 0.5 * ((double) w * h);
        // if (www * hhh < ar) {
        // return;
        // }
        Area inner = new Area(shape);

        Shape rect = new Rectangle2D.Double(0, 0, w, h);
        Area outer = new Area(rect);
        outer.subtract(inner);

        AffineTransform tr = AffineTransform.getTranslateInstance(offX * fileNum, offY * fileNum);
        outer.transform(tr);

        Area input = outer;

        if (onlyInner) {
            input = inner;
        }
        Color col = cols.get(fileNum % cols.size());
        opG.setBackground(new Color(0, 0, 0, 255));
        if (fileNum == numSVGs - 1) {
            GradientPaint gp = getGradientPaint(col);
            opG.setPaint(gp);
            opG.fill(input);
        } else {
            drawInner(fileNum, input, col);
        }
        System.out.println(fileNum + ":" + svgPath.getNodeType());
    }

    private GradientPaint getGradientPaint(Color col) {
        Color col1 = col.brighter();
        Color col2 = col.darker();
        GradientPaint gp = new GradientPaint(w, 0, col1, 0, h, col2);
        return gp;
    }

    private void drawInner(double fileNum, Area outer, Color col) throws NoninvertibleTransformException {
        double ww = w;
        double hh = h;
        double f = fileNum / numSVGs; // FTB = 0 -> 1
        double fr = 1 - f; // FTB = 1 -> 0
        double sc = 1; // 0.95 + 0.05*fr;
        double xOff = 2 * shrinkage * fr;
        double yOff = -shrinkage * fr;

        double rot = Math.toRadians(fr * totDegsRot);
        AffineTransform t = new AffineTransform();
        t.translate(xOff, yOff);
        t.rotate(rot, ww / 2, hh / 2);
        t.translate(ww / 2, hh / 2);
        t.scale(sc, sc);
        t.translate(-ww / 2, -hh / 2);
        Shape trans = t.createTransformedShape(outer);

        double filterRad = Math.sqrt(shadowX * shadowX + shadowY * shadowY);
        if (filterShadowRad > -1) {
            filterRad = filterShadowRad;
        }
        ShadowFilter filter = new ShadowFilter((int) filterRad, (int) shadowX, (int) shadowY, filterShadowAlpha);
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g.setPaint(getGradientPaint(col));
        g.fill(trans);
        filter.filter(bi, obi);

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
        File f = new File(dir + opF);
        FileOutputStream fos = new FileOutputStream(f);
        ImageIO.write(obi, "png", fos);

        System.out.println("saved: " + f.getAbsolutePath());
    }

}