package com.Jonas.CCC.candle;

import com.Jonas.CCC.screen.Bitmap;
import com.Jonas.CCC.screen.Screen;

public class Candle {
	private Wax[] wax;
	private int width, height;
	private int xOffs, yOffs;
	
	public Candle(int width, int height) {
		this.width = width;
		this.height = height;
		wax = new Wax[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				wax[x + y * width] = new Wax(x, y);
			}
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Wax u, d, l, r;
				u = d = l = r = null;

				if (y < height - 1) u = wax[x + (y + 1) * width];
				if (y > 0) d = wax[x + (y - 1) * width];
				if (x < width - 1) l = wax[(x + 1) + y * width];
				if (x > 0) r = wax[(x - 1) + y * width];
				
				wax[x + y * width].setUDLR(u, d, l, r);
			}
		}
		
		for (int x = 0; x < width; x++) wax[x].setTemperature(370);

		xOffs = Screen.WIDTH/2-width/2;
		yOffs = Screen.HEIGHT-height;
	}
	
	public void update() {
		for (Wax w : wax) {
			w.update();
		}
	}
	
	public void render(Bitmap screen) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double temp = wax[x + y * width].getTemperature();
				temp /= 370.0;

				if (temp > 1) temp = 1;
				if (temp < 0) temp = 0;
				
				screen.pixels[(x + xOffs) + (y + yOffs) * Screen.WIDTH] = ((int) ((temp) * 0xff) << 16) | (int) ((1.0 - temp) * 0xff);
			}
		}
	}
}
