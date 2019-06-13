package terg.evolife;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;

import terg.evolife.Self.Status;
import terg.evolife.Self.Type;
import terg.evolife.Space.State;
import terg.iinur.Evol;
import terg.iinur.Individ;
import terg.iinur.Topology;
import terg.iinur.TrainSet;

public class Population{
	private Random r = new Random();
	private float WinWidth;
	private float WinHeight;
	private float lSize; //size of one cell
	private int cx; //count of cells on horizontal
	private int cy; //count of cells on vertical
	private float borderx;
	private float bordery;
	private Self[][] mapofcells;
	private ArrayList<Line> net;
	private int maxPop;
	private Evol[] evolution;
	private Topology top = new Topology(new int[] {25, 20, 15, 9});
	private int[][] colorsAndAmount;
	private int health;
	public Population(float lSize, int maxSizeOfPop, int health){
		this.maxPop = maxSizeOfPop;
		WinWidth = App.getWinWidth();
		WinHeight = App.getWinHeight();
		this.lSize = (lSize==0) ? 10 : lSize;
		cx = (int) (WinWidth/lSize);
		cy = (int) (WinHeight/lSize);
		borderx = (WinWidth-cx*lSize)/2;
		bordery = (WinHeight-cy*lSize)/2;
		mapofcells = new Self[cy][cx];
		this.health = health;
		net = new ArrayList<Line>();
		for(int i = 0; i<mapofcells.length; i++) {
			for(int j = 0; j<mapofcells[i].length; j++) {
				mapofcells[i][j] = new Self(new Rectangle(j*lSize+borderx, i*lSize+bordery, lSize, lSize));
			}
		}
	}
	public void trimNetSpaceToPopulation(){
		float startx = mapofcells[0][0].getForm().getBackShape().getMinX();
		float starty = mapofcells[0][0].getForm().getBackShape().getMinY();
		float finishx = mapofcells[mapofcells.length-1][mapofcells[0].length-1].getForm().getBackShape().getMaxX();
		float finishy = mapofcells[mapofcells.length-1][mapofcells[0].length-1].getForm().getBackShape().getMaxY();
		float x = startx;
		float y = starty;
		while(x<=finishx+1) {
			net.add(new Line(x, starty, x, finishy));
			x+=lSize;
		}
		while(y<=finishy+1) {
			net.add(new Line(startx, y, finishx, y));
			y+=lSize;
		}
		net.trimToSize();
	}
	public ArrayList<Line> getNetSpace(){
		trimNetSpaceToPopulation();
		return net;
	}
	public Polygon[] getAddPolygons() {
		TreeMap<Integer, Polygon> addLines = new TreeMap<Integer, Polygon>();
		for(int i = 0; i<mapofcells.length; i++) {
			for(int j = 0; j<mapofcells[i].length; j++) {
				switch(mapofcells[i][j].getStatus()) {
				case ToFather:
					if(!addLines.containsKey(mapofcells[i][j].getID())) {
						Polygon p = new Polygon();
						p.addPoint(mapofcells[i][j].getForm().getBackShape().getCenterX(), mapofcells[i][j].getForm().getBackShape().getCenterY());
						addLines.put(mapofcells[i][j].getID(), p);
					}
					else {
						addLines.get(mapofcells[i][j].getID()).addPoint(mapofcells[i][j].getForm().getBackShape().getCenterX(), mapofcells[i][j].getForm().getBackShape().getCenterY());
					}
					break;
				case ToMother:
					if(!addLines.containsKey(mapofcells[i][j].getID())) {
						Polygon p = new Polygon();
						p.addPoint(mapofcells[i][j].getForm().getBackShape().getCenterX(), mapofcells[i][j].getForm().getBackShape().getCenterY());
						addLines.put(mapofcells[i][j].getID(), p);
					}
					else {
						addLines.get(mapofcells[i][j].getID()).addPoint(mapofcells[i][j].getForm().getBackShape().getCenterX(), mapofcells[i][j].getForm().getBackShape().getCenterY());
					}
					break;
				case ToChild:
					if(!addLines.containsKey(mapofcells[i][j].getID())) {
						Polygon p = new Polygon();
						p.addPoint(mapofcells[i][j].getForm().getBackShape().getCenterX(), mapofcells[i][j].getForm().getBackShape().getCenterY());
						addLines.put(mapofcells[i][j].getID(), p);
					}
					else {
						addLines.get(mapofcells[i][j].getID()).addPoint(mapofcells[i][j].getForm().getBackShape().getCenterX(), mapofcells[i][j].getForm().getBackShape().getCenterY());
					}
					break;
				default:
					break;
				}
			}
		}
		Polygon[] out = new Polygon[addLines.size()];
		for(int i = 1; i<=addLines.size(); i++) {
			addLines.get(i).addPoint(addLines.get(i).getPoints()[0], addLines.get(i).getPoints()[1]);
			out[i-1] = addLines.get(i);
		}
		return out;
	}
	public void moveSpace(float posMouseX, float posMouseY){
		for(int i = 0; i<mapofcells.length; i++) {
			for(int j = 0; j<mapofcells[i].length; j++) {
				mapofcells[i][j].getForm().setLocation(mapofcells[i][j].getForm().getX()+posMouseX, mapofcells[i][j].getForm().getY()+posMouseY);
			}
		}
		net = new ArrayList<Line>();
		trimNetSpaceToPopulation();
	}
	public void zoom(float posMouseX, float posMouseY, float zoomlevel, boolean zooming) throws Exception {
		if(zoomlevel<1 && zoomlevel>0) {
			float reversezl = 1-zoomlevel;
			short s;
			if(zooming) {
				s = 1;
			}
			else {
				s = -1;
			}
			lSize += s*lSize*reversezl;
			float centerx = posMouseX;
			float centery = posMouseY;
			for(int i = 0; i<mapofcells.length; i++) {
				for(int j = 0; j<mapofcells[i].length; j++) {
					float newX = s*(Math.abs(centerx-mapofcells[i][j].getForm().getX()))*reversezl;
					float newY = s*(Math.abs(centery-mapofcells[i][j].getForm().getY()))*reversezl;
					mapofcells[i][j].getForm().setSize(lSize, lSize);
					if(mapofcells[i][j].getForm().getX()<=centerx && mapofcells[i][j].getForm().getY()<=centery) {
						mapofcells[i][j].getForm().setLocation(mapofcells[i][j].getForm().getX()-newX, mapofcells[i][j].getForm().getY()-newY);
					}
					else if(mapofcells[i][j].getForm().getX()>=centerx && mapofcells[i][j].getForm().getY()<=centery) {
						mapofcells[i][j].getForm().setLocation(mapofcells[i][j].getForm().getX()+newX, mapofcells[i][j].getForm().getY()-newY);
					}
					else if(mapofcells[i][j].getForm().getX()>=centerx && mapofcells[i][j].getForm().getY()>=centery) {
						mapofcells[i][j].getForm().setLocation(mapofcells[i][j].getForm().getX()+newX, mapofcells[i][j].getForm().getY()+newY);
					}
					else if(mapofcells[i][j].getForm().getX()<=centerx && mapofcells[i][j].getForm().getY()>=centery) {
						mapofcells[i][j].getForm().setLocation(mapofcells[i][j].getForm().getX()-newX, mapofcells[i][j].getForm().getY()+newY);
					}
				}
			}
			net = new ArrayList<Line>();
			trimNetSpaceToPopulation();
		}
		else {
			throw new Exception("ZoomLevelIsMoreThenOneOrLessThenZero");
		}
	}
	public float getSizeOfOneCell() {
		return lSize;
	}
	public void newPopulation(int[] colorsOrder) throws Exception {
		this.colorsAndAmount = new int[2][];
		this.colorsAndAmount[0] = colorsOrder;
		this.colorsAndAmount[1] = new int[colorsOrder.length];
		trimNetSpaceToPopulation();
		int[][] veluij = new int[2][maxPop];
		for(int i = 0; i<maxPop; i++) {
			int[] k = new int[2];
			boolean h = true;
			if(i==0) {
				k[0] = r.nextInt(cx);
				k[1] = r.nextInt(cy);
			}
			else {
				while(h) {
					k[0] = r.nextInt(cx);
					k[1] = r.nextInt(cy);
					for(int j = 0; j<i; j++) {
						if(k[0]==veluij[0][j] && k[1]==veluij[1][j]) {
							h = true;
							break;
						}
						else {
							h = false;
						}
					}
				}
			}
			mapofcells[k[1]][k[0]].setAlive(health);
			if(colorsOrder!=null) {
				int newColor = colorsOrder[r.nextInt(colorsOrder.length)];
				mapofcells[k[1]][k[0]].setColor(newColor);
			}
			veluij[0][i] = k[0];
			veluij[1][i] = k[1];
		}
		evolution = new Evol[colorsOrder.length/2];
	}
	private void updateAll() throws Exception {
		ArrayList<Individ> forEvol;
		for(int k = 0, kevol = 0; k<colorsAndAmount[0].length; k++) {
			colorsAndAmount[1][k] = 0;
			forEvol = new ArrayList<Individ>();
			for(int i = 0; i<mapofcells.length; i++) {
				for(int j = 0; j<mapofcells[i].length; j++) {
					if(mapofcells[i][j].isAlive() && mapofcells[i][j].getColorInt()==colorsAndAmount[0][k]) {
						colorsAndAmount[1][k]++;
						TrainSet TSet = new TrainSet(getInputForEvolution(i, j, mapofcells.length, mapofcells[0].length), new double[] {mapofcells[i][j].getHealth()});
						Individ ind = new Individ(top, TSet, false) {
							@Override
							public double effect(double[] TrainSetArgs) {
								return TrainSetArgs[0];
							}
						};
						mapofcells[i][j].setBrain(ind);
						if(Type.Undefined.isMachines(colorsAndAmount[0][k])) {
							forEvol.add(ind);
						}
					}
				}
			}
			if(Type.Undefined.isMachines(colorsAndAmount[0][k])) {
				evolution[kevol] = new Evol(forEvol);
				evolution[kevol++].setID(colorsAndAmount[0][k]);
			}
		}
	}
	public void newGeneration_Machine(int color) throws Exception {
		updateAll();
		int IDofEvolution = -1;
		for(int i = 0; i<evolution.length; i++) {
			if(color == evolution[i].getID()) {
				IDofEvolution = i;
				break;
			}
		}
		if(IDofEvolution==-1) {
			throw new Exception("Error in state of new generation of machine");
		}
		int IDofColor = 0;
		for(int i = 0; i<colorsAndAmount[0].length; i++) {
			if(color == colorsAndAmount[0][i]) {
				IDofColor = i;
				break;
			}
		}
		ArrayList<Individ> update = evolution[IDofEvolution].nextGen();
		int adi = 0;
		for(int i = 0; i<mapofcells.length; i++) {
			for(int j = 0; j<mapofcells[i].length; j++) {
				if(mapofcells[i][j].isAlive() && mapofcells[i][j].getColorInt()==colorsAndAmount[0][IDofColor]) {
					mapofcells[i][j].setBrain(update.get(adi++));
				}
			}
		}
		int amountOfNewCells = maxPop-colorsAndAmount[1][IDofColor];
		adi = evolution[IDofEvolution].getSizeOfPopulation();
		while(adi<update.size() && adi<evolution[IDofEvolution].getSizeOfPopulation()+amountOfNewCells) {
			int ri = 0;
			int rj = 0;
			do {
				ri = r.nextInt(mapofcells.length);
				rj = r.nextInt(mapofcells[0].length);
			}while(mapofcells[ri][rj].isAlive());
			mapofcells[ri][rj].setAlive(update.get(adi++), health, colorsAndAmount[0][IDofColor]);
		}
		for(int i = 0; i<mapofcells.length; i++) {
			for(int j = 0; j<mapofcells[i].length; j++) {
				if(mapofcells[i][j].isAlive() && mapofcells[i][j].getColorInt()==colorsAndAmount[0][IDofColor]) {
					mapofcells[i][j].update(getInputForEvolution(i, j, mapofcells.length,  mapofcells[i].length));
					int[] newcoord = mapofcells[i][j].move(i, j, mapofcells.length, mapofcells[i].length);
					if(newcoord[0]==i && newcoord[1]==j) {
						mapofcells[i][j].setHealth(mapofcells[i][j].getHealth()-mapofcells[i][j].getBackgroundhealth());
						continue;
					}
					if(mapofcells[newcoord[0]][newcoord[1]].getColorInt()==mapofcells[i][j].getColorInt()) {
						continue;
					}
					mapofcells[newcoord[0]][newcoord[1]].setGost(new Self(mapofcells[i][j]));
					mapofcells[i][j].setDead();
				}
			}
		}
		for(int i = 0; i<mapofcells.length; i++) {
			for(int j = 0; j<mapofcells[i].length; j++) {
				if(!mapofcells[i][j].isActivation()) {
					mapofcells[i][j].activation();
				}
			}
		}
	}
	public void newGeneration_Player(int carrentColor) throws Exception {
		updateAll();
		if(!(carrentColor>=0 && carrentColor<10)) {
			throw new Exception("Error in state of new generation of player");
		}
		TreeMap<Integer, int[]> mothers = new TreeMap<Integer, int[]>();
		TreeMap<Integer, int[]> fathers = new TreeMap<Integer, int[]>();
		TreeMap<Integer, int[]> children = new TreeMap<Integer, int[]>();
		ArrayList<int[]> mutants = new ArrayList<int[]>();
		
		for(int i = 0; i<mapofcells.length; i++) {
			for(int j = 0; j<mapofcells[i].length; j++) {
				switch(mapofcells[i][j].getStatus()) {
				case ToKill:
					mapofcells[i][j].setDead();
					break;
				case ToMother:
					mothers.put(mapofcells[i][j].getID(), new int[] {i, j});
					break;
				case ToFather:
					fathers.put(mapofcells[i][j].getID(), new int[] {i, j});
					break;
				case ToChild:
					children.put(mapofcells[i][j].getID(), new int[] {i, j});
					break;
				case ToMutation:
					mutants.add(new int[] {i, j});
					break;
				default:
				}
				
			}
		}
		mutants.trimToSize();
		
		if(mothers.size() != fathers.size() || mothers.size() != children.size() || fathers.size() != children.size()) {
			throw new Exception("Error");
		}
		for(int i = 1; i<=mothers.size(); i++) {
			double[] m = mapofcells[mothers.get(i)[0]][mothers.get(i)[1]].getBrain().getChrom();
			double[] f = mapofcells[fathers.get(i)[0]][fathers.get(i)[1]].getBrain().getChrom();
			double[] ch = new double[m.length];
			for(int k = 0; k<ch.length; k++) {
				ch[k] = r.nextBoolean() ? m[k] : f[k];
			}
			Individ ich = mapofcells[mothers.get(i)[0]][mothers.get(i)[1]].getBrain();
			ich.setChrom(ch);
			mapofcells[children.get(i)[0]][children.get(i)[1]].setAlive(ich, health, carrentColor);
		}
		for(int i = 0; i<mutants.size(); i++) {
			double[] ch = mapofcells[mutants.get(i)[0]][mutants.get(i)[1]].getBrain().getChrom();
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
			mapofcells[mutants.get(i)[0]][mutants.get(i)[1]].getBrain().setChrom(ch);
		}
		int IDofColor = 0;
		for(int i = 0; i<colorsAndAmount[0].length; i++) {
			if(carrentColor == colorsAndAmount[0][i]) {
				IDofColor = i;
				break;
			}
		}
		for(int i = 0; i<mapofcells.length; i++) {
			for(int j = 0; j<mapofcells[i].length; j++) {
				if(mapofcells[i][j].isAlive() && mapofcells[i][j].getColorInt()==colorsAndAmount[0][IDofColor]) {
					mapofcells[i][j].update(getInputForEvolution(i, j, mapofcells.length,  mapofcells[i].length));
					int[] newcoord = mapofcells[i][j].move(i, j, mapofcells.length, mapofcells[i].length);
					if(newcoord[0]==i && newcoord[1]==j) {
						mapofcells[i][j].setHealth(mapofcells[i][j].getHealth()-mapofcells[i][j].getBackgroundhealth());
						continue;
					}
					if(mapofcells[newcoord[0]][newcoord[1]].getColorInt()==mapofcells[i][j].getColorInt()) {
						continue;
					}
					mapofcells[newcoord[0]][newcoord[1]].setGost(new Self(mapofcells[i][j]));
					mapofcells[i][j].setDead();
				}
			}
		}
		for(int i = 0; i<mapofcells.length; i++) {
			for(int j = 0; j<mapofcells[i].length; j++) {
				if(!mapofcells[i][j].isActivation()) {
					mapofcells[i][j].activation();
				}
				mapofcells[i][j].setStatus(Status.Undefined);
			}
		}
	}
	public boolean getSelect(State selectState, GameContainer container, int color, int amountOfSelect, short button) throws Exception {
		if(selectState != State.PlayerIsSelecting) {
			throw new Exception("Error in state of selection");
		}
		int IDofColor = 0;
		for(int i = 0; i<colorsAndAmount[0].length; i++) {
			if(color == colorsAndAmount[0][i]) {
				IDofColor = i;
				break;
			}
		}
		boolean b = false;
		for(int i = 0; i<mapofcells.length; i++) {
			for(int j = 0; j<mapofcells[i].length; j++) {
				if(mapofcells[i][j].getForm().getBackShape().contains(container.getInput().getMouseX(), container.getInput().getMouseY())) {
					if(button==0) {
						if(mapofcells[i][j].isAlive() && colorsAndAmount[0][IDofColor]==mapofcells[i][j].getColorInt() && mapofcells[i][j].getStatus() == Status.Undefined) {
							mapofcells[i][j].setStatus(Status.ToKill);
							mapofcells[i][j].setID(amountOfSelect+1);
							b = true;
							break;
						}
					}
					else if(button==1) {
						if(mapofcells[i][j].isAlive() && colorsAndAmount[0][IDofColor]==mapofcells[i][j].getColorInt() && mapofcells[i][j].getStatus() == Status.ToKill) {
							mapofcells[i][j].setStatus(Status.Undefined);
							mapofcells[i][j].setID(-1);
							b = true;
							break;
						}
					}
				}
			}
		}
		return b;
	}
	public boolean getCross(State crossState, GameContainer container, int color, int amountOfSelect, short button) throws Exception {
		if(crossState != State.PlayerIsCrossing_Moth && crossState != State.PlayerIsCrossing_Fath && crossState != State.PlayerIsCrossing_Child) {
			throw new Exception("Error in state of crossing");
		}
		int IDofColor = 0;
		for(int i = 0; i<colorsAndAmount[0].length; i++) {
			if(color == colorsAndAmount[0][i]) {
				IDofColor = i;
				break;
			}
		}
		boolean b = false;
		for(int i = 0; i<mapofcells.length; i++) {
			for(int j = 0; j<mapofcells[i].length; j++) {
				if(mapofcells[i][j].getForm().getBackShape().contains(container.getInput().getMouseX(), container.getInput().getMouseY())) {
					switch(crossState) {
					case PlayerIsCrossing_Fath:
						if(button==0) {
							if(mapofcells[i][j].isAlive() && colorsAndAmount[0][IDofColor]==mapofcells[i][j].getColorInt() && mapofcells[i][j].getStatus() == Status.Undefined) {
								mapofcells[i][j].setStatus(Status.ToFather);
								mapofcells[i][j].setID(amountOfSelect+1);
								b = true;
							}
						}
						else if(button==1) {
							if(mapofcells[i][j].isAlive() && colorsAndAmount[0][IDofColor]==mapofcells[i][j].getColorInt() && mapofcells[i][j].getStatus() == Status.ToFather) {
								mapofcells[i][j].setStatus(Status.Undefined);
								updateIDForCross(crossState, mapofcells[i][j].getID());
								mapofcells[i][j].setID(-1);
								b = true;
							}
						}
						break;
					case PlayerIsCrossing_Moth:
						if(button==0) {
							if(mapofcells[i][j].isAlive() && colorsAndAmount[0][IDofColor]==mapofcells[i][j].getColorInt() && mapofcells[i][j].getStatus() == Status.Undefined) {
								mapofcells[i][j].setStatus(Status.ToMother);
								mapofcells[i][j].setID(amountOfSelect+1);
								b = true;
							}
						}
						else if(button==1) {
							if(mapofcells[i][j].isAlive() && colorsAndAmount[0][IDofColor]==mapofcells[i][j].getColorInt() && mapofcells[i][j].getStatus() == Status.ToMother) {
								mapofcells[i][j].setStatus(Status.Undefined);
								updateIDForCross(crossState, mapofcells[i][j].getID());
								mapofcells[i][j].setID(-1);
								b = true;
							}
						}
						break;
					case PlayerIsCrossing_Child:
						if(button==0) {
							if(!mapofcells[i][j].isAlive() && mapofcells[i][j].getStatus() == Status.Undefined) {
								mapofcells[i][j].setStatus(Status.ToChild);
								mapofcells[i][j].setID(amountOfSelect+1);
								b = true;
							}
						}
						else if(button==1) {
							if(!mapofcells[i][j].isAlive() && mapofcells[i][j].getStatus() == Status.ToChild) {
								mapofcells[i][j].setStatus(Status.Undefined);
								updateIDForCross(crossState, mapofcells[i][j].getID());
								mapofcells[i][j].setID(-1);
								b = true;
							}
						}
						break;
					default:
					}
				}
			}
		}
		return b;
	}
	private void updateIDForCross(State input, int deletedNum) {
		for(int i = 0; i<mapofcells.length; i++) {
			for(int j = 0; j<mapofcells[i].length; j++) {
				switch(input) {
				case PlayerIsCrossing_Fath:
					if(mapofcells[i][j].getStatus() == Status.ToFather && mapofcells[i][j].getID()>deletedNum) {
						mapofcells[i][j].setID(mapofcells[i][j].getID()-1);
					}
					break;
				case PlayerIsCrossing_Moth:
					if(mapofcells[i][j].getStatus() == Status.ToMother && mapofcells[i][j].getID()>deletedNum) {
						mapofcells[i][j].setID(mapofcells[i][j].getID()-1);
					}
					break;
				case PlayerIsCrossing_Child:
					if(mapofcells[i][j].getStatus() == Status.ToChild && mapofcells[i][j].getID()>deletedNum) {
						mapofcells[i][j].setID(mapofcells[i][j].getID()-1);
					}
					break;
				default:
				}
			}
		}
	}
	public boolean getMutation(State mutateState, GameContainer container, int color, int amountOfSelect, short button) throws Exception {
		if(mutateState != State.PlayerIsMutating) {
			throw new Exception("Error in state of mutation");
		}
		int IDofColor = 0;
		for(int i = 0; i<colorsAndAmount[0].length; i++) {
			if(color == colorsAndAmount[0][i]) {
				IDofColor = i;
				break;
			}
		}
		boolean b = false;
		for(int i = 0; i<mapofcells.length; i++) {
			for(int j = 0; j<mapofcells[i].length; j++) {
				if(mapofcells[i][j].getForm().getBackShape().contains(container.getInput().getMouseX(), container.getInput().getMouseY())) {
					if(button==0) {
						if(mapofcells[i][j].isAlive() && colorsAndAmount[0][IDofColor]==mapofcells[i][j].getColorInt() && mapofcells[i][j].getStatus() == Status.Undefined) {
							mapofcells[i][j].setStatus(Status.ToMutation);
							mapofcells[i][j].setID(amountOfSelect+1);
							b = true;
							break;
						}
					}
					else if(button==1) {
						if(mapofcells[i][j].isAlive() && colorsAndAmount[0][IDofColor]==mapofcells[i][j].getColorInt() && mapofcells[i][j].getStatus() == Status.ToMutation) {
							mapofcells[i][j].setStatus(Status.Undefined);
							mapofcells[i][j].setID(-1);
							b = true;
							break;
						}
					}
				}
			}
		}
		return b;
	}
	public int getSizeOfPop(int color) {
		int IDofColor = 0;
		for(int i = 0; i<colorsAndAmount[0].length; i++) {
			if(color == colorsAndAmount[0][i]) {
				IDofColor = i;
				break;
			}
		}
		int out = 0;
		for(int i = 0; i<mapofcells.length; i++) {
			for(int j = 0; j<mapofcells[i].length; j++) {
				if(mapofcells[i][j].isAlive() && mapofcells[i][j].getColorInt()==colorsAndAmount[0][IDofColor]) {
					out++;
				}
			}
		}
		return out;
	}
	public int getMaxPop() {
		return maxPop;
	}
	private double[] getInputForEvolution(int x, int y, int xmax, int ymax) {
		double[] out = new double[25];
		int i = 0;
		for(int a = 0; a<=2; a++) {
			for(int yi = y-a; yi<=y+a; yi++) {
				for(int xi = x-a; xi<=x+a; xi++) {
					if(!(Math.abs(xi-x)<a && Math.abs(yi-y)<a)) {
						if(xi<0 || yi<0 || xi>=xmax || yi>=ymax || mapofcells[xi][yi].getColorInt()==mapofcells[x][y].getColorInt()) {
							out[i++] = 0;
						}
						else {
							out[i++] = mapofcells[xi][yi].getHealth();
						}
					}
				}
			}
		}
		return out;
	}
	public Self[][] getCurrentPopulation(){
		return mapofcells;
	}
}