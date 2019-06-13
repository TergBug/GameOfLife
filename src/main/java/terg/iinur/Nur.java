package terg.iinur;

public class Nur {
	private double[] w;
	private int sigmoidK;
	public Nur(double[] w) {
		this.w = w;
		sigmoidK = 1;
	}
	public double[] getW() {
		return w;
	}
	public void setW(double[] w) {
		this.w = w;
	}
	public double getOut(double[] input) throws Exception {
		if(w.length!=input.length) {
			throw new Exception("Error!");
		}
		double sum = 0;
		for(int i = 0; i<input.length; i++) {
			sum+=input[i]*w[i];
		}
		return 1/(1+Math.exp(-sigmoidK*(sum)));
	}
}
