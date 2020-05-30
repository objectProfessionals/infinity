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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DepthMap extends Base {

    private static DepthMap generate = new DepthMap();

    private String dir = host + "map/";
    private String png = ".png";
    private String svg = ".svg";
    private String ipFileName = "VirgaSq2A";
    private String palFileName = "index3";
    private String svgF = dir + ipFileName + svg;
    private String emF = dir + ipFileName + "_EMB" + png;
    private String palF = dir + palFileName + ".jpg";
    private String ipF = dir + ipFileName + png;
    private String opF = dir + ipFileName + "_DEPTH" + png;
    private int w = -1;
    private int h = -1;
    private int pw = -1;
    private int ph = -1;
    private BufferedImage obi;
    private Graphics2D opG;
    private BufferedImage ibi;
    private Graphics2D ipG;
    private BufferedImage pbi;
    private BufferedImage ebi;

    private DocumentBuilder builder;
    private NodeList svgPaths;
    private double numSVGs = -1;
    private String lines[] = {"8f6b6b", "806765", "776460", "73645f", "5f5653"};

    private double shadow = 0;
    private double shadowX = shadow / 2.0;
    private double shadowY = -shadow / 2.0;
    private double filterShadowRad = shadow / 2.0;
    private float filterShadowAlpha = 0.5f;
    private int border;

    private String pal2 = dir + palFileName + "3.jpg";

    public static void main(String[] args) throws Exception {
        generate.doGeneration();
    }

    private void doGeneration() throws Exception {
        init();

        //createPallette();
        //drawDepth();
        drawDepthFromSVG();
        applyEmboss();
        applySVGs();

        redrawOnBorder();
        drawExtras(200);
        drawExtras(400);

        save();

    }

    private void drawDepthFromSVG() throws NoninvertibleTransformException {
        Color colP = getColor(ph - 50);
        drawSVG(21, colP, true, false);

        for (int i = ((int) numSVGs) - 1; i > 0; i--) {
            int c = (int) numSVGs - i;
            colP = getColor(ph - (c * 100 + 50));
            drawSVG(i, colP, true, true);
        }
    }

    private Color getColor(int y) {
        return new Color(pbi.getRGB(pw / 2, y));
    }

    private void applyEmboss() {
        int alpha = 32;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < h; x++) {
                Color col = new Color(ebi.getRGB(x, y));
                Color colE = new Color(col.getRed(), col.getGreen(), col.getBlue(), alpha);
                opG.setColor(colE);
                opG.fillRect(x, y, 1, 1);
            }
        }
    }

    private void applySVGs() throws Exception {
        for (int i = 1; i < numSVGs; i++) {
            int c = (int) numSVGs - i;
            drawSVG(c, Color.BLACK, false, true);
        }
    }

    private void drawSVG(int fileNum, Color col, boolean fill, boolean invert) throws NoninvertibleTransformException {
        Node svgPath = svgPaths.item(fileNum);
        String path = svgPath.getNodeValue();

        Shape shape = parsePathShape(path);
        Area area = new Area(shape);

        drawInner(fileNum, area, col, fill, invert);
        System.out.println("svg=" + fileNum);
    }

    private void drawInner(double fileNum, Area shape, Color col, boolean fill, boolean invert) throws NoninvertibleTransformException {
        if (fill) {
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) bi.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            //g.setPaint(getGradientPaint(col));
            g.setColor(col);

            if (invert) {
                Area inner = new Area(new Rectangle2D.Double(0, 0, w, h));
                inner.subtract(shape);
                g.fill(inner);
            } else {
                g.fill(shape);
            }
            ShadowFilter filter = new ShadowFilter((int) filterShadowRad, (int) shadowX, (int) shadowY, filterShadowAlpha);
            filter.filter(bi, obi);

        } else {
            float str = (float) (w / 1000);
            if (fileNum == 0) {
                opG.setColor(Color.decode("#" + lines[0]));
                BasicStroke st = new BasicStroke(str);
                opG.setStroke(st);
            } else {
                int ii = (int) (fileNum % lines.length);
                opG.setColor(Color.decode("#" + lines[ii]));
                float st = str * (ii + 1);
                float dash1[] = {st};
                BasicStroke dashed = new BasicStroke(str, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, st, dash1,
                        0.0f);
                opG.setStroke(dashed);
            }
            opG.draw(shape);

        }
    }

    private void redrawOnBorder() {
        border = w / 10;
        BufferedImage newObi = createAlphaBufferedImage(w + border * 2, h + border * 2);
        Graphics2D newG2d = (Graphics2D) newObi.getGraphics();
        newG2d.drawImage(obi, border, border, null);

        obi = newObi;
        opG = newG2d;

        w = w + 2 * border;
        h = h + 2 * border;

        opG.setColor(Color.WHITE);
        Area outer = new Area(new Rectangle2D.Double(0, 0, w, h));
        Area inner = new Area(new Rectangle2D.Double(border, border, w - 2 * border, h - 2 * border));
        outer.subtract(inner);
        opG.fill(outer);

        opG.setStroke(new BasicStroke(10));
        opG.setColor(Color.BLACK);
        opG.draw(inner);
    }

    private void drawExtras(int d) {

        float str = 1 + (d / 100);
        float dashStr = 1 + (d / 50);
        float dash1[] = {dashStr * 2, dashStr * 0.5f, dashStr, dashStr * 0.5f};
        int c = 0;
        String lineCol = "312004";
        for (int y = border; y < h - border; y = y + d) {
            opG.setPaint(Color.decode("#" + lineCol));
            BasicStroke dashed = new BasicStroke(str, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, str, dash1,
                    0.0f);
            opG.setStroke(dashed);

            opG.drawLine(border, y + border, w - border, y + border);
            c++;
        }

        c = 0;
        for (int x = border; x < w - border; x = x + d) {
            BasicStroke dashed = new BasicStroke(str, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, str, dash1,
                    0.0f);
            opG.setStroke(dashed);

            opG.drawLine(x + border, border, x + border, h - border);
            c++;
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


    private void init() throws Exception {
        pbi = ImageIO.read(new File(palF));
        pw = pbi.getWidth();
        ph = pbi.getHeight();

        ibi = ImageIO.read(new File(ipF));
        ipG = (Graphics2D) ibi.getGraphics();
        w = ibi.getWidth();
        h = ibi.getHeight();

        ebi = ImageIO.read(new File(emF));

        obi = createAlphaBufferedImage(w, h);
        opG = (Graphics2D) obi.getGraphics();

        shadow = w / 100;
        shadowX = shadow / 2.0;
        shadowY = -shadow / 2.0;
        filterShadowRad = shadow / 2.0;
        filterShadowAlpha = 0.5f;

        initSVG();
    }

    private void initSVG() throws SAXException, IOException, XPathExpressionException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(false);
        factory.setValidating(false);
        factory.setFeature("http://xml.org/sax/features/namespaces", false);
        factory.setFeature("http://xml.org/sax/features/validation", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        builder = factory.newDocumentBuilder();
        Document document = builder.parse(svgF);
        String xpathExpression = "//path/@d";
        //String xpathExpression = "path[contains(@id, 'Selection ')]//path/@d";
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        XPathExpression expression = xpath.compile(xpathExpression);

        svgPaths = (NodeList) expression.evaluate(document, XPathConstants.NODESET);

        String viewBox = document.getElementsByTagName("svg").item(0).getAttributes().getNamedItem("viewBox")
                .getNodeValue().substring(4);
        System.out.println(viewBox);
//        w = Integer.parseInt(viewBox.substring(0, viewBox.indexOf(" ")));
//        h = Integer.parseInt(viewBox.substring(viewBox.indexOf(" ") + 1));

        numSVGs = svgPaths.getLength();
        System.out.println(numSVGs);
    }

    private void save() throws Exception {
        File fFile1 = new File(opF);
        savePNGFile(obi, fFile1, 600);
    }

    private void createPallette() {
        double numPal = 21;
        int pd = (int) (((double) ph) / (numPal));
        double pX = pw / 2;
        int pch = 100;
        int pcw = 100;
        int yy = 0;
        for (int y = 0; y < ph - (pd / 2); y = y + pd) {
            int xxx = pw / 2;
            int yyy = y + (pd / 2);
            Color col = new Color(pbi.getRGB(xxx, yyy));
            opG.setColor(col);
            opG.fillRect(0, yy, pcw, pch);
            yy = yy + pch;
        }
    }

    private void drawDepth() {
        int numPal = (int) numSVGs;
        double pd = ph / (numPal);
        double pX = pw / 2;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < h; x++) {
                Color col = new Color(ibi.getRGB(x, y));
                double cf = 1 - (((double) (col.getRed() + col.getGreen() + col.getBlue())) / (3 * 255.0));
                int pY = (int) ((cf * ((double) (ph - 1))));
                //System.out.println(pX + "," + pY);
                Color colP = new Color(pbi.getRGB((int) pX, pY));
                opG.setColor(colP);
                opG.fillRect(x, y, 1, 1);
            }
        }
    }

}