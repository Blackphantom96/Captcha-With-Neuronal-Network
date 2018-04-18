package Main;

import java.util.Random;

import org.ojalgo.function.NullaryFunction;
import org.ojalgo.random.RandomNumber;

public class RandomDistri extends RandomNumber {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long x,y;
	public RandomDistri(long y, long x) {
		super();
		this.x = x+1;
		this.y = y;
	}

	public double getExpected() {
		return 0;
	}

	@Override
	protected double generate() {
		Random rand = new Random();
		return rand.nextInt((int) (x-y)) + y;
	}

}
