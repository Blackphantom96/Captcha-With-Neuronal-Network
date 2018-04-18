package Main;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.random.Weibull;

public class Main {

	public static void main(String[] args) {
		
		PhysicalStore.Factory<Double, PrimitiveDenseStore> storeFactory = PrimitiveDenseStore.FACTORY;
		PrimitiveDenseStore NEURONS = storeFactory.makeFilled(2, 2, new RandomDistri(1,1));
		PrimitiveDenseStore x = storeFactory.makeFilled(1, 2, new RandomDistri(1,1));
		PrimitiveDenseStore a = (PrimitiveDenseStore) x.multiply(NEURONS);
		a=(PrimitiveDenseStore) a.multiply(10);
		a.add(0, 1, 8);
		System.out.println(a);
	}

}
