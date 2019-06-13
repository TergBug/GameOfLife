package terg.iinur;

public class STopology implements Tops{
	private int[] numbersOnL;
	private int[] sizesOfCores;
	private int[] sizesOfPools;
	public STopology(int[] numbersOnL, int[] sizesOfCores) throws Exception {
		if(numbersOnL.length!=sizesOfCores.length+1) {
			throw new Exception("Error");
		}
		this.numbersOnL = numbersOnL;
		this.sizesOfCores = sizesOfCores;
		sizesOfPools = new int[numbersOnL.length];
		for(int i = 0; i<sizesOfPools.length; i++) {
			sizesOfPools[i] = 1;
		}
	}
	public STopology(int[] numbersOnL, int[] sizesOfCores, int[] sizesOfPools) throws Exception {
		if(numbersOnL.length!=sizesOfCores.length+1) {
			throw new Exception("Error");
		}
		this.numbersOnL = numbersOnL;
		this.sizesOfCores = sizesOfCores;
		this.sizesOfPools = sizesOfPools;
	}
	@Override
	public int getL(int ID) {
		return numbersOnL[ID];
	}
	public int getLConvs(int ID) {
		return numbersOnL[ID+1];
	}
	public int getSizeCore(int ID) {
		return sizesOfCores[ID];
	}
	public int getSizePool(int ID) {
		return sizesOfPools[ID];
	}
	@Override
	public int getNumberOfL() {
		return numbersOnL.length;
	}
	public int getNumberOfConvs() {
		return numbersOnL.length-1;
	}
	public int getNumberOfNums() {
		int sum = 0;
		for(int i = 0; i<sizesOfCores.length; i++) {
			sum+=numbersOnL[i+1]*numbersOnL[i]*Math.pow(sizesOfCores[i], 2);
		}
		return sum;
	}
}
