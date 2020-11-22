package com.Jonas.CCC.candle;

import com.Jonas.CCC.Main;
import com.Jonas.CCC.candle.Wax.State;
import com.Jonas.CCC.screen.Bitmap;
import com.Jonas.CCC.screen.Screen;

public class Candle {
	public static final double LUMINOSITY = 12.57; //W/m^3
	
	private static int LIGHT_X, LIGHT_Y;
	
	private Wax[] wax;
	private int width, height;
	private int xOffs, yOffs;
	
	public Candle(int width, int height) {
		this.width = width;
		this.height = height;
		wax = new Wax[width * height];

		LIGHT_X = Screen.WIDTH / 2;
		LIGHT_Y = Screen.HEIGHT - height - 10;

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
		
		//for (int x = 0; x < width; x++) wax[x].setTemperature(370);

		xOffs = Screen.WIDTH/2-width/2;
		yOffs = Screen.HEIGHT-height;
	}
	
	public void update() {
		for (Wax w : wax) {
			if (w.getState() == Wax.State.GAS) continue; 
			
			double xx = w.x + xOffs - LIGHT_X;
			double yy = w.y + yOffs - LIGHT_Y;
			
			//If wax is part of shell, then add energy
			if (w.isShell() && calculateWaxIntersection(w.x, w.y, LIGHT_X - xOffs, LIGHT_Y - yOffs) == 1) 
				w.addEnergy(0.00000001 / (Math.sqrt(xx * xx + yy * yy) * Wax.PIXEL_LENGTH) * Main.getDeltaTime());
			
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

				//screen.pixels[(x + xOffs) + (y + yOffs) * Screen.WIDTH] = ((int) ((temp) * 0xff) << 16) | (int) ((1.0 - temp) * 0xff);
				screen.pixels[(x + xOffs) + (y + yOffs) * Screen.WIDTH] = ((((int) ((temp) * 0xff) << 16) | (int) ((1.0 - temp) * 0xff))) | (wax[x + y * width].getState() == State.GAS ? 0x00ff00 : 0);
			}
		}
	}
	
	private int calculateWaxIntersection(int sx, int sy, int ex, int ey) {
		int wax = 0;
		
		int deltaX = ex - sx;
		int deltaY = ey - sy;
		
		if (deltaX == 0) {
			int x = sx;
			for (int y = sy; (deltaY > 0 && y < ey) || (deltaY < 0 && y > ey); y += deltaY > 0 ? 1 : -1) {
				if (x < 0 || x >= width || y < 0 || y >= height) break;
				
				if (this.wax[x + y * width].getState() != Wax.State.GAS) wax++;
			}
			
			return wax;
		}
		
		double deltaErr = Math.abs((double) deltaY / deltaX);
		double error = 0;
		int y = sy;
		for (int x = sx; (deltaX > 0 && x < ex) || (deltaX < 0 && x > ex); x += deltaX > 0 ? 1 : -1) {
			if (x < 0 || x >= width || y < 0 || y >= height) break;
			
			if (this.wax[x + y * width].getState() != Wax.State.GAS) wax++;
			
			error += deltaErr;
			if (error > 0.5) {
				y += deltaY < 0 ? -1 : 1;
				error--;
			}
		}
		
		return wax;
	}
}
