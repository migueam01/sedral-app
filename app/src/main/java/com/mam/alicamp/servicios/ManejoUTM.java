package com.mam.alicamp.servicios;

import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

/**
 * Created by Miguel Moscoso on 06/11/2019.
 */

public class ManejoUTM {
    private double coordX;
    private double coordY;
    private double radLongitud;
    private double radLatitud;
    private int huso;
    private double meridianoHuso;
    private double deltaLambda;
    private double a;
    private double xi;
    private double eta;
    private double ni;
    private double zeta;
    private double a1;
    private double a2;
    private double j2;
    private double j4;
    private double j6;
    private double alfa;
    private double beta;
    private double gamma;
    private double bFi;
    private String zona;

    public double getRadLongitud() {
        return radLongitud;
    }

    public void setRadLongitud(double radLongitud) {
        this.radLongitud = radLongitud;
    }

    public double getRadLatitud() {
        return radLatitud;
    }

    public void setRadLatitud(double radLatitud) {
        this.radLatitud = radLatitud;
    }

    public int getHuso() {
        return huso;
    }

    public void setHuso(int huso) {
        this.huso = huso;
    }

    public double getMeridianoHuso() {
        return meridianoHuso;
    }

    public void setMeridianoHuso(double meridianoHuso) {
        this.meridianoHuso = meridianoHuso;
    }

    public double getDeltaLambda() {
        return deltaLambda;
    }

    public void setDeltaLambda(double deltaLambda) {
        this.deltaLambda = deltaLambda;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getXi() {
        return xi;
    }

    public void setXi(double xi) {
        this.xi = xi;
    }

    public double getEta() {
        return eta;
    }

    public void setEta(double eta) {
        this.eta = eta;
    }

    public double getNi() {
        return ni;
    }

    public void setNi(double ni) {
        this.ni = ni;
    }

    public double getZeta() {
        return zeta;
    }

    public void setZeta(double zeta) {
        this.zeta = zeta;
    }

    public double getA1() {
        return a1;
    }

    public void setA1(double a1) {
        this.a1 = a1;
    }

    public double getA2() {
        return a2;
    }

    public void setA2(double a2) {
        this.a2 = a2;
    }

    public double getJ2() {
        return j2;
    }

    public void setJ2(double j2) {
        this.j2 = j2;
    }

    public double getJ4() {
        return j4;
    }

    public void setJ4(double j4) {
        this.j4 = j4;
    }

    public double getJ6() {
        return j6;
    }

    public void setJ6(double j6) {
        this.j6 = j6;
    }

    public double getAlfa() {
        return alfa;
    }

    public void setAlfa(double alfa) {
        this.alfa = alfa;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public double getbFi() {
        return bFi;
    }

    public void setbFi(double bFi) {
        this.bFi = bFi;
    }

    public double getCoordX() {
        return coordX;
    }

    public void setCoordX(double coordX) {
        this.coordX = coordX;
    }

    public double getCoordY() {
        return coordY;
    }

    public void setCoordY(double coordY) {
        this.coordY = coordY;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public void calcularCoordUTM(double longitud, double latitud) {
        double auxX, auxY, auxZ;
        double auxHuso;
        String husoCadena, sufijoZona;
        double SEMIEJE_MAYOR = 6378137;
        double SEMIEJE_MENOR = 6356752.31424518;
        auxX = sqrt(pow(SEMIEJE_MAYOR, 2) - pow(SEMIEJE_MENOR, 2)) / SEMIEJE_MENOR;
        auxY = pow(auxX, 2);
        auxZ = (pow(SEMIEJE_MAYOR, 2)) / SEMIEJE_MENOR;
        radLongitud = longitud * PI / 180;
        radLatitud = latitud * PI / 180;
        auxHuso = (longitud / 6) + 31;
        husoCadena = "" + auxHuso;
        int posicionPunto = husoCadena.indexOf('.');
        huso = Integer.parseInt(husoCadena.substring(0, posicionPunto));
        if (latitud < 0) {
            sufijoZona = "M";
        } else {
            sufijoZona = "N";
        }
        zona = huso + " " + sufijoZona;
        meridianoHuso = 6 * huso - 183;
        deltaLambda = radLongitud - ((meridianoHuso * PI) / 180);
        a = cos(radLatitud) * sin(deltaLambda);
        xi = (0.5) * log((1 + a) / (1 - a));
        eta = atan((tan(radLatitud)) / cos(deltaLambda)) - radLatitud;
        ni = (auxZ / pow(1 + auxY * pow(cos(radLatitud), 2), 0.5)) * 0.9996;
        zeta = (auxY / 2) * pow(xi, 2) * pow(cos(radLatitud), 2);
        a1 = sin(2 * radLatitud);
        a2 = a1 * pow(cos(radLatitud), 2);
        j2 = radLatitud + (a1 / 2);
        j4 = ((3 * j2) + a2) / 4;
        j6 = (5 * j4 + a2 * pow(cos(radLatitud), 2)) / 3;
        alfa = 3 * auxY / 4;
        beta = 5 * pow(alfa, 2) / 3;
        gamma = 35 * pow(alfa, 3) / 27;
        bFi = 0.9996 * auxZ * (radLatitud - (alfa * j2) + (beta * j4) - (gamma * j6));
        coordX = xi * ni * (1 + zeta / 3) + 500000;
        coordY = eta * ni * (1 + zeta) + bFi;
        if (latitud < 0) {
            coordY = coordY + 10000000;
        }
    }
}