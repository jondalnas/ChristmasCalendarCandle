package com.Jonas.CCC.candle;

public class Wax {
	private int x, y;
	private double temp;
	private Wax u, d, l, r;
	
	public Wax(int x, int y) {
		this.x = x;
		this.y = y;
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
}
