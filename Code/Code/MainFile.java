package Code;

import javax.swing.JOptionPane;

import Animals.Mammal;

import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

import Level.DefaultLevel;
import graphics.*;
import inputs.*;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.Graphics;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainFile extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;
	public static int WIDTH = 800;
	public static int HEIGHT = 800;
	public static int SCALE = 1;

	public boolean running = false;

	JFrame frame;
	private Thread thread;
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

	public static Display SCREEN;
	public DefaultLevel lev = new DefaultLevel();

	private Keyboard keyboard;
	boolean paused = false;
	private boolean menued;
	public static String title = "Animal House";

	public static MainFile thisMain;

	public static void main(String[] args) {

		JOptionPane pane = new JOptionPane("hello",0,1 );
		// Configure via set methods
		JDialog dialog = pane.createDialog(null, title);
		// the line below is added to the example from the docs
		dialog.setModal(false); // this says not to block background components
		dialog.setVisible(true);
		dialog.setContentPane(pane);
		dialog.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {

			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				System.out.println(pane.getValue());
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		

		// JOptionPane.showMessageDialog(null, "Welcome to Animal House \nMade
		// by Mahan Pandey \n", "Welcome!", 1);
		//
		// MainFile main = new MainFile();
		// main.frame.setResizable(false);
		// main.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// main.frame.add(main);
		// main.frame.pack();
		// main.frame.setLocationRelativeTo(null);
		// main.frame.setTitle(title);
		// main.frame.setVisible(true);
		// main.start();

	}

	public MainFile() {
		// thisMain = this;
		//
		// Dimension size = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
		// setPreferredSize(size);
		// frame = new JFrame();
		// SCREEN = new Display();
		// keyboard = Keyboard.defKeyboard;
		// addKeyListener(keyboard);
		// requestFocus();
		//
		// try {
		// startMenu();
		// } catch (Exception e) {
		// JOptionPane.showMessageDialog(null, "Not an accepted value, program
		// will close", title,
		// JOptionPane.INFORMATION_MESSAGE);
		// System.exit(0);
		// }

	}

	public void startMenu() {

		int num = HowMany("Animals", 20);
		for (int i = 0; i < num; i++)
			lev.AddAnimal(new Mammal(true));
	}

	public void mainMenu() {
		try {
			String options = "0.Close Menu \n1.Add more animals";
			int num = Integer.parseInt(JOptionPane.showInputDialog(frame, "Choose an option:\n" + options, title,
					JOptionPane.INFORMATION_MESSAGE));
			switch (num) {
			case 1:
				startMenu();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Invalid Option. Closing Menu.");
		}
		menued = true;
	}

	public int HowMany(String name, int max) {
		try {
			int num = Integer.parseInt(JOptionPane.showInputDialog(frame,
					"How many " + name + " would you like?" + "\n -1. Randomize \n Or type any integer", title,
					JOptionPane.INFORMATION_MESSAGE));

			if (num == -1) {
				num = (int) (Math.random() * max);
			}
			return num;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

	public void run() {

		long timer = System.currentTimeMillis();
		long lastTime = System.nanoTime();

		final double wantedUpdates = 10000000000.0 / 120.0;
		double delta = 0;
		while (running) {

			render();
			long now = System.nanoTime();
			delta += (now - lastTime) / wantedUpdates;
			lastTime = now;

			while (delta >= 1) {
				update();

				delta--;
			}

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
			}
		}

	}

	public void update() {
		System.out.println(keyboard.up);
		keyboard.update();
		lev.Update();
		if (!menued) {
			if (keyboard.menu)
				mainMenu();
		} else if (!keyboard.menu) {
			menued = false;
		}
	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		SCREEN.clear();
		lev.Render(SCREEN);

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = SCREEN.pixels[i];
		}

		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();

	}

	private void start() {
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}

	public synchronized void stop() {

		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}