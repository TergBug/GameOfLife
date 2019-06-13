package terg.iinur;

public class Topology implements Tops{
	private int[] numbersOnL;
	private int numberOfL;
	public Topology(int[] numbersOnL) {
		this.numbersOnL = numbersOnL;
		numberOfL = numbersOnL.length;
	}
	@Override
	public int getL(int ID) {
		return numbersOnL[ID];
	}
	@Override
	public int getNumberOfL() {
		return numberOfL;
	}
}
