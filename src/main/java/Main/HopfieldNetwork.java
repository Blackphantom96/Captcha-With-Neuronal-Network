package Main;

import java.util.Arrays;
import java.util.Random;

import org.ojalgo.OjAlgoUtils;
import org.ojalgo.RecoverableCondition;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.decomposition.QR;
import org.ojalgo.matrix.store.ElementsSupplier;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.task.InverterTask;
import org.ojalgo.matrix.task.SolverTask;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.random.Weibull;

public class HopfieldNetwork {
	PhysicalStore.Factory<Double, PrimitiveDenseStore> storeFactory = PrimitiveDenseStore.FACTORY;
	private PrimitiveDenseStore WEIGHT;
	private PrimitiveDenseStore NEURONS;
	private PrimitiveDenseStore PATTERNS;
	private long P;
	private long N;

	public HopfieldNetwork(PrimitiveDenseStore Matrixpatterns) {
		PATTERNS = Matrixpatterns;
		P = PATTERNS.countColumns();
		N = PATTERNS.countRows();
		training();
		for (int i = 0; i < N; i++) {
			WEIGHT.set(i, i, 0);
		}
		NEURONS = storeFactory.makeFilled(N, 1, new RandomDistri(1, 1));
		out();
		System.out.println("Encontre " + P + " patrones -- El Maximo de patrones que puede almacenar es: "
				+ (int) N / (2 * Math.log(N)));
	}

	public void out() {
		PrimitiveDenseStore x = (PrimitiveDenseStore) WEIGHT.multiply(NEURONS); // TODO mirar si es de tamaño Nx1
		for (int i = 0; i < x.countRows(); i++) {
			for (int j = 0; j < x.countColumns(); j++) {
				x.set(i, j, sgn(x.get(i, j)) == 0 ? x.get(i, j) : sgn(x.get(i, j)));
			}
		}
		NEURONS = x;
	}

	public double out(int j) {
		double temp = 0;
		for (int i = 0; i < N; i++) {
			temp += WEIGHT.get(j, i) * NEURONS.get(i, 0);
		}
		return sgn(temp) == 0 ? temp : sgn(temp);
	}

	private double sgn(Double double1) {
		if (double1 == 0)
			return 0;
		return Math.abs(double1) / double1;
	}

	public void training() {
		PrimitiveDenseStore res = (PrimitiveDenseStore) PATTERNS.multiply(PATTERNS.transpose());
		//System.out.println(" entrenando: "+res+Arrays.toString(res.data));
		res = (PrimitiveDenseStore) res.multiply(1 / N); // TODO revisar porque da -0 y 0
		WEIGHT = res; 
	}

	public PrimitiveDenseStore getOut(PrimitiveDenseStore NEURONS1) {
		NEURONS = NEURONS1.copy();
		Random rand = new Random();
		boolean flag = false;
		while (!flag) {
			PrimitiveDenseStore copy = NEURONS.copy();
			boolean[] vals = new boolean[(int) N];
			int values = 0;
			while (values != (int) N) {
				int randNeuron = rand.nextInt((int) N);
				if (!vals[randNeuron]) {
					vals[randNeuron] = true;
					values++;
				}
				double x = out(randNeuron);
				NEURONS.set(randNeuron, 0, x);
			}
			flag = copy.equals(NEURONS);
		}
		return NEURONS;
	}
}
