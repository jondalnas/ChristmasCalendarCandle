package com.Jonas.CCC;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.Jonas.CCC.candle.Candle;
import com.Jonas.CCC.screen.Renderer;

public class Main implements Runnable {
	private static Renderer renderer;
	private Thread thread;
	private static int FRAME_CAP = 60;
	private static double DELTA_TIME;
	private static double SPEED = 1;

	public static final int CANDLE_WIDTH = 120, CANDLE_HEIGHT = 320;
	//public static final int CANDLE_WIDTH = 30, CANDLE_HEIGHT = 80;
	
	private static Candle candle;
	
	public Main() {
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Engine");
		JPanel panel = new JPanel(new BorderLayout());

		candle = Save.load();
		if (candle == null) candle = new Candle(CANDLE_WIDTH, CANDLE_HEIGHT);
		renderer = new Renderer(candle);
		
		panel.add(renderer, 0);
		
		frame.setContentPane(panel);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		new Main().start();
	}
	
	private void start() {
		thread = new Thread(this);
		thread.start();
	}
	
	public void run() {
		long last = System.nanoTime();
		while(true) {
			if ((System.nanoTime() - last) * 1.0e-9 < 1.0 / FRAME_CAP) {
				long delay = (long) ((1.0 / FRAME_CAP) * 1.0e3 - (System.nanoTime() - last) * 1.0e-6);
				if (delay > 0) {
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			DELTA_TIME = (System.nanoTime() - last) * 1.0e-9;
			
			candle.update();
			renderer.render();
			
			last = System.nanoTime();
		}
	}
	
	public static double getDeltaTime() {
		return DELTA_TIME * SPEED;
	}
}
