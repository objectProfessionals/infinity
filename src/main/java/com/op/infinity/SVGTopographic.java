package com.op.infinity;

import com.jhlabs.image.ShadowFilter;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class SVGTopographic {

    private static SVGTopographic generate = new SVGTopographic();

    private String host = "../host/infinity/images/out/misc/infinity/";
    private String dir = host + "map/";
    private String file = "VirgaSq2";
    private String colsIndex = "index";
    private String svg = ".svg";
    private String opF = file + "_TOP_OUT.png";
    private BufferedImage ibi;
    private BufferedImage obi;
    private Graphics2D opG;
    private int w = -1;
    private int h = -1;
    private double borderF = 0.1;
    private int border = -1;
    private DocumentBuilder builder;
    private ArrayList<Color> cols = new ArrayList<Color>();
    private Color st = Color.decode("#c9eaa3");
    private Color en = Color.decode("#404935");
    private NodeList svgPaths;
    private NodeList otherPaths;
    private double numSVGs = -1;
    private double leave = 0;
    private int seed = 1;

    private String[] colsStrBeg = {"1681bf", "2889c1", "509ecb", "6faccf", "99c1d7"};
    private String[] colsStrEnd = {"606344", "76715c", "968e78", "bfb8a4", "e8e0d3"};
    private String lines[] = {"8f6b6b", "806765", "776460", "73645f", "5f5653"};
    private double shrinkage = 0; //10;
    private double shadowX = shrinkage / 2.0;
    private double shadowY = -shrinkage / 2.0;
    private double filterShadowRad = shrinkage / 2.0;
    private float filterShadowAlpha = 0.5f;
    private double totDegsRot = 0.0;
    private boolean lighterStack = true;
    //Gimp Filters-Edge Detect-Generate Paths By Grey

    public static void main(String[] args) throws Exception {
        generate.doGeneration();

    }

    private void doGeneration() throws Exception {
        initSVGDocument();
        initNodes();
        //initColsFromStr();
        initColsFromImage();
        // initColsFromRnd();
        initImage();

        // initColsFromPalette();
        opG.setClip(border, border, w, h);

        drawPaths();
        drawExtras();
        opG.setClip(null);

        save();

    }

    private void initColsFromImage() throws IOException {
        BufferedImage bi = ImageIO.read(new File(dir + colsIndex + ".jpg"));
        //numSVGs = num - 1;
        int num = (int) numSVGs + 1;
        int x = bi.getWidth() / 2;
        for (int y = bi.getHeight() / (num * 2); y < bi.getHeight(); y = y + bi.getHeight() / num) {
            int rgb = bi.getRGB(x, y);
            Color col = new Color(rgb);
            cols.add(col);
        }
        Collections.reverse(cols);
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
        for (int i = 0; i < colsStrBeg.length; i++) {
            Color col = Color.decode("#" + colsStrBeg[i]);
            cols.add(col);
        }

        double n = numSVGs - colsStrBeg.length - colsStrEnd.length - 1;
        cols.add(st);
        double r1 = st.getRed();
        double g1 = st.getGreen();
        double b1 = st.getBlue();

        double r2 = en.getRed();
        double g2 = en.getGreen();
        double b2 = en.getBlue();

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
        for (int i = 0; i < colsStrEnd.length; i++) {
            Color col = Color.decode("#" + colsStrEnd[i]);
            cols.add(col);
        }
        System.out.println("cols: " + cols.size());

        // Collections.reverse(cols);
    }

    private void initColsFromRnd() {
        Random rnd = new Random(seed);
        for (int i = 0; i < numSVGs; i++) {
            int r = (int) (rnd.nextDouble() * 255);
            int g = (int) (rnd.nextDouble() * 255);
            int b = (int) (rnd.nextDouble() * 255);
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

    private void drawPaths() throws Exception {
        for (int i = 0; i < numSVGs; i++) {
            int c = (int) numSVGs - i - 1;
            if (lighterStack) {
                // c = i;
            } else {
                c = i;
            }

            drawSVGPath(c + (int) leave);
        }

    }

    private void initNodes() throws SAXException, IOException, XPathExpressionException {
        Document document = builder.parse(dir + file + svg);
        String xpathExpression = "//path/@d";
        //String xpathExpression = "path[contains(@id, 'Selection ')]//path/@d";
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        XPathExpression expression = xpath.compile(xpathExpression);

        svgPaths = (NodeList) expression.evaluate(document, XPathConstants.NODESET);

//        NodeList allPaths = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
//        otherPaths = getPaths(allPaths, false);
//        svgPaths = getPaths(allPaths, true);

        String viewBox = document.getElementsByTagName("svg").item(0).getAttributes().getNamedItem("viewBox")
                .getNodeValue().substring(4);
        w = Integer.parseInt(viewBox.substring(0, viewBox.indexOf(" ")));
        h = Integer.parseInt(viewBox.substring(viewBox.indexOf(" ") + 1));


        border = (int) (((double) w) * borderF);

        numSVGs = svgPaths.getLength() - leave;
        System.out.println(numSVGs);
    }


    private NodeList getPaths(NodeList allPaths, boolean b) {
        for (int i = 0; i < allPaths.getLength(); i++) {
            if (allPaths.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) allPaths.item(i);
                if (el.getNodeName().contains("Selection #")) {
                } else {

                }
            }

        }
        return null;
    }


    private void drawSVGPath(int fileNum) throws NoninvertibleTransformException {
        Node svgPath = svgPaths.item(fileNum);
        String path = svgPath.getNodeValue();

        Shape shape = parsePathShape(path);
        Area inner = new Area(shape);

        Shape rect = new Rectangle2D.Double(0, 0, w, h);
        Area outer = new Area(rect);
        outer.subtract(inner);

        //AffineTransform tr = AffineTransform.getTranslateInstance(border, border);
        //outer.transform(tr);

        Area input = outer;

        opG.setBackground(new Color(0, 0, 0, 255));
        if (fileNum == numSVGs - 1) {
            int cPos = (int) numSVGs - fileNum;
            Color col = cols.get(cPos);
            //GradientPaint gp = getGradientPaint(col);
            //opG.setPaint(gp);
            //opG.fill(input);
            drawInner(fileNum, input, col);
        } else {
            int cPos = (int) numSVGs - fileNum;
            Color col = cols.get(cPos);
            drawInner(fileNum, input, col);
        }
        System.out.println("svg=" + fileNum);
    }

    private GradientPaint getGradientPaint(Color col) {
        Color col1 = col.brighter();
        Color col2 = col.darker();
        GradientPaint gp = new GradientPaint(w, 0, col1, 0, h, col2);
        return gp;
    }

    private void drawInner(double fileNum, Area outer, Color col) throws NoninvertibleTransformException {
        //opG.setClip(outer.createTransformedArea(AffineTransform.getTranslateInstance(border, border)));
        //opG.drawImage(ibi, null, border, border);

        double ww = w + 2 * border;
        double hh = h + 2 * border;
        double f = fileNum / numSVGs; // FTB = 0 -> 1
        double fr = 1 - f; // FTB = 1 -> 0
        double sc = 1; // 0.95 + 0.05*fr;
        double xOff = 2 * shrinkage * fr;
        double yOff = -shrinkage * fr;

        double rot = Math.toRadians(fr * totDegsRot);
        AffineTransform t = new AffineTransform();
        //t.translate(-border, -border);
        t.rotate(rot, ww / 2, hh / 2);
        t.translate(ww / 2, hh / 2);
        t.scale(sc, sc);
        t.translate(-ww / 2, -hh / 2);
        t.translate(border, border);
        Shape trans = t.createTransformedShape(outer);

        BufferedImage bi = new BufferedImage(w + 2 * border, h + 2 * border, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        //g.setPaint(getGradientPaint(col));
        g.setColor(col);
        g.fill(trans);
        //g.translate(border, border);
        double filterRad = Math.sqrt(shadowX * shadowX + shadowY * shadowY);
        if (filterShadowRad > -1) {
            filterRad = filterShadowRad;
        }
        ShadowFilter filter = new ShadowFilter((int) filterRad, (int) shadowX, (int) shadowY, filterShadowAlpha);
        filter.filter(bi, obi);

//        EmbossFilter filter2 = new EmbossFilter(135, 135, 100);
//        filter2.filter(bi, obi);

        float str = 1;
        if (fileNum == 0) {
            opG.setPaint(Color.decode("#" + lines[0]));
            BasicStroke st = new BasicStroke(str);
            opG.setStroke(st);
            //addTextToPath(g, trans);
        } else {
            int ii = (int) (fileNum % lines.length);

//            opG.setPaint(Color.decode("#" + lines[ii]));
//            float strokes = (ii / (lines.length - 1)) + 1;
//            BasicStroke st = new BasicStroke(str * strokes);
//            opG.setStroke(st);

            float st = str * (ii + 1);
            float dash1[] = {st};
            BasicStroke dashed = new BasicStroke(str, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, st, dash1,
                    0.0f);
            opG.setStroke(dashed);
            //addTextToPath(g, trans);
        }
        opG.draw(trans);

    }

    private void addTextToPath(Graphics2D g, Shape trans) {
        PathIterator pi = trans.getPathIterator(null);
        while (!pi.isDone()) {
            double[] coords = {0, 0, 0, 0, 0, 0};
            int seg = pi.currentSegment(coords);
            if (seg == PathIterator.SEG_QUADTO) {


            }
            pi.next();
        }
    }

    private void drawExtras() {
        int d = w / 6;
        int ddx = (w - ((w / d) * d)) / 2;
        int ddy = (h - ((h / d) * d)) / 2;

        float str = 1 + (w / 600);
        float dashStr = 1 + (w / 200);
        float dash1[] = {dashStr * 2, dashStr * 0.5f, dashStr, dashStr * 0.5f};
        int c = 0;
        String lineCol = "312004";
        for (int y = ddy; y < h; y = y + d) {
            opG.setPaint(Color.decode("#" + lineCol));
            BasicStroke dashed = new BasicStroke(str, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, str, dash1,
                    0.0f);
            opG.setStroke(dashed);

            opG.drawLine(border, y + border, w + border, y + border);
            c++;
        }

        c = 0;
        for (int x = ddx; x < w; x = x + d) {
            BasicStroke dashed = new BasicStroke(str, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, str, dash1,
                    0.0f);
            opG.setStroke(dashed);

            opG.drawLine(x + border, border, x + border, h + border);
            c++;
        }
        opG.setColor(Color.WHITE);
        Area outer = new Area(new Rectangle2D.Double(0, 0, w + border * 2, h + border * 2));
        Area inner = new Area(new Rectangle2D.Double(border, border, w, h));
        outer.subtract(inner);
        opG.fill(outer);
    }

    private void initImage() throws IOException {
        obi = new BufferedImage(w + border * 2, h + border * 2, BufferedImage.TYPE_INT_ARGB);
        opG = (Graphics2D) obi.getGraphics();
        opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        opG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        opG.setColor(Color.WHITE);
        opG.fillRect(0, 0, w + border * 2, h + border * 2);
        opG.setColor(cols.get(0));
        opG.fillRect(border, border, w, h);

        ibi = ImageIO.read(new File(dir + file + ".jpg"));
    }

    private void initSVGDocument() throws Exception {
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

