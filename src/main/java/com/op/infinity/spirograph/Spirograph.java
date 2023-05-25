package com.op.infinity.spirograph;

import com.op.infinity.Base;
import org.apache.batik.ext.awt.geom.PathLength;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.awt.Color.*;

public class Spirograph extends Base {

    private static Spirograph strips = new Spirograph();
    private String dir = host + "spiro/";
    private String fontFile = "Flamante-Round-Medium-FFP.ttf";
    private String dirFont = "../host/fonts/" + fontFile;
    private String letter = "V";
    private String opFile = "SPIRO_" + letter;
    private String svgIn = dir + letter + "-in.svg";
    private String svgOut = dir + letter + "-out.svg";
    private int wmm = 200;
    private int hmm = 200;
    private double mm2in = 25.4;
    private double dpi = 300;
    private int w = (int) ((((double) wmm) / mm2in) * dpi);
    private int h = (int) ((((double) hmm) / mm2in) * dpi);
    private double radius = 200;
    private double penRadius = radius ;//* 0.75;
    private double dotRadius = radius * 0.1;
    private float pathInc = 1f;
    private double times = 1;
    private int str = 1;

    private DocumentBuilder builder;

    private BufferedImage obi;
    private Graphics2D opG;
    private Font font;
    private float fontSize = (float) (((double) (w)) * 0.75);
    private int animateSteps = (int) (100 / pathInc);


    public static void main(String[] args) throws Exception {
        strips.run();
    }

    private void run() throws Exception {
        setup();

        drawAll();
        //drawAllFromFont();

        save();
    }

    private void drawAll() throws SAXException, ParserConfigurationException, XPathExpressionException, IOException {
        NodeList in = readSVG(svgIn);
        Shape inner = parsePathShape(in.item(0));

        NodeList out = readSVG(svgOut);
        Shape outer = parsePathShape(out.item(0));

        opG.setStroke(new BasicStroke(2f));
        opG.setColor(LIGHT_GRAY);
        opG.fill(inner);
        opG.setColor(DARK_GRAY);
        opG.draw(inner);
//
//        opG.setColor(BLACK);
//        opG.draw(outer);

        drawFromPath(inner, outer);
    }

    private void drawFromPath(Shape inner, Shape outer) {
        PathLength pl = new PathLength(inner);
        double ang = 180;

        double c = Math.PI * 2 * radius;
        double lastX = pl.pointAtLength(0).getX();
        double lastY = pl.pointAtLength(0).getY();
        opG.setColor(GREEN);
        opG.fillRect((int) lastX, (int) lastY, 10, 10);
        int count = 0;
        for (double t = 1; t <= times; t++) {
            //for (float p = pathInc; p < 100; p = p + pathInc) {
            double[] switchXY = null;
            for (float p = pathInc; p <= pl.lengthOfPath(); p = p + pathInc) {
                double x = pl.pointAtLength(p).getX();
                double y = pl.pointAtLength(p).getY();
                if (switchXY != null) {
                    if (Math.abs(dp(switchXY[0]) - dp(x)) > 1
                            || Math.abs(dp(switchXY[1]) - dp(y)) > 1) {
                        continue;
                    }
                }

                if (switchXY !=null) {
                    opG.setColor(MAGENTA);
                    Shape outline1 = new Ellipse2D.Double(x - dotRadius, y - dotRadius, dotRadius * 2, dotRadius * 2);
                    opG.fill(outline1);
                    switchXY = null;
                }

                double dx = lastX - x;
                double dy = lastY - y;
                double aa = Math.atan2(dy, dx);
                double perp = aa + Math.PI / 2;

                double z = Math.sqrt(dx * dx + dy * dy);
                double cFAng = (Math.PI * 2 * (pathInc / c));
                double cAng = Math.toRadians(ang) + cFAng;


                //opG.setColor(BLUE);
                double cx = (x + radius * Math.cos(perp));
                double cy = (y + radius * Math.sin(perp));
//                opG.fillRect((int) cx, (int) cy, str, str);

                double xx = cx + penRadius * Math.cos(Math.toRadians(Math.toDegrees(cAng) - 90));
                double yy = cy + penRadius * Math.sin(Math.toRadians(Math.toDegrees(cAng) - 90));

                double[] newXYA = intersects(inner, cx, cy);
                if (newXYA != null) {
                    switchXY = newXYA;
                    ang = newXYA[2];
                    lastX = newXYA[0];
                    lastY = newXYA[1];

                    opG.setColor(RED);
                    Shape outline1 = new Ellipse2D.Double(switchXY[0] - dotRadius, switchXY[1] - dotRadius, dotRadius * 2, dotRadius * 2);
                    opG.fill(outline1);
                    opG.setColor(CYAN);
                    outline1 = new Ellipse2D.Double(cx - penRadius, cy - penRadius, penRadius * 2, penRadius * 2);
                    opG.draw(outline1);
                    continue;
                }

                opG.setColor(RED);
                opG.fillRect((int) xx, (int) yy, str, str);

                Shape outline = new Ellipse2D.Double(cx - radius, cy - radius, radius * 2, radius * 2);
                if ((int)ang % 90 == 0) {
                    opG.setColor(GREEN);
//                    opG.draw(outline);

                    opG.setColor(CYAN);
                    outline = new Ellipse2D.Double(cx - penRadius, cy - penRadius, penRadius * 2, penRadius * 2);
                    opG.draw(outline);

                    opG.setColor(BLUE);
                    outline = new Ellipse2D.Double(xx - dotRadius, yy - dotRadius, dotRadius * 2, dotRadius * 2);
                    opG.fill(outline);
                }


                ang = ang + Math.toDegrees(cFAng);
                lastX = x;
                lastY = y;

                count++;
            }
        }
    }

    protected double dp(double p) {
        BigDecimal bd = BigDecimal.valueOf(p);
        bd = bd.setScale(3, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private double[] intersects(Shape inner, double cx, double cy) {
        double rad = radius * 0.99;
        Shape outline = new Ellipse2D.Double(cx - rad, cy - rad, rad * 2, rad * 2);
        PathLength pl = new PathLength(outline);


        for (float p = pathInc; p <= pl.lengthOfPath(); p = p + 1) {
            double x = pl.pointAtLength(p).getX();
            double y = pl.pointAtLength(p).getY();
            if (inner.contains(x, y)) {
                double dx= cx - x;
                double dy= cy - y;
                double ang = Math.toDegrees(Math.atan2(dy, dx));
                double[] arr = {x, y, ang};
                return arr;
            }
        }
        return null;
    }

    private void drawAllFromFont() {
        FontRenderContext frc = opG.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, letter);
        Shape glyph = gv.getOutline();
        Rectangle2D rect = glyph.getBounds2D();
        AffineTransform tr = AffineTransform.getTranslateInstance((w - rect.getWidth()) * 0.5, h - rect.getHeight() * 0.5);
        Shape transformedGlyph = tr.createTransformedShape(glyph);

        opG.setColor(BLACK);
        opG.setStroke(new BasicStroke(2f));
        opG.draw(transformedGlyph);

        opG.setColor(RED);
        PathLength pl = new PathLength(transformedGlyph);
        double ang = 0;
        int str = 3;

        double c = Math.PI * 2 * radius;
        double lastX = pl.pointAtLength(0).getX();
        double lastY = pl.pointAtLength(0).getY();
        for (double t = 1; t <= times; t++) {
            for (float p = pathInc; p < pl.lengthOfPath(); p = p + pathInc) {
                double x = pl.pointAtLength(p).getX();
                double y = pl.pointAtLength(p).getY();

                double dx = lastX - x;
                double dy = lastY - y;
                double aa = Math.atan2(dy, dx);
                double perp = aa + Math.PI / 2;

                double z = Math.sqrt(dx * dx + dy * dy);
                double cFAng = (Math.PI * 2 * (pathInc / c));
                double cAng = Math.toRadians(ang) + cFAng;


//            opG.setColor(GREEN);
//            opG.fillRect((int) x, (int) y, str, str);

                opG.setColor(BLUE);
                double xxx = (x + radius * Math.cos(perp));
                double yyy = (y + radius * Math.sin(perp));
                opG.fillRect((int) xxx, (int) yyy, 5, 5);

                double xx = xxx + penRadius * Math.cos(Math.toRadians(Math.toDegrees(cAng) - 90));
                double yy = yyy + penRadius * Math.sin(Math.toRadians(Math.toDegrees(cAng) - 90));

                opG.setColor(RED);
                opG.fillRect((int) xx, (int) yy, str, str);

                ang = ang + Math.toDegrees(cFAng);
                lastX = x;
                lastY = y;
            }
        }
    }

    public Shape parsePathShape(Node svgPath) {
        try {
            String path = svgPath.getNodeValue();

            AWTPathProducer pathProducer = new AWTPathProducer();
            PathParser pathParser = new PathParser();
            pathParser.setPathHandler(pathProducer);
            pathParser.parse(path);
            return pathProducer.getShape();
        } catch (ParseException ex) {
            // Fallback to default square shape if shape is incorrect
            return new Rectangle2D.Float(0, 0, 1, 1);
        }
    }

    void setup() throws IOException, FontFormatException {
        obi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        opG = (Graphics2D) obi.getGraphics();
        opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
                RenderingHints.VALUE_ANTIALIAS_ON);
        opG.setColor(WHITE);
        opG.fillRect(0, 0, w, h);

        font = Font.createFont(Font.TRUETYPE_FONT, new File(dirFont));
        font = font.deriveFont(Font.BOLD, fontSize);
        opG.setFont(font);
    }

    private NodeList readSVG(String fileName) throws SAXException, IOException, XPathExpressionException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(false);
        factory.setValidating(false);
        factory.setFeature("http://xml.org/sax/features/namespaces", false);
        factory.setFeature("http://xml.org/sax/features/validation", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        builder = factory.newDocumentBuilder();
        Document document = builder.parse(fileName);
        String xpathExpression = "//path/@d";
        //String xpathExpression = "path[contains(@id, 'Selection ')]//path/@d";
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        XPathExpression expression = xpath.compile(xpathExpression);

        return (NodeList) expression.evaluate(document, XPathConstants.NODESET);
    }


    private void save() throws Exception {
        File op1 = new File(dir + opFile + ".png");
        savePNGFile(obi, op1, dpi);
    }


}
