package terg.evolife;

import java.io.IOException;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import terg.iinur.Individ;
import terg.iinur.TrainSet;

public class Self {
	public enum Type{
		White {
			@Override
			public boolean isPlayers(int color) throws Exception { throw new Exception("Error in type of self"); }
			@Override
			public boolean isMachines(int color) throws Exception { throw new Exception("Error in type of self"); }
		},
		Black {
			@Override
			public boolean isPlayers(int color) throws Exception { throw new Exception("Error in type of self"); }
			@Override
			public boolean isMachines(int color) throws Exception { throw new Exception("Error in type of self"); }
		},
		Players {
			@Override
			public boolean isPlayers(int color) throws Exception { throw new Exception("Error in type of self"); }
			@Override
			public boolean isMachines(int color) throws Exception { throw new Exception("Error in type of self"); }
		},
		Machines {
			@Override
			public boolean isPlayers(int color) throws Exception { throw new Exception("Error in type of self"); }
			@Override
			public boolean isMachines(int color) throws Exception { throw new Exception("Error in type of self"); }
		},
		Undefined {
			@Override
			public boolean isPlayers(int color) {
				return color>=0 && color<10;
			}
			@Override
			public boolean isMachines(int color) {
				return color>=10 && color<20;
			}
		};
		public abstract boolean isPlayers(int color) throws Exception;
		public abstract boolean isMachines(int color) throws Exception;
	}
	public enum Status{
		Undefined,
		ToKill,
		ToMother,
		ToFather,
		ToChild,
		ToMutation;
	}
	public class selfShape{
		private Rectangle backShape;
		private Shape frontShape;
		private boolean isEllipse;
		private Image image;
		private float persent;
		public selfShape(Rectangle backShape) {
			this.backShape = backShape;
			frontShape = backShape;
			persent = 0.0f;
			image = null;
			isEllipse = false;
		}
		public Rectangle getBackShape() {
			return backShape;
		}
		public Shape getFrontShape() {
			return frontShape;
		}
		public Image getImage() {
			return image;
		}
		public void setImage(Image image) {
			this.image = image;
		}
		public void setBorders(float persent, boolean isEllipse) {
			this.persent = persent;
			this.isEllipse = isEllipse;
			if(isEllipse) {
				frontShape = new Ellipse(backShape.getCenterX(), backShape.getCenterY(), backShape.getWidth()/2, backShape.getHeight()/2);
				return;
			}
			if(persent == 0.0f) {
				frontShape = backShape;
				return;
			}
			frontShape = new Rectangle(backShape.getX()+(backShape.getWidth()*persent), backShape.getY()+(backShape.getHeight()*persent), backShape.getWidth()-(backShape.getWidth()*persent*2), backShape.getHeight()-(backShape.getHeight()*persent*2));
		}
		public float getX() {
			return backShape.getX();
		}
		public float getY() {
			return backShape.getY();
		}
		public void setSize(float width, float hieght) {
			((Rectangle) backShape).setSize(width, hieght);
			setBorders(persent, isEllipse);
		}
		public void setLocation(float x, float y) {
			((Rectangle) backShape).setLocation(x, y);
			setBorders(persent, isEllipse);
		}
	}
	private Random r = new Random();
	private boolean alive;
	private selfShape form;
	private Type type;
	private Status status;
	private int ID = -1;
	private final Type backgroundtype = r.nextInt(100)<90 ? Type.Black : Type.White;
	private final int backgroundhealth = backgroundtype==Type.Black ? 1 : -1;
	private final int backgroundcolor = backgroundtype==Type.Black ? 21 : 20;
	private Self gost;
	private boolean activation = true;
	private int health;
	private int color;
	private Individ brain;
	public Self(Rectangle rec){
		form = new selfShape(rec);
		setDead();
	}
	public Self(Rectangle rec, Individ individ, int health, int color) throws Exception{
		form = new selfShape(rec);
		setAlive(individ, health, color);
	}
	public Self(Self newobj) throws Exception{
		form = new selfShape(newobj.getForm().getBackShape());
		if(newobj.isAlive()) {
			setAlive(newobj.getBrain(), newobj.getHealth(), newobj.getColorInt());
		}
		else {
			setDead();
		}
	}
	public selfShape getForm() {
		return form;
	}
	public Color getColor() {
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
	public int getColorInt() {
		return color;
	}
	public int getHealth() {
		return health;
	}
	public Individ getBrain() {
		return brain;
	}
	public void update(double[] input) throws Exception {
		brain.setSet(new TrainSet(input, new double[] {this.getHealth()}));
	}
	public Type getType() {
		return type;
	}
	public Type getBackgroundtype() {
		return backgroundtype;
	}
	public int getBackgroundhealth() {
		return backgroundhealth;
	}
	public Color getBackgroundcolor() {
		switch(backgroundcolor) {
		//** dead colors **//
		case 20: return Color.white;
		case 21: return Color.black;
		//** wrong color **//
		default: return Color.gray;
		}
	}
	public Self getGost() {
		return gost;
	}
	public Status getStatus() {
		return status;
	}
	public int getID() {
		return ID;
	}
	public void setID(int ID) {
		this.ID = ID;
	}
	public void setStatus(Status status) throws SlickException, IOException {
		this.status = status;
		switch(status) {
		case ToChild:
			if(type == Type.Black) {
				form.setBorders(0.0f, true);
				color = 20;
			}
			form.setImage(new Image(Setup.temp("child.png"), false));
			break;
		case ToFather:
			form.setImage(new Image(Setup.temp("father.png"), false));
			break;
		case ToKill:
			form.setImage(new Image(Setup.temp("kill.png"), false));
			break;
		case ToMother:
			form.setImage(new Image(Setup.temp("mother.png"), false));
			break;
		case ToMutation:
			form.setImage(new Image(Setup.temp("mutation.png"), false));
			break;
		case Undefined:
			if(type == Type.Black) {
				form.setBorders(0.0f, false);
				color = backgroundcolor;
			}
			form.setImage(null);
			break;
		default:
			break;
		}
	}
	public void setGost(Self gost) {
		this.gost = gost;
		activation = false;
	}
	public void setBrain(Individ brain) {
		this.brain = brain;
	}
	public void setHealth(int health) {
		this.health = health;
		if(this.health<=0) {
			setDead();
		}
	}
	public void setColor(int color) {
		this.color = color;
		if(color>=0 && color<10) {
			type = Type.Players;
		}
		else if(color>=10 && color<20) {
			type = Type.Machines;
		}
	}
	public void setAlive(Individ individ, int health, int color) throws Exception {
		if(color>=0 && color<10) {
			type = Type.Players;
		}
		else if(color>=10 && color<20) {
			type = Type.Machines;
		}
		alive = true;
		this.health = health;
		this.color = color;
		brain = individ;
		activation = true;
		status = Status.Undefined;
		form.setBorders(0.2f, false);
	}
	public void setAlive(int health) throws Exception {
		alive = true;
		this.health = health;
		brain = null;
		type = Type.Undefined;
		activation = true;
		status = Status.Undefined;
		form.setBorders(0.2f, false);
	}
	public void setDead() {
		alive = false;
		color = backgroundcolor;
		health = backgroundhealth;
		type = backgroundtype;
		brain = null;
		status = Status.Undefined;
		form.setBorders(0.0f, false);
	}
	public void activation() throws Exception {
		if(gost!=null && gost.isAlive()) {
			if(this.alive) {
				int newhp = this.health-gost.getHealth();
				if(newhp>0) {
					gost.setHealth(newhp);
					this.brain = gost.getBrain();
					this.color = gost.getColorInt();
					this.health = gost.getHealth();
				}
				else if(newhp==0) {
					this.setDead();
				}
				else {
					this.health = Math.abs(newhp);
				}
			}
			else {
				this.setAlive(gost.getBrain(), gost.getHealth(), gost.getColorInt());
			}
			gost = null;
		}
		activation = true;
	}
	public boolean isActivation() {
		return activation;
	}
	public boolean isAlive() {
		return alive;
	}
	public int[] move(int x, int y, int borderx, int bordery) throws Exception {
		double[] nurOut = brain.getOut();
		double max = nurOut[0];
		int maxi = 0;
		for(int i = 1; i<nurOut.length; i++) {
			if(max<nurOut[i]) {
				max = nurOut[i];
				maxi = i;
			}
		}
		int[] out = new int[2];
		switch(maxi) {
		case 0:
			out[0] = x;
			out[1] = y;
			break;
		case 1:
			out[0] = x-1>=0 ? x-1 : x;
			out[1] = y-1>=0 ? y-1 : y;
			break;
		case 2:
			out[0] = x;
			out[1] = y-1>=0 ? y-1 : y;
			break;
		case 3:
			out[0] = x+1<borderx ? x+1 : x;
			out[1] = y-1>=0 ? y-1 : y;
			break;
		case 4:
			out[0] = x-1>=0 ? x-1 : x;
			out[1] = y;
			break;
		case 5:
			out[0] = x+1<borderx ? x+1 : x;
			out[1] = y;
			break;
		case 6:
			out[0] = x-1>=0 ? x-1 : x;
			out[1] = y+1<bordery ? y+1 : y;
			break;
		case 7:
			out[0] = x;
			out[1] = y+1<bordery ? y+1 : y;
			break;
		case 8:
			out[0] = x+1<borderx ? x+1 : x;
			out[1] = y+1<bordery ? y+1 : y;
			break;
		}
		return out;
	}
}
