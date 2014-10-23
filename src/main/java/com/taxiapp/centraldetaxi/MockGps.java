package com.taxiapp.centraldetaxi;

public class MockGps implements Gps {
	private double latitude;
	private double longitude;

	public MockGps(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	
	public double calculaDistancia(Gps gps) {
		return getDistanciaEntreDoisPontos(latitude, gps.getLatitude(), longitude, gps.getLongitude());
	}
	
	public double getDistanciaEntreDoisPontos(double xB, double xA, double yB, double yA){
		return Math.sqrt( Math.pow((xB-xA), 2) + Math.pow(yB-yA, 2) );
	}

	@Override
	public String toString() {
		return "MockGps [latitude=" + latitude + ", longitude=" + longitude
				+ "]";
	}

	

}
