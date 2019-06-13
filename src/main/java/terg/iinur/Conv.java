package terg.iinur;

public class Conv {
	private Tensor core;
	private STopology topology;
	private int ID;
	public Conv(STopology top, int IDofL) throws Exception {
		topology = top;
		if(IDofL<0 && IDofL>=top.getNumberOfConvs()) {
			throw new Exception("Error");
		}
		ID = IDofL;
		core = new Tensor(top.getL(ID), top.getSizeCore(ID), top.getSizeCore(ID), true);
	}
	public Conv(STopology top, int IDofL, Tensor core) throws Exception {
		topology = top;
		if(IDofL<0 && IDofL>=top.getNumberOfConvs()) {
			throw new Exception("Error");
		}
		ID = IDofL;
		this.core = core;
	}
	private int mulTensors(Tensor a, Tensor b) throws Exception {
		if(!a.sEquals(b)) {
			throw new Exception("Error");
		}
		int out = 0;
		for(int k = 0; k<a.getDeep(); k++) {
			for(int i = 0; i<a.getWidth(); i++) {
				for(int j = 0; j<a.getHeight(); j++) {
					out+=a.getTens(k, i, j)*b.getTens(k, i, j);
				}
			}
		}
		return out;
	}
	private int ReLU(int in) {
		return in<0 ? 0 : in;
	}
	private int[][] pooling(int[][] in) {
		//bad point!!!
		int[][] out = new int [in.length/topology.getSizePool(ID)][in[0].length/topology.getSizePool(ID)];
		for(int i = 0; i<in.length; i+=topology.getSizePool(ID)) {
			for(int j = 0; j<in.length; j+=topology.getSizePool(ID)) {
				int max = in[i][j];
				for(int k1 = 0; k1<i+topology.getSizePool(ID); k1++) {
					for(int k2 = 0; k2<j+topology.getSizePool(ID); k2++) {
						max = in[k1][k2]>max ? in[k1][k2] : max;
					}
				}
			}
		}
		return out;
	}
	public int[][] getOut(Tensor in, boolean withPool) throws Exception {
		if(in.getDeep()!=topology.getL(ID)) {
			throw new Exception("Error");
		}
		int[][] out = new int[in.getWidth()-(topology.getSizeCore(ID)-1)][in.getHeight()-(topology.getSizeCore(ID)-1)];
		for(int i = 0; i<out.length; i++) {
			for(int j = 0; j<out[i].length; j++) {
				out[i][j] = ReLU(mulTensors(core, in.getSubTensor(0, core.getDeep(), i, i+core.getWidth(), j, j+core.getHeight())));
			}
		}
		if(withPool) {
			out = pooling(out);
		}
		return out;
	}
	public Tensor getCore() {
		return core;
	}
	public void setCore(Tensor core) {
		this.core = core;
	}
	public int getID() {
		return ID;
	}
}
