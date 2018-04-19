package Main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.random.Weibull;

public class Main {

    public static final double COLOR_SENSIBILITY = 0.9;
    public static final int DELTA_X = 12;
    public static final int DELTA_Y = 18;
    public static final int INITIAL_X = 15;
    public static final int INITIAL_Y = 4;
    public static final int LIMIT_X = 75;

    private static char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static int h, w;

    public static void main(String[] args) throws IOException {
        PhysicalStore.Factory<Double, PrimitiveDenseStore> storeFactory = PrimitiveDenseStore.FACTORY;
        ArrayList<int[]> patterns = new ArrayList<int[]>();
        for (char c : chars) {
            //System.out.println(System.getProperty("user.dir") + "/src/images/characters/" + c + ".jpg");
            BufferedImage imgBuffer = ImageIO
                    .read(new File(System.getProperty("user.dir") + "/src/images/characters/" + c + ".png"));
            h = imgBuffer.getHeight();
            w = imgBuffer.getWidth();
            //System.out.println(c+Arrays.toString(toVector(imgBuffer)));
            printChar(toVector(imgBuffer), h, w);
            patterns.add(toVector(imgBuffer));
        }

        /* TODO: BORRAR --------- */
        BufferedImage imgBuffer = ImageIO
                .read(new File(System.getProperty("user.dir") + "/src/images/training_images/" + 1 + ".jpg"));
        int hh = imgBuffer.getHeight();
        int ww = imgBuffer.getWidth();
        printChar(toVector(imgBuffer), hh, ww);
        List<int[]> imgs = getImagePortions(imgBuffer);

        for (int[] img : imgs) {
            printChar(img, 18, 12);
        }

        /* HASTA AQUI ------- */
        PrimitiveDenseStore NEURONS = storeFactory.makeFilled(2, 2, new RandomDistri(1, 1));
        PrimitiveDenseStore x = storeFactory.makeFilled(1, 2, new RandomDistri(1, 1));
        PrimitiveDenseStore a = (PrimitiveDenseStore) x.multiply(NEURONS);
        a = (PrimitiveDenseStore) a.multiply(10);
        a.add(0, 1, 8);
        System.out.println(a);

        PrimitiveDenseStore patternsMatrix = storeFactory.makeZero(w * h, patterns.size());
        for (int i = 0; i < patterns.size(); i++) {
            for (int j = 0; j < w * h; j++) {
                patternsMatrix.set(j, i, patterns.get(i)[j]);
            }
        }
        HopfieldNetwork net = new HopfieldNetwork(patternsMatrix);
    }

    private static double getPixelGrayscaleMeanNormalized(int x) {
        double r = 0.0;
        r += (x & ((1 << 8) - 1));
        r += ((x >> 8) & ((1 << 8) - 1));
        r += ((x >> 16) & ((1 << 8) - 1));
        r = Math.min(r / (3.0 * 255.0), 1.0);
        //System.out.println(r);
        return r;
    }

    private static int[] toVector(BufferedImage imgBuffer) {
        int index = 0;
        int w = imgBuffer.getWidth();
        int h = imgBuffer.getHeight();
        int temp[] = new int[w * h];
        for (int j = 0; j < h; j++) {
            for (int k = 0; k < w; k++) {
                int x = (imgBuffer.getRGB(k, j));
                temp[index++] = getPixelGrayscaleMeanNormalized(x) < COLOR_SENSIBILITY ? 1 : -1;
            }
        }
        return temp;
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

    public static List<int[]> getImagePortions(BufferedImage image) {
        printChar(toVector(image), image.getHeight(), image.getWidth());
        int x = INITIAL_X;
        int y = INITIAL_Y;

        List<int[]> res = new ArrayList<>();

        for (; x < LIMIT_X; x += DELTA_X) {
            res.add(toVector(image.getSubimage(x, y, DELTA_X, DELTA_Y)));
        }

        return res;
    }
}
