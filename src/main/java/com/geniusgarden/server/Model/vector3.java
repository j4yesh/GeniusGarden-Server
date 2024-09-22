package com.geniusgarden.server.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
//@AllArgsConstructor
@NoArgsConstructor

public class vector3 {
    private float x,y;
    private float angle;

    public vector3(float x, float y, float angle) {
        this.x=x;
        this.y=y;
        this.angle=angle;
    }

    public vector3(float x,float y){
        this.x=x;
        this.y=y;
        this.angle=0;
    }

    public void setPosition(float x,float y){
        this.x=x;
        this.y=y;
    }

    public List<Float> getList() {
        return Arrays.asList(this.x, this.y, 0f);
    }
}
