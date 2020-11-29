package com.Jonas.CCC.screen;

import java.awt.event.MouseEvent;

import com.Jonas.CCC.candle.Candle;

public class Renderer extends Screen {
	private static final long serialVersionUID = 1L;
	
	private Candle candle;
	
	public static int mx, my;
	
	public Renderer(Candle candle) {
		super();
		
		this.candle = candle;
	}
	
	public void renderCandle() {
		candle.render(screen);
	}

	public void mouseDragged(MouseEvent e) {
		mx = e.getX() / SCALE;
		my = e.getY() / SCALE;
	}

	public void mouseMoved(MouseEvent e) {
		mx = e.getX() / SCALE;
		my = e.getY() / SCALE;
	}
}
