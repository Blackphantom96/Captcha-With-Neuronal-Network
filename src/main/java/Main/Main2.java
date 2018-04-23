package Main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;

public class Main2 {
	public static final double COLOR_SENSIBILITY = 0.5;
	private static PhysicalStore.Factory<Double, PrimitiveDenseStore> storeFactory = PrimitiveDenseStore.FACTORY;
	private static int h,w;
	public static void main(String[] args) throws IOException {
		BufferedImage imgBuffer = ImageIO.read(new File(System.getProperty("user.dir") + "/src/tmp/dog.jpg"));
		h=imgBuffer.getHeight();
		w=imgBuffer.getWidth();
		PrimitiveDenseStore patternsMatrix = convertIntVecToMatrix(toVector(imgBuffer));
		System.out.println("training...");
		HopfieldNetwork hp = new HopfieldNetwork(patternsMatrix);
		BufferedImage imgDamage = ImageIO.read(new File(System.getProperty("user.dir") + "/src/tmp/dogDamage.jpg"));
		PrimitiveDenseStore damageMatrix = convertIntVecToMatrix(toVector(imgDamage));
		System.out.println("testing...");
		PrimitiveDenseStore res = hp.getOut(damageMatrix);
		int[] resInt = printMatrix(res, res.countRows(), res.countColumns());
		BufferedImage finalImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		int pixel=0;
		for (int i = 0; i < w; i++) {
	        for (int j = 0; j < h; j++) {
	            System.out.println("The pixel in Matrix: "+resInt[pixel]);
	            finalImage.setRGB(j, i, resInt[pixel++]);
	            System.out.println("The pixel in BufferedImage: "+finalImage.getRGB(j, i));
	        }
	    }
		ImageIO.write(finalImage,"jpg",new File(System.getProperty("user.dir") + "/src/tmp/dog-out.jpg"));
		System.out.println("end.");
	}
	
	
	private static int[] toVector(BufferedImage imgBuffer) {
		int index = 0;
		w = imgBuffer.getWidth();
		h = imgBuffer.getHeight();
		int temp[] = new int[w * h];
		for (int j = 0; j < h; j++) {
			for (int k = 0; k < w; k++) {
				int x = (imgBuffer.getRGB(k, j));
				temp[index++] = getPixelGrayscaleMeanNormalized(x) < COLOR_SENSIBILITY ? 1 : -1;
			}
		}
		return temp;
	}
	
	private static double getPixelGrayscaleMeanNormalized(int x) {
		double r = 0.0;
		r += (x & ((1 << 8) - 1));
		r += ((x >> 8) & ((1 << 8) - 1));
		r += ((x >> 16) & ((1 << 8) - 1));
		r = Math.min(r / (3.0 * 255.0), 1.0);
		// System.out.println(r);
		return r;
	}
	
	public static void printChar(int[] in, int h, int w) {
		int index = 0;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				System.out.print((in[index++] == 1 ? "*" : ".") + "");
			}
			System.out.println();
		}
		System.out.println("----------------------------");
	}
	
	public static PrimitiveDenseStore convertIntVecToMatrix(int[] vec) {
		PrimitiveDenseStore r = storeFactory.makeZero(h * w, 1);
		for (int i = 0; i < vec.length; i++) {
			r.set(i, 0, vec[i]);
		}
		return r;
	}
	
	public static int[] printMatrix(PrimitiveDenseStore in, long row, long col) {
		System.out.println(in+" "+row+" "+col);
		int[] res = new int[h*w];
		int count=0;
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				res[count++] = (int) Math.floor(in.get(i, j));
			}
		}
		return res;
	}
	
}
