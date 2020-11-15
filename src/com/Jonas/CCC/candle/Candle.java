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

		xOffs = Screen.WIDTH/2-width/2;
		yOffs = Screen.HEIGHT-height;
	}
	
	public void render(Bitmap screen) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				screen.pixels[(x + xOffs) + (y + yOffs) * Screen.WIDTH] = (int) ((wax[x + y * width].getTemperature() / 370.0) * 0xff0000) + (int) ((1.0 - wax[x + y * width].getTemperature() / 370.0) * 0x0000ff);
			}
		}
	}
}
