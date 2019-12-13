import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

public class HW {

	static int HEIGHT;
	static int WIDTH;

	static int R[][];
	static int G[][];
	static int B[][];

	static int LR[][];
	static int LG[][];
	static int LB[][];

	static BufferedImage buffer;
	static BufferedImage original;

	static MyCanvas canvas;

	public static void main(String args[]) {
		Scanner input = new Scanner(System.in);
		String keyin;

		/*
		 * Necessary AWT/Swing steps.
		 */
		JFrame frame = new JFrame();
		//Splitting it up so i can see a side by side of original and edited version 
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		pane.setDividerLocation(1000);
		canvas = new MyCanvas("edit");
		MyCanvas original = new MyCanvas("original");
		;

		pane.setLeftComponent(new JScrollPane(original));
		pane.setRightComponent(new JScrollPane(canvas));

		frame.getContentPane().add(pane);

		/*
		 * Boilerplate. Just do this. I don't even remember what it all does any more,
		 * but it's required.
		 */
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Change if you like.
		frame.setTitle("Image test");

		/*
		 * Y > HEIGHT because of "Window Decorations" (Java's terminology, not mine.)
		 */

		frame.setSize(WIDTH, HEIGHT + 15);
		frame.setVisible(true);
		while (true) {
			printmenu();
			keyin = input.next();
			switch (keyin) {

			/*
			 * Anything your program "does" goes in here. Right now, read and display
			 */
			case "a":
			case "A":
				antialiasing();
				break;
			case "q":
			case "Q":
				supersample();
				frame.repaint();
				break;
			case "m":
			case "M":
				reduceBits();
				break;
			case "r":
			case "R":
				System.out.printf("Name: ");
				readimage(input.next());
				break;
			case "o":
			case "O":
				rollside();
				break;
			case "u":
			case "U":
				rollup();
				break;
			case "g":
			case "G":
				greyscale();
				break;
			case "s":
			case "S":
				saturation();
				break;
			case "d":
			case "D":
				displayimage();
				break;
			case "x":
			case "X":
				input.close();
				System.exit(0);

			}
			// readimage("balls.png");
			// reduceBits();
		}

	}

	private static void antialiasing() {
		
	

		for (int x = 0; x < HEIGHT; x++) {
			for (int y = 0; y < WIDTH; y++) {
				// Passes only odd x and y values
				if ((x % 2 != 0) && (y % 2 != 0)) {
					R[(int) Math.ceil(x / 2)][(int) Math.ceil(y / 2)] = LR[x][y];
					G[(int) Math.ceil(x / 2)][(int) Math.ceil(y / 2)] = LG[x][y];
					B[(int) Math.ceil(x / 2)][(int) Math.ceil(y / 2)] = LB[x][y];
				}
			}
		}
	}

	private static void supersample() {
		/*
		 * Method that  will scale up the image four times, because i have to.
		 * 
		 */
		Image sc = buffer.getScaledInstance(WIDTH * 4, HEIGHT * 4, BufferedImage.SCALE_FAST);
		BufferedImage upScaled = new BufferedImage(WIDTH * 4, HEIGHT * 4, buffer.getType());
		upScaled.getGraphics().drawImage(sc, 0, 0, null);

		Graphics2D g2d = (Graphics2D) buffer.getGraphics();

		for (int i = 0; i < HEIGHT; i++) {
			int row = i * 4;
			for (int j = 0; j < WIDTH; j++) {
				int col = j * 4;

				long pixel = 0;
				/*
				 *  Here scale is the multiple byte which i increased
				 * the size. So if we up-scale by 4 we need to read 16pixes and take the
				 * average. The color of the new image is set to the average of those pixels.
				 */
				for (int xoff = 0; xoff < 4; xoff++) {
					for (int yoff = 0; yoff < 4; yoff++) {
						pixel += upScaled.getRGB(col + xoff, row + yoff);
					}
				}
				
				pixel = pixel / 16;
				/*
				 * using g2d to draw into the buffer here is actually doing the downscale as
				 * well as averaging all at once
				 */
				g2d.setColor(new Color((int) (pixel)));
				g2d.fillRect(j, i, 1, 1);
				// System.out.println(i + " " + j);

			}

		}
		// displayimage();
	}

	public static void saturation() {

		for (int col = 0; col < WIDTH; col++) {
			// for loop to shift through each column of the pixels
			for (int row = 0; row < HEIGHT; row++) {
				// convert each rgb to matching hsbValues value
				float[] hsbValues = null;

				hsbValues = Color.RGBtoHSB(R[row][col], G[row][col], B[row][col], null);

				float sat = hsbValues[1];

				// increase saturation by 15%
				sat = sat + 15 / 100.0f;

				// convert it back
				int rgb = Color.HSBtoRGB(hsbValues[0], sat, hsbValues[2]);

				// update the arrays
				R[row][col] = (rgb >> 16) & 0xFF;
				G[row][col] = (rgb >> 8) & 0xFF;
				B[row][col] = rgb & 0xFF;
			}
		}
	}

	private static void rollside() {
		for (int height = 0; height < HEIGHT; height++) {
			// loop to shift through pixels
			for (int row = 0; row < HEIGHT; row++) {
				// another for loop for column
				for (int col = 0; col < WIDTH; col++) {
					if ((col + 1) < WIDTH) {
						// shift each each pixel
						R[row][col] = R[row][col + 1];
						G[row][col] = G[row][col + 1];
						B[row][col] = B[row][col + 1];
					} else {
						R[row][col] = R[row][(col + 1) - WIDTH];
						G[row][col] = G[row][(col + 1) - WIDTH];
						B[row][col] = B[row][(col + 1) - WIDTH];
					}
				}
			}

			// draw the image on the canvas
			displayimage();
		}

	}

	public static void rollup() {

		// go through image
		for (int width = 0; width < HEIGHT; width++) {
			// loop to shift values
			for (int row = 0; row < HEIGHT; row++) {
				// another loop but for rows
				for (int col = 0; col < WIDTH; col++) {
					if ((row + 1) < HEIGHT) {
						// shift pixels
						R[row][col] = R[row + 1][col];
						G[row][col] = G[row + 1][col];
						B[row][col] = B[row + 1][col];
					} else {

						R[row][col] = R[(row + 1) - HEIGHT][col];
						G[row][col] = G[(row + 1) - HEIGHT][col];
						B[row][col] = B[(row + 1) - HEIGHT][col];
					}
				}
			}

			displayimage();
		}

	}

	private static void greyscale() {
		for (int row = 0; row < HEIGHT; row++) {
			for (int col = 0; col < WIDTH; col++) {
				int grey_scale_percent = (R[row][col] + G[row][col] + B[row][col]) / 3 * 100;

				// divide by 16

				R[row][col] = grey_scale_percent / 16;
				G[row][col] = grey_scale_percent / 16;
				B[row][col] = grey_scale_percent / 16;
			}
		}

		displayimage();
	}

	public static void printmenu() {
		System.out.printf("Menu:\n");
		System.out.printf("r:\tread image\n");
		System.out.printf("o:\troll image sideways\n");
		System.out.printf("d:\tdisplay image in memory\n");
		System.out.printf("x:\texit\n");
		System.out.printf("g:\tgreyscale the image!\n");
		System.out.printf("u:\troll image up\n");
		System.out.printf("s:\tincrease saturation\n");
		System.out.printf("m:\tReduce colors\n");
		System.out.printf("a:\tAntiAlias image\n");
		System.out.printf("q:\tsupersample image\n");
		System.out.printf("Enter an option: ");

	}

	public static void readimage(String name) {
		int x, y;
		Color c;
		try {
			buffer = ImageIO.read(new File(name));
			original = ImageIO.read(new File(name));
			WIDTH = buffer.getWidth();
			HEIGHT = buffer.getHeight();
			R = new int[HEIGHT][WIDTH];
			G = new int[HEIGHT][WIDTH];
			B = new int[HEIGHT][WIDTH];

			LR = new int[HEIGHT][WIDTH];
			LG = new int[HEIGHT][WIDTH];
			LB = new int[HEIGHT][WIDTH];

			for (x = 0; x < WIDTH; x++) {
				for (y = 0; y < HEIGHT; y++) {
					c = new Color(buffer.getRGB(x, y));

					// RBG pixel locations
					R[y][x] = c.getRed();
					G[y][x] = c.getGreen();
					B[y][x] = c.getBlue();
				}
			}
		} catch (IOException e) {
			System.out.println("HALP ME.");
			e.printStackTrace();
		}
	}

	private static void buildHistogram() {

	}

	public static void reduceBits() {
		/*
		 * The next three lines of the code create maps which have a color value as the key
		 * and the number of pixes that the color appears in as the value. So it's
		 * essentially a histogram using the hashmap.
		 */
		HashMap<Byte, Integer> rMap = new HashMap<>();
		HashMap<Byte, Integer> gMap = new HashMap<>();
		HashMap<Byte, Integer> bMap = new HashMap<>();
//building histogram by going through the pixels by width and height 
		for (int row = 0; row < HEIGHT; row++) {
			for (int col = 0; col < WIDTH; col++) {
				int value = rMap.getOrDefault(R[row][col], 0);
				rMap.put((byte) R[row][col], ++value);

				value = gMap.getOrDefault(G[row][col], 0);
				gMap.put((byte) G[row][col], ++value);

				value = bMap.getOrDefault(B[row][col], 0);
				bMap.put((byte) R[row][col], ++value);
			}
		}
		/*
		 * you can't sort a hashmpa B value. So we convert the keys (which are colors)
		 * into a list.And then in the following step we sort the keys by the number of
		 * times the color occurs in the histogram
		 *
		 * List Byte is form of array, can add content without limit
		 * 
		 */
		List<Byte> red = new ArrayList<>(rMap.keySet());
		List<Byte> green = new ArrayList<>(gMap.keySet());
		List<Byte> blue = new ArrayList<>(bMap.keySet());

		Collections.sort(red, (Comparator<Byte>) (Byte a, Byte b) -> {
			return rMap.get(b) - rMap.get(a);
		});
		Collections.sort(green, (Comparator<Byte>) (Byte a, Byte b) -> {
			return gMap.get(b) - gMap.get(a);
		});
		Collections.sort(blue, (Comparator<Byte>) (Byte a, Byte b) -> {
			return bMap.get(b) - bMap.get(a);
		});

		byte[] selectedRed = new byte[256];
		byte[] selectedGreen = new byte[256];
		byte[] selectedBlue = new byte[256];

		/*
		 * convert the list to an array because the index color model only works with
		 * byte arrays.
		 */
		for (int i = 0; i < 256; i++) {
			try {
				selectedRed[i] = red.get(i);
				selectedGreen[i] = green.get(i);
				selectedBlue[i] = blue.get(i);
			} catch (Exception e) {
				// added a try catch block here.
				// to avoid the error
				selectedRed[i] = (byte) 255;
				selectedGreen[i] = (byte) 255;
				selectedBlue[i] = (byte) 255;
			}
		}
		;

		/// * the first param says use only 8 bits
		IndexColorModel model = new IndexColorModel(8, 256, selectedRed, selectedGreen, selectedBlue, 255);
		/* create another buffered image which of the new color type which is index */
		BufferedImage n = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_INDEXED, model);
		Graphics2D g2d = (Graphics2D) n.getGraphics();
		/*
		 * paint into the new one, the indexed color model will discard all the colors
		 * that are not mappable. and the result is an eight bit image
		 */
		g2d.drawImage(buffer, null, 0, 0);
		buffer = n;
		canvas.repaint();
	}

	public static void displayimage() {
		// resetting the buffer, Also the index access in the loop below was.
		// just matched it with the way it's done in readImage()
		// so that the image is read in the same format as it written. 
		buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				try {
					// find the int value rgb color
					Color c = new Color(R[y][x], G[y][x], B[y][x]);
					// write to buffer
					buffer.setRGB(x, y, c.getRGB());

				} catch (Exception e) {

				}
			}
		}
		// fetch the repaint method
		canvas.repaint();
	}

	/*
	 * Inner classes. Only exist for graphics, as far as I know
	 */
	public static class MyCanvas extends JPanel {
		String name;

		public MyCanvas(String name) {
			super();
			this.name = name;
		}

		public void paint(Graphics g) {
			if (this.name.equals("edit")) {

				g.drawImage(buffer, 0, 0, Color.red, null);
			} else {

				g.drawImage(original, 0, 0, null);
			}
		}
	}
}