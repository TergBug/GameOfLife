package terg.iinur;

import java.util.ArrayList;
import java.util.Random;

public class Evol {
	private Random r = new Random();
	private int ID;
	private Individ individ;
	private ArrayList<Individ> population;
	private ArrayList<Individ> localpop;
	private int sizeOfPopulation;
	private boolean isSelected;
	public Evol(int sizeOfPopulation, Individ individ) throws Exception {
		this.sizeOfPopulation = sizeOfPopulation;
		this.individ = individ;
		population = new ArrayList<Individ>();
		for(int i = 0; i<sizeOfPopulation; i++) {
			individ.reinit(individ.getTop(), individ.getSet(), individ.isTeacher());
			population.add(individ);
		}
		population.trimToSize();
		isSelected = false;
	}
	public Evol(ArrayList<Individ> population) throws Exception {
		this.population = population;
		this.individ = new Individ(population.get(0).getTop(), new TrainSet(new double[] {0}, new double[] {0}), false) {
			@Override
			public double effect(double[] TrainSetArgs) {
				return 0;
			}
		};
		sizeOfPopulation = population.size();
		isSelected = false;
	}
	public void givePopulation(ArrayList<Individ> population) {
		this.population = population;
		sizeOfPopulation = population.size();
	}
	public int getSizeOfPopulation() {
		return sizeOfPopulation;
	}
	private void select() throws Exception {
		double avg = 0.0;
		double sig = 0.0;
		for(int i = 0; i<population.size(); i++) {
			population.get(i).updata();
			avg+=population.get(i).getFitness();
		}
		avg=avg/population.size();
		for(int i = 0; i<population.size(); i++) {
			sig+=Math.pow(population.get(i).getFitness()-1, 2);
		}
		sig = Math.sqrt(sig/population.size());
		localpop = new ArrayList<Individ>();
		for(int i = 0; i<population.size(); i++) {
			//float pi = (1+((population.get(i).getFitness()-avg)/(2*sig)))/fi;
			//float pi = population.get(i).getFitness()+(avg-3*sig);
			if(r.nextFloat()<=population.get(i).getFitness()/avg) {
				localpop.add(population.get(i));
			}
		}
		localpop.trimToSize();
		population.clear();
		isSelected = true;
	}
	private void cross() throws Exception{
		do {
			if(!isSelected) {
				localpop = new ArrayList<Individ>();
				for(int i = 0; i<population.size(); i++) {
					localpop.add(population.get(i));
				}
				localpop.trimToSize();
				population.clear();
			}
			int p = localpop.size();
			for(int i = 0; i<p; i++) {
				int fthi = r.nextInt(p);
				Individ fth = localpop.get(fthi);
				Individ mth = localpop.get(r.nextInt(p));
				double[] sth = new double[mth.getChrom().length];
				double[] dth = new double[mth.getChrom().length];
				for(int k = 0; k<sth.length; k++) {
					sth[k] = r.nextBoolean() ? fth.getChrom()[k] : mth.getChrom()[k];
					dth[k] = r.nextBoolean() ? fth.getChrom()[k] : mth.getChrom()[k];
				}
				individ.setChrom(sth);
				localpop.set(fthi, individ);
				individ.setChrom(dth);
				localpop.add(individ);
			}
			localpop.trimToSize();
		}while(localpop.size()<sizeOfPopulation);
	}
	private void mutation() throws Exception{
		int k = r.nextInt(localpop.size());
		for(int i = 0; i<k; i++) {
			int c = r.nextInt(localpop.size());
			double[] ch = localpop.get(c).getChrom();
			int kj = r.nextInt((int) (0.6*ch.length));
			for(int j = 0; j<kj; j++) {
				int q = r.nextInt(ch.length);
				switch(r.nextInt(4)) {
				case 0:
					int p = r.nextInt(5);
					if(ch[q]<100-p && ch[q]>p-100) {
						ch[q] = r.nextBoolean() ? ch[q]+p : ch[q]-p;
					}
					break;
				case 1:
					int q1 = r.nextInt(ch.length);
					ch[q] = ch[q1]+ch[q];
					ch[q1] = ch[q]-ch[q1];
					ch[q] = ch[q]-ch[q1];
					break;
				case 2:
					ch[q]*=-1;
					break;
				case 3:
					double d = r.nextInt(100);
					ch[q] = r.nextBoolean() ? d : -1*d;
					break;
				}
			}
			localpop.get(c).setChrom(ch);
		}
	}
	private void cut(boolean erase) throws Exception{
		if(erase) {
			localpop.sort(null);
			for(int i = 0; i<localpop.size(); i++) {
				for(int j = i+1; j<localpop.size(); j++) {
					if(localpop.get(i).equals(localpop.get(j))) {
						localpop.remove(j);
						j--;
					}
				}
			}
			if(localpop.size()<sizeOfPopulation) {
				sizeOfPopulation=localpop.size();
			}
		}
		for(int i = 0; i<localpop.size(); i++) {
			population.add(localpop.get(i));
		}
		localpop.clear();
	}
	public NN fullTrain() throws Exception {
		do {
			select();
			cross();
			mutation();
			cut(true);
//			for(int j = 0; j<population.size(); j++) {
//				System.out.print("Error: "+population.get(j).getFitness()+"  "+"Chroms: ");
//				for(int k = 0; k<population.get(j).getChrom().length; k++) {
//					System.out.print(population.get(j).getChrom()[k]+"  ");
//				}
//				System.out.println("");
//			}
//			System.out.println("\n");
		}while(population.get(0).getFitness()<=0.98);
		return new NN(individ.getTop(), population.get(0).getChrom());
	}
	public ArrayList<Individ> nextGen() throws Exception{
		cross();
		mutation();
		cut(false);
		return population;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
}
