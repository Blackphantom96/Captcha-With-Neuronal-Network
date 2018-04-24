package Main;

import static Main.Main.IMAGE_FACTOR;
import static Main.Main.scale;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;

public class Main2 {

    public static double COLOR_SENSIBILITY = 0.5;
    private static PhysicalStore.Factory<Double, PrimitiveDenseStore> storeFactory = PrimitiveDenseStore.FACTORY;
    private static int h, w;

    public static final int START_IMAGE = 1;
    public static final int AMOUNT_IMAGES = 15;

    //private static final List<Integer> SELECTED = Arrays.asList(2, 8, 25, 54, 71, 92, 77);
    public static void main(String[] args) throws IOException {
        for (int cycle = 1; cycle <= 7; cycle++) {
            ArrayList<int[]> patterns = new ArrayList<int[]>();
            for (int c = AMOUNT_IMAGES * cycle + START_IMAGE; c <= AMOUNT_IMAGES * (cycle + 1); c++) {
                //for (Integer c : SELECTED) {
                BufferedImage imgBuffer = ImageIO
                        .read(new File(System.getProperty("user.dir") + "/src/images/dataSetTraining/" + c + ".jpg"));
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

            System.out.println(patternsMatrix);
            System.out.println("TRAINING...");

            HopfieldNetwork net = new HopfieldNetwork(patternsMatrix);

            /* FIN ENTRENAMIENTO */
            for (int c = START_IMAGE + cycle * AMOUNT_IMAGES; c <= AMOUNT_IMAGES * (cycle+1); c++) {
                //for (Integer c : SELECTED) {
                BufferedImage imgDamage = ImageIO.read(new File(System.getProperty("user.dir") + "/src/images/dataSetTest/" + c + ".jpg"));
                PrimitiveDenseStore damageMatrix = convertIntVecToMatrix(toVector(imgDamage));
                System.out.println("TEST #" + c);
                PrimitiveDenseStore res = net.getOut(damageMatrix);
                int[] resInt = printMatrix(res, res.countRows(), res.countColumns());
                BufferedImage finalImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                int pixel = 0;
                for (int i = 0; i < w; i++) {
                    for (int j = 0; j < h; j++) {
                        //System.out.println("The pixel in Matrix: " + resInt[pixel]);
                        finalImage.setRGB(j, i, resInt[pixel++]);
                        //System.out.println("The pixel in BufferedImage: " + finalImage.getRGB(j, i));
                    }
                }
                ImageIO.write(finalImage, "jpg", new File(System.getProperty("user.dir") + "/src/images/out/" + c + ".jpg"));
            }
        }

        System.out.println("THAT'S ALL FOLKS.");
    }

    private static int[] toVector(BufferedImage imgBuffer) {
        int index = 0;
        w = imgBuffer.getWidth();
        h = imgBuffer.getHeight();
        COLOR_SENSIBILITY = 0.0;
        int temp[] = new int[w * h];
        for (int j = 0; j < h; j++) {
            for (int k = 0; k < w; k++) {
                int x = (imgBuffer.getRGB(k, j));
                COLOR_SENSIBILITY += getPixelGrayscaleMeanNormalized(x);
            }
        }

        COLOR_SENSIBILITY /= w * h;

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
        System.out.println(in + " " + row + " " + col);
        int[] res = new int[h * w];
        int count = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                res[count++] = (int) Math.floor(in.get(i, j));
            }
        }
        return res;
    }

}
