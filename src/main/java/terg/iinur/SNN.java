package terg.iinur;

public class SNN implements Network{
	private Conv[][] convs;
	private STopology top;
	public SNN(STopology top) {
		this.top = top;
		convs = new Conv[top.getNumberOfConvs()][];
		for(int i = 0; i<convs.length; i++) {
			convs[i] = new Conv[top.getLConvs(i)];
			for(int j = 0; j<convs[i].length; j++) {
				try {
					convs[i][j] = new Conv(top, i);
				} catch (Exception e) {	e.printStackTrace(); }
			}
		}
	}
	public SNN(STopology top, double[] chrom) {
		this.top = top;
		convs = new Conv[top.getNumberOfConvs()][];
		try {
			Tensor[][] cores = getTensFromChrom(chrom);
			for(int i = 0; i<convs.length; i++) {
				convs[i] = new Conv[top.getLConvs(i)];
				for(int j = 0; j<convs[i].length; j++) {
					convs[i][j] = new Conv(top, i, cores[i][j]);
				}
			}
		} catch (Exception e) {	e.printStackTrace(); }
	}
	private Tensor[][] getTensFromChrom(double[] chrom) throws Exception {
		if(chrom.length!=top.getNumberOfNums()) {
			throw new Exception("Error");
		}
		Tensor[][] out = new Tensor[top.getNumberOfConvs()][];
		int c = 0;
		for(int i = 0; i<out.length; i++) {
			out[i] = new Tensor[top.getLConvs(i)];
			for(int j = 0; j<out[i].length; j++) {
				out[i][j] = new Tensor(top.getL(i), top.getSizeCore(i), top.getSizeCore(i), false);
				for(int k1 = 0; k1<out[i][j].getDeep(); k1++) {
					for(int k2 = 0; k2<out[i][j].getWidth(); k2++) {
						for(int k3 = 0; k3<out[i][j].getHeight(); k3++) {
							out[i][j].setTens(k1, k2, k3, (int) chrom[c++]);
						}
					}
				}
			}
		}
		return out;
	}
	@Override
	public double[] getChrom() {
		double[] out = new double[top.getNumberOfNums()];
		int c = 0;
		for(int i = 0; i<convs.length; i++) {
			for(int j = 0; j<convs[i].length; j++) {
				Tensor core = convs[i][j].getCore();
				for(int k1 = 0; k1<core.getDeep(); k1++) {
					for(int k2 = 0; k2<core.getWidth(); k2++) {
						for(int k3 = 0; k3<core.getHeight(); k3++) {
							out[c++] = core.getTens(k1, k2, k3);
						}
					}
				}
			}
		}
		return out;
	}

	@Override
	public double[] work(double[] input) {
		return null;
	}

	@Override
	public double[] work(double[] input, double[] chrom) {
		return null;
	}

}
