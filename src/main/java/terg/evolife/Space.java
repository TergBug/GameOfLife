package terg.evolife;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import terg.evolife.Controller.controllerShape;
import terg.evolife.Self.Type;

public class Space extends BasicGameState{
	private static Controller controller;
	public enum State{
		MachineIsWorking,
		PlayerIsSelecting,
		PlayerIsCrossing,
		PlayerIsCrossing_Moth,
		PlayerIsCrossing_Fath,
		PlayerIsCrossing_Child,
		PlayerIsMutating,
		PlayerIsUndefined,
		Undefined;
		private boolean active;
		State(){
			active = false;
		}
		public State update(int color, Player[] players) throws SlickException, IOException {
			controller.reinit(this, color, players);
			return this;
		}
		public boolean isActive() {
			return active;
		}
		public void setActive(boolean active) {
			this.active = active;
		}
	}
	private static State state;
	
	//** common vars **//
	private static Random random;
	//private static DecimalFormat df;
	private static KeyListener k;
	
	//** design vars **//
	private static TrueTypeFont font;
	private static TrueTypeFont font1;
	
	//** mouse vars **//
	private static int hg;
	private static float mouseX;
	private static float mouseY;
	
	//** population vars **//
	private static Population live;
	private static int[] colorsOrder;
	private static int point;
	private static float cellSize;
	
	//** gameplay vars **//
	private static int timer;
	private static int currentGeneration;
	private static boolean isChanged;
	private static int countOfGenerations;
	private static boolean isGameOver;
	private static String stringOfWinner;
	
	//** ratesystem vars **//
	private static Player[] players;

	public void init(GameContainer container, StateBasedGame sbg) throws SlickException {
		if(sbg.getCurrentStateID()==1) {
			try {
				reinit(container, sbg);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
	public static void reinit(final GameContainer container, final StateBasedGame sbg) throws Exception {
		random = new Random();
		try {
			File fontFile = new File(Setup.temp("font.ttf"));
			Font awtf = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            awtf = awtf.deriveFont(15f); // set font size
            font = new TrueTypeFont(awtf, true);
            awtf = awtf.deriveFont(40f);
            font1 = new TrueTypeFont(awtf, true);
		} catch (Exception e) { e.printStackTrace(); }
		container.getInput().removeAllListeners();
		k = new KeyListener() {
			@Override
			public void inputEnded() {}
			@Override
			public void inputStarted() {}
			@Override
			public boolean isAcceptingInput() { return true; }
			@Override
			public void setInput(Input input) {}
			@Override
			public void keyPressed(int key, char c) {
				if(key == Keyboard.KEY_ESCAPE) {
					sbg.enterState(0, new FadeOutTransition(Color.black, 1000), new FadeInTransition(Color.black, 1000));
					try {
						Menu.reinit(container, sbg);
					} catch (SlickException e) { e.printStackTrace(); } 
					catch (IOException e) { e.printStackTrace(); } catch (Exception e) { e.printStackTrace(); }
				}
			}
			@Override
			public void keyReleased(int key, char c) {}
		};
		container.getInput().addKeyListener(k);
		hg = 0;
		mouseX = container.getInput().getMouseX();
		mouseY = container.getInput().getMouseY();
		timer = 0;
		cellSize = 30;
		colorsOrder = Menu.getColorsOrder();
		point = 0;
		isGameOver = false;
		isChanged = false;
		stringOfWinner = "";
		Setup setup = new Setup();
		setup.check();
		players = setup.getPlayers();
		live = new Population(cellSize, setup.getMaxpop(), setup.getHealth());
		live.newPopulation(colorsOrder);
		state = State.Undefined;
		if(Type.Undefined.isMachines(colorsOrder[0])) { state = State.MachineIsWorking; }
		else if(Type.Undefined.isPlayers(colorsOrder[0])) { state = State.PlayerIsUndefined; }
		controller = new Controller(state, container.getWidth(), container.getHeight(), colorsOrder[0], players, font);
		currentGeneration = 1;
		countOfGenerations = setup.getMaxgen();
	}
	public void update(GameContainer container, StateBasedGame sbg, int delta) throws SlickException {
		if(isGameOver) {
			if(stringOfWinner.length()==0) {
				int[] sizesOfPop = new int[colorsOrder.length];
				int[] maxSizeAndID = new int[] {0, 0};
				for(int i = 0; i<sizesOfPop.length; i++) {
					sizesOfPop[i] = live.getSizeOfPop(colorsOrder[i]);
					if(sizesOfPop[i]>maxSizeAndID[0]) {
						maxSizeAndID[0] = sizesOfPop[i];
						maxSizeAndID[1] = i;
					}
				}
				try {
					if(Type.Undefined.isMachines(colorsOrder[maxSizeAndID[1]])) {
						stringOfWinner = "Machine is winner";
					}
					else if(Type.Undefined.isPlayers(colorsOrder[maxSizeAndID[1]])) {
						for(int i = 0; i<players.length; i++) {
							if(players[i].getColorInt()==colorsOrder[maxSizeAndID[1]]) {
								stringOfWinner = players[i].getNickname()+" is winner";
								break;
							}
						}
					}
				} catch (Exception e) { e.printStackTrace(); }
			}
			return;
		}
		for(int i = 0; i<players.length; i++) {
			if(colorsOrder[point]==players[i].getColorInt()) {
				players[i].setActive(true);
			}
			else {
				players[i].setActive(false);
			}
		}
		try {
			controller.reinit(state, colorsOrder[point], players);
		} catch (IOException e1) { e1.printStackTrace(); }
		if(controller.contains(container.getInput().getMouseX(), container.getInput().getMouseY())) {
			state.setActive(true);
		}
		else {
			state.setActive(false);
		}
		if(!state.isActive()) {
			if(Mouse.isButtonDown(Input.MOUSE_LEFT_BUTTON)) {
				float deltamX = container.getInput().getMouseX()-mouseX;
				float deltamY = container.getInput().getMouseY()-mouseY;
				live.moveSpace(deltamX, deltamY);
			}
			hg = Mouse.getDWheel();
			if(hg>0) {
				try {
					live.zoom(container.getInput().getMouseX(), container.getInput().getMouseY(), 0.85f, true);
				} catch (Exception e) { e.printStackTrace(); }
			}
			else if(hg<0 && live.getSizeOfOneCell()>4) {
				try {
					live.zoom(container.getInput().getMouseX(), container.getInput().getMouseY(), 0.85f, false);
				} catch (Exception e) { e.printStackTrace(); }
			}
		}
		short button = -1;
		if(container.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			button = 0;
		}
		else if(container.getInput().isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
			button = 1;
		}
		switch(state) {
		case MachineIsWorking:
			timer+=delta;
			if(timer >= delta*100) {
				try {
					live.newGeneration_Machine(colorsOrder[point]);
				} catch (Exception e) { e.printStackTrace(); }
				timer = 0;
				try {
					state = State.Undefined.update(colorsOrder[point], players);
				} catch (IOException e) { e.printStackTrace(); }
			}
			break;
		case PlayerIsCrossing:
			if(button!=-1) {
				try {
					if(state.isActive()) {
						state = controller.getWorkOfButton(container.getInput().getMouseX(), container.getInput().getMouseY(), button).update(colorsOrder[point], players);
					}
				} catch (Exception e) { e.printStackTrace(); }
			}
			break;
		case PlayerIsCrossing_Child:
			if(button!=-1) {
				try {
					if(!state.isActive() && button==0 && (live.getMaxPop()-live.getSizeOfPop(colorsOrder[point])-controller.getAmountOnEachState(state)+controller.getAmountOnEachState(State.PlayerIsSelecting)>0)) {
						isChanged = live.getCross(state, container, colorsOrder[point], controller.getAmountOnEachState(state), button);
					}
					else if(!state.isActive() && button==1) {
						isChanged = live.getCross(state, container, colorsOrder[point], controller.getAmountOnEachState(state), button);
					}
					else if(state.isActive()) {
						state = controller.getWorkOfButton(container.getInput().getMouseX(), container.getInput().getMouseY(), button).update(colorsOrder[point], players);
					}
				} catch (Exception e) { e.printStackTrace(); }
			}
			break;
		case PlayerIsCrossing_Fath:
			if(button!=-1) {
				try {
					if(!state.isActive() && button==0 && (live.getMaxPop()-live.getSizeOfPop(colorsOrder[point])-controller.getAmountOnEachState(state)+controller.getAmountOnEachState(State.PlayerIsSelecting)>0)) {
						isChanged = live.getCross(state, container, colorsOrder[point], controller.getAmountOnEachState(state), button);
					}
					else if(!state.isActive() && button==1) {
						isChanged = live.getCross(state, container, colorsOrder[point], controller.getAmountOnEachState(state), button);
					}
					else if(state.isActive()) {
						state = controller.getWorkOfButton(container.getInput().getMouseX(), container.getInput().getMouseY(), button).update(colorsOrder[point], players);
					}
				} catch (Exception e) { e.printStackTrace(); }
			}
			break;
		case PlayerIsCrossing_Moth:
			if(button!=-1) {
				try {
					if(!state.isActive() && button==0 && (live.getMaxPop()-live.getSizeOfPop(colorsOrder[point])-controller.getAmountOnEachState(state)+controller.getAmountOnEachState(State.PlayerIsSelecting)>0)) {
						isChanged = live.getCross(state, container, colorsOrder[point], controller.getAmountOnEachState(state), button);
					}
					else if(!state.isActive() && button==1) {
						isChanged = live.getCross(state, container, colorsOrder[point], controller.getAmountOnEachState(state), button);
					}
					else if(state.isActive()) {
						state = controller.getWorkOfButton(container.getInput().getMouseX(), container.getInput().getMouseY(), button).update(colorsOrder[point], players);
					}
				}catch (Exception e) { e.printStackTrace(); }
			}
			break;
		case PlayerIsMutating:
			if(button!=-1) {
				try {
					if(!state.isActive()) {
						isChanged = live.getMutation(state, container, colorsOrder[point], controller.getAmountOnEachState(state), button);
					}
					else if(state.isActive()) {
						state = controller.getWorkOfButton(container.getInput().getMouseX(), container.getInput().getMouseY(), button).update(colorsOrder[point], players);
					}
				} catch (Exception e) { e.printStackTrace(); }
			}
			break;
		case PlayerIsSelecting:
			if(button!=-1) {
				try {
					if(!state.isActive()) {
						isChanged = live.getSelect(state, container, colorsOrder[point], controller.getAmountOnEachState(state), button);
					}
					else if(state.isActive()) {
						state = controller.getWorkOfButton(container.getInput().getMouseX(), container.getInput().getMouseY(), button).update(colorsOrder[point], players);
					}
				} catch (Exception e) { e.printStackTrace(); }
			}
			break;
		case PlayerIsUndefined:
			if(button!=-1) {
				try {
					if(state.isActive()) {
						state = controller.getWorkOfButton(container.getInput().getMouseX(), container.getInput().getMouseY(), button).update(colorsOrder[point], players);
						if(state == State.Undefined) {
							live.newGeneration_Player(colorsOrder[point]);
						}
					}
				} catch (Exception e) { e.printStackTrace(); }
			}
			break;
		case Undefined:
			if(point<colorsOrder.length-1) { point++; }
			else {
				point=0;
				currentGeneration++;
				if(currentGeneration>countOfGenerations) {
					isGameOver = true;
					break;
				}
			}
			try {
				if(Type.Undefined.isPlayers(colorsOrder[point])) {
					state = State.PlayerIsUndefined.update(colorsOrder[point], players);
				}
				else if(Type.Undefined.isMachines(colorsOrder[point])) {
					state = State.MachineIsWorking.update(colorsOrder[point], players);
				}
			} catch (Exception e) { e.printStackTrace(); }
			break;
		}
		mouseX = container.getInput().getMouseX();
		mouseY = container.getInput().getMouseY();
	}
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.setBackground(Color.black);
		g.setLineWidth(1.0f);
		int[] amountOfEachState = new int[] {0, 0, 0, 0, 0};
		for(int i = 0; i<live.getCurrentPopulation().length; i++) {
			for(int j = 0; j<live.getCurrentPopulation()[i].length; j++) {
				if(live.getCurrentPopulation()[i][j].getColor()!=Color.black && (live.getCurrentPopulation()[i][j].getForm().getBackShape().getCenterX()<App.getWinWidth()+live.getSizeOfOneCell() && live.getCurrentPopulation()[i][j].getForm().getBackShape().getCenterX()>-live.getSizeOfOneCell()) && (live.getCurrentPopulation()[i][j].getForm().getBackShape().getCenterY()<App.getWinHeight()+live.getSizeOfOneCell() && live.getCurrentPopulation()[i][j].getForm().getBackShape().getCenterY()>-live.getSizeOfOneCell())) {
					g.setColor(live.getCurrentPopulation()[i][j].getBackgroundcolor());
					g.fill(live.getCurrentPopulation()[i][j].getForm().getBackShape());
					g.setColor(live.getCurrentPopulation()[i][j].getColor());
					g.fill(live.getCurrentPopulation()[i][j].getForm().getFrontShape());
					if(live.getCurrentPopulation()[i][j].getForm().getImage()!=null) {
						
						g.texture(live.getCurrentPopulation()[i][j].getForm().getFrontShape(), live.getCurrentPopulation()[i][j].getForm().getImage(), true);
					}
				}
				if(isChanged) {
					switch(live.getCurrentPopulation()[i][j].getStatus()) {
					case ToKill:
						amountOfEachState[0]++;
						break;
					case ToFather:
						amountOfEachState[1]++;
						break;
					case ToMother:
						amountOfEachState[2]++;
						break;
					case ToChild:
						amountOfEachState[3]++;
						break;
					case ToMutation:
						amountOfEachState[4]++;
						break;
					default:
					}
				}
			}
		}
		if(isChanged) {
			for(int i = 0; i<=amountOfEachState.length; i++) {
				switch(i) {
				case 0:
					controller.setAmountOnEachState(State.PlayerIsSelecting, amountOfEachState[i]);
					break;
				case 1:
					controller.setAmountOnEachState(State.PlayerIsCrossing_Fath, amountOfEachState[i]);
					break;
				case 2:
					controller.setAmountOnEachState(State.PlayerIsCrossing_Moth, amountOfEachState[i]);
					break;
				case 3:
					controller.setAmountOnEachState(State.PlayerIsCrossing_Child, amountOfEachState[i]);
					break;
				case 4:
					controller.setAmountOnEachState(State.PlayerIsMutating, amountOfEachState[i]);
					break;
				case 5:
					isChanged = false;
					break;
				}
			}
		}
		g.setColor(Color.darkGray.darker(0.3f));
		for(Line l : live.getNetSpace()) {
			g.drawLine(l.getX1(), l.getY1(), l.getX2(), l.getY2());
		}
		g.setColor(getColorFromInt(colorsOrder[point]));
		for(Polygon p : live.getAddPolygons()) {
			g.draw(p);
		}
		controllerShape[] shapesOfController = controller.getFullFill();
		for(int i = 0; i<shapesOfController.length; i++) {
			g.setColor(shapesOfController[i].getColor());
			g.fill(shapesOfController[i].getShape());
			if(shapesOfController[i].getImage() != null) {
				g.texture(shapesOfController[i].getShape(), shapesOfController[i].getImage(), true);
			}
			try {
				if(shapesOfController[i].getColorOfShForText()!=null) {
					g.setColor(shapesOfController[i].getColorOfShForText());
					g.fill(shapesOfController[i].getShapeForText());
				}
				font.drawString(shapesOfController[i].getXyOfText()[0], shapesOfController[i].getXyOfText()[1], shapesOfController[i].getText(), Color.white);
			} catch (Exception e) { e.printStackTrace(); }
		}
		if(isGameOver) {
			font1.drawString(App.getWinWidth()/2-font1.getWidth(stringOfWinner)/2, App.getWinHeight()/2-font1.getHeight(stringOfWinner)/2, stringOfWinner, new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
		}
	}
	public static int getCurrentGeneration() {
		return currentGeneration;
	}
	public int getID() {
		return 1;
	}
	private Color getColorFromInt(int color) {
		switch(color) {
		//** player`s colors **//
		case 0: return Color.magenta;
		case 1: return Color.blue;
		case 2: return Color.cyan;
		case 3: return Color.green;
		//** machine`s colors **//
		case 10: return Color.pink;
		case 11: return Color.red;
		case 12: return Color.orange;
		case 13: return Color.yellow;
		//** dead colors **//
		case 20: return Color.white;
		case 21: return Color.black;
		//** wrong color **//
		default: return Color.gray;
		}
	}
}