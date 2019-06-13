package terg.iinur;

public class NN implements Network{
	private Nur layers[][];
	private Topology top;
	public NN(Topology in) {
		top = in;
		layers = new Nur[top.getNumberOfL()][];
		for(int i = 0; i<layers.length; i++) {
			layers[i] = new Nur[top.getL(i)];
			for(int j = 0; j<layers[i].length; j++) {
				if(i == 0) {
					layers[i][j] = new Nur(new double[] {1.0f});
				}
				else {
					layers[i][j] = new Nur(new double[layers[i-1].length]);
				}
			}
		}
	}
	public NN(Topology in, double[] chrom) {
		top = in;
		layers = new Nur[top.getNumberOfL()][];
		double[][][] w = extractW(chrom);
		for(int i = 0; i<layers.length; i++) {
			layers[i] = new Nur[top.getL(i)];
			for(int j = 0; j<layers[i].length; j++) {
				if(i == 0) {
					layers[i][j] = new Nur(new double[] {1.0f});
				}
				else {
					layers[i][j] = new Nur(w[i-1][j]);
				}
			}
		}
	}
	@Override
	public double[] getChrom() {
		int countGens = 0;
		for(int i = 0; i<top.getNumberOfL()-1; i++) {
			countGens+=top.getL(i)*top.getL(i+1);
		}
		double[] out = new double[countGens];
		int t = 0;
		for(int i = 1; i<layers.length; i++) {
			for(int j = 0; j<layers[i].length; j++) {
				for(int k = 0; k<layers[i-1].length; k++) {
					out[t] = layers[i][j].getW()[k];
					t++;
				}
			}
		}
		return out;
	}
	private double[][][] extractW(double[] chrom){
		double[][][] out = new double[layers.length-1][][];
		int marker = 0;
		for(int i = 0; i<out.length; i++) {
			out[i] = new double[top.getL(i+1)][];
			for(int j = 0; j<out[i].length; j++) {
				out[i][j] = new double[top.getL(i)];
				for(int k = 0; k<out[i][j].length; k++) {
					out[i][j][k] = chrom[marker];
					marker++;
				}
			}
		}
		return out;
	}
	@Override
	public double[] work(double[] input) {
		try {
			if(layers[0].length!=input.length) {
				throw new Exception("Error!");
			}
			double[][] out = new double[layers.length][];
			for(int i = 0; i<layers.length; i++) {
				out[i] = new double[layers[i].length];
				for(int j = 0; j<layers[i].length; j++) {
					out[i][j] = layers[i][j].getOut(i==0 ? new double[] {input[j]} : out[i-1]);
				}
			}
			return out[layers.length-1];
		} catch (Exception e) {	e.printStackTrace(); return null;}
	}
	@Override
	public double[] work(double[] input, double[] chrom) {
		try {
			int countGens = 0;
			for(int i = 0; i<top.getNumberOfL()-1; i++) {
				countGens+=top.getL(i)*top.getL(i+1);
			}
			if(layers[0].length!=input.length || chrom.length!=countGens) {
				throw new Exception("Error!");
			}
			double[][][] w = extractW(chrom);
			double[][] out = new double[layers.length][];
			for(int i = 0; i<layers.length; i++) {
				out[i] = new double[layers[i].length];
				for(int j = 0; j<layers[i].length; j++) {
					if(i!=0) {
						layers[i][j].setW(w[i-1][j]);
					}
					out[i][j] = layers[i][j].getOut(i==0 ? new double[] {input[j]} : out[i-1]);
				}
			}
			return out[layers.length-1];
		} catch (Exception e) {	e.printStackTrace(); return null;}
		
	}
	public double getError(double[] outO, double[] IDEAL) throws Exception {
		if(IDEAL.length!=outO.length) {
			throw new Exception("Error!");
		}
		double sum = 0;
		for(int i = 0; i<IDEAL.length; i++) {
			sum+=Math.pow(IDEAL[i]-outO[i], 2);
		}
		return 1-Math.sqrt(sum/IDEAL.length);
	}
}
