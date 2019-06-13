package terg.evolife;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Shape;

import terg.evolife.Space.State;

public class Controller {
	public class controllerShape{
		private Shape shape;
		private Color color;
		private Image image;
		private String text;
		private float[] xyOfText;
		private Shape shapeForText;
		private Color colorOfShForText;
		public controllerShape(Shape shape, Color color, Image image, String text, float xOfText, float yOfText) {
			this.shape = shape;
			this.color = color;
			this.image = image;
			this.text = text;
			xyOfText = new float[] {xOfText, yOfText};
			shapeForText = null;
			colorOfShForText = null;
		}
		public Shape getShape() { return shape; }
		public Color getColor() { return color; }
		public Image getImage() { return image; }
		public String getText() { return text; }
		public float[] getXyOfText() { return xyOfText; }
		public Shape getShapeForText() { return shapeForText; } 
		public Color getColorOfShForText() { return colorOfShForText; }
		public void setShapeForText(Shape sh, Color color) {
			shapeForText = sh;
			colorOfShForText = color;
		 }
	}
	private ArrayList<controllerShape> allShapes;
	private State innerState;
	private float gameWidth;
	private float gameHeight;
	private Player[] players;
	private TrueTypeFont font;
	private int[] amountOnEachState;
	private TreeMap<String, Image> images;
	public Controller(State innerState, float gameWidth, float gameHeight, int playerColor, Player[] players, TrueTypeFont font) throws SlickException, IOException {
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.font = font;
		amountOnEachState = new int[] {0, 0, 0, 0, 0};
		images = new TreeMap<String, Image>();
		
		images.put("father", new Image(Setup.temp("father.png"), false));
		images.put("mother", new Image(Setup.temp("mother.png"), false));
		images.put("child", new Image(Setup.temp("child.png"), false));
		images.put("cross", new Image(Setup.temp("cross.jpg"), false));
		images.put("kill", new Image(Setup.temp("kill.png"), false));
		images.put("mutation", new Image(Setup.temp("mutation.png"), false));
		images.put("newgen", new Image(Setup.temp("newgen.png"), false));
		images.put("eye_open", new Image(Setup.temp("eye_open.png"), false));
		images.put("eye_mach", new Image(Setup.temp("eye_mach.png"), false));
		images.put("eye_close", new Image(Setup.temp("eye_close.jpg"), false));
		images.put("background_controller", new Image(Setup.temp("background_controller.png"), false));
		reinit(innerState, playerColor, players);
	}
	public void reinit(State innerState, int playerColor, Player[] players) throws SlickException, IOException {
		this.players = players;
		this.innerState = innerState;
		Color c;
		switch(playerColor) {
		//** player`s colors **//
		case 0: c = Color.magenta; break;
		case 1: c = Color.blue; break;
		case 2: c = Color.cyan; break;
		case 3: c = Color.green; break;
		//** machine`s colors **//
		case 10: c = Color.pink; break;
		case 11: c = Color.red; break;
		case 12: c = Color.orange; break;
		case 13: c = Color.yellow; break;
		//** dead colors **//
		case 20: c = Color.white; break;
		case 21: c = Color.black; break;
		//** wrong color **//
		default: c = Color.gray; break;
		}
		allShapes = new ArrayList<controllerShape>();
		controllerShape[] loc;
		Ellipse e1;
		Ellipse e2;
		Ellipse e3;
		switch(innerState) {
		case MachineIsWorking:
			allShapes.add(new controllerShape(new RoundedRectangle(0, gameHeight-100.0f, gameWidth, 100.0f, 40), Color.white, null, "", 0, 0));
			amountOnEachState = new int[] {0, 0, 0, 0, 0};
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getMaxX()-allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getMinY()-((font.getHeight()+10)*4+20), allShapes.get(0).getShape().getHeight(), (font.getHeight()+10)*4+20, 10), Color.orange.darker(0.3f), null, "", 0, 0));
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getCenterX()-allShapes.get(0).getShape().getHeight()/2, allShapes.get(0).getShape().getMinY(), allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getHeight(), 10), Color.white, images.get("eye_mach"), "", 0, 0));
			loc = getTable((RoundedRectangle) allShapes.get(1).getShape());
			for(controllerShape lcsh : loc) {
				allShapes.add(lcsh);
			}
			break;
		case PlayerIsCrossing:
			allShapes.add(new controllerShape(new RoundedRectangle(0, gameHeight-100.0f, gameWidth, 100.0f, 40), Color.cyan, images.get("background_controller"), "", 0, 0));
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getMaxX()-allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getMinY(), allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getHeight(), 10), Color.orange.darker(0.3f), images.get("cross"), "", 0, 0));
			e1 = new Ellipse(allShapes.get(0).getShape().getMinX()+((allShapes.get(0).getShape().getWidth()-allShapes.get(1).getShape().getWidth())*1/4), allShapes.get(0).getShape().getMinY()+(allShapes.get(0).getShape().getHeight()*1/2), allShapes.get(0).getShape().getHeight()/2, allShapes.get(0).getShape().getHeight()/2);
			e2 = new Ellipse(allShapes.get(0).getShape().getMinX()+((allShapes.get(0).getShape().getWidth()-allShapes.get(1).getShape().getWidth())*2/4), allShapes.get(0).getShape().getMinY()+(allShapes.get(0).getShape().getHeight()*1/2), allShapes.get(0).getShape().getHeight()/2, allShapes.get(0).getShape().getHeight()/2);
			e3 = new Ellipse(allShapes.get(0).getShape().getMinX()+((allShapes.get(0).getShape().getWidth()-allShapes.get(1).getShape().getWidth())*3/4), allShapes.get(0).getShape().getMinY()+(allShapes.get(0).getShape().getHeight()*1/2), allShapes.get(0).getShape().getHeight()/2, allShapes.get(0).getShape().getHeight()/2);
			allShapes.add(new controllerShape(e1, c, images.get("father"), String.valueOf(amountOnEachState[1]), e1.getMaxX()-e1.getRadius1()/2, e1.getMaxY()-e1.getRadius1()/2));
			allShapes.get(2).setShapeForText(new Ellipse(allShapes.get(2).getXyOfText()[0]+font.getWidth(String.valueOf(amountOnEachState[1]))/2, allShapes.get(2).getXyOfText()[1]+font.getHeight(String.valueOf(amountOnEachState[1]))/2, 10, 10), Color.darkGray);
			allShapes.add(new controllerShape(e2, c, images.get("mother"), String.valueOf(amountOnEachState[2]), e2.getMaxX()-e2.getRadius1()/2, e2.getMaxY()-e2.getRadius1()/2));
			allShapes.get(3).setShapeForText(new Ellipse(allShapes.get(3).getXyOfText()[0]+font.getWidth(String.valueOf(amountOnEachState[1]))/2, allShapes.get(3).getXyOfText()[1]+font.getHeight(String.valueOf(amountOnEachState[1]))/2, 10, 10), Color.darkGray);
			allShapes.add(new controllerShape(e3, c, images.get("child"), String.valueOf(amountOnEachState[3]), e3.getMaxX()-e3.getRadius1()/2, e3.getMaxY()-e3.getRadius1()/2));
			allShapes.get(4).setShapeForText(new Ellipse(allShapes.get(4).getXyOfText()[0]+font.getWidth(String.valueOf(amountOnEachState[1]))/2, allShapes.get(4).getXyOfText()[1]+font.getHeight(String.valueOf(amountOnEachState[1]))/2, 10, 10), Color.darkGray);
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getMaxX()-allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getMinY()-((font.getHeight()+10)*4+20), allShapes.get(0).getShape().getHeight(), (font.getHeight()+10)*4+20, 10), Color.orange.darker(0.3f), null, "", 0, 0));
			loc = getTable((RoundedRectangle) allShapes.get(5).getShape());
			for(controllerShape lcsh : loc) {
				allShapes.add(lcsh);
			}
			break;
		case PlayerIsCrossing_Fath:
			allShapes.add(new controllerShape(new RoundedRectangle(0, gameHeight-100.0f, gameWidth, 100.0f, 40), Color.cyan, images.get("background_controller"), "", 0, 0));
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getMaxX()-allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getMinY(), allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getHeight(), 10), Color.orange.darker(0.3f), images.get("father"), "", 0, 0));
			e1 = new Ellipse(allShapes.get(0).getShape().getMinX()+((allShapes.get(0).getShape().getWidth()-allShapes.get(1).getShape().getWidth())*1/4), allShapes.get(0).getShape().getMinY()+(allShapes.get(0).getShape().getHeight()*1/2), allShapes.get(0).getShape().getHeight()/2, allShapes.get(0).getShape().getHeight()/2);
			e2 = new Ellipse(allShapes.get(0).getShape().getMinX()+((allShapes.get(0).getShape().getWidth()-allShapes.get(1).getShape().getWidth())*2/4), allShapes.get(0).getShape().getMinY()+(allShapes.get(0).getShape().getHeight()*1/2), allShapes.get(0).getShape().getHeight()/2, allShapes.get(0).getShape().getHeight()/2);
			e3 = new Ellipse(allShapes.get(0).getShape().getMinX()+((allShapes.get(0).getShape().getWidth()-allShapes.get(1).getShape().getWidth())*3/4), allShapes.get(0).getShape().getMinY()+(allShapes.get(0).getShape().getHeight()*1/2), allShapes.get(0).getShape().getHeight()/2, allShapes.get(0).getShape().getHeight()/2);
			allShapes.add(new controllerShape(e1, Color.white, images.get("father"), String.valueOf(amountOnEachState[1]), e1.getMaxX()-e1.getRadius1()/2, e1.getMaxY()-e1.getRadius1()/2));
			allShapes.get(2).setShapeForText(new Ellipse(allShapes.get(2).getXyOfText()[0]+font.getWidth(String.valueOf(amountOnEachState[1]))/2, allShapes.get(2).getXyOfText()[1]+font.getHeight(String.valueOf(amountOnEachState[1]))/2, 10, 10), Color.darkGray);
			allShapes.add(new controllerShape(e2, c, images.get("mother"), String.valueOf(amountOnEachState[2]), e2.getMaxX()-e2.getRadius1()/2, e2.getMaxY()-e2.getRadius1()/2));
			allShapes.get(3).setShapeForText(new Ellipse(allShapes.get(3).getXyOfText()[0]+font.getWidth(String.valueOf(amountOnEachState[1]))/2, allShapes.get(3).getXyOfText()[1]+font.getHeight(String.valueOf(amountOnEachState[1]))/2, 10, 10), Color.darkGray);
			allShapes.add(new controllerShape(e3, c, images.get("child"), String.valueOf(amountOnEachState[3]), e3.getMaxX()-e3.getRadius1()/2, e3.getMaxY()-e3.getRadius1()/2));
			allShapes.get(4).setShapeForText(new Ellipse(allShapes.get(4).getXyOfText()[0]+font.getWidth(String.valueOf(amountOnEachState[1]))/2, allShapes.get(4).getXyOfText()[1]+font.getHeight(String.valueOf(amountOnEachState[1]))/2, 10, 10), Color.darkGray);
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getMaxX()-allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getMinY()-((font.getHeight()+10)*4+20), allShapes.get(0).getShape().getHeight(), (font.getHeight()+10)*4+20, 10), Color.orange.darker(0.3f), null, "", 0, 0));
			loc = getTable((RoundedRectangle) allShapes.get(5).getShape());
			for(controllerShape lcsh : loc) {
				allShapes.add(lcsh);
			}
			break;
		case PlayerIsCrossing_Moth:
			allShapes.add(new controllerShape(new RoundedRectangle(0, gameHeight-100.0f, gameWidth, 100.0f, 40), Color.cyan, images.get("background_controller"), "", 0, 0));
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getMaxX()-allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getMinY(), allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getHeight(), 10), Color.orange.darker(0.3f), images.get("mother"), "", 0, 0));
			e1 = new Ellipse(allShapes.get(0).getShape().getMinX()+((allShapes.get(0).getShape().getWidth()-allShapes.get(1).getShape().getWidth())*1/4), allShapes.get(0).getShape().getMinY()+(allShapes.get(0).getShape().getHeight()*1/2), allShapes.get(0).getShape().getHeight()/2, allShapes.get(0).getShape().getHeight()/2);
			e2 = new Ellipse(allShapes.get(0).getShape().getMinX()+((allShapes.get(0).getShape().getWidth()-allShapes.get(1).getShape().getWidth())*2/4), allShapes.get(0).getShape().getMinY()+(allShapes.get(0).getShape().getHeight()*1/2), allShapes.get(0).getShape().getHeight()/2, allShapes.get(0).getShape().getHeight()/2);
			e3 = new Ellipse(allShapes.get(0).getShape().getMinX()+((allShapes.get(0).getShape().getWidth()-allShapes.get(1).getShape().getWidth())*3/4), allShapes.get(0).getShape().getMinY()+(allShapes.get(0).getShape().getHeight()*1/2), allShapes.get(0).getShape().getHeight()/2, allShapes.get(0).getShape().getHeight()/2);
			allShapes.add(new controllerShape(e1, c, images.get("father"), String.valueOf(amountOnEachState[1]), e1.getMaxX()-e1.getRadius1()/2, e1.getMaxY()-e1.getRadius1()/2));
			allShapes.get(2).setShapeForText(new Ellipse(allShapes.get(2).getXyOfText()[0]+font.getWidth(String.valueOf(amountOnEachState[1]))/2, allShapes.get(2).getXyOfText()[1]+font.getHeight(String.valueOf(amountOnEachState[1]))/2, 10, 10), Color.darkGray);
			allShapes.add(new controllerShape(e2, Color.white, images.get("mother"), String.valueOf(amountOnEachState[2]), e2.getMaxX()-e2.getRadius1()/2, e2.getMaxY()-e2.getRadius1()/2));
			allShapes.get(3).setShapeForText(new Ellipse(allShapes.get(3).getXyOfText()[0]+font.getWidth(String.valueOf(amountOnEachState[1]))/2, allShapes.get(3).getXyOfText()[1]+font.getHeight(String.valueOf(amountOnEachState[1]))/2, 10, 10), Color.darkGray);
			allShapes.add(new controllerShape(e3, c, images.get("child"), String.valueOf(amountOnEachState[3]), e3.getMaxX()-e3.getRadius1()/2, e3.getMaxY()-e3.getRadius1()/2));
			allShapes.get(4).setShapeForText(new Ellipse(allShapes.get(4).getXyOfText()[0]+font.getWidth(String.valueOf(amountOnEachState[1]))/2, allShapes.get(4).getXyOfText()[1]+font.getHeight(String.valueOf(amountOnEachState[1]))/2, 10, 10), Color.darkGray);
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getMaxX()-allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getMinY()-((font.getHeight()+10)*4+20), allShapes.get(0).getShape().getHeight(), (font.getHeight()+10)*4+20, 10), Color.orange.darker(0.3f), null, "", 0, 0));
			loc = getTable((RoundedRectangle) allShapes.get(5).getShape());
			for(controllerShape lcsh : loc) {
				allShapes.add(lcsh);
			}
			break;
		case PlayerIsCrossing_Child:
			allShapes.add(new controllerShape(new RoundedRectangle(0, gameHeight-100.0f, gameWidth, 100.0f, 40), Color.cyan, images.get("background_controller"), "", 0, 0));
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getMaxX()-allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getMinY(), allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getHeight(), 10), Color.orange.darker(0.3f), images.get("child"), "", 0, 0));
			e1 = new Ellipse(allShapes.get(0).getShape().getMinX()+((allShapes.get(0).getShape().getWidth()-allShapes.get(1).getShape().getWidth())*1/4), allShapes.get(0).getShape().getMinY()+(allShapes.get(0).getShape().getHeight()*1/2), allShapes.get(0).getShape().getHeight()/2, allShapes.get(0).getShape().getHeight()/2);
			e2 = new Ellipse(allShapes.get(0).getShape().getMinX()+((allShapes.get(0).getShape().getWidth()-allShapes.get(1).getShape().getWidth())*2/4), allShapes.get(0).getShape().getMinY()+(allShapes.get(0).getShape().getHeight()*1/2), allShapes.get(0).getShape().getHeight()/2, allShapes.get(0).getShape().getHeight()/2);
			e3 = new Ellipse(allShapes.get(0).getShape().getMinX()+((allShapes.get(0).getShape().getWidth()-allShapes.get(1).getShape().getWidth())*3/4), allShapes.get(0).getShape().getMinY()+(allShapes.get(0).getShape().getHeight()*1/2), allShapes.get(0).getShape().getHeight()/2, allShapes.get(0).getShape().getHeight()/2);
			allShapes.add(new controllerShape(e1, c, images.get("father"), String.valueOf(amountOnEachState[1]), e1.getMaxX()-e1.getRadius1()/2, e1.getMaxY()-e1.getRadius1()/2));
			allShapes.get(2).setShapeForText(new Ellipse(allShapes.get(2).getXyOfText()[0]+font.getWidth(String.valueOf(amountOnEachState[1]))/2, allShapes.get(2).getXyOfText()[1]+font.getHeight(String.valueOf(amountOnEachState[1]))/2, 10, 10), Color.darkGray);
			allShapes.add(new controllerShape(e2, c, images.get("mother"), String.valueOf(amountOnEachState[2]), e2.getMaxX()-e2.getRadius1()/2, e2.getMaxY()-e2.getRadius1()/2));
			allShapes.get(3).setShapeForText(new Ellipse(allShapes.get(3).getXyOfText()[0]+font.getWidth(String.valueOf(amountOnEachState[1]))/2, allShapes.get(3).getXyOfText()[1]+font.getHeight(String.valueOf(amountOnEachState[1]))/2, 10, 10), Color.darkGray);
			allShapes.add(new controllerShape(e3, Color.white, images.get("child"), String.valueOf(amountOnEachState[3]), e3.getMaxX()-e3.getRadius1()/2, e3.getMaxY()-e3.getRadius1()/2));
			allShapes.get(4).setShapeForText(new Ellipse(allShapes.get(4).getXyOfText()[0]+font.getWidth(String.valueOf(amountOnEachState[1]))/2, allShapes.get(4).getXyOfText()[1]+font.getHeight(String.valueOf(amountOnEachState[1]))/2, 10, 10), Color.darkGray);
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getMaxX()-allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getMinY()-((font.getHeight()+10)*4+20), allShapes.get(0).getShape().getHeight(), (font.getHeight()+10)*4+20, 10), Color.orange.darker(0.3f), null, "", 0, 0));
			loc = getTable((RoundedRectangle) allShapes.get(5).getShape());
			for(controllerShape lcsh : loc) {
				allShapes.add(lcsh);
			}
			break;
		case PlayerIsMutating:
			allShapes.add(new controllerShape(new RoundedRectangle(0, gameHeight-100.0f, gameWidth, 100.0f, 40), Color.cyan, images.get("background_controller"), "", 0, 0));
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getMaxX()-allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getMinY(), allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getHeight(), 10), Color.orange.darker(0.3f), images.get("mutation"), "", 0, 0));
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getMaxX()-allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getMinY()-((font.getHeight()+10)*4+20), allShapes.get(0).getShape().getHeight(), (font.getHeight()+10)*4+20, 10), Color.orange.darker(0.3f), null, "", 0, 0));
			loc = getTable((RoundedRectangle) allShapes.get(2).getShape());
			for(controllerShape lcsh : loc) {
				allShapes.add(lcsh);
			}
			break;
		case PlayerIsSelecting:
			allShapes.add(new controllerShape(new RoundedRectangle(0, gameHeight-100.0f, gameWidth, 100.0f, 40), Color.cyan, images.get("background_controller"), "", 0, 0));
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getMaxX()-allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getMinY(), allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getHeight(), 10), Color.orange.darker(0.3f), images.get("kill"), "", 0, 0));
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getMaxX()-allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getMinY()-((font.getHeight()+10)*4+20), allShapes.get(0).getShape().getHeight(), (font.getHeight()+10)*4+20, 10), Color.orange.darker(0.3f), null, "", 0, 0));
			loc = getTable((RoundedRectangle) allShapes.get(2).getShape());
			for(controllerShape lcsh : loc) {
				allShapes.add(lcsh);
			}
			break;
		case PlayerIsUndefined:
			allShapes.add(new controllerShape(new RoundedRectangle(0, gameHeight-100.0f, gameWidth, 100.0f, 40), Color.cyan, images.get("background_controller"), "", 0, 0));
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getMaxX()-allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getMinY(), allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getHeight(), 10), Color.orange.darker(0.3f), images.get("newgen"), "", 0, 0));
			e1 = new Ellipse(allShapes.get(0).getShape().getMinX()+((allShapes.get(0).getShape().getWidth()-allShapes.get(1).getShape().getWidth())*1/4), allShapes.get(0).getShape().getMinY()+(allShapes.get(0).getShape().getHeight()*1/2), allShapes.get(0).getShape().getHeight()/2, allShapes.get(0).getShape().getHeight()/2);
			e2 = new Ellipse(allShapes.get(0).getShape().getMinX()+((allShapes.get(0).getShape().getWidth()-allShapes.get(1).getShape().getWidth())*2/4), allShapes.get(0).getShape().getMinY()+(allShapes.get(0).getShape().getHeight()*1/2), allShapes.get(0).getShape().getHeight()/2, allShapes.get(0).getShape().getHeight()/2);
			e3 = new Ellipse(allShapes.get(0).getShape().getMinX()+((allShapes.get(0).getShape().getWidth()-allShapes.get(1).getShape().getWidth())*3/4), allShapes.get(0).getShape().getMinY()+(allShapes.get(0).getShape().getHeight()*1/2), allShapes.get(0).getShape().getHeight()/2, allShapes.get(0).getShape().getHeight()/2);
			allShapes.add(new controllerShape(e1, c, images.get("kill"), String.valueOf(amountOnEachState[0]), e1.getMaxX()-e1.getRadius1()/2, e1.getMaxY()-e1.getRadius1()/2));
			allShapes.get(2).setShapeForText(new Ellipse(allShapes.get(2).getXyOfText()[0]+font.getWidth(String.valueOf(amountOnEachState[1]))/2, allShapes.get(2).getXyOfText()[1]+font.getHeight(String.valueOf(amountOnEachState[1]))/2, 10, 10), Color.darkGray);
			allShapes.add(new controllerShape(e2, c, images.get("cross"), String.valueOf(amountOnEachState[3]), e2.getMaxX()-e2.getRadius1()/2, e2.getMaxY()-e2.getRadius1()/2));
			allShapes.get(3).setShapeForText(new Ellipse(allShapes.get(3).getXyOfText()[0]+font.getWidth(String.valueOf(amountOnEachState[1]))/2, allShapes.get(3).getXyOfText()[1]+font.getHeight(String.valueOf(amountOnEachState[1]))/2, 10, 10), Color.darkGray);
			allShapes.add(new controllerShape(e3, c, images.get("mutation"), String.valueOf(amountOnEachState[4]), e3.getMaxX()-e3.getRadius1()/2, e3.getMaxY()-e3.getRadius1()/2));
			allShapes.get(4).setShapeForText(new Ellipse(allShapes.get(4).getXyOfText()[0]+font.getWidth(String.valueOf(amountOnEachState[1]))/2, allShapes.get(4).getXyOfText()[1]+font.getHeight(String.valueOf(amountOnEachState[1]))/2, 10, 10), Color.darkGray);
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getMaxX()-allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getMinY()-((font.getHeight()+10)*4+20), allShapes.get(0).getShape().getHeight(), (font.getHeight()+10)*4+20, 10), Color.orange.darker(0.3f), null, "", 0, 0));
			loc = getTable((RoundedRectangle) allShapes.get(5).getShape());
			for(controllerShape lcsh : loc) {
				allShapes.add(lcsh);
			}
			break;
		case Undefined:
			allShapes.add(new controllerShape(new RoundedRectangle(0, gameHeight-100.0f, gameWidth, 100.0f, 40), Color.cyan, images.get("background_controller"), "", 0, 0));
			amountOnEachState = new int[] {0, 0, 0, 0, 0};
			allShapes.add(new controllerShape(new RoundedRectangle(allShapes.get(0).getShape().getMaxX()-allShapes.get(0).getShape().getHeight(), allShapes.get(0).getShape().getMinY()-((font.getHeight()+10)*4+20), allShapes.get(0).getShape().getHeight(), (font.getHeight()+10)*4+20, 10), Color.orange.darker(0.3f), null, "", 0, 0));
			loc = getTable((RoundedRectangle) allShapes.get(1).getShape());
			for(controllerShape lcsh : loc) {
				allShapes.add(lcsh);
			}
			break;
		}
		String gen = "Generation"+Space.getCurrentGeneration();
		RoundedRectangle rgen = new RoundedRectangle((gameWidth-(font.getWidth(gen)+20))/2, 0, font.getWidth(gen)+20, font.getHeight(gen)+20, 10);
		allShapes.add(new controllerShape(rgen, Color.orange.darker(0.3f), null, gen, rgen.getMinX()+10, rgen.getMinY()+10));
	}
	public int getAmountOnEachState(State currentState) {
		switch(currentState) {
		case PlayerIsSelecting:
			return amountOnEachState[0];
		case PlayerIsCrossing_Fath:
			return amountOnEachState[1];
		case PlayerIsCrossing_Moth:
			return amountOnEachState[2];
		case PlayerIsCrossing_Child:
			return amountOnEachState[3];
		case PlayerIsMutating:
			return amountOnEachState[4];
		default:
			return -1;
		}
	}
	public void setAmountOnEachState(State currentState, int amountOnThisState){
		switch(currentState) {
		case PlayerIsSelecting:
			amountOnEachState[0] = amountOnThisState;
			break;
		case PlayerIsCrossing_Fath:
			amountOnEachState[1] = amountOnThisState;
			break;
		case PlayerIsCrossing_Moth:
			amountOnEachState[2] = amountOnThisState;
			break;
		case PlayerIsCrossing_Child:
			amountOnEachState[3] = amountOnThisState;
			break;
		case PlayerIsMutating:
			amountOnEachState[4] = amountOnThisState;
			break;
		default:
		}
	}
	private controllerShape[] getTable(RoundedRectangle base) throws SlickException, IOException {
		controllerShape[] out = new controllerShape[players.length];
		for(int i = 0; i<out.length; i++) {
			Ellipse e = new Ellipse(10+base.getMinX(), 10+base.getMinY()+(font.getHeight()+10)*i, 10, 10);
			Image im = players[i].isActive() ? images.get("eye_open") : images.get("eye_close");
			out[i] = new controllerShape(e, players[i].getColor(), im, players[i].getNickname(), e.getMaxX()+5, e.getMinY());
		}
		return out;
	}
	public State getWorkOfButton(float mouseX, float mouseY, short button) throws Exception {
		if(button!=0) {
			return innerState;
		}
		switch(innerState) {
		case MachineIsWorking:
			throw new Exception("Error in getWorkOfButton");
		case PlayerIsCrossing:
			if(allShapes.get(1).getShape().contains(mouseX, mouseY) && allShapes.get(2).getText().equals(allShapes.get(3).getText()) && allShapes.get(3).getText().equals(allShapes.get(4).getText())){
				innerState = State.PlayerIsUndefined;
				break;
			}
			for(int i = 0; i<allShapes.size(); i++) {
				if(allShapes.get(i).getShape().contains(mouseX, mouseY)){
					switch(i) {
					case 2:
						innerState = State.PlayerIsCrossing_Fath;
						break;
					case 3:
						innerState = State.PlayerIsCrossing_Moth;
						break;
					case 4:
						innerState = State.PlayerIsCrossing_Child;
						break;
					default:
					}
				}
			}
			break;
		case PlayerIsCrossing_Fath:
			if(allShapes.get(1).getShape().contains(mouseX, mouseY)){
				innerState = State.PlayerIsCrossing;
				break;
			}
			for(int i = 0; i<allShapes.size(); i++) {
				if(allShapes.get(i).getShape().contains(mouseX, mouseY)){
					switch(i) {
					case 3:
						innerState = State.PlayerIsCrossing_Moth;
						break;
					case 4:
						innerState = State.PlayerIsCrossing_Child;
						break;
					default:
					}
				}
			}
			break;
		case PlayerIsCrossing_Moth:
			if(allShapes.get(1).getShape().contains(mouseX, mouseY)){
				innerState = State.PlayerIsCrossing;
				break;
			}
			for(int i = 0; i<allShapes.size(); i++) {
				if(allShapes.get(i).getShape().contains(mouseX, mouseY)){
					switch(i) {
					case 2:
						innerState = State.PlayerIsCrossing_Fath;
						break;
					case 4:
						innerState = State.PlayerIsCrossing_Child;
						break;
					default:
					}
				}
			}
			break;
		case PlayerIsCrossing_Child:
			if(allShapes.get(1).getShape().contains(mouseX, mouseY)){
				innerState = State.PlayerIsCrossing;
				break;
			}
			for(int i = 0; i<allShapes.size(); i++) {
				if(allShapes.get(i).getShape().contains(mouseX, mouseY)){
					switch(i) {
					case 2:
						innerState = State.PlayerIsCrossing_Fath;
						break;
					case 3:
						innerState = State.PlayerIsCrossing_Moth;
						break;
					default:
					}
				}
			}
			break;
		case PlayerIsSelecting:
			if(allShapes.get(1).getShape().contains(mouseX, mouseY)){
				innerState = State.PlayerIsUndefined;
				break;
			}
			break;
		case PlayerIsMutating:
			if(allShapes.get(1).getShape().contains(mouseX, mouseY)){
				innerState = State.PlayerIsUndefined;
				break;
			}
			break;
		case PlayerIsUndefined:
			if(allShapes.get(1).getShape().contains(mouseX, mouseY)){
				innerState = State.Undefined;
				break;
			}
			for(int i = 0; i<allShapes.size(); i++) {
				if(allShapes.get(i).getShape().contains(mouseX, mouseY)){
					switch(i) {
					case 2:
						innerState = State.PlayerIsSelecting;
						break;
					case 3:
						innerState = State.PlayerIsCrossing;
						break;
					case 4:
						innerState = State.PlayerIsMutating;
						break;
					default:
					}
				}
			}
			break;
		case Undefined:
			break;
		default:
			break;
		}
		return innerState;
	}
	public controllerShape[] getFullFill() {
		return allShapes.toArray(new controllerShape[allShapes.size()]);
	}
	public boolean contains(float x, float y) {
		for(int i = 0; i<allShapes.size(); i++) {
			if(allShapes.get(i).getShape().contains(x, y)) {
				return true;
			}
		}
		return false;
	}
}
