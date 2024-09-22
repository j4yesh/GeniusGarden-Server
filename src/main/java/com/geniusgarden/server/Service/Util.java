package com.geniusgarden.server.Service;

import com.geniusgarden.server.Model.vector3;

public class Util {
    public static float calculateDistance(float x1, float y1, float x2, float y2) {
        float xDiff = x2 - x1;
        float yDiff = y2 - y1;
        return (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }
    public static float calculateDistance(vector3 a, vector3 b){
        return calculateDistance(a.getX(),a.getY(),b.getX(),b.getY());
    }
}
