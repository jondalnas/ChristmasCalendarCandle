package com.Jonas.CCC.candle;

import com.Jonas.CCC.Main;
import com.Jonas.CCC.candle.Wax.State;
import com.Jonas.CCC.screen.Bitmap;
import com.Jonas.CCC.screen.Renderer;
import com.Jonas.CCC.screen.Screen;

public class Candle {
	public static final double LUMINOSITY = 12.57; //W/m^3
	public static int PADDING; //px
	public static int FLAME_DISTANCE_TO_CANDLE = 8; //px
	
	private static int LIGHT_X, LIGHT_Y;
	
	private Wax[] wax;
	private int candleWidth, candleHeight;
	private int width, height;
	private int xOffs, yOffs;
	
	public Candle(int width, int height) {
		PADDING = (Screen.WIDTH - width) / 2;
		this.candleWidth = width;
		this.candleHeight = height;
		this.width = width + PADDING * 2;
		this.height = height;
		wax = new Wax[this.width * height];

		LIGHT_X = Screen.WIDTH / 2;
		LIGHT_Y = Screen.HEIGHT - this.height - 10;

		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				wax[x + y * this.width] = new Wax(this, x, y);
			}
		}

		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				Wax u, d, l, r;
				u = d = l = r = null;

				if (y < this.height - 1) u = wax[x + (y + 1) * this.width];
				if (y > 0) 				 d = wax[x + (y - 1) * this.width];
				if (x < this.width - 1)  l = wax[(x + 1) + y * this.width];
				if (x > 0) 				 r = wax[(x - 1) + y * this.width];
				
				wax[x + y * this.width].setUDLR(u, d, l, r);
			}
		}
		
		for (int y = 0; y < this.height; y++) {
			for (int dx = 0; dx < PADDING; dx++) {
				wax[dx + y * this.width].remove();
				wax[(this.width-dx-1) + y * this.width].remove();
			}
		}
		
		//for (int x = 0; x < width; x++) wax[x].setTemperature(370);

		xOffs = Screen.WIDTH/2-(this.width)/2;
		yOffs = Screen.HEIGHT-height;
		
		wax[LIGHT_X - xOffs].hasLight = true;
	}
	
	public void update() {
		if (Renderer.mx >= xOffs && Renderer.mx < width && Renderer.my >= yOffs && Renderer.my < height + yOffs) System.out.println(wax[Renderer.mx - xOffs/* + (Renderer.my - yOffs) * width*/].toString());
		
		for (Wax w : wax) {
			if (w.getState() == Wax.State.GAS) continue; 
			
			double xx = w.x + xOffs - LIGHT_X;
			double yy = w.y + yOffs - LIGHT_Y;
			
			//If wax is part of shell, then add energy
			if (w.isShell() && calculateWaxIntersection(w.x, w.y-1, LIGHT_X - xOffs, LIGHT_Y - yOffs) == 0) 
				w.addEnergy(0.00000000025 / ((xx * xx + yy * yy) * Wax.PIXEL_LENGTH * Wax.PIXEL_LENGTH) * Main.getDeltaTime());
			
			w.update();
			
			if (w.hasLight) LIGHT_Y = w.y + yOffs - FLAME_DISTANCE_TO_CANDLE;
		}
	}
	
	public boolean moveWax(Wax wax, int dx) {
		int x = wax.x;
		int y = wax.y;
		
		if (y == height - 1) {
			this.wax[x + y * width].remove();
			return false;
		}
		
		if (this.wax[x + (y + 1) * width].getState() == Wax.State.GAS) {
			this.wax[x + (y + 1) * width].cloneProperties(wax);
			this.wax[x + y * width].remove();
			
			return true;
		}
		
		if (this.wax[x + dx + y * width].getState() != Wax.State.GAS) return false;
		
		if (this.wax[x + dx + (y + 1) * width].getState() == Wax.State.GAS) {
			this.wax[x + dx + (y + 1) * width].cloneProperties(wax);
			this.wax[x + y * width].remove();
			
			return true;
		}
		
		this.wax[x + dx + y * width].cloneProperties(wax);
		this.wax[x + y * width].remove();
		
		return true;
	}
	
	public void render(Bitmap screen) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double temp = wax[x + y * width].getTemperature();
				temp /= 370.0;

				if (temp > 1) temp = 1;
				if (temp < 0) temp = 0;

				//screen.pixels[(x + xOffs) + (y + yOffs) * Screen.WIDTH] = ((int) ((temp) * 0xff) << 16) | (int) ((1.0 - temp) * 0xff);
				if (wax[x + y * width].getState() != Wax.State.GAS) screen.pixels[(x + xOffs) + (y + yOffs) * Screen.WIDTH] = ((int) (temp * 0xff) << 16) | (int) ((1.0 - temp) * 0xff);
			}
		}
		
		screen.pixels[LIGHT_X + LIGHT_Y * width] = 0xff0000;
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
	
	public boolean isGas(int x, int y) {
		return wax[x + y * width].getState() == Wax.State.GAS;
	}
}
