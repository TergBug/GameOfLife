package terg.iinur;

public class TrainSet {
	private double[][] input;
	private double[][] ideal_args;
	private int count;
	private int ID;
	public TrainSet(double[][] input, double[][] ideal) throws Exception {
		if(input.length!=ideal.length) {
			throw new Exception("Error!");
		}
		this.input = input;
		this.ideal_args = ideal;
		count = ideal.length;
		ID = 0;
	}
	public TrainSet(double[] input, double[] args) throws Exception {
		this.input = new double[1][];
		this.input[0] = input;
		this.ideal_args = new double[1][];
		this.ideal_args[0] = args;
		count = ideal_args.length;
		ID = 0;
	}
	public double[] getInput() {
		return input[ID];
	}
	public double[] getIdeal() {
		return ideal_args[ID];
	}
	public double[] getArgs() {
		return ideal_args[0];
	}
	public int getCount() {
		return count;
	}
	public boolean next() {
		if(ID+1>=input.length) {
			ID = 0;
			return true;
		}
		ID++;
		return false;
	}
	public int getID() {
		return ID;
	}
}
