package com.rv.pij2021.VUFRB.model;

import android.graphics.RectF;

public class CustomRectF extends RectF {

    // tamanho do frame de desenho
    public float width, height;
    public double phi0, theta0;

    // (posição das arestas do retângulo)
    public CustomRectF(float left, float top, float right, float bottom, float width, float height) {
        super(left, top, right, bottom);
        this.width = width;
        this.height = height;
    }

    public CustomRectF(float left, float top, float right, float bottom, float width, float height, float phi0, float theta0) {
        super(left, top, right, bottom);
        this.width = width;
        this.height = height;
        this.phi0 = phi0;
        this.theta0 = theta0;
    }

    public CustomRectF(RectF location){
        super(location);
    }

    public CustomRectF(){

    }

    public CustomRectF(float width, float height){
        this.width = width;
        this.height = height;
    }

    public RectF getRelativeValues(){
        RectF rectF = new RectF();

        rectF.left = this.left/width;
        rectF.right = this.right/width;
        rectF.top = this.top/height;
        rectF.bottom = this.bottom/height;

        return rectF;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public double getPhi0() {
        return phi0;
    }

    public void setPhi0(double phi0) {
        this.phi0 = phi0;
    }

    public double getTheta0() {
        return theta0;
    }

    public void setTheta0(double theta0) {
        this.theta0 = theta0;
    }


}
