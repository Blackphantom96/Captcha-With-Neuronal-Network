package Main;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.random.Weibull;

public class Main {

    public static final int IMAGE_FACTOR = 4;
    public static final double COLOR_SENSIBILITY = 0.91;
    public static final int DELTA_X = 12 * IMAGE_FACTOR;
    public static final int DELTA_Y = 18 * IMAGE_FACTOR;
    public static final int INITIAL_X = 15 * IMAGE_FACTOR;
    public static final int INITIAL_Y = 4 * IMAGE_FACTOR;
    public static final int LIMIT_X = 75 * IMAGE_FACTOR;

    private static char[] chars = {'8', '3', '0', '2', '1'};
    private static int h, w;

    private static PhysicalStore.Factory<Double, PrimitiveDenseStore> storeFactory = PrimitiveDenseStore.FACTORY;

    public static void main(String[] args) throws IOException {
        ArrayList<int[]> patterns = new ArrayList<int[]>();
        for (char c : chars) {
            BufferedImage imgBuffer = ImageIO
                    .read(new File(System.getProperty("user.dir") + "/src/images/characters/" + c + ".png"));
            h = imgBuffer.getHeight();
            w = imgBuffer.getWidth();
            h *= IMAGE_FACTOR;
            w *= IMAGE_FACTOR;
            imgBuffer = scale(imgBuffer, w, h);
            //printChar(toVector(imgBuffer), h, w);
            patterns.add(toVector(imgBuffer));
        }

        PrimitiveDenseStore patternsMatrix = storeFactory.makeZero(w * h, patterns.size());
        for (int i = 0; i < patterns.size(); i++) {
            for (int j = 0; j < w * h; j++) {
                patternsMatrix.set(j, i, patterns.get(i)[j]);
            }
        }
        HopfieldNetwork net = new HopfieldNetwork(patternsMatrix);

        /* TODO: BORRAR --------- */
        BufferedImage imgBuffer = ImageIO
                .read(new File(System.getProperty("user.dir") + "/src/images/training_images/" + "48" + ".jpg"));
        int hh = imgBuffer.getHeight();
        int ww = imgBuffer.getWidth();
        hh *= IMAGE_FACTOR;
        ww *= IMAGE_FACTOR;
        imgBuffer = scale(imgBuffer, ww, hh);
        //printChar(toVector(imgBuffer), hh, ww);
        List<int[]> imgs = getImagePortions(imgBuffer);

        for (int[] img : imgs) {
            System.out.println("entrada");
            printChar(img, h, w);
            System.out.println("saldia");
            printChar(net.getOut(convertIntVecToMatrix(img)), h, w);
        }

        /* HASTA AQUI ------- */
        PrimitiveDenseStore NEURONS = storeFactory.makeFilled(2, 2, new RandomDistri(1, 1));
        PrimitiveDenseStore x = storeFactory.makeFilled(1, 2, new RandomDistri(1, 1));
        PrimitiveDenseStore a = (PrimitiveDenseStore) x.multiply(NEURONS);
        a = (PrimitiveDenseStore) a.multiply(10);
        a.add(0, 1, 8);
        System.out.println(a);
    }

    public static BufferedImage scale(BufferedImage src, int newW, int newH) {
        Image tmp = src.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public static PrimitiveDenseStore convertIntVecToMatrix(int[] vec) {
        PrimitiveDenseStore r = storeFactory.makeZero(h * w, 1);
        for (int i = 0; i < vec.length; i++) {
            r.set(i, 0, vec[i]);
        }
        return r;
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

    public static void printChar(PrimitiveDenseStore in, int h, int w) {
        int index = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                System.out.print((in.get(index++, 0) == 1 ? "*" : ".") + "");
            }
            System.out.println();
        }
        System.out.println("----------------------------");
    }

    public static void printMatrix(PrimitiveDenseStore in, long l, long m) {
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < m; j++) {
                System.out.printf("%.3f ", in.get(i, j));
            }
            System.out.println();
        }
        System.out.println("----------------------------");
    }

    public static List<int[]> getImagePortions(BufferedImage image) {
        // printChar(toVector(image), image.getHeight(), image.getWidth());
        int x = INITIAL_X;
        int y = INITIAL_Y;

        List<int[]> res = new ArrayList<int[]>();

        for (; x < LIMIT_X; x += DELTA_X) {
            res.add(toVector(image.getSubimage(x, y, DELTA_X, DELTA_Y)));
        }

        return res;
    }
}
