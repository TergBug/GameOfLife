package terg.iinur;

public interface Network {
	double[] getChrom();
	double[] work(double[] input);
	double[] work(double[] input, double[] chrom);
}
