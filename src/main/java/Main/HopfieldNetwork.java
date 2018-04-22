package Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.plaf.synth.SynthSpinnerUI;

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

	private static final double EPS = 1e-12;

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
		// for (int i = 0; i < N; i++) {
		// WEIGHT.set(i, i, 0.0);
		// }
		NEURONS = storeFactory.makeFilled(N, 1, new RandomDistri(-1, 1));
		System.out.println("Encontre " + P + " patrones -- El Maximo de patrones que puede almacenar es: "
				+ (int) (N / (2.0 * Math.log(N))));
	}

	public double out(int j) {
		double temp = 0.0;
		for (int i = 0; i < N; i++) {
			temp += WEIGHT.get(j, i) * NEURONS.get(i, 0); // TODO: ver si es ji o ij
		}
		return Math.abs(sgn(temp)) == 0.0 ? NEURONS.get(j, 0) : sgn(temp);
	}

	private double sgn(double x) {
		if (Math.abs(x) < EPS) {
			return 0.0;
		}
		return x < 0.0 ? -1.0 : 1.0;
	}

	public void training() {
		PrimitiveDenseStore res = (PrimitiveDenseStore) PATTERNS.multiply(PATTERNS.transpose());
		res = (PrimitiveDenseStore) res.multiply(1.0 / N);
		WEIGHT = res;
	}

	public void training2() {
		WEIGHT = storeFactory.makeZero(N, N);
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				double res = 0.0;
				if (i == j) {
					res = 0.0;
				} else {
					for (int k = 0; k < P; k++) {
						res += PATTERNS.get(i, k) * PATTERNS.get(j, k);
					}
				}
				WEIGHT.set(j, i, res / N);
			}
		}
	}

	public PrimitiveDenseStore getOut(PrimitiveDenseStore NEURONS1) {
		NEURONS = NEURONS1;
		Random rand = new Random();
		WEIGHT = (PrimitiveDenseStore) PATTERNS.multiply(PATTERNS.transpose());
		WEIGHT = (PrimitiveDenseStore) WEIGHT.multiply(1.0 / N);
		for (int i = 0; i < WEIGHT.countColumns(); i++)
			WEIGHT.set(i, i, 0);
		ArrayList<Integer> randomVector = new ArrayList<Integer>();
		while (randomVector.size() != N) {
			int x = rand.nextInt((int) N);
			if(!randomVector.contains(x))
				randomVector.add(x);
		}
		for(int i =0 ; i< N;i++) {
			int count =0 ;
			int index = randomVector.get(i);
			while(count!=50) {
				Double delta = NEURONS.get(index, 0);
				sgn(index);
				if(delta.equals(NEURONS.get(index, 0)))
					count++;
			}
			
		}
		return NEURONS;
	}
}
