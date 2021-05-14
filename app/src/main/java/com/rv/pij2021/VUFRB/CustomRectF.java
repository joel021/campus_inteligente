package com.rv.pij2021.VUFRB;

import android.graphics.RectF;

public class CustomRectF extends RectF {

    public float width, height;

    public CustomRectF(float left, float top, float right, float bottom, float width, float height) {
        super(left, top, right, bottom);
        this.width = width;
        this.height = height;
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
}
