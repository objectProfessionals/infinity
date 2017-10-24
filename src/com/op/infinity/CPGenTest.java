package com.op.infinity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class CPGenTest {
	private static CPGenTest test = new CPGenTest();

	// private String host = "host/infinity/";
	private String host = "host/images/out/misc/infinity/";

	private String file = "vader";
	private String dir = "vader";
	private String srcDir = dir + "/";
	private String svg = ".svg";
	private String dirO = host + "outputs/" + srcDir;
	private String dirI = host + "inputs/" + srcDir;
	private String opF = file + "CPGenTest.png";
	private BufferedImage obi;
	private Graphics2D opG;
	private int w = 100 * 10;
	private int h = 100;

	public static void main(String[] args) throws Exception {
		test.doTest();

	}

	private void doTest() throws Exception {
		init();
		create();

		save();

	}

	private void save() throws Exception {
		File f = new File(dirO + opF);
		FileOutputStream fos = new FileOutputStream(f);
		ImageIO.write(obi, "png", fos);

		System.out.println("saved: " + f.getAbsolutePath());
	}

	private void create() {
		ArrayList<Color> cols = new ArrayList<Color>();

		Color st = Color.decode("#FFFFFF");
		Color en = Color.decode("#110000");
		opG.setColor(st);
		opG.fillRect(0, 0, 100, 100);

		cols.add(st);
		double r1 = st.getRed();
		double g1 = st.getGreen();
		double b1 = st.getBlue();

		double r2 = en.getRed();
		double g2 = en.getGreen();
		double b2 = en.getBlue();

		double n = 8;
		for (double i = 0; i < n; i++) {

			double fac = (i + 1) / (n + 1);
			double r3 = r1 + (r2 - r1) * fac;
			double g3 = g1 + (g2 - g1) * fac;
			double b3 = b1 + (b2 - b1) * fac;

			Color col = new Color((int) r3, (int) g3, (int) b3);
			cols.add(col);

			opG.setColor(col);
			opG.fillRect(100 + ((int) i * 100), 0, 100, 100);
		}

		cols.add(en);
		opG.setColor(en);
		opG.fillRect(900, 0, 100, 100);

		for (Color c : cols) {
			String hex = "#" + Integer.toHexString(c.getRGB()).substring(2);
			System.out.println(hex);
		}
	}

	private void init() {
		obi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		opG = (Graphics2D) obi.getGraphics();
		opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		opG.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		opG.setColor(Color.BLACK);
		opG.fillRect(0, 0, w, h);
	}

}