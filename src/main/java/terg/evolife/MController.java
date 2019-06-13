package terg.evolife;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;

public class MController {
	public enum MState{
		Setup,
		SetupMaxPop,
		SetupMaxPop_IN,
		SetupHealth,
		SetupHealth_IN,
		SetupMaxGen,
		SetupMaxGen_IN,
		Player,
		PlayerOne,
		PlayerOne_Nickname,
		PlayerOne_Color,
		PlayerTwo,
		PlayerTwo_Nickname,
		PlayerTwo_Color,
		PlayerThree,
		PlayerThree_Nickname,
		PlayerThree_Color,
		PlayerFour,
		PlayerFour_Nickname,
		PlayerFour_Color,
		Undefined;
	}
	private class controllerShape{
		private Shape[] shapes;
		private boolean[] isFill;
		private Color[] color;
		private String text;
		private float[] xyOfText;
		private SpriteSheet ss;
		private Animation anim;
		private int IDofShapeToAnim;
		private ParticleSystem psystem;
		private ConfigurableEmitter emitter;
		private int IDofShapeToPS;
		public controllerShape(Shape shape, Color color, String text, float xOfText, float yOfText) {
			shapes = new Shape[1];
			shapes[0] = shape;
			isFill = new boolean[1];
			isFill[0] = false;
			this.color = new Color[1];
			this.color[0] = color;
			this.text = text;
			xyOfText = new float[] {xOfText, yOfText};
			ss = null;
			anim = null;
			IDofShapeToAnim = 0;
			psystem = null;
			emitter = null;
			IDofShapeToPS = 0;
		}
		public void setAddShapes(Shape[] addShapes, Color[] addShapesColor) {
			if(addShapes.length!=addShapesColor.length) { return; }
			Shape sh  = shapes[0];
			Color c = color[0];
			shapes = new Shape[addShapes.length+1];
			color = new Color[addShapesColor.length+1];
			isFill = new boolean[addShapes.length+1];
			shapes[0] = sh;
			color[0] = c;
			isFill[0] = false;
			for(int i = 1; i<=addShapes.length; i++) {
				shapes[i] = addShapes[i-1];
				color[i] = addShapesColor[i-1];
				isFill[i] = false;
			}
		}
		public void setColor(int IDofShape, Color color) { this.color[IDofShape] = color; }
		public void setFill(int IDofShape, boolean isFill) { this.isFill[IDofShape] = isFill; }
		public void setFill(boolean[] isFill) {
			if(color.length!=isFill.length) { return; }
			this.isFill = isFill;
		}
		public void setText(String text, float x, float y) {
			this.text = text;
			xyOfText[0] = x;
			xyOfText[1] = y;
		}
		public Shape[] getShapes() { return shapes; }
		public boolean[] isFill() { return isFill; }
		public Color[] getColor() { return color; }
		public String getText() { return text; }
		public float[] getXyOfText() { return xyOfText; }
		public void setAnimation(int IDofElement, Image image, int tw, int th, int IDofStopFrame, int duration) {
			ss = new SpriteSheet(image, tw, th);
			anim = new Animation(ss, duration);
			anim.stopAt(IDofStopFrame);
			IDofShapeToAnim = IDofElement;
		}
		public void setParticleSystem(int IDofElement, Image image, File XML) throws IOException {
			psystem = new ParticleSystem(image, 90000);
			emitter = ParticleIO.loadEmitter(XML);
			psystem.addEmitter(emitter);
			IDofShapeToPS = IDofElement;
		}
		public void setupPS(int countOfParticles) {
			if(psystem!=null && emitter!=null) {
				emitter.spawnCount.setMax(countOfParticles);
				emitter.spawnCount.setMin(countOfParticles);
				psystem.removeEmitter(emitter);
				psystem.addEmitter(emitter);
				psystem.reset();
			}
		}
		public void updateAll(int delta) {
			if(ss!=null && anim!=null) {
				anim.update(delta);
				if(anim.isStopped()) { anim.restart(); }
			}
			if(psystem!=null && emitter!=null) { psystem.update(delta); }
		}
		public void renderAll() {
			if(ss!=null && anim!=null) {
				anim.draw(shapes[IDofShapeToAnim].getMinX(), shapes[IDofShapeToAnim].getMinY(), shapes[IDofShapeToAnim].getWidth(), shapes[IDofShapeToAnim].getHeight());
			}
			if(psystem!=null && emitter!=null) {
				emitter.setPosition(shapes[IDofShapeToPS].getCenterX(), shapes[IDofShapeToPS].getCenterY(), false);
				psystem.render();
			}
		}
	}
	private TreeMap<MState, controllerShape> allShapes;
	private MState innerState;
	private MState addState_Text;
	private Player[] players;
	private int maxpop;
	private int health;
	private int maxgen;
	private boolean inputText;
	private TrueTypeFont font;
	private TreeMap<String, Image> images;
	public MController(float gameWidth, float gameHeight, TrueTypeFont font) throws Exception {
		images = new TreeMap<String, Image>();
		images.put("health", new Image(Setup.temp("health.png"), false));
		images.put("maxpop", new Image(Setup.temp("maxpop.png"), false));
		images.put("maxgen", new Image(Setup.temp("maxgen.png"), false));
		
		inputText = false;
		this.font = font;
		players = new Player[] {new Player("###", "gray"), new Player("###", "gray"), new Player("###", "gray"), new Player("###", "gray")};
		Setup setup = new Setup();
		setup.check();
		maxpop = setup.getMaxpop();
		health = setup.getHealth();
		maxgen = setup.getMaxgen();
		innerState = MState.Undefined;
		addState_Text = MState.Undefined;
		allShapes = new TreeMap<MState, controllerShape>();
		allShapes.put(MState.Player, new controllerShape(new Rectangle(0, gameHeight/2, gameWidth, gameHeight/2), Color.transparent, "", 0, 0));
		allShapes.put(MState.Setup, new controllerShape(new Rectangle(0, 0, gameWidth, gameHeight/2), Color.transparent, "", 0, 0));
		allShapes.put(MState.PlayerOne, new controllerShape(new Rectangle(allShapes.get(MState.Player).getShapes()[0].getMinX()+allShapes.get(MState.Player).getShapes()[0].getWidth()*0/4, allShapes.get(MState.Player).getShapes()[0].getMinY(), allShapes.get(MState.Player).getShapes()[0].getWidth()*1/4, allShapes.get(MState.Player).getShapes()[0].getHeight()), Color.white, "", 0, 0));
		allShapes.put(MState.PlayerTwo, new controllerShape(new Rectangle(allShapes.get(MState.Player).getShapes()[0].getMinX()+allShapes.get(MState.Player).getShapes()[0].getWidth()*1/4, allShapes.get(MState.Player).getShapes()[0].getMinY(), allShapes.get(MState.Player).getShapes()[0].getWidth()*1/4, allShapes.get(MState.Player).getShapes()[0].getHeight()), Color.white, "", 0, 0));
		allShapes.put(MState.PlayerThree, new controllerShape(new Rectangle(allShapes.get(MState.Player).getShapes()[0].getMinX()+allShapes.get(MState.Player).getShapes()[0].getWidth()*2/4, allShapes.get(MState.Player).getShapes()[0].getMinY(), allShapes.get(MState.Player).getShapes()[0].getWidth()*1/4, allShapes.get(MState.Player).getShapes()[0].getHeight()), Color.white, "", 0, 0));
		allShapes.put(MState.PlayerFour, new controllerShape(new Rectangle(allShapes.get(MState.Player).getShapes()[0].getMinX()+allShapes.get(MState.Player).getShapes()[0].getWidth()*3/4, allShapes.get(MState.Player).getShapes()[0].getMinY(), allShapes.get(MState.Player).getShapes()[0].getWidth()*1/4, allShapes.get(MState.Player).getShapes()[0].getHeight()), Color.white, "", 0, 0));
		
		allShapes.put(MState.PlayerOne_Color, new controllerShape(new Rectangle(allShapes.get(MState.PlayerOne).getShapes()[0].getMinX()+(allShapes.get(MState.PlayerOne).getShapes()[0].getWidth()-100), allShapes.get(MState.PlayerOne).getShapes()[0].getMinY(), 100, allShapes.get(MState.PlayerOne).getShapes()[0].getHeight()), Color.white, "", 0, 0));
		Shape[] addShapesColor = new Ellipse[4];
		addShapesColor[0] = new Ellipse(allShapes.get(MState.PlayerOne_Color).getShapes()[0].getMinX()+allShapes.get(MState.PlayerOne_Color).getShapes()[0].getWidth()*1/2, allShapes.get(MState.PlayerOne_Color).getShapes()[0].getMinY()+allShapes.get(MState.PlayerOne_Color).getShapes()[0].getHeight()*1/5, 30, 30);
		addShapesColor[1] = new Ellipse(allShapes.get(MState.PlayerOne_Color).getShapes()[0].getMinX()+allShapes.get(MState.PlayerOne_Color).getShapes()[0].getWidth()*1/2, allShapes.get(MState.PlayerOne_Color).getShapes()[0].getMinY()+allShapes.get(MState.PlayerOne_Color).getShapes()[0].getHeight()*2/5, 30, 30);
		addShapesColor[2] = new Ellipse(allShapes.get(MState.PlayerOne_Color).getShapes()[0].getMinX()+allShapes.get(MState.PlayerOne_Color).getShapes()[0].getWidth()*1/2, allShapes.get(MState.PlayerOne_Color).getShapes()[0].getMinY()+allShapes.get(MState.PlayerOne_Color).getShapes()[0].getHeight()*3/5, 30, 30);
		addShapesColor[3] = new Ellipse(allShapes.get(MState.PlayerOne_Color).getShapes()[0].getMinX()+allShapes.get(MState.PlayerOne_Color).getShapes()[0].getWidth()*1/2, allShapes.get(MState.PlayerOne_Color).getShapes()[0].getMinY()+allShapes.get(MState.PlayerOne_Color).getShapes()[0].getHeight()*4/5, 30, 30);
		allShapes.get(MState.PlayerOne_Color).setAddShapes(addShapesColor, new Color[] {Color.magenta, Color.blue, Color.cyan, Color.green});
		allShapes.get(MState.PlayerOne_Color).setFill(new boolean[] {false, true, true, true, true});
		Rectangle r = new RoundedRectangle(allShapes.get(MState.PlayerOne).getShapes()[0].getMinX()+(allShapes.get(MState.PlayerOne).getShapes()[0].getWidth()-allShapes.get(MState.PlayerOne_Color).getShapes()[0].getWidth())*1/2-50, allShapes.get(MState.PlayerOne).getShapes()[0].getMinY()+allShapes.get(MState.PlayerOne).getShapes()[0].getHeight()*1/3-15, 100, 30, 10);
		allShapes.put(MState.PlayerOne_Nickname, new controllerShape(r, Color.white, "Nickname", r.getMinX()+(r.getWidth()/2-font.getWidth("Nickname")/2), r.getMinY()+(r.getHeight()/2-font.getHeight("Nickname")/2)));
		allShapes.get(MState.PlayerOne_Nickname).setAddShapes(new Shape[] {new Ellipse(allShapes.get(MState.PlayerOne).getShapes()[0].getMinX()+(allShapes.get(MState.PlayerOne).getShapes()[0].getWidth()-allShapes.get(MState.PlayerOne_Color).getShapes()[0].getWidth())*1/2, allShapes.get(MState.PlayerOne).getShapes()[0].getMinY()+allShapes.get(MState.PlayerOne).getShapes()[0].getHeight()*2/3, 30, 30)}, new Color[] {Color.white});
		
		allShapes.put(MState.PlayerTwo_Color, new controllerShape(new Rectangle(allShapes.get(MState.PlayerTwo).getShapes()[0].getMinX()+(allShapes.get(MState.PlayerTwo).getShapes()[0].getWidth()-100), allShapes.get(MState.PlayerTwo).getShapes()[0].getMinY(), 100, allShapes.get(MState.PlayerTwo).getShapes()[0].getHeight()), Color.white, "", 0, 0));
		addShapesColor = new Ellipse[4];
		addShapesColor[0] = new Ellipse(allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getMinX()+allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getWidth()*1/2, allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getMinY()+allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getHeight()*1/5, 30, 30);
		addShapesColor[1] = new Ellipse(allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getMinX()+allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getWidth()*1/2, allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getMinY()+allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getHeight()*2/5, 30, 30);
		addShapesColor[2] = new Ellipse(allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getMinX()+allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getWidth()*1/2, allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getMinY()+allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getHeight()*3/5, 30, 30);
		addShapesColor[3] = new Ellipse(allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getMinX()+allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getWidth()*1/2, allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getMinY()+allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getHeight()*4/5, 30, 30);
		allShapes.get(MState.PlayerTwo_Color).setAddShapes(addShapesColor, new Color[] {Color.magenta, Color.blue, Color.cyan, Color.green});
		allShapes.get(MState.PlayerTwo_Color).setFill(new boolean[] {false, true, true, true, true});
		r = new RoundedRectangle(allShapes.get(MState.PlayerTwo).getShapes()[0].getMinX()+(allShapes.get(MState.PlayerTwo).getShapes()[0].getWidth()-allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getWidth())*1/2-50, allShapes.get(MState.PlayerTwo).getShapes()[0].getMinY()+allShapes.get(MState.PlayerTwo).getShapes()[0].getHeight()*1/3-15, 100, 30, 10);
		allShapes.put(MState.PlayerTwo_Nickname, new controllerShape(r, Color.white, "Nickname", r.getMinX()+(r.getWidth()/2-font.getWidth("Nickname")/2), r.getMinY()+(r.getHeight()/2-font.getHeight("Nickname")/2)));
		allShapes.get(MState.PlayerTwo_Nickname).setAddShapes(new Shape[] {new Ellipse(allShapes.get(MState.PlayerTwo).getShapes()[0].getMinX()+(allShapes.get(MState.PlayerTwo).getShapes()[0].getWidth()-allShapes.get(MState.PlayerTwo_Color).getShapes()[0].getWidth())*1/2, allShapes.get(MState.PlayerTwo).getShapes()[0].getMinY()+allShapes.get(MState.PlayerTwo).getShapes()[0].getHeight()*2/3, 30, 30)}, new Color[] {Color.white});
		
		allShapes.put(MState.PlayerThree_Color, new controllerShape(new Rectangle(allShapes.get(MState.PlayerThree).getShapes()[0].getMinX()+(allShapes.get(MState.PlayerThree).getShapes()[0].getWidth()-100), allShapes.get(MState.PlayerThree).getShapes()[0].getMinY(), 100, allShapes.get(MState.PlayerThree).getShapes()[0].getHeight()), Color.white, "", 0, 0));
		addShapesColor = new Ellipse[4];
		addShapesColor[0] = new Ellipse(allShapes.get(MState.PlayerThree_Color).getShapes()[0].getMinX()+allShapes.get(MState.PlayerThree_Color).getShapes()[0].getWidth()*1/2, allShapes.get(MState.PlayerThree_Color).getShapes()[0].getMinY()+allShapes.get(MState.PlayerThree_Color).getShapes()[0].getHeight()*1/5, 30, 30);
		addShapesColor[1] = new Ellipse(allShapes.get(MState.PlayerThree_Color).getShapes()[0].getMinX()+allShapes.get(MState.PlayerThree_Color).getShapes()[0].getWidth()*1/2, allShapes.get(MState.PlayerThree_Color).getShapes()[0].getMinY()+allShapes.get(MState.PlayerThree_Color).getShapes()[0].getHeight()*2/5, 30, 30);
		addShapesColor[2] = new Ellipse(allShapes.get(MState.PlayerThree_Color).getShapes()[0].getMinX()+allShapes.get(MState.PlayerThree_Color).getShapes()[0].getWidth()*1/2, allShapes.get(MState.PlayerThree_Color).getShapes()[0].getMinY()+allShapes.get(MState.PlayerThree_Color).getShapes()[0].getHeight()*3/5, 30, 30);
		addShapesColor[3] = new Ellipse(allShapes.get(MState.PlayerThree_Color).getShapes()[0].getMinX()+allShapes.get(MState.PlayerThree_Color).getShapes()[0].getWidth()*1/2, allShapes.get(MState.PlayerThree_Color).getShapes()[0].getMinY()+allShapes.get(MState.PlayerThree_Color).getShapes()[0].getHeight()*4/5, 30, 30);
		allShapes.get(MState.PlayerThree_Color).setAddShapes(addShapesColor, new Color[] {Color.magenta, Color.blue, Color.cyan, Color.green});
		allShapes.get(MState.PlayerThree_Color).setFill(new boolean[] {false, true, true, true, true});
		r = new RoundedRectangle(allShapes.get(MState.PlayerThree).getShapes()[0].getMinX()+(allShapes.get(MState.PlayerThree).getShapes()[0].getWidth()-allShapes.get(MState.PlayerThree_Color).getShapes()[0].getWidth())*1/2-50, allShapes.get(MState.PlayerThree).getShapes()[0].getMinY()+allShapes.get(MState.PlayerThree).getShapes()[0].getHeight()*1/3-15, 100, 30, 10);
		allShapes.put(MState.PlayerThree_Nickname, new controllerShape(r, Color.white, "Nickname", r.getMinX()+(r.getWidth()/2-font.getWidth("Nickname")/2), r.getMinY()+(r.getHeight()/2-font.getHeight("Nickname")/2)));
		allShapes.get(MState.PlayerThree_Nickname).setAddShapes(new Shape[] {new Ellipse(allShapes.get(MState.PlayerThree).getShapes()[0].getMinX()+(allShapes.get(MState.PlayerThree).getShapes()[0].getWidth()-allShapes.get(MState.PlayerThree_Color).getShapes()[0].getWidth())*1/2, allShapes.get(MState.PlayerThree).getShapes()[0].getMinY()+allShapes.get(MState.PlayerThree).getShapes()[0].getHeight()*2/3, 30, 30)}, new Color[] {Color.white});
		
		allShapes.put(MState.PlayerFour_Color, new controllerShape(new Rectangle(allShapes.get(MState.PlayerFour).getShapes()[0].getMinX()+(allShapes.get(MState.PlayerFour).getShapes()[0].getWidth()-100), allShapes.get(MState.PlayerFour).getShapes()[0].getMinY(), 100, allShapes.get(MState.PlayerFour).getShapes()[0].getHeight()), Color.white, "", 0, 0));
		addShapesColor = new Ellipse[4];
		addShapesColor[0] = new Ellipse(allShapes.get(MState.PlayerFour_Color).getShapes()[0].getMinX()+allShapes.get(MState.PlayerFour_Color).getShapes()[0].getWidth()*1/2, allShapes.get(MState.PlayerFour_Color).getShapes()[0].getMinY()+allShapes.get(MState.PlayerFour_Color).getShapes()[0].getHeight()*1/5, 30, 30);
		addShapesColor[1] = new Ellipse(allShapes.get(MState.PlayerFour_Color).getShapes()[0].getMinX()+allShapes.get(MState.PlayerFour_Color).getShapes()[0].getWidth()*1/2, allShapes.get(MState.PlayerFour_Color).getShapes()[0].getMinY()+allShapes.get(MState.PlayerFour_Color).getShapes()[0].getHeight()*2/5, 30, 30);
		addShapesColor[2] = new Ellipse(allShapes.get(MState.PlayerFour_Color).getShapes()[0].getMinX()+allShapes.get(MState.PlayerFour_Color).getShapes()[0].getWidth()*1/2, allShapes.get(MState.PlayerFour_Color).getShapes()[0].getMinY()+allShapes.get(MState.PlayerFour_Color).getShapes()[0].getHeight()*3/5, 30, 30);
		addShapesColor[3] = new Ellipse(allShapes.get(MState.PlayerFour_Color).getShapes()[0].getMinX()+allShapes.get(MState.PlayerFour_Color).getShapes()[0].getWidth()*1/2, allShapes.get(MState.PlayerFour_Color).getShapes()[0].getMinY()+allShapes.get(MState.PlayerFour_Color).getShapes()[0].getHeight()*4/5, 30, 30);
		allShapes.get(MState.PlayerFour_Color).setAddShapes(addShapesColor, new Color[] {Color.magenta, Color.blue, Color.cyan, Color.green});
		allShapes.get(MState.PlayerFour_Color).setFill(new boolean[] {false, true, true, true, true});
		r = new RoundedRectangle(allShapes.get(MState.PlayerFour).getShapes()[0].getMinX()+(allShapes.get(MState.PlayerFour).getShapes()[0].getWidth()-allShapes.get(MState.PlayerFour_Color).getShapes()[0].getWidth())*1/2-50, allShapes.get(MState.PlayerFour).getShapes()[0].getMinY()+allShapes.get(MState.PlayerFour).getShapes()[0].getHeight()*1/3-15, 100, 30, 10);
		allShapes.put(MState.PlayerFour_Nickname, new controllerShape(r, Color.white, "Nickname", r.getMinX()+(r.getWidth()/2-font.getWidth("Nickname")/2), r.getMinY()+(r.getHeight()/2-font.getHeight("Nickname")/2)));
		allShapes.get(MState.PlayerFour_Nickname).setAddShapes(new Shape[] {new Ellipse(allShapes.get(MState.PlayerFour).getShapes()[0].getMinX()+(allShapes.get(MState.PlayerFour).getShapes()[0].getWidth()-allShapes.get(MState.PlayerFour_Color).getShapes()[0].getWidth())*1/2, allShapes.get(MState.PlayerFour).getShapes()[0].getMinY()+allShapes.get(MState.PlayerFour).getShapes()[0].getHeight()*2/3, 30, 30)}, new Color[] {Color.white});
		
		r = new Rectangle(allShapes.get(MState.Setup).getShapes()[0].getMinX()+allShapes.get(MState.Setup).getShapes()[0].getWidth()*0/3, allShapes.get(MState.Setup).getShapes()[0].getMinY(), allShapes.get(MState.Setup).getShapes()[0].getWidth()*1/3, allShapes.get(MState.Setup).getShapes()[0].getHeight());
		allShapes.put(MState.SetupMaxPop, new controllerShape(r, Color.white, "Space", r.getMinX()+(r.getWidth()*1/2-font.getWidth("Space")/2), r.getMinY()+10));
		addShapesColor = new Polygon[3];
		Polygon p = new Polygon();
		p.addPoint(allShapes.get(MState.SetupMaxPop).getShapes()[0].getCenterX()-100, allShapes.get(MState.SetupMaxPop).getShapes()[0].getCenterY()-100);
		p.addPoint(allShapes.get(MState.SetupMaxPop).getShapes()[0].getCenterX()+100, allShapes.get(MState.SetupMaxPop).getShapes()[0].getCenterY()-100);
		p.addPoint(allShapes.get(MState.SetupMaxPop).getShapes()[0].getCenterX()+100, allShapes.get(MState.SetupMaxPop).getShapes()[0].getCenterY()+100);
		p.addPoint(allShapes.get(MState.SetupMaxPop).getShapes()[0].getCenterX()-100, allShapes.get(MState.SetupMaxPop).getShapes()[0].getCenterY()+100);
		p.addPoint(allShapes.get(MState.SetupMaxPop).getShapes()[0].getCenterX()-100, allShapes.get(MState.SetupMaxPop).getShapes()[0].getCenterY()-100);
		addShapesColor[0] = p;
		p = new Polygon();
		p.addPoint(addShapesColor[0].getCenterX()-15, addShapesColor[0].getMaxY()+20);
		p.addPoint(addShapesColor[0].getCenterX()-15, addShapesColor[0].getMaxY()+20+20);
		p.addPoint(addShapesColor[0].getCenterX()-30, addShapesColor[0].getMaxY()+20+10);
		p.addPoint(addShapesColor[0].getCenterX()-15, addShapesColor[0].getMaxY()+20);
		addShapesColor[1] = p;
		p = new Polygon();
		p.addPoint(addShapesColor[0].getCenterX()+15, addShapesColor[0].getMaxY()+20);
		p.addPoint(addShapesColor[0].getCenterX()+15, addShapesColor[0].getMaxY()+20+20);
		p.addPoint(addShapesColor[0].getCenterX()+30, addShapesColor[0].getMaxY()+20+10);
		p.addPoint(addShapesColor[0].getCenterX()+15, addShapesColor[0].getMaxY()+20);
		addShapesColor[2] = p;
		allShapes.put(MState.SetupMaxPop_IN, new controllerShape(addShapesColor[0], Color.white, String.valueOf(maxpop), addShapesColor[0].getCenterX()-font.getWidth(String.valueOf(maxpop))/2, addShapesColor[1].getMinY()+(addShapesColor[1].getHeight()/2-font.getHeight(String.valueOf(maxpop))/2)));
		allShapes.get(MState.SetupMaxPop_IN).setAddShapes(new Shape[] {addShapesColor[1], addShapesColor[2]}, new Color[] {Color.white, Color.white});
		allShapes.get(MState.SetupMaxPop_IN).setFill(new boolean[] {false, true, true});
		allShapes.get(MState.SetupMaxPop_IN).setAnimation(0, images.get("maxpop"), 30, 30, maxpop-10, 300);
		
		r = new Rectangle(allShapes.get(MState.Setup).getShapes()[0].getMinX()+allShapes.get(MState.Setup).getShapes()[0].getWidth()*1/3, allShapes.get(MState.Setup).getShapes()[0].getMinY(), allShapes.get(MState.Setup).getShapes()[0].getWidth()*1/3, allShapes.get(MState.Setup).getShapes()[0].getHeight());
		allShapes.put(MState.SetupHealth, new controllerShape(r, Color.white, "Health", r.getMinX()+(r.getWidth()*1/2-font.getWidth("Health")/2), r.getMinY()+10));
		addShapesColor = new Polygon[4];
		p = new Polygon();
		p.addPoint(allShapes.get(MState.SetupHealth).getShapes()[0].getCenterX()-100, allShapes.get(MState.SetupHealth).getShapes()[0].getCenterY()-100);
		p.addPoint(allShapes.get(MState.SetupHealth).getShapes()[0].getCenterX()+100, allShapes.get(MState.SetupHealth).getShapes()[0].getCenterY()-100);
		p.addPoint(allShapes.get(MState.SetupHealth).getShapes()[0].getCenterX()+100, allShapes.get(MState.SetupHealth).getShapes()[0].getCenterY()+100);
		p.addPoint(allShapes.get(MState.SetupHealth).getShapes()[0].getCenterX()-100, allShapes.get(MState.SetupHealth).getShapes()[0].getCenterY()+100);
		p.addPoint(allShapes.get(MState.SetupHealth).getShapes()[0].getCenterX()-100, allShapes.get(MState.SetupHealth).getShapes()[0].getCenterY()-100);
		addShapesColor[0] = p;
		p = new Polygon();
		p.addPoint(addShapesColor[0].getCenterX()-15, addShapesColor[0].getMaxY()+20);
		p.addPoint(addShapesColor[0].getCenterX()-15, addShapesColor[0].getMaxY()+20+20);
		p.addPoint(addShapesColor[0].getCenterX()-30, addShapesColor[0].getMaxY()+20+10);
		p.addPoint(addShapesColor[0].getCenterX()-15, addShapesColor[0].getMaxY()+20);
		addShapesColor[1] = p;
		p = new Polygon();
		p.addPoint(addShapesColor[0].getCenterX()+15, addShapesColor[0].getMaxY()+20);
		p.addPoint(addShapesColor[0].getCenterX()+15, addShapesColor[0].getMaxY()+20+20);
		p.addPoint(addShapesColor[0].getCenterX()+30, addShapesColor[0].getMaxY()+20+10);
		p.addPoint(addShapesColor[0].getCenterX()+15, addShapesColor[0].getMaxY()+20);
		addShapesColor[2] = p;
		p = new Polygon();
		p.addPoint(addShapesColor[0].getCenterX()-20, addShapesColor[0].getMaxY()-40);
		p.addPoint(addShapesColor[0].getCenterX()+20, addShapesColor[0].getMaxY()-40);
		p.addPoint(addShapesColor[0].getCenterX()+20, addShapesColor[0].getMaxY());
		p.addPoint(addShapesColor[0].getCenterX()-20, addShapesColor[0].getMaxY());
		p.addPoint(addShapesColor[0].getCenterX()-20, addShapesColor[0].getMaxY()-40);
		addShapesColor[3] = p;
		allShapes.put(MState.SetupHealth_IN, new controllerShape(addShapesColor[0], Color.white, String.valueOf(health), addShapesColor[0].getCenterX()-font.getWidth(String.valueOf(health))/2, addShapesColor[1].getMinY()+(addShapesColor[1].getHeight()/2-font.getHeight(String.valueOf(health))/2)));
		allShapes.get(MState.SetupHealth_IN).setAddShapes(new Shape[] {addShapesColor[1], addShapesColor[2], addShapesColor[3]}, new Color[] {Color.white, Color.white, Color.green});
		allShapes.get(MState.SetupHealth_IN).setFill(new boolean[] {false, true, true, true});
		allShapes.get(MState.SetupHealth_IN).setParticleSystem(3, images.get("health"), new File(Setup.temp("emitter1.xml")));
		allShapes.get(MState.SetupHealth_IN).setupPS(health);
		
		r = new Rectangle(allShapes.get(MState.Setup).getShapes()[0].getMinX()+allShapes.get(MState.Setup).getShapes()[0].getWidth()*2/3, allShapes.get(MState.Setup).getShapes()[0].getMinY(), allShapes.get(MState.Setup).getShapes()[0].getWidth()*1/3, allShapes.get(MState.Setup).getShapes()[0].getHeight());
		allShapes.put(MState.SetupMaxGen, new controllerShape(r, Color.white, "Time", r.getMinX()+(r.getWidth()*1/2-font.getWidth("Time")/2), r.getMinY()+10));
		addShapesColor = new Polygon[3];
		p = new Polygon();
		p.addPoint(allShapes.get(MState.SetupMaxGen).getShapes()[0].getCenterX()-100, allShapes.get(MState.SetupMaxGen).getShapes()[0].getCenterY()-100);
		p.addPoint(allShapes.get(MState.SetupMaxGen).getShapes()[0].getCenterX()+100, allShapes.get(MState.SetupMaxGen).getShapes()[0].getCenterY()-100);
		p.addPoint(allShapes.get(MState.SetupMaxGen).getShapes()[0].getCenterX()+100, allShapes.get(MState.SetupMaxGen).getShapes()[0].getCenterY()+100);
		p.addPoint(allShapes.get(MState.SetupMaxGen).getShapes()[0].getCenterX()-100, allShapes.get(MState.SetupMaxGen).getShapes()[0].getCenterY()+100);
		p.addPoint(allShapes.get(MState.SetupMaxGen).getShapes()[0].getCenterX()-100, allShapes.get(MState.SetupMaxGen).getShapes()[0].getCenterY()-100);
		addShapesColor[0] = p;
		p = new Polygon();
		p.addPoint(addShapesColor[0].getCenterX()-15, addShapesColor[0].getMaxY()+20);
		p.addPoint(addShapesColor[0].getCenterX()-15, addShapesColor[0].getMaxY()+20+20);
		p.addPoint(addShapesColor[0].getCenterX()-30, addShapesColor[0].getMaxY()+20+10);
		p.addPoint(addShapesColor[0].getCenterX()-15, addShapesColor[0].getMaxY()+20);
		addShapesColor[1] = p;
		p = new Polygon();
		p.addPoint(addShapesColor[0].getCenterX()+15, addShapesColor[0].getMaxY()+20);
		p.addPoint(addShapesColor[0].getCenterX()+15, addShapesColor[0].getMaxY()+20+20);
		p.addPoint(addShapesColor[0].getCenterX()+30, addShapesColor[0].getMaxY()+20+10);
		p.addPoint(addShapesColor[0].getCenterX()+15, addShapesColor[0].getMaxY()+20);
		addShapesColor[2] = p;
		allShapes.put(MState.SetupMaxGen_IN, new controllerShape(addShapesColor[0], Color.white, String.valueOf(maxgen), addShapesColor[0].getCenterX()-font.getWidth(String.valueOf(maxgen))/2, addShapesColor[1].getMinY()+(addShapesColor[1].getHeight()/2-font.getHeight(String.valueOf(maxgen))/2)));
		allShapes.get(MState.SetupMaxGen_IN).setAddShapes(new Shape[] {addShapesColor[1], addShapesColor[2]}, new Color[] {Color.white, Color.white});
		allShapes.get(MState.SetupMaxGen_IN).setFill(new boolean[] {false, true, true});
		allShapes.get(MState.SetupMaxGen_IN).setAnimation(0, images.get("maxgen"), 2300, 2300, 8, 10*maxgen);
	}
	public void giveCoordsOfMouse(float x, float y) {
		for(int i = 0; i<=19; i++) {
			if(allShapes.containsKey(getIteration(i))) {
				for(int j = 0; j<allShapes.get(getIteration(i)).getShapes().length; j++) {
					if(allShapes.get(getIteration(i)).getShapes()[j].contains(x, y)) {
						innerState = getIteration(i);
					}
				}
			}
		}
	}
	public boolean isInputText() {
		return inputText;
	}
	public void getWork(float mx, float my, short button) throws SlickException, IOException {
		if(button==-1) { return; }
		else {
			inputText = false;
			if(addState_Text!=MState.Undefined) {
				if(allShapes.get(addState_Text).getText().equals("")) {
					allShapes.get(addState_Text).setColor(0, Color.white);
					allShapes.get(addState_Text).setText("Nickname", allShapes.get(addState_Text).getShapes()[0].getCenterX()-font.getWidth("Nickname")/2, allShapes.get(addState_Text).getShapes()[0].getCenterY()-font.getHeight("Nickname")/2);
					allShapes.get(addState_Text).setFill(0, false);
				}
			}
		}
		String s = "";
		switch(innerState) {
		case PlayerOne_Color:
			for(int i = 1; i<allShapes.get(innerState).getShapes().length; i++) {
				if(allShapes.get(innerState).getShapes()[i].contains(mx, my) && allShapes.get(innerState).isFill()[i] && button==0) {
					players[0].setColor(allShapes.get(innerState).getColor()[i]);
					allShapes.get(MState.PlayerOne_Color).setFill(i, false);
					allShapes.get(MState.PlayerTwo_Color).setFill(i, false);
					allShapes.get(MState.PlayerThree_Color).setFill(i, false);
					allShapes.get(MState.PlayerFour_Color).setFill(i, false);
					if(allShapes.get(MState.PlayerOne_Nickname).isFill()[1]) {
						for(int j = 1; j<allShapes.get(innerState).getShapes().length; j++) {
							if(allShapes.get(innerState).getColor()[j].equals(allShapes.get(MState.PlayerOne_Nickname).getColor()[1])) {
								allShapes.get(MState.PlayerOne_Color).setFill(j, true);
								allShapes.get(MState.PlayerTwo_Color).setFill(j, true);
								allShapes.get(MState.PlayerThree_Color).setFill(j, true);
								allShapes.get(MState.PlayerFour_Color).setFill(j, true);
								break;
							}
						}
					}
					allShapes.get(MState.PlayerOne_Nickname).setFill(1, true);
					allShapes.get(MState.PlayerOne_Nickname).setColor(1, allShapes.get(innerState).getColor()[i]);
				}
			}
			break;
		case PlayerOne_Nickname:
			if(allShapes.get(innerState).getShapes()[1].contains(mx, my) && allShapes.get(innerState).isFill()[1] && button==1) {
				for(int j = 1; j<allShapes.get(MState.PlayerOne_Color).getShapes().length; j++) {
					if(allShapes.get(MState.PlayerOne_Color).getColor()[j].equals(allShapes.get(innerState).getColor()[1])) {
						allShapes.get(MState.PlayerOne_Color).setFill(j, true);
						allShapes.get(MState.PlayerTwo_Color).setFill(j, true);
						allShapes.get(MState.PlayerThree_Color).setFill(j, true);
						allShapes.get(MState.PlayerFour_Color).setFill(j, true);
						break;
					}
				}
				allShapes.get(innerState).setFill(1, false);
				allShapes.get(innerState).setColor(1, Color.white);
				players[0].setColor(Color.gray);
			}
			if(allShapes.get(innerState).getShapes()[0].contains(mx, my) && button==0) {
				allShapes.get(innerState).setText("", allShapes.get(innerState).getShapes()[0].getCenterX(), allShapes.get(innerState).getShapes()[0].getCenterY());
				allShapes.get(innerState).setFill(0, true);
				allShapes.get(innerState).setColor(0, new Color(72, 61, 139));
				inputText = true;
				addState_Text = innerState;
			}
			break;
		case PlayerTwo_Color:
			for(int i = 1; i<allShapes.get(innerState).getShapes().length; i++) {
				if(allShapes.get(innerState).getShapes()[i].contains(mx, my) && allShapes.get(innerState).isFill()[i] && button==0) {
					players[1].setColor(allShapes.get(innerState).getColor()[i]);
					allShapes.get(MState.PlayerOne_Color).setFill(i, false);
					allShapes.get(MState.PlayerTwo_Color).setFill(i, false);
					allShapes.get(MState.PlayerThree_Color).setFill(i, false);
					allShapes.get(MState.PlayerFour_Color).setFill(i, false);
					if(allShapes.get(MState.PlayerTwo_Nickname).isFill()[1]) {
						for(int j = 1; j<allShapes.get(innerState).getShapes().length; j++) {
							if(allShapes.get(innerState).getColor()[j].equals(allShapes.get(MState.PlayerTwo_Nickname).getColor()[1])) {
								allShapes.get(MState.PlayerOne_Color).setFill(j, true);
								allShapes.get(MState.PlayerTwo_Color).setFill(j, true);
								allShapes.get(MState.PlayerThree_Color).setFill(j, true);
								allShapes.get(MState.PlayerFour_Color).setFill(j, true);
								break;
							}
						}
					}
					allShapes.get(MState.PlayerTwo_Nickname).setFill(1, true);
					allShapes.get(MState.PlayerTwo_Nickname).setColor(1, allShapes.get(innerState).getColor()[i]);
				}
			}
			break;
		case PlayerTwo_Nickname:
			if(allShapes.get(innerState).getShapes()[1].contains(mx, my) && allShapes.get(innerState).isFill()[1] && button==1) {
				for(int j = 1; j<allShapes.get(MState.PlayerTwo_Color).getShapes().length; j++) {
					if(allShapes.get(MState.PlayerTwo_Color).getColor()[j].equals(allShapes.get(innerState).getColor()[1])) {
						allShapes.get(MState.PlayerOne_Color).setFill(j, true);
						allShapes.get(MState.PlayerTwo_Color).setFill(j, true);
						allShapes.get(MState.PlayerThree_Color).setFill(j, true);
						allShapes.get(MState.PlayerFour_Color).setFill(j, true);
						break;
					}
				}
				allShapes.get(innerState).setFill(1, false);
				allShapes.get(innerState).setColor(1, Color.white);
				players[1].setColor(Color.gray);
			}
			if(allShapes.get(innerState).getShapes()[0].contains(mx, my) && button==0) {
				allShapes.get(innerState).setText("", allShapes.get(innerState).getShapes()[0].getCenterX(), allShapes.get(innerState).getShapes()[0].getCenterY());
				allShapes.get(innerState).setFill(0, true);
				allShapes.get(innerState).setColor(0, new Color(72, 61, 139));
				inputText = true;
				addState_Text = innerState;
			}
			break;
		case PlayerThree_Color:
			for(int i = 1; i<allShapes.get(innerState).getShapes().length; i++) {
				if(allShapes.get(innerState).getShapes()[i].contains(mx, my) && allShapes.get(innerState).isFill()[i] && button==0) {
					players[2].setColor(allShapes.get(innerState).getColor()[i]);
					allShapes.get(MState.PlayerOne_Color).setFill(i, false);
					allShapes.get(MState.PlayerTwo_Color).setFill(i, false);
					allShapes.get(MState.PlayerThree_Color).setFill(i, false);
					allShapes.get(MState.PlayerFour_Color).setFill(i, false);
					if(allShapes.get(MState.PlayerThree_Nickname).isFill()[1]) {
						for(int j = 1; j<allShapes.get(innerState).getShapes().length; j++) {
							if(allShapes.get(innerState).getColor()[j].equals(allShapes.get(MState.PlayerThree_Nickname).getColor()[1])) {
								allShapes.get(MState.PlayerOne_Color).setFill(j, true);
								allShapes.get(MState.PlayerTwo_Color).setFill(j, true);
								allShapes.get(MState.PlayerThree_Color).setFill(j, true);
								allShapes.get(MState.PlayerFour_Color).setFill(j, true);
								break;
							}
						}
					}
					allShapes.get(MState.PlayerThree_Nickname).setFill(1, true);
					allShapes.get(MState.PlayerThree_Nickname).setColor(1, allShapes.get(innerState).getColor()[i]);
				}
			}
			break;
		case PlayerThree_Nickname:
			if(allShapes.get(innerState).getShapes()[1].contains(mx, my) && allShapes.get(innerState).isFill()[1] && button==1) {
				for(int j = 1; j<allShapes.get(MState.PlayerThree_Color).getShapes().length; j++) {
					if(allShapes.get(MState.PlayerThree_Color).getColor()[j].equals(allShapes.get(innerState).getColor()[1])) {
						allShapes.get(MState.PlayerOne_Color).setFill(j, true);
						allShapes.get(MState.PlayerTwo_Color).setFill(j, true);
						allShapes.get(MState.PlayerThree_Color).setFill(j, true);
						allShapes.get(MState.PlayerFour_Color).setFill(j, true);
						break;
					}
				}
				allShapes.get(innerState).setFill(1, false);
				allShapes.get(innerState).setColor(1, Color.white);
				players[2].setColor(Color.gray);
			}
			if(allShapes.get(innerState).getShapes()[0].contains(mx, my) && button==0) {
				allShapes.get(innerState).setText("", allShapes.get(innerState).getShapes()[0].getCenterX(), allShapes.get(innerState).getShapes()[0].getCenterY());
				allShapes.get(innerState).setFill(0, true);
				allShapes.get(innerState).setColor(0, new Color(72, 61, 139));
				inputText = true;
				addState_Text = innerState;
			}
			break;
		case PlayerFour_Color:
			for(int i = 1; i<allShapes.get(innerState).getShapes().length; i++) {
				if(allShapes.get(innerState).getShapes()[i].contains(mx, my) && allShapes.get(innerState).isFill()[i] && button==0) {
					players[3].setColor(allShapes.get(innerState).getColor()[i]);
					allShapes.get(MState.PlayerOne_Color).setFill(i, false);
					allShapes.get(MState.PlayerTwo_Color).setFill(i, false);
					allShapes.get(MState.PlayerThree_Color).setFill(i, false);
					allShapes.get(MState.PlayerFour_Color).setFill(i, false);
					if(allShapes.get(MState.PlayerFour_Nickname).isFill()[1]) {
						for(int j = 1; j<allShapes.get(innerState).getShapes().length; j++) {
							if(allShapes.get(innerState).getColor()[j].equals(allShapes.get(MState.PlayerFour_Nickname).getColor()[1])) {
								allShapes.get(MState.PlayerOne_Color).setFill(j, true);
								allShapes.get(MState.PlayerTwo_Color).setFill(j, true);
								allShapes.get(MState.PlayerThree_Color).setFill(j, true);
								allShapes.get(MState.PlayerFour_Color).setFill(j, true);
								break;
							}
						}
					}
					allShapes.get(MState.PlayerFour_Nickname).setFill(1, true);
					allShapes.get(MState.PlayerFour_Nickname).setColor(1, allShapes.get(innerState).getColor()[i]);
				}
			}
			break;
		case PlayerFour_Nickname:
			if(allShapes.get(innerState).getShapes()[1].contains(mx, my) && allShapes.get(innerState).isFill()[1] && button==1) {
				for(int j = 1; j<allShapes.get(MState.PlayerFour_Color).getShapes().length; j++) {
					if(allShapes.get(MState.PlayerFour_Color).getColor()[j].equals(allShapes.get(innerState).getColor()[1])) {
						allShapes.get(MState.PlayerOne_Color).setFill(j, true);
						allShapes.get(MState.PlayerTwo_Color).setFill(j, true);
						allShapes.get(MState.PlayerThree_Color).setFill(j, true);
						allShapes.get(MState.PlayerFour_Color).setFill(j, true);
						break;
					}
				}
				allShapes.get(innerState).setFill(1, false);
				allShapes.get(innerState).setColor(1, Color.white);
				players[3].setColor(Color.gray);
			}
			if(allShapes.get(innerState).getShapes()[0].contains(mx, my) && button==0) {
				allShapes.get(innerState).setText("", allShapes.get(innerState).getShapes()[0].getCenterX(), allShapes.get(innerState).getShapes()[0].getCenterY());
				allShapes.get(innerState).setFill(0, true);
				allShapes.get(innerState).setColor(0, new Color(72, 61, 139));
				inputText = true;
				addState_Text = innerState;
			}
			break;
		case SetupMaxPop_IN:
			if(allShapes.get(innerState).getShapes()[1].contains(mx, my) && button==0 && maxpop-1>=10) {
				maxpop--;
				allShapes.get(innerState).setAnimation(0, images.get("maxpop"), 30, 30, maxpop-10, 300);
			}
			else if(allShapes.get(innerState).getShapes()[2].contains(mx, my) && button==0 && maxpop+1<=100) {
				maxpop++;
				allShapes.get(innerState).setAnimation(0, images.get("maxpop"), 30, 30, maxpop-10, 300);
			}
			s = String.valueOf(maxpop);
			allShapes.get(innerState).setText(s, allShapes.get(innerState).getShapes()[1].getMaxX()+(Math.abs(allShapes.get(innerState).getShapes()[2].getMinX()-allShapes.get(innerState).getShapes()[1].getMaxX())/2-font.getWidth(s)/2), allShapes.get(innerState).getShapes()[1].getMinY()+((allShapes.get(innerState).getShapes()[1].getMaxY()-allShapes.get(innerState).getShapes()[1].getMinY())/2-font.getHeight(s)/2));
			break;
		case SetupHealth_IN:
			if(allShapes.get(innerState).getShapes()[1].contains(mx, my) && button==0 && health-1>=1) {
				health--;
				allShapes.get(innerState).setupPS(health);
			}
			else if(allShapes.get(innerState).getShapes()[2].contains(mx, my) && button==0 && health+1<=10) {
				health++;
				allShapes.get(innerState).setupPS(health);
			}
			s = String.valueOf(health);
			allShapes.get(innerState).setText(s, allShapes.get(innerState).getShapes()[1].getMaxX()+(Math.abs(allShapes.get(innerState).getShapes()[2].getMinX()-allShapes.get(innerState).getShapes()[1].getMaxX())/2-font.getWidth(s)/2), allShapes.get(innerState).getShapes()[1].getMinY()+((allShapes.get(innerState).getShapes()[1].getMaxY()-allShapes.get(innerState).getShapes()[1].getMinY())/2-font.getHeight(s)/2));
			break;
		case SetupMaxGen_IN:
			if(allShapes.get(innerState).getShapes()[1].contains(mx, my) && button==0 && maxgen-1>=5) {
				maxgen--;
				allShapes.get(innerState).setAnimation(0, images.get("maxgen"), 2300, 2300, 8, 10*maxgen);
			}
			else if(allShapes.get(innerState).getShapes()[2].contains(mx, my) && button==0 && maxgen+1<=20) {
				maxgen++;
				allShapes.get(innerState).setAnimation(0, images.get("maxgen"), 2300, 2300, 8, 10*maxgen);
			}
			s = String.valueOf(maxgen);
			allShapes.get(innerState).setText(s, allShapes.get(innerState).getShapes()[1].getMaxX()+(Math.abs(allShapes.get(innerState).getShapes()[2].getMinX()-allShapes.get(innerState).getShapes()[1].getMaxX())/2-font.getWidth(s)/2), allShapes.get(innerState).getShapes()[1].getMinY()+((allShapes.get(innerState).getShapes()[1].getMaxY()-allShapes.get(innerState).getShapes()[1].getMinY())/2-font.getHeight(s)/2));
			break;
		default: break;
		}
	}
	public void giveStr(String str) {
		if(!inputText) { return; }
		allShapes.get(addState_Text).setText(str, allShapes.get(addState_Text).getShapes()[0].getCenterX()-font.getWidth(str)/2, allShapes.get(addState_Text).getShapes()[0].getCenterY()-font.getHeight(str)/2);
		int IDP = 0;
		switch(addState_Text) {
		case PlayerOne_Nickname: IDP = 0; break;
		case PlayerTwo_Nickname: IDP = 1; break;
		case PlayerThree_Nickname: IDP = 2; break;
		case PlayerFour_Nickname: IDP = 3; break;
		default: break;
		}
		players[IDP].setNickname(str);
	}
	public String getInputText() {
		if(!inputText) {
			return "";
		}
		return allShapes.get(addState_Text).getText();
	}
	public void writeSetup() throws Exception {
		boolean b = false;
		for(int i = 0; i<players.length; i++) {
			if(players[i].getColor()!=Color.gray && !players[i].getNickname().equals("###") && !players[i].getNickname().equals("")) {
				b = true;
			}
			else {
				players[i] = new Player("###", "gray");
			}
		}
		if(!b) {
			players[0] = new Player("Admin", "green");
		}
		Setup setup = new Setup();
		setup.writeSetup(maxpop, health, maxgen, players);
	}
	public Player[] getRealPlayers() {
		ArrayList<Player> out = new ArrayList<Player>();
		for(int i = 0; i<players.length; i++) {
			if(players[i].getColor()!=Color.gray && !players[i].getNickname().equals("###") && !players[i].getNickname().equals("")) {
				out.add(players[i]);
			}
		}
		out.trimToSize();
		return out.toArray(new Player[out.size()]);
	}
	public Shape[][] getFill() {
		Shape[][] out = new Shape[allShapes.size()][];
		for(int i = 0, j = 0; i<=19 && j<out.length; i++) {
			if(allShapes.containsKey(getIteration(i))) {
				out[j] = new Shape[allShapes.get(getIteration(i)).getShapes().length];
				for(int k = 0; k<allShapes.get(getIteration(i)).getShapes().length; k++) {
					out[j][k] = allShapes.get(getIteration(i)).getShapes()[k];
				}
				j++;
			}
		}
		return out;
	}
	public boolean[][] getIsFill(){
		boolean[][] out = new boolean[allShapes.size()][];
		for(int i = 0, j = 0; i<=19 && j<out.length; i++) {
			if(allShapes.containsKey(getIteration(i))) {
				out[j] = new boolean[allShapes.get(getIteration(i)).getColor().length];
				for(int k = 0; k<allShapes.get(getIteration(i)).getColor().length; k++) {
					out[j][k] = allShapes.get(getIteration(i)).isFill()[k];
				}
				j++;
			}
		}
		return out;
	}
	public Color[][] getColors() {
		Color[][] out = new Color[allShapes.size()][];
		for(int i = 0, j = 0; i<=19 && j<out.length; i++) {
			if(allShapes.containsKey(getIteration(i))) {
				out[j] = new Color[allShapes.get(getIteration(i)).getColor().length];
				for(int k = 0; k<allShapes.get(getIteration(i)).getColor().length; k++) {
					out[j][k] = allShapes.get(getIteration(i)).getColor()[k];
				}
				j++;
			}
		}
		return out;
	}
	public String[] getText() {
		String[] out = new String[allShapes.size()];
		for(int i = 0, j = 0; i<=19 && j<out.length; i++) {
			if(allShapes.containsKey(getIteration(i))) {
				out[j++] = allShapes.get(getIteration(i)).getText();
			}
		}
		return out;
	}
	public float[][] getCoordOfText() {
		float[][] out = new float[allShapes.size()][2];
		for(int i = 0, j = 0; i<=19 && j<out.length; i++) {
			if(allShapes.containsKey(getIteration(i))) {
				out[j++] = allShapes.get(getIteration(i)).getXyOfText();
			}
		}
		return out;
	}
	public void renderAnimAndPS() {
		for(int i = 0; i<=19; i++) {
			if(allShapes.containsKey(getIteration(i))) {
				allShapes.get(getIteration(i)).renderAll();
			}
		}
	}
	public void updateAnimAndPS(int delta) {
		for(int i = 0; i<=19; i++) {
			if(allShapes.containsKey(getIteration(i))) {
				allShapes.get(getIteration(i)).updateAll(delta);
			}
		}
	}
	private MState getIteration(int i) {
		switch(i) {
		case 0: return MState.Player;
		case 1: return MState.PlayerOne;
		case 2: return MState.PlayerOne_Nickname;
		case 3: return MState.PlayerOne_Color;
		case 4: return MState.PlayerTwo;
		case 5: return MState.PlayerTwo_Nickname;
		case 6: return MState.PlayerTwo_Color;
		case 7: return MState.PlayerThree;
		case 8: return MState.PlayerThree_Nickname;
		case 9: return MState.PlayerThree_Color;
		case 10: return MState.PlayerFour;
		case 11: return MState.PlayerFour_Nickname;
		case 12: return MState.PlayerFour_Color;
		case 13: return MState.Setup;
		case 14: return MState.SetupMaxPop;
		case 15: return MState.SetupMaxPop_IN;
		case 16: return MState.SetupHealth;
		case 17: return MState.SetupHealth_IN;
		case 18: return MState.SetupMaxGen;
		case 19: return MState.SetupMaxGen_IN;
		default: return null;
		}
	}
}
