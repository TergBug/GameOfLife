package terg.iinur;

import java.util.Arrays;
import java.util.Random;

public class Tensor {
	private int[][][] tens;
	private int deep;
	private int width;
	private int height;
	public Tensor(int sdeep, int swidth, int sheight, boolean random) {
		Random r = new Random();
		this.deep = sdeep; this.width = swidth; this.height = sheight;
		tens = new int[sdeep][swidth][sheight];
		for(int k = 0; k<tens.length; k++) {
			for(int i = 0; i<tens[k].length; i++) {
				for(int j = 0; j<tens[k][i].length; j++) {
					if(random) {
						tens[k][i][j] = r.nextBoolean() ? r.nextInt(100) : -1*r.nextInt(100);
					}
					else {
						tens[k][i][j] = 0;
					}
				}
			}
		}
	}
	public Tensor(int[][][] tens) {
		this.tens = tens;
		this.deep = tens.length; this.width = tens[0].length; this.height = tens[0][0].length;
	}
	public int getTens(int deep, int width, int height) {
		return tens[deep][width][height];
	}
	public int getDeep() {
		return deep;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public Tensor getSubTensor(int deep1, int deep2, int width1, int width2, int height1, int height2) {
		Tensor out = new Tensor(Math.abs(deep2-deep1), Math.abs(width2-width1), Math.abs(height2-height1), false);
		for(int k = 0; k<out.getDeep(); k++) {
			for(int i = 0; i<out.getWidth(); i++) {
				for(int j = 0; j<out.getHeight(); j++) {
					int ktens = (deep1<deep2 ? deep1 : deep2)+k;
					int itens = (width1<width2 ? width1 : width2)+i;
					int jtens = (height1<height2 ? height1 : height2)+j;
					out.setTens(k, i, j, tens[ktens][itens][jtens]);
				}
			}
		}
		return out;
	}
	public void setTens(int deep, int width, int height, int value) {
		tens[deep][width][height] = value;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(tens);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tensor other = (Tensor) obj;
		if (!Arrays.deepEquals(tens, other.tens))
			return false;
		return true;
	}
	public boolean sEquals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tensor other = (Tensor) obj;
		if (deep!=other.deep || width!=other.width || height!=other.height)
			return false;
		return true;
	}
}
