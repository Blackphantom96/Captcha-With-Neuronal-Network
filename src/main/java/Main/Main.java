package Main;

import java.net.SocketException;

import org.ujmp.core.*;
import org.ujmp.core.util.matrices.MandelbrotMatrix;

public class Main {

	public static void main(String[] args) throws SocketException {
		// create a matrix from the Mandelbrot set
		Matrix m = new MandelbrotMatrix();

		// show on screen
		m.showGUI();
	}

}
