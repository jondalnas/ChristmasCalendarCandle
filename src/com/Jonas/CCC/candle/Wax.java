package com.Jonas.CCC.candle;

import java.io.Serializable;

import com.Jonas.CCC.Main;
import com.Jonas.CCC.screen.Screen;

public class Wax implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final double DIFFUSIVITY = 0.15e-6;
	public static final double PIXEL_LENGTH = 1/(1500.0 / Screen.SCALE); //m/px
	public static final double PIXEL_VOLUME = PIXEL_LENGTH * PIXEL_LENGTH * PIXEL_LENGTH; //m^3/px^3
	public static final double SPECIFIC_HEAT_CAPACITY = 2.5; //kJ/(kg*K)
	public static final double MELTING_POINT = 55; //C
	public static final double BOILING_POINT = 370; //C
	public static final double LATENT_HEAT = 176; //kJ/kg
	public static final double ACTUAL_MELTING_POINT = MELTING_POINT + LATENT_HEAT / SPECIFIC_HEAT_CAPACITY; //C
	public static final double DENSITY = 900; //kg/m^3
	public static final double PIXEL_MASS = PIXEL_VOLUME * DENSITY; //kg/px^3 / kg
	public static final double SPECIFIC_HEAT_CAPACITY_MASS = SPECIFIC_HEAT_CAPACITY * PIXEL_MASS; //kJ/K
	public static final double LIQUID_MOVE_TIME = 1.0/(1.0 * Screen.SCALE); //s/px
	
	public enum State {
		SOLID, LIQUID, GAS
	}
	
	public final int x, y;
	protected double temp = 27;
	private transient Wax u, d, l, r;
	private Candle parent;
	protected int dir;
	
	protected State state = State.SOLID;
	
	public boolean hasLight;
	
	public int color = 0xffffff;
	
	protected double time;
	
	public Wax(Candle candle, int x, int y) {
		this.x = x;
		this.y = y;
		parent = candle;
	}
	
	public void update() {
		changeTemperature(calculateHeatChange() * Main.getDeltaTime());
		
		//If not connected to anything, then fall down
		if ((l == null || l.state != State.SOLID) && (r == null || r.state != State.SOLID) && (u == null || u.state != State.SOLID) && (d == null || d.state != State.SOLID)) {
			parent.moveWax(this, 0);
		}
		
		switch(state) {
		case SOLID:
			if (getTemperature() > MELTING_POINT) {
				state = State.LIQUID;
				if (hasLight) {
					hasLight = false;
					u.hasLight = true;
				}
			}
			break;
			
		case LIQUID:
			if (getTemperature() < MELTING_POINT)
				state = State.SOLID;
			else if (getTemperature() > BOILING_POINT) {
				remove();
			} else {
				time += Main.getDeltaTime();
				
				if (time > LIQUID_MOVE_TIME) {
					time -= LIQUID_MOVE_TIME;
					
					if (dir == 0) {
						for (int dx = 1; dx < Main.CANDLE_WIDTH; dx++) {
							int xm = x - dx;
							int xp = x + dx;
							
							if (!parent.isGas(xm, y) || parent.isGas(xp, y + 1)) {
								dir = 1;
								break;
							} else if (!parent.isGas(xp, y) || parent.isGas(xm, y + 1)) {
								dir = -1;
								break;
							}
						}
					} else {
						if (!parent.moveWax(this, dir)) dir = 0;
					}
				}
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
		return l == null || r == null || u == null || d == null || l.state == State.GAS || r.state == State.GAS || u.state == State.GAS || d.state == State.GAS;
	}
	
	public State getState() {
		return state;
	}
	
	public void cloneProperties(Wax wax) {
		temp = wax.temp;
		dir = wax.dir;
		time = wax.time;
		state = wax.state;
	}
	
	public void remove() {
		temp = 0;
		dir = 0;
		time = 0;
		state = State.GAS;
	}
	
	public String toString() {
		return "x=" + x + ", y=" + y + ", temp=" + temp + ", state=" + state.name() + ", le=" + (l == null || l.state == State.GAS)
																					+ ", re=" + (r == null || r.state == State.GAS)
																					+ ", ue=" + (u == null || u.state == State.GAS)
																					+ ", de=" + (d == null || d.state == State.GAS);
	}
}
