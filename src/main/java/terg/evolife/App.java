package terg.evolife;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class App extends StateBasedGame{
	private static AppGameContainer app;
	private static float winWidth;
	private static float winHeight;
	public App(String name) {
		super(name);
	}
	public static void main(String[] args) throws SlickException, IOException {
		app = new AppGameContainer(new App("Evol/Lif"));
		if(args.length==1) {
			if(args[0].equals("f")) {
				setFullscr(true, false);
			}
		}
		else {
			setFullscr(false, false);
		}
		app.setMinimumLogicUpdateInterval(10);
		app.setMaximumLogicUpdateInterval(20);
		app.setIcon(Setup.temp("icon.png"));
		app.setShowFPS(false);
		app.start();
	}
	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		this.addState(new Menu());
		this.addState(new Space());
	}
	public static float getWinWidth() {
		return winWidth;
	}
	public static float getWinHeight() {
		return winHeight;
	}
	public static void setFullscr(boolean full, boolean reinitApp) throws SlickException, IOException {
		Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
		if(full) { app.setDisplayMode(sSize.width, sSize.height, true); }
		else { app.setDisplayMode(sSize.width-100, sSize.height-100, false); }
		if(reinitApp) { app.reinit(); }
		winWidth = app.getWidth();
		winHeight = app.getHeight();
	}
	public static boolean isFullscr() {
		return app.isFullscreen();
	}
}
