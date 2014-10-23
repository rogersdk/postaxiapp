package com.taxiapp.centraldetaxi;

import java.util.Date;

public class MockDataHora implements DataHoraSistema{
	private Date date;
	
	public MockDataHora(Date date){
		this.date = date;
	}
	
	public void atrasarSegundos(long segundos){
		segundos = segundos * 1000;
		this.date = new Date(date.getTime() - segundos);
	}
	
	public void adiantarSegundos(long segundos){
		segundos = segundos * 1000;
		this.date = new Date(date.getTime() + segundos);
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
}
