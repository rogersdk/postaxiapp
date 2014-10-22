package com.taxiapp.centraldetaxi;

public interface Gps {
	public double getLatitude();
	public double getLongitude();
	public double calculaDistancia(Gps gps);
	
}
