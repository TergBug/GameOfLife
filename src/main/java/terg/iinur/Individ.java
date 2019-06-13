package terg.iinur;

import java.util.Random;

public abstract class Individ implements Comparable<Individ>{
	private Random r = new Random();
	private double[] chrom;
	private double fitness;
	private Topology top;
	private TrainSet set;
	private boolean teacher;
	public Individ(Topology top, TrainSet set, boolean teacher) throws Exception {
		reinit(top, set, teacher);
	}
	public void reinit(Topology top, TrainSet set, boolean teacher) throws Exception {
		int countGens = 0;
		this.top = top;
		this.set = set;
		this.teacher = teacher;
		for(int i = 0; i<top.getNumberOfL()-1; i++) {
			countGens+=top.getL(i)*top.getL(i+1);
		}
		this.chrom = new double[countGens];
		if((set.getCount()==1)&&teacher) {
			throw new Exception("Error!");
		}
		
		for(int j = 0; j<countGens; j++) {
			double d = r.nextInt(100);
			chrom[j] = r.nextBoolean() ? d : -1*d;
		}
		
		updata();
	}
	public double[] getChrom() {
		return chrom;
	}
	public Topology getTop() {
		return top;
	}
	public TrainSet getSet() {
		return set;
	}
	public void setSet(TrainSet set) {
		this.set = set;
	}
	public boolean isTeacher() {
		return teacher;
	}
	public void setChrom(double[] in) throws Exception {
		chrom = in;
		updata();
	}
	public double getFitness() {
		return fitness;
	}
	public double[] getOut() {
		NN p = new NN(top);
		return p.work(set.getInput(), chrom);
	}
	public void updata() throws Exception {
		NN p = new NN(top);
		if(teacher) {
			fitness = 0;
			do {
				fitness += Math.pow(p.getError(p.work(set.getInput(), chrom), set.getIdeal()), 2);
			}while(!set.next());
			fitness = Math.sqrt(fitness/set.getCount());
		}
		else {
			fitness = effect(set.getArgs());
		}
	}
	public abstract double effect(double[] TrainSetArgs);
	@Override
	public int compareTo(Individ o) {
		if(fitness<o.getFitness()) {
			return 1;
		}
		else if(fitness>o.getFitness()){
			return -1;
		}
		return 0;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(fitness);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Individ other = (Individ) obj;
		if (Double.doubleToLongBits(fitness) != Double.doubleToLongBits(other.fitness))
			return false;
		return true;
	}
	
}
