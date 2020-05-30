package com.op.infinity;

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
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SVGHershey extends Base {

    private static SVGHershey generate = new SVGHershey();


    private String dir = host + "Hershey/";
    private String tableFile = dir + "glyphTableSimple1";
    private String textFile = dir + "DADOES.txt";
    private String svg = ".svg";
    private String ip = dir + "Andrew.png";
    private BufferedImage ibi;
    private String opF = tableFile + "HersheyOut" + svg;
    private int w = 1000;
    private int h = 1000;
    private DocumentBuilder builder;
    private NodeList svgPaths;
    private double numSVGs = 0;
    private PrintWriter writer;
    private String chars = "0123456789,.;-?\"'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private double scale = 1;

    public static void main(String[] args) throws Exception {
        generate.doGeneration();
    }

    private void doGeneration() throws Exception {
        init();
        initNodes();

        drawAllLetters();
        save();

    }

    String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private void drawAllLetters() throws Exception {
        String message = "We shall all miss you very much Andrew";
        //message = readFile(textFile, Charset.defaultCharset());

        int c = 0;
        int d = 10;
        double xFr = 0.7;
        for (int y = 0; y < h; y = y + d * 2) {
            double yPos = y;
            int xSt = 10 + (int) (5 * Math.random());
            double xPos = xSt;
            for (int x = 0; x < w; x = x + d) {
                int rgb = ibi.getRGB(x, y);
                Color col = new Color(rgb);
                int add = ((col.getRed() + col.getGreen() + col.getBlue()) / 3);
                double grey = ((double) add) / 255;
                double sc = 0.1 + 0.9 * (1 - grey);
                int cc = c % message.length();
                String ch = message.substring(cc, cc + 1);
                String letter = "" + ch;

                if (letter.equals(" ")) {
                    Letter character = drawLetter(0, 0, 0, sc);
                    xPos = xPos + character.w;
                } else {
                    int p = chars.indexOf(letter);
                    Letter character = drawLetter(p, xPos * xFr, yPos, sc);
                    writer.println(character.path);
                    xPos = xPos + character.w;
                }
                c++;
            }
        }
        double sc = 1;
        for (char ch : message.toCharArray()) {
            sc = sc - 0.01;
        }
    }

    private void initNodes() throws SAXException, IOException,
            XPathExpressionException {
        Document document = builder.parse(tableFile + svg);
        String xpathExpression = "//path/@d";
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        XPathExpression expression = xpath.compile(xpathExpression);
        svgPaths = (NodeList) expression.evaluate(document,
                XPathConstants.NODESET);
        numSVGs = (double) svgPaths.getLength();
        System.out.println(numSVGs);
    }

    private Letter drawLetter(int fileNum, double xPos, double yPos, double scale)
            throws NoninvertibleTransformException {
        Node svgPath = svgPaths.item(fileNum);
        String path = svgPath.getNodeValue();

        String character = chars.substring(fileNum, fileNum + 1);
        Shape shape = parsePathShape(path);
        Letter l = new Letter();
        l.character = character + " " + xPos + ":" + yPos;
        l.x = shape.getBounds2D().getX();
        l.y = shape.getBounds2D().getY();
        l.w = shape.getBounds().getWidth();
        l.h = shape.getBounds().getHeight();

        AffineTransform at = new AffineTransform();
        AffineTransform tr = AffineTransform.getTranslateInstance(-l.x, 0);
        AffineTransform sc = AffineTransform.getScaleInstance(1, scale);
        AffineTransform tr2 = AffineTransform.getTranslateInstance(xPos, yPos);
        at.concatenate(tr2);
        at.concatenate(sc);
        at.concatenate(tr);
        PathIterator pi = shape.getPathIterator(at);
        String allParts = makeSvg(pi).toString();

        String l1 = "<path id=\"" + l.character + "\" ";
        String l2 = "d=\"" + allParts + "\" ";
        String l3 = "style=\"fill:none;stroke:#000000\" />";

        l.path = l1 + l2 + l3;

        System.out.println(fileNum + ":" + character);

        return l;
    }

    private StringBuilder makeSvg(PathIterator pi) {
        StringBuilder sb = new StringBuilder();
        double[] c = new double[6];
        while (!pi.isDone()) {
            switch (pi.currentSegment(c)) {
                case PathIterator.SEG_MOVETO:
                    sb.append(String.format("M%.2f,%.2f ", c[0], c[1]));
                    break;
                case PathIterator.SEG_LINETO:
                    sb.append(String.format("L%.2f,%.2f ", c[0], c[1]));
                    break;
                case PathIterator.SEG_QUADTO:
                    sb.append(String.format("Q%.2f,%.2f,%.2f,%.2f ", c[0], c[1], c[2], c[3]));
                    break;
                case PathIterator.SEG_CUBICTO:
                    sb.append(String.format("C%.2f,%.2f,%.2f,%.2f,%.2f,%.2f ", c[0], c[1], c[2], c[3], c[4], c[5]));
                    break;
                case PathIterator.SEG_CLOSE:
                    sb.append("Z");
                    break;
            }
            pi.next();
        }
        return sb;
    }

    private void init() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(false);
        factory.setValidating(false);
        factory.setFeature("http://xml.org/sax/features/namespaces", false);
        factory.setFeature("http://xml.org/sax/features/validation", false);
        factory.setFeature(
                "http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
                false);
        factory.setFeature(
                "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                false);

        builder = factory.newDocumentBuilder();

        writer = new PrintWriter(opF, "UTF-8");
        writer.println("<svg width=\"" + w + "\" height=\"" + h + "\" xmlns=\"http://www.w3.org/2000/svg\">");

        ibi = ImageIO.read(new File(ip));
        w = ibi.getWidth();
        h = ibi.getHeight();
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
        writer.println("</svg>");
        writer.close();
    }

    private class Letter {
        String character = "";
        String path = "";
        double x = 0;
        double y = 0;
        double w = 1;
        double h = 1;
    }

}