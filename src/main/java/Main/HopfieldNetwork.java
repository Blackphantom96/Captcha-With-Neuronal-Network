package Main;

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
		WEIGHT = storeFactory.makeFilled(N, N, new RandomDistri(-P / N, P / N));
		for (int i = 0; i < N; i++) {
			WEIGHT.set(i, i, 0);
		}
		// FIXME poner WEIGHT simetrico => wij = wji
		NEURONS = storeFactory.makeFilled(N, 1, new RandomDistri(-1, 1));
		System.out.println("Max patterns = " + (int) N / (2 * Math.log(N)));
	}

	public void out() {
		PrimitiveDenseStore x = (PrimitiveDenseStore) WEIGHT.multiply(NEURONS); // TODO mirar si es de tamaño Nx1
		for (int i = 0; i < x.countRows(); i++) {
			for (int j = 0; j < x.countColumns(); j++) {
				x.set(i, j, sgn(x.get(i, j)) == 0 ?x.get(i, j):sgn(x.get(i, j)));
			}
		}
		NEURONS = x;
	}

	private double sgn(Double double1) {
		if (double1 == 0)
			return 0;
		return Math.abs(double1) / double1;
	}

	public void training() {
		PrimitiveDenseStore res = (PrimitiveDenseStore) PATTERNS.multiply(PATTERNS.transpose());
		res=(PrimitiveDenseStore) res.multiply(1 / N); // TODO revisar si multiplica el escalar
		WEIGHT = res; // TODO mirar si es de tamaño NxN
	}
}
