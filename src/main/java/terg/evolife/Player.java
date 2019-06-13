package terg.evolife;

import org.newdawn.slick.Color;

public class Player {
	private String nickname;
	private Color playcolor;
	private boolean active;
	public Player(String nickname, String color) {
		this.nickname = nickname;
		playcolor = strToColor(color);
		active = false;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public void setColor(Color playcolor) {
		this.playcolor = playcolor;
	}
	public String getNickname() {
		return nickname;
	}
	public Color getColor() {
		return playcolor;
	}
	public int getColorInt() {
		if(playcolor.equals(Color.magenta)) { return 0; }
		else if(playcolor.equals(Color.blue)) { return 1; }
		else if(playcolor.equals(Color.cyan)) { return 2; }
		else if(playcolor.equals(Color.green)) { return 3; }
		else if(playcolor.equals(Color.pink)) { return 10; }
		else if(playcolor.equals(Color.red)) { return 11; }
		else if(playcolor.equals(Color.orange)) { return 12; }
		else if(playcolor.equals(Color.yellow)) { return 13; }
		else if(playcolor.equals(Color.white)) { return 20; }
		else if(playcolor.equals(Color.black)) { return 21; }
		else if(playcolor.equals(Color.gray)) { return -1; }
		else { return -1; }
	}
	public String getColorStr() {
		if(playcolor.equals(Color.magenta)) { return "magenta"; }
		else if(playcolor.equals(Color.blue)) { return "blue"; }
		else if(playcolor.equals(Color.cyan)) { return "cyan"; }
		else if(playcolor.equals(Color.green)) { return "green"; }
		else if(playcolor.equals(Color.pink)) { return "pink"; }
		else if(playcolor.equals(Color.red)) { return "red"; }
		else if(playcolor.equals(Color.orange)) { return "orange"; }
		else if(playcolor.equals(Color.yellow)) { return "yellow"; }
		else if(playcolor.equals(Color.white)) { return "white"; }
		else if(playcolor.equals(Color.black)) { return "black"; }
		else { return "gray"; }
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	private Color strToColor(String color) {
		if(color.equalsIgnoreCase("magenta")) { return Color.magenta; }
		else if(color.equalsIgnoreCase("blue")) { return Color.blue; }
		else if(color.equalsIgnoreCase("cyan")) { return Color.cyan; }
		else if(color.equalsIgnoreCase("green")) { return Color.green; }
		else if(color.equalsIgnoreCase("pink")) { return Color.pink; }
		else if(color.equalsIgnoreCase("red")) { return Color.red; }
		else if(color.equalsIgnoreCase("orange")) { return Color.orange; }
		else if(color.equalsIgnoreCase("yellow")) { return Color.yellow; }
		else if(color.equalsIgnoreCase("white")) { return Color.white; }
		else if(color.equalsIgnoreCase("black")) { return Color.black; }
		else { return Color.gray; }
	}
	@SuppressWarnings("serial")
	public class GamingException extends Exception{
		public GamingException(String message){
	    	super(message);
		}
	}
}

