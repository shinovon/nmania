package nmania.ui;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;

import nmania.Nmania;
import nmania.Skin;
import symnovel.SNUtils;

public class MainScreen extends GameCanvas implements Runnable {

	public MainScreen() {
		super(false);
		setFullScreenMode(true);
		(new Thread(this, "Main menu repainter")).start();
	}

	private Graphics g;
	private boolean needThread;
	public static int bgColor = SNUtils.toARGB("0xffbd55");
	private Image logo, menu;
	int state = -2;

	protected void keyPressed(int k) {
		lastInputIsTouch = false;
		if (state == 0) {
			state = 1;
			return;
		}
		if (state == 2) {
			if (k == -5 || k == '5') {
				state = 4;
				action = 1;
				return;
			}
			if (k == -6 || k == '1') {
				state = 4;
				action = 2;
				return;
			}
			if (k == -3 || k == '7') {
				state = 4;
				action = 3;
				return;
			}
			if (k == -4 || k == '3') {
				state = 4;
				action = 4;
				return;
			}
			if (k == -7 || k == '9') {
				state = 5;
				return;
			}
		}
	}

	protected void pointerReleased(int arg0, int arg1) {
		lastInputIsTouch = true;
		if (state == 0) {
			state = 1;
			return;
		}
	}

	int action = 0;
	boolean lastInputIsTouch;

	protected void pointerPressed(int x, int y) {
		lastInputIsTouch = true;
		if (state == 2) {
			x -= (getWidth() - menu.getWidth());
			y -= (getHeight() - menu.getHeight());
			x *= mul;
			y *= mul;
			if (x < 210) {
				if (y > 180) {
					// skin
					action = 3;
				} else {
					// sets
					action = 2;
				}
				state = 4;
				return;
			} else if (x > 640 - 210) {
				if (y > 180) {
					// exit
					state = 5;
					return;
				} else {
					state = 4;
					action = 4;
					return;
				}
			} else {
				state = 4;
				action = 1;
				return;
			}
		}
	}

	private void Open() {
		switch (action) {
		case 1:
			Play();
			break;
		case 2:
			Nmania.Push(new SettingsScreen(lastInputIsTouch));
			break;
		case 3:
			Nmania.Push(new SkinSelect());
			break;
		case 4:
			Nmania.Push(new InfoScreen());
			break;
		default:
			break;
		}
		needThread = false;
	}

	/**
	 * Launches song select.
	 */
	private void Play() {
		(new Thread(new Runnable() {
			public void run() {
				try {
					String wd;
					try {
						Class.forName("emulator.Emulator");
						wd = "file://root/";
					} catch (Exception e) {
						wd = "file:///C:/Data/Sounds/nmania/";
					}
					Nmania.LoadManager(wd);
					if (Nmania.skin == null) {
						Nmania.skin = new Skin();
					}
					Nmania.Push(new BeatmapSetsList(Nmania.bm));
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e.toString());
				}
			}
		}, "BMSL loader")).start();
	}

	float mul;

	public void run() {
		state = -1;
		needThread = true;
		g = getGraphics();
		int w = getWidth();
		int h = getHeight();
		int l = (w > h) ? w : h;
		g.setColor(0);
		g.fillRect(0, 0, w, h);

		String suffix;
		if (w >= 1280 && h >= 720) {
			suffix = "1x";
			mul = 0.5f;
		} else if (w >= 640 && h >= 360) {
			suffix = "0.5x";
			mul = 1f;
		} else if (w >= 320 && h >= 180) {
			suffix = "0.25x";
			mul = 2f;
		} else {
			suffix = "0.12x";
			mul = 4f;
		}
		try {
			logo = Image.createImage("/ui/nmania-logo-" + suffix + ".png");
			menu = Image.createImage("/ui/menu-" + suffix + ".png");
		} catch (IOException e) {
			e.printStackTrace();
		}

		// - > LOGO
		long startTime = System.currentTimeMillis();
		while (needThread) {
			long now = System.currentTimeMillis();
			g.setColor(bgColor);
			int arcSize = (int) ((now - startTime) * l / 500);
			g.fillArc(w / 2 - arcSize, h / 2 - arcSize, arcSize * 2, arcSize * 2, 0, 360);
			flushGraphics();
			if (now - startTime > 500)
				break;
		}
		startTime = System.currentTimeMillis();
		while (needThread) {
			long now = System.currentTimeMillis();
			g.setColor(-1);
			int arcSize = (int) ((now - startTime) * Math.min(w, h) / 500) / 2;
			g.fillArc(w / 2 - arcSize, h / 2 - arcSize, arcSize * 2, arcSize * 2, 0, 360);
			flushGraphics();
			if (now - startTime > 500)
				break;
		}
		startTime = System.currentTimeMillis();
		while (needThread) {
			long now = System.currentTimeMillis();
			g.setColor(bgColor);
			g.fillRect(0, 0, w, h);
			g.drawImage(logo, w / 2, h / 2, 3);
			g.setColor(-1);
			int arcSize = (int) ((500 - (now - startTime)) * Math.min(w, h) / 500) / 2;
			g.fillArc(w / 2 - arcSize, h / 2 - arcSize, arcSize * 2, arcSize * 2, 0, 360);
			flushGraphics();
			if (now - startTime > 500)
				break;
		}
		state = 0;
		// LOGO
		while (needThread && state == 0) {
			g.setColor(bgColor);
			g.fillRect(0, 0, w, h);
			g.drawImage(logo, w / 2, h / 2, 3);
			flushGraphics();
		}
		// LOGO > MENU
		startTime = System.currentTimeMillis();
		while (needThread) {
			long now = System.currentTimeMillis();
			g.setColor(-1);
			int arcSize = (int) ((now - startTime) * l / 500);
			g.fillArc(w / 2 - arcSize, h / 2 - arcSize, arcSize * 2, arcSize * 2, 0, 360);
			flushGraphics();
			if (now - startTime > 500)
				break;
		}
		// MENU
		state = 2;
		startTime = System.currentTimeMillis();
		while (needThread && state == 2) {
			long now = System.currentTimeMillis();
			g.setColor(bgColor);
			g.fillRect(0, 0, w, h);
			g.drawImage(menu, w / 2, h / 2, 3);
			if (now - startTime < 1000) {
				g.setColor(-1);
				int h1 = (int) (h * (1000 - (now - startTime)) / 1000) / 2;
				g.fillRect(0, 0, w, h1);
				g.fillRect(0, h - h1, w, h1);
			}
			flushGraphics();
		}
		// MENU > SUBMENU
		if (state == 4) {
			startTime = System.currentTimeMillis();
			// play
			while (true) {
				int length = 500;
				long now = System.currentTimeMillis();
				g.setColor(0);
				int h1 = (int) (h * (now - startTime) / length);
				g.fillRect(0, 0, w, h1);
				g.fillRect(0, h - h1, w, h1);
				flushGraphics();
				if (now - startTime > length) {
					Open();
					// loading animation
					startTime = System.currentTimeMillis();
					while (needThread) {
						now = System.currentTimeMillis();
						g.setColor(0);
						g.fillRect(0, 0, w, h);
						g.setColor(-1);
						g.fillArc(w / 2 - 20, h / 2 - 20, 40, 40, (int) now, 90);
						g.fillArc(w / 2 - 20, h / 2 - 20, 40, 40, (int) now + 180, 90);
						flushGraphics();
						try {
							Thread.sleep(30);
						} catch (InterruptedException e) {
							return;
						}
					}
					return;
				}
			}
		}
		// MENU > EXIT
		if (state == 5) {
			startTime = System.currentTimeMillis();
			// exit
			while (true) {
				long now = System.currentTimeMillis();
				g.setColor(-1);
				int rw = (int) (w * (now - startTime) / 1000) / 2;

				g.fillRect(0, 0, rw, h);
				g.fillRect(w - rw, 0, rw, h);
				int arcSize = (int) (h * (now - startTime) / 1000) / 2;
				g.fillArc(w / 2 - arcSize, h / 2 - arcSize, arcSize * 2, arcSize * 2, 0, 360);
				flushGraphics();
				if (now - startTime > 1000) {
					Nmania.exit();
					return;
				}
			}
		}
	}
}
