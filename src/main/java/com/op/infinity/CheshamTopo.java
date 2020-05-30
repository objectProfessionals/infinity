package com.op.infinity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class CheshamTopo extends Base {
    private static CheshamTopo topo = new CheshamTopo();

    private String dir = host + "map/Chesham/";
    private String ipFileName = "CheshamViewHieghtMap.png";
    private String rangeFileName = "Range-715-157ft.png";
    private String opFileName = "CheshamHeights.png";

    private BufferedImage ibi;
    private Graphics2D ipG;
    private BufferedImage obi;
    private Graphics2D opG;
    private int w = 0;
    private int h = 0;
    private int[] rangesFt = {157, 715};
    private ArrayList<Color> colors;

    public static void main(String[] args) throws Exception {
        topo.generate();
    }

    private void generate() throws Exception {
        init();

        analyse();

        save();
    }

    private void analyse() {

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color col = new Color(ibi.getRGB(x, y));
                float v = (float) getValue(col);
                opG.setColor(new Color(v, v, v));
                opG.fillRect(x, y, 1, 1);
            }
        }
    }

    private void save() throws Exception {
        savePNGFile(obi, new File(dir + opFileName), 300);
    }


    private void init() throws Exception {
        ibi = ImageIO.read(new File(dir + ipFileName));
        ipG = (Graphics2D) ibi.getGraphics();
        w = ibi.getWidth();
        h = ibi.getHeight();


        obi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        opG = (Graphics2D) obi.getGraphics();

        initPallete();

    }

    public double getValue(Color color) {
        return ((double) colors.indexOf(color)) / ((double) (colors.size()));
    }

    public void initPallete() {
        colors = new ArrayList<Color>();
        colors.add(new Color(0, 1, 0));
        colors.add(new Color(120, 131, 241));
        colors.add(new Color(127, 131, 236));
        colors.add(new Color(121, 135, 237));
        colors.add(new Color(129, 137, 234));
        colors.add(new Color(125, 141, 237));
        colors.add(new Color(135, 144, 235));
        colors.add(new Color(130, 149, 238));
        colors.add(new Color(139, 151, 235));
        colors.add(new Color(217, 140, 130));
        colors.add(new Color(212, 142, 130));
        colors.add(new Color(134, 155, 237));
        colors.add(new Color(218, 141, 131));
        colors.add(new Color(220, 142, 127));
        colors.add(new Color(219, 142, 132));
        colors.add(new Color(215, 144, 126));
        colors.add(new Color(216, 145, 133));
        colors.add(new Color(142, 157, 234));
        colors.add(new Color(221, 144, 134));
        colors.add(new Color(218, 146, 128));
        colors.add(new Color(213, 148, 128));
        colors.add(new Color(215, 148, 123));
        colors.add(new Color(220, 145, 140));
        colors.add(new Color(216, 147, 140));
        colors.add(new Color(220, 147, 129));
        colors.add(new Color(219, 148, 136));
        colors.add(new Color(138, 162, 237));
        colors.add(new Color(221, 149, 130));
        colors.add(new Color(222, 150, 131));
        colors.add(new Color(214, 153, 126));
        colors.add(new Color(219, 150, 143));
        colors.add(new Color(219, 152, 126));
        colors.add(new Color(216, 152, 137));
        colors.add(new Color(218, 152, 132));
        colors.add(new Color(211, 156, 126));
        colors.add(new Color(146, 165, 235));
        colors.add(new Color(221, 152, 145));
        colors.add(new Color(221, 154, 128));
        colors.add(new Color(220, 154, 134));
        colors.add(new Color(220, 153, 151));
        colors.add(new Color(222, 155, 129));
        colors.add(new Color(223, 153, 146));
        colors.add(new Color(218, 157, 129));
        colors.add(new Color(142, 169, 238));
        colors.add(new Color(222, 156, 136));
        colors.add(new Color(217, 156, 152));
        colors.add(new Color(219, 156, 147));
        colors.add(new Color(212, 162, 124));
        colors.add(new Color(222, 159, 125));
        colors.add(new Color(220, 159, 131));
        colors.add(new Color(217, 161, 124));
        colors.add(new Color(219, 160, 137));
        colors.add(new Color(220, 159, 155));
        colors.add(new Color(150, 172, 235));
        colors.add(new Color(222, 161, 133));
        colors.add(new Color(220, 164, 128));
        colors.add(new Color(219, 164, 133));
        colors.add(new Color(146, 176, 238));
        colors.add(new Color(223, 162, 158));
        colors.add(new Color(219, 167, 123));
        colors.add(new Color(217, 167, 129));
        colors.add(new Color(214, 169, 123));
        colors.add(new Color(223, 166, 129));
        colors.add(new Color(224, 167, 131));
        colors.add(new Color(154, 179, 235));
        colors.add(new Color(220, 170, 131));
        colors.add(new Color(221, 167, 161));
        colors.add(new Color(222, 171, 126));
        colors.add(new Color(222, 168, 168));
        colors.add(new Color(149, 183, 238));
        colors.add(new Color(222, 172, 133));
        colors.add(new Color(219, 174, 128));
        colors.add(new Color(225, 173, 129));
        colors.add(new Color(216, 177, 129));
        colors.add(new Color(224, 177, 124));
        colors.add(new Color(222, 177, 130));
        colors.add(new Color(157, 186, 234));
        colors.add(new Color(221, 177, 136));
        colors.add(new Color(219, 179, 124));
        colors.add(new Color(221, 174, 170));
        colors.add(new Color(224, 179, 132));
        colors.add(new Color(217, 183, 127));
        colors.add(new Color(153, 191, 238));
        colors.add(new Color(223, 182, 127));
        colors.add(new Color(226, 183, 122));
        colors.add(new Color(222, 183, 134));
        colors.add(new Color(160, 193, 235));
        colors.add(new Color(222, 186, 123));
        colors.add(new Color(225, 185, 130));
        colors.add(new Color(160, 195, 230));
        colors.add(new Color(222, 187, 130));
        colors.add(new Color(227, 186, 131));
        colors.add(new Color(225, 189, 126));
        colors.add(new Color(157, 198, 239));
        colors.add(new Color(220, 191, 126));
        colors.add(new Color(224, 190, 132));
        colors.add(new Color(168, 199, 216));
        colors.add(new Color(227, 191, 128));
        colors.add(new Color(224, 193, 122));
        colors.add(new Color(219, 195, 122));
        colors.add(new Color(226, 192, 135));
        colors.add(new Color(164, 201, 236));
        colors.add(new Color(224, 194, 129));
        colors.add(new Color(229, 193, 129));
        colors.add(new Color(170, 202, 231));
        colors.add(new Color(164, 204, 232));
        colors.add(new Color(228, 197, 125));
        colors.add(new Color(226, 197, 131));
        colors.add(new Color(223, 199, 126));
        colors.add(new Color(222, 199, 131));
        colors.add(new Color(166, 207, 221));
        colors.add(new Color(229, 199, 133));
        colors.add(new Color(231, 199, 128));
        colors.add(new Color(226, 202, 128));
        colors.add(new Color(225, 202, 134));
        colors.add(new Color(221, 204, 128));
        colors.add(new Color(168, 209, 237));
        colors.add(new Color(229, 203, 123));
        colors.add(new Color(224, 205, 123));
        colors.add(new Color(174, 210, 232));
        colors.add(new Color(229, 204, 130));
        colors.add(new Color(227, 204, 137));
        colors.add(new Color(232, 206, 125));
        colors.add(new Color(228, 208, 126));
        colors.add(new Color(226, 208, 133));
        colors.add(new Color(171, 216, 236));
        colors.add(new Color(233, 208, 134));
        colors.add(new Color(173, 217, 225));
        colors.add(new Color(225, 212, 128));
        colors.add(new Color(221, 214, 122));
        colors.add(new Color(232, 211, 122));
        colors.add(new Color(231, 211, 129));
        colors.add(new Color(227, 213, 123));
        colors.add(new Color(177, 219, 220));
        colors.add(new Color(178, 218, 233));
        colors.add(new Color(233, 213, 131));
        colors.add(new Color(231, 213, 137));
        colors.add(new Color(228, 215, 131));
        colors.add(new Color(177, 222, 216));
        colors.add(new Color(188, 229, 118));
        colors.add(new Color(186, 229, 124));
        colors.add(new Color(184, 229, 131));
        colors.add(new Color(231, 216, 126));
        colors.add(new Color(183, 230, 138));
        colors.add(new Color(174, 223, 236));
        colors.add(new Color(184, 232, 119));
        colors.add(new Color(182, 230, 145));
        colors.add(new Color(227, 219, 126));
        colors.add(new Color(192, 231, 113));
        colors.add(new Color(234, 218, 121));
        colors.add(new Color(180, 225, 213));
        colors.add(new Color(183, 233, 126));
        colors.add(new Color(231, 218, 134));
        colors.add(new Color(192, 232, 121));
        colors.add(new Color(188, 232, 134));
        colors.add(new Color(234, 219, 128));
        colors.add(new Color(188, 234, 115));
        colors.add(new Color(186, 234, 121));
        colors.add(new Color(199, 231, 114));
        colors.add(new Color(180, 225, 232));
        colors.add(new Color(231, 221, 122));
        colors.add(new Color(187, 231, 153));
        colors.add(new Color(198, 231, 121));
        colors.add(new Color(191, 233, 128));
        colors.add(new Color(182, 235, 135));
        colors.add(new Color(187, 233, 141));
        colors.add(new Color(187, 235, 123));
        colors.add(new Color(185, 235, 129));
        colors.add(new Color(230, 222, 130));
        colors.add(new Color(195, 232, 135));
        colors.add(new Color(185, 226, 227));
        colors.add(new Color(185, 234, 148));
        colors.add(new Color(206, 231, 116));
        colors.add(new Color(196, 234, 116));
        colors.add(new Color(194, 234, 123));
        colors.add(new Color(236, 221, 130));
        colors.add(new Color(234, 221, 136));
        colors.add(new Color(205, 231, 123));
        colors.add(new Color(187, 233, 161));
        colors.add(new Color(183, 235, 155));
        colors.add(new Color(211, 230, 124));
        colors.add(new Color(179, 229, 229));
        colors.add(new Color(189, 237, 124));
        colors.add(new Color(189, 232, 174));
        colors.add(new Color(185, 234, 168));
        colors.add(new Color(203, 234, 117));
        colors.add(new Color(201, 234, 124));
        colors.add(new Color(199, 234, 130));
        colors.add(new Color(195, 236, 124));
        colors.add(new Color(214, 231, 118));
        colors.add(new Color(229, 226, 125));
        colors.add(new Color(227, 226, 131));
        colors.add(new Color(198, 236, 118));
        colors.add(new Color(183, 235, 175));
        colors.add(new Color(234, 225, 125));
        colors.add(new Color(183, 229, 236));
        colors.add(new Color(188, 231, 205));
        colors.add(new Color(233, 225, 132));
        colors.add(new Color(187, 234, 181));
        colors.add(new Color(185, 234, 188));
        colors.add(new Color(210, 234, 119));
        colors.add(new Color(208, 234, 126));
        colors.add(new Color(182, 235, 195));
        colors.add(new Color(189, 233, 195));
        colors.add(new Color(205, 236, 119));
        colors.add(new Color(234, 226, 133));
        colors.add(new Color(186, 231, 225));
        colors.add(new Color(203, 236, 126));
        colors.add(new Color(186, 234, 201));
        colors.add(new Color(238, 227, 120));
        colors.add(new Color(236, 227, 127));
        colors.add(new Color(187, 233, 214));
        colors.add(new Color(220, 232, 127));
        colors.add(new Color(183, 235, 208));
        colors.add(new Color(217, 234, 120));
        colors.add(new Color(184, 234, 220));
        colors.add(new Color(183, 233, 233));
        colors.add(new Color(217, 235, 115));
        colors.add(new Color(216, 234, 128));
        colors.add(new Color(212, 236, 120));
        colors.add(new Color(210, 236, 127));
        colors.add(new Color(227, 231, 128));
        colors.add(new Color(225, 231, 134));
        colors.add(new Color(223, 233, 121));
        colors.add(new Color(181, 236, 215));
        colors.add(new Color(238, 228, 128));
        colors.add(new Color(236, 228, 135));
        colors.add(new Color(234, 230, 122));
        colors.add(new Color(232, 230, 128));
        colors.add(new Color(218, 235, 122));
        colors.add(new Color(229, 232, 122));
        colors.add(new Color(213, 237, 122));
        colors.add(new Color(211, 237, 128));
        colors.add(new Color(181, 236, 228));
        colors.add(new Color(221, 234, 135));
        colors.add(new Color(219, 236, 123));
        colors.add(new Color(232, 231, 136));
        colors.add(new Color(226, 235, 124));
        colors.add(new Color(224, 235, 130));
        colors.add(new Color(218, 237, 130));
        colors.add(new Color(235, 232, 131));
        colors.add(new Color(231, 234, 124));
        colors.add(new Color(221, 237, 124));
        colors.add(new Color(228, 236, 117));
        colors.add(new Color(230, 234, 131));
        colors.add(new Color(228, 234, 137));
        colors.add(new Color(237, 233, 125));
        colors.add(new Color(233, 235, 125));
        colors.add(new Color(227, 237, 125));
        colors.add(new Color(226, 237, 132));
        colors.add(new Color(237, 234, 133));
        colors.add(new Color(228, 238, 126));
        colors.add(new Color(239, 235, 127));
        colors.add(new Color(234, 237, 127));
        colors.add(new Color(233, 237, 133));
    }


}
