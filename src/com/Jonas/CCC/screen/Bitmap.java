package com.Jonas.CCC.screen;

public class Bitmap {
	public final int width, height;
	public int[] pixels;
	
	public static double[] ROTATION;
	
	public Bitmap(int width, int height) {
		this.width = width;
		this.height = height;
		
		pixels = new int[width * height];
		
		if (ROTATION == null) {
			ROTATION = new double[91];
			
			for (int i = 0; i <= 90; i++) {
				ROTATION[i] = Math.cos(Math.toRadians(i));
			}
		}
	}
	
	public void draw(Bitmap bitmap, int x0, int y0) {
		for (int y = 0; y < bitmap.height; y++) {
			int yy = y + y0;
			if (yy < 0 || yy >= height) continue;
			
			for (int x = 0; x < bitmap.width; x++) {
				int xx = x + x0;
				if (xx < 0 || xx >= width) continue;
				
				int col = bitmap.pixels[x + y * bitmap.width];
				if (col == 0xff00ff) continue;
				
				pixels[xx + yy * width] = col;
			}
		}
	}
	
	public void draw(Bitmap bitmap, int x0, int y0, int xi, int yi, int wi, int hi) {
		for (int y = 0; y < hi; y++) {
			int yp = y + yi;
			if (yp < 0 || yp >= bitmap.height) continue;

			int yy = y + y0;
			if (yy < 0 || yy >= height) continue;
			
			for (int x = 0; x < wi; x++) {
				int xp = x + xi;
				if (xp < 0 || xp >= bitmap.width) continue;
				
				int col = bitmap.pixels[xp + yp * bitmap.width];
				if (col == 0xff00ff) continue;
				
				int xx = x + x0;
				if (xx < 0 || xx >= width) continue;
				
				pixels[xx + yy * width] = col;
			}
		}
	}
	
	public void draw(Bitmap bitmap, int x0, int y0, int w, int h, int xi, int yi, int wi, int hi) {
		double xScale = (double) wi / w;
		double yScale = (double) hi / h;
		
		for (int y = 0; y < h; y++) {
			int yp = (int) (y * yScale) + yi;
			if (yp < 0 || yp >= bitmap.height) continue;

			int yy = y + y0;
			if (yy < 0 || yy >= height) continue;
			
			for (int x = 0; x < w; x++) {
				int xp = (int) (x * xScale) + xi;
				if (xp < 0 || xp >= bitmap.width) continue;
				
				int col = bitmap.pixels[xp + yp * bitmap.width];
				if (col == 0xff00ff) continue;

				int xx = x + x0;
				if (xx < 0 || xx >= width) continue;
				
				pixels[xx + yy * width] = col;
			}
		}
	}
	
	public void draw(Bitmap bitmap, int x0, int y0, int xi, int yi, int wi, int hi, double rotation) {
		if (Math.abs((int) rotation - rotation) < 0.001) {
			draw(bitmap, x0, y0, xi, yi, wi, hi, (int) rotation);
			return;
		}
		
		rotation = Math.toRadians(rotation);
		
		double cos = Math.cos(rotation);
		double sin = Math.sin(rotation);
		
		for (int y = 0; y < hi; y++) {
			int yp = y + yi;
			if (yp < 0 || yp >= bitmap.height) continue;
			
			for (int x = 0; x < wi; x++) {
				int xp = x + xi;
				if (xp < 0 || xp >= bitmap.width) continue;
				
				int col = bitmap.pixels[xp + yp * bitmap.width];
				if (col == 0xff00ff) continue;

				int xx = (int) ((x - wi / 2) * cos - (y - hi / 2) * sin) + x0;
				if (xx < 0 || xx >= width) continue;

				int yy = (int) ((x - wi / 2) * sin + (y - hi / 2) * cos) + y0;
				if (yy < 0 || yy >= height) continue;
				
				pixels[xx + yy * width] = col;
			}
		}
	}
	
	public void draw(Bitmap bitmap, int x0, int y0, int xi, int yi, int wi, int hi, int rotation) {
		rotation = (rotation % 360 + 360) % 360;
		
		double cos;
		double sin;
		
		if (rotation <= 90) {
			cos = ROTATION[rotation];
			sin = ROTATION[90-rotation];
			
		} else if (rotation <= 180) {
			cos = -ROTATION[90-(rotation-90)];
			sin = ROTATION[rotation-90];
			
		} else if (rotation <= 270) {
			cos = -ROTATION[rotation-180];
			sin = -ROTATION[90-(rotation-180)];
			
		} else {
			cos = ROTATION[90-(rotation-270)];
			sin = -ROTATION[rotation-270];
			
		}
		
		for (int y = 0; y < hi; y++) {
			int yp = y + yi;
			if (yp < 0 || yp >= bitmap.height) continue;
			
			for (int x = 0; x < wi; x++) {
				int xp = x + xi;
				if (xp < 0 || xp >= bitmap.width) continue;
				
				int col = bitmap.pixels[xp + yp * bitmap.width];
				if (col == 0xff00ff) continue;

				int xx = (int) ((x - wi / 2) * cos - (y - hi / 2) * sin) + x0;
				if (xx < 0 || xx >= width) continue;

				int yy = (int) ((x - wi / 2) * sin + (y - hi / 2) * cos) + y0;
				if (yy < 0 || yy >= height) continue;
				
				pixels[xx + yy * width] = col;
			}
		}
	}
	
	public void fill(int color, int x0, int y0, int w, int h) {
		for (int yy = 0; yy < h; yy++) {
			int y = y0 + yy;
			if (y < 0 || y >= height) continue; //Could be optimized
			
			for (int xx = 0; xx < w; xx++) {
				int x = x0 + xx;
				if (x < 0 || x >= width) continue; //Could be optimized
				
				pixels[x + y * width] = color;
			}	
		}
	}
	
	public void draw(int color, int x0, int y0, int w, int h) {
		for (int yy = 0; yy < h; yy++) {
			int y = y0 + yy;
			if (y < 0 || y >= height) continue;
			
			for (int xx = 0; xx < w; xx++) {
				int x = x0 + xx;
				if (x < 0 || x >= width) continue;
				
				if (yy != 0 && xx != 0 && yy != h-1 && xx != w-1) continue;
				
				pixels[x + y * width] = color;
			}	
		}
	}
}
