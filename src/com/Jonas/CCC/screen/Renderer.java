package com.Jonas.CCC.screen;

import com.Jonas.CCC.candle.Candle;

public class Renderer extends Screen {
	private static final long serialVersionUID = 1L;
	
	private Candle candle;
	
	public Renderer(Candle candle) {
		super();
		
		this.candle = candle;
	}
	
	public void renderCandle() {
		candle.render(screen);
	}
}
