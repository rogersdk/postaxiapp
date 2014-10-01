/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.taxiapp.centraldetaxi;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author oswaldolinhares
 */
    public class CentralDeTaxi {
    private List<Taxi> taxis = new ArrayList<Taxi>();
    //List usar tipo mais generico caso precise ser trocado;
    public void cadastrarTaxi(Taxi taxi) {
        if(taxis.contains(taxi)){
            throw new TaxiJaCadastradoException();
        }
        taxis.add(taxi);
        
    }

    public void atualizarLocalizacao(Taxi taxi) {
     }

    public List<Taxi> getTaxiProximos(double latitude, double longitude, double raio) {
        List<Taxi> taxisProximos = new ArrayList<Taxi>();
        for (Taxi taxi: taxis){
            if (getDistancia(latitude, longitude, taxi) <= raio) {
                taxisProximos.add(taxi);
            }
        }
        return taxisProximos;
    }

    public double getDistancia(double latitude, double logintude, Taxi taxi) {
        double dx = latitude - taxi.getGps().getLatitude();
        double dy = logintude - taxi.getGps().getLongitude();
        return Math.sqrt(dx * dx + dy * dy);
    }


    
}
