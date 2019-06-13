package terg.evolife;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class Menu extends BasicGameState{
	private static Random random;
	private static TrueTypeFont font;
	private static TrueTypeFont font1;
	private static KeyListener k;
	private static String strState;
	
	private static MController controller;
	private static String strForController;
	private static boolean isInput;
	
	private static Ellipse buttonGO;
	private static boolean showGO;
	private static boolean letsGO;
	private static String strGO;
	private static int timer;
	private static ParticleSystem system;
	private static ConfigurableEmitter emitter;
	
	public static int[] getColorsOrder() {
		Player[] players = controller.getRealPlayers();
		int[] machcolors  = new int[players.length];
		for(int i = 0; i<machcolors.length; i++) {
			machcolors[i] = 10+random.nextInt(4);
			for(int j = 0; j<i; j++) {
				if(machcolors[i]==machcolors[j]) {
					machcolors[i] = 10+random.nextInt(4);
					j=-1;
				}
			}
		}
		int[] colors = new int[players.length*2];
		for(int i = 0; i<colors.length; i++) {
			colors[i] = random.nextBoolean() ? players[random.nextInt(players.length)].getColorInt() : machcolors[random.nextInt(machcolors.length)];
			for(int j = 0; j<i; j++) {
				if(colors[i]==colors[j]) {
					colors[i] = random.nextBoolean() ? players[random.nextInt(players.length)].getColorInt() : machcolors[random.nextInt(machcolors.length)];
					j=-1;
				}
			}
		}
		return colors;
	}
	public void init(GameContainer container, StateBasedGame sbg) throws SlickException {
		if(sbg.getCurrentStateID()==0) {
			try {
				reinit(container, sbg);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
	public static void reinit(final GameContainer container, final StateBasedGame sbg) throws Exception {
		random = new Random();
		strForController = "";
		try {
			Font awtf = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream("font.ttf"));
            awtf = awtf.deriveFont(16f); // set font size
            font = new TrueTypeFont(awtf, true);
            awtf = awtf.deriveFont(40f);
            font1 = new TrueTypeFont(awtf, true);
		} catch (Exception e) { e.printStackTrace(); }
		strState = "";
		container.getInput().removeAllListeners();
		k = new KeyListener() {
			private boolean isdown = false;
			private long timeing = 0;
			private int[] key = new int[2];
			private char c;
			private int maxstr = 6;
			float speed = 0.8f;
			@Override
			public void inputEnded() {
			}
			@Override
			public void inputStarted() {
				if(isInput && System.currentTimeMillis()>=timeing) {
					keyDown();
					timeing = System.currentTimeMillis()+80;
				}
				else if(!isInput) {
					keyDown();
				}
			}
			@Override
			public boolean isAcceptingInput() {
				return true;
			}
			@Override
			public void setInput(Input input) {
			}
			@Override
			public void keyPressed(int key, char c) {
				timeing = System.currentTimeMillis()+500;
				if(key == Keyboard.KEY_ESCAPE) {
					System.exit(0);
				}
				boolean istext = !((int) c>=1040 && (int) c<=1103) && ((key>=2 && key<=11) || (key>=16 && key<=27) || (key>=30 && key<=40) || (key>=44 && key<=57 && key!=54 && key!=56) || (key>=71 && key<=83));
				if(!isdown) {
					this.key[0] = key;
					this.key[1] = key;
				}
				else {
					this.key[1] = key;
				}
				this.c = c;
				if((int) c>=1040 && (int) c<=1103) { strState = "Please set English keyboard layout"; }
				else { strState = ""; }
				if(isInput) {
					strForController = controller.getInputText();
					if(strForController.length()<maxstr && istext) {
						strForController += c;
					}
					if(strForController.length()>0 && key==14) {
						strForController = strForController.substring(0, strForController.length()-1);
						if(strForController.length()==0) {
							isInput = false;
						}
					}
				}
				else if(!isInput) {
					if(this.key[0]==200 || this.key[1]==200) {
						float l = emitter.getY()>50 ? speed : 0;
						emitter.setPosition(emitter.getX(), emitter.getY()-l, false);
						buttonGO.setCenterX(buttonGO.getCenterX());
						buttonGO.setCenterY(buttonGO.getCenterY()-l);
					}
					if(this.key[0]==208 || this.key[1]==208) {
						float l = emitter.getY()<container.getHeight()-50 ? speed : 0;
						emitter.setPosition(emitter.getX(), emitter.getY()+l, false);
						buttonGO.setCenterX(buttonGO.getCenterX());
						buttonGO.setCenterY(buttonGO.getCenterY()+l);
					}
					if(this.key[0]==203 || this.key[1]==203) {
						float l = emitter.getX()>50 ? speed : 0;
						emitter.setPosition(emitter.getX()-l, emitter.getY(), false);
						buttonGO.setCenterX(buttonGO.getCenterX()-l);
						buttonGO.setCenterY(buttonGO.getCenterY());
					}
					if(this.key[0]==205 || this.key[1]==205) {
						float l = emitter.getX()<container.getWidth()-50 ? speed : 0;
						emitter.setPosition(emitter.getX()+l, emitter.getY(), false);
						buttonGO.setCenterX(buttonGO.getCenterX()+l);
						buttonGO.setCenterY(buttonGO.getCenterY());
					}
				}
				if(key==68) {
					try {
						App.setFullscr(!App.isFullscr(), true);
						reinit(container, sbg);
					} catch(Exception e) {}
				}
				isdown = true;
			}
			@Override
			public void keyReleased(int key, char c) {
				timeing = 0;
				if(this.key[0]==this.key[1]) { isdown = false; }
				if(this.key[0]!=this.key[1] && (this.key[0]==key || this.key[1]==key)) {
					this.key[0] = this.key[0]==key ? this.key[1] : this.key[0];
					this.key[1] = this.key[1]==key ? this.key[0] : this.key[1];
				}
			}
			private void keyDown() {
				boolean istext = !((int) c>=1040 && (int) c<=1103) && ((key[0]>=2 && key[0]<=11) || (key[0]>=16 && key[0]<=27) || (key[0]>=30 && key[0]<=40) || (key[0]>=44 && key[0]<=57 && key[0]!=54 && key[0]!=56) || (key[0]>=71 && key[0]<=83));
				if(isdown) {
					if(isInput) {
						strForController = controller.getInputText();
						if(strForController.length()<maxstr && istext) {
							strForController += c;
						}
						if(strForController.length()>0 && key[0]==14) {
							strForController = strForController.substring(0, strForController.length()-1);
							if(strForController.length()==0) {
								isInput = false;
							}
						}
					}
					else if(!isInput ) {
						if(this.key[0]==200 || this.key[1]==200) {
							float l = emitter.getY()>50 ? speed : 0;
							emitter.setPosition(emitter.getX(), emitter.getY()-l, false);
							buttonGO.setCenterX(buttonGO.getCenterX());
							buttonGO.setCenterY(buttonGO.getCenterY()-l);
						}
						if(this.key[0]==208 || this.key[1]==208) {
							float l = emitter.getY()<container.getHeight()-50 ? speed : 0;
							emitter.setPosition(emitter.getX(), emitter.getY()+l, false);
							buttonGO.setCenterX(buttonGO.getCenterX());
							buttonGO.setCenterY(buttonGO.getCenterY()+l);
						}
						if(this.key[0]==203 || this.key[1]==203) {
							float l = emitter.getX()>50 ? speed : 0;
							emitter.setPosition(emitter.getX()-l, emitter.getY(), false);
							buttonGO.setCenterX(buttonGO.getCenterX()-l);
							buttonGO.setCenterY(buttonGO.getCenterY());
						}
						if(this.key[0]==205 || this.key[1]==205) {
							float l = emitter.getX()<container.getWidth()-50 ? speed : 0;
							emitter.setPosition(emitter.getX()+l, emitter.getY(), false);
							buttonGO.setCenterX(buttonGO.getCenterX()+l);
							buttonGO.setCenterY(buttonGO.getCenterY());
						}
					}
				}
			}
		};
		container.getInput().addKeyListener(k);
		try{
			system = new ParticleSystem(Setup.temp("particle.png"), 90000);
			File xmlFile = new File(Setup.temp("emitter.xml"));
			emitter = ParticleIO.loadEmitter(xmlFile);
			emitter.setPosition(50+random.nextInt(container.getWidth()-100), (container.getHeight()/2-25)+random.nextInt(50));
			system.addEmitter(emitter);
		} catch (Exception e){ e.printStackTrace(); }
		system.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
		buttonGO = new Ellipse(emitter.getX(), emitter.getY(), 20, 20);
		showGO = false;
		letsGO = false;
		strGO = "";
		timer = 0;
		controller = new MController(App.getWinWidth(), App.getWinHeight(), font);
	}
	public void update(GameContainer container, StateBasedGame sbg, int delta) throws SlickException {
		system.update(delta);
		if(letsGO) {
			showGO = false;
			try {
				if(buttonGO.getRadius1()<container.getWidth() || buttonGO.getRadius2()<container.getHeight()) {
					buttonGO.setRadii(buttonGO.getRadius1()+4, buttonGO.getRadius2()+4);
					buttonGO.setCenterX(emitter.getX());
					buttonGO.setCenterY(emitter.getY());
					return;
				}
				if(strGO.equals("")) {
					timer+=delta;
					if(timer >= delta*100) {
						strGO = "START GAME";
						timer = 0;
					}
					return;
				}
				timer+=delta;
				if(timer >= delta*100) {
					sbg.enterState(1, new FadeOutTransition(Color.black, 2000), new FadeInTransition(Color.black, 2000));
					Space.reinit(container, sbg);
					timer = 0;
				}
			} catch (Exception e) { e.printStackTrace(); }
			return;
		}
		controller.updateAnimAndPS(delta);
		short button = -1;
		if(container.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			button = 0;
		}
		else if(container.getInput().isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
			button = 1;
		}
		float mouseX = container.getInput().getMouseX();
		float mouseY = container.getInput().getMouseY();
		if(buttonGO.contains(mouseX, mouseY)) {
			showGO = true;
			if(button==0) {
				try {
					letsGO = true;
					controller.writeSetup();
				} catch (Exception e) { e.printStackTrace(); }
			}
		}
		else {
			showGO = false;
		}
		controller.giveCoordsOfMouse(mouseX, mouseY);
		try {
			controller.getWork(mouseX, mouseY, button);
		} catch (IOException e) { e.printStackTrace(); }
		isInput = controller.isInputText();
		if(isInput && !strForController.equals("#")) {
			controller.giveStr(strForController);
			strForController = "#";
		}
	}
	public void render(GameContainer container, StateBasedGame arg1, Graphics g) throws SlickException {
		if(system != null) {
			system.render();
		}
		
		g.setColor(Color.black);
		g.fill(buttonGO);
		if(showGO) {
			font.drawString(buttonGO.getCenterX()-font.getWidth("GO")/2, buttonGO.getCenterY()-font.getHeight("GO")/2, "GO", Color.white);
		}
		if(letsGO) {
			font1.drawString(container.getWidth()/2-font1.getWidth(strGO)/2, container.getHeight()/2-font1.getHeight(strGO)/2, strGO, new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
			return;
		}
		controller.renderAnimAndPS();
		Shape[][] shapesOfController = controller.getFill();
		boolean[][] isFillShapes = controller.getIsFill();
		Color[][] colorsOfController = controller.getColors();
		String[] textOfController = controller.getText();
		float[][] XYtextOfController = controller.getCoordOfText();
		for(int i = 0; i<shapesOfController.length; i++) {
			for(int j = 0; j<shapesOfController[i].length; j++) {
				g.setColor(colorsOfController[i][j]);
				if(isFillShapes[i][j]) {
					g.fill(shapesOfController[i][j]);
				}
				else {
					g.draw(shapesOfController[i][j]);
				}
			}
			try {
				if(!textOfController[i].equals("")) {
					font.drawString(XYtextOfController[i][0], XYtextOfController[i][1], textOfController[i], new Color(255, 255, 255));
				}
			} catch (Exception e) { e.printStackTrace(); }
		}
		if(!strState.equals("")) {
			font.drawString(10, 10, strState, Color.white);
		}
	}
	public int getID() {
		return 0;
	}
}