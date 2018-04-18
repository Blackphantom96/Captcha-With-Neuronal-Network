package Main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.random.Weibull;

public class Main {
	private static char[] chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static int h,w;
	public static void main(String[] args) throws IOException {
		PhysicalStore.Factory<Double, PrimitiveDenseStore> storeFactory = PrimitiveDenseStore.FACTORY;
		for (char c : chars) {
			//System.out.println(System.getProperty("user.dir") + "/src/images/characters/" + c + ".jpg");
			BufferedImage imgBuffer = ImageIO
					.read(new File(System.getProperty("user.dir") + "/src/images/characters/" + c + ".jpg"));
			h=imgBuffer.getHeight();
			w=imgBuffer.getWidth();
			//System.out.println(c+Arrays.toString(toVector(imgBuffer)));
			printChar(toVector(imgBuffer));
		}

		
		
		
		
		PrimitiveDenseStore NEURONS = storeFactory.makeFilled(2, 2, new RandomDistri(1, 1));
		PrimitiveDenseStore x = storeFactory.makeFilled(1, 2, new RandomDistri(1, 1));
		PrimitiveDenseStore a = (PrimitiveDenseStore) x.multiply(NEURONS);
		a = (PrimitiveDenseStore) a.multiply(10);
		a.add(0, 1, 8);
		System.out.println(a);
	}
	
	private static int[] toVector(BufferedImage imgBuffer) {
		int index=0;
		int temp[] = new int[w*h];
		for (int j = 0; j < h; j++) {
			for (int k = 0; k < w; k++) {
				int x = (imgBuffer.getRGB(k, j));
				temp[index++] = x==-1?-1:1;
			}
		}
		return temp;
	}
	public static void printChar(int[] in) {
		int index=0;
		for(int i=0; i<h;i++) {
			for(int j=0;j<w;j++) {
				System.out.print((in[index++]==1?"*":".") +"");
			}
			System.out.println();
		}
		System.out.println("----------------------------");
	}

}
