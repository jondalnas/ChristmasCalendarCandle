package com.Jonas.CCC.candle;

import com.Jonas.CCC.Main;

public class Wax {
	public static final double DIFFUSIVITY = 0.15e-6;
	public static final double PIXEL_LENGTH = 1/600.0; //m/px
	public static final double PIXEL_VOLUME = PIXEL_LENGTH * PIXEL_LENGTH * PIXEL_LENGTH; //m^3/px^3
	public static final double SPECIFIC_HEAT_CAPACITY = 2.5; //kJ/(kg*K)
	public static final double MELTING_POINT = 55; //C
	public static final double BOILING_POINT = 370; //C
	public static final double LATENT_HEAT = 176; //kJ/kg
	public static final double ACTUAL_MELTING_POINT = MELTING_POINT + LATENT_HEAT / SPECIFIC_HEAT_CAPACITY; //C
	public static final double DENSITY = 900; //kg/m^3
	public static final double PIXEL_MASS = PIXEL_VOLUME * DENSITY; //kg/px^3 / kg
	public static final double SPECIFIC_HEAT_CAPACITY_MASS = SPECIFIC_HEAT_CAPACITY * PIXEL_MASS; //kJ/K
	
	public enum State {
		SOLID, LIQUID, GAS
	}
	
	public int x, y;
	private double temp;
	private Wax u, d, l, r;
	
	private State state = State.SOLID;
	
	public Wax(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void update() {
		changeTemperature(calculateHeatChange() * Main.getDeltaTime());
		
		switch(state) {
		case SOLID:
			if (getTemperature() > MELTING_POINT)
				state = State.LIQUID;
			break;
			
		case LIQUID:
			if (getTemperature() < MELTING_POINT)
				state = State.SOLID;
			
			if (getTemperature() > BOILING_POINT) {
				state = State.GAS;

				if (l != null) l.removeR();
				if (r != null) r.removeL();
				if (u != null) u.removeD();
				if (d != null) d.removeU();
			}
			break;
		case GAS:
			break;
		}
	}
	
	private void changeTemperature(double temperature) { //C
		temp += temperature;
	}
	
	public void addEnergy(double energy) { //kJ
		temp += energy / SPECIFIC_HEAT_CAPACITY_MASS;
	}
	
	private double calculateHeatChange() {
		double lChange = 0;
		double rChange = 0;

		//dx/dt
		if (l != null) lChange = (  getTemperature() - l.getTemperature()) / PIXEL_LENGTH;
		if (r != null) rChange = (r.getTemperature() -   getTemperature()) / PIXEL_LENGTH;

		double uChange = 0;
		double dChange = 0;

		//dy/dt
		if (u != null) uChange = (  getTemperature() - u.getTemperature()) / PIXEL_LENGTH;
		if (d != null) dChange = (d.getTemperature() -   getTemperature()) / PIXEL_LENGTH;

		//a*(dx^2/d^2t+dy^2/d^2t+dz^2/d^2t)
		//x is an estimate of z
		return DIFFUSIVITY * (2 * (rChange - lChange) + (dChange - uChange));
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

	public boolean isShell() {
		return l == null || r == null || u == null || d == null;
	}
	
	public State getState() {
		return state;
	}
	
	public void removeL() {
		l = null;
	}
	
	public void removeR() {
		r = null;
	}
	
	public void removeU() {
		u = null;
	}
	
	public void removeD() {
		d = null;
	}
}
