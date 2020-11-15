package com.Jonas.CCC.candle;

import com.Jonas.CCC.Main;

public class Wax {
	public static final double DIFFUSIVITY = 0.15e-6;
	public static final double PIXEL_LENGTH = 1/600.0;
	
	private int x, y;
	private double temp;
	private Wax u, d, l, r;
	
	public Wax(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void update() {
		temp += calculateHeatChange() * Main.getDeltaTime();
	}
	
	private double calculateHeatChange() {
		double lChange = 0;
		double rChange = 0;

		if (l != null) lChange = (temp - l.getTemperature()) / PIXEL_LENGTH;
		if (r != null) rChange = (r.getTemperature() - temp) / PIXEL_LENGTH;

		double uChange = 0;
		double dChange = 0;

		if (u != null) uChange = (temp - u.getTemperature()) / PIXEL_LENGTH;
		if (d != null) dChange = (d.getTemperature() - temp) / PIXEL_LENGTH;
		
		return DIFFUSIVITY * ((rChange - lChange) + (dChange - uChange));
	}
	
	public void setUDLR(Wax u, Wax d, Wax l, Wax r) {
		this.u = u;
		this.d = d;
		this.l = l;
		this.r = r;
	}
	
	public double getTemperature() {
		return temp;
	}

	public void setTemperature(int temperature) {
		temp = temperature;
	}
}
