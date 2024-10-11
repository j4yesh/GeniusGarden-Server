package com.geniusgarden.server.Service;

import com.geniusgarden.server.GameplayModel.vector3;

import java.util.Random;

public class Util {
    public static float calculateDistance(float x1, float y1, float x2, float y2) {
        float xDiff = x2 - x1;
        float yDiff = y2 - y1;
        return (xDiff * xDiff + yDiff * yDiff);
    }
    public static float calculateDistance(vector3 a, vector3 b){
        return calculateDistance(a.getX(),a.getY(),b.getX(),b.getY());
    }
    public static String generateRandomString() {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        int length = 5;  // Length of the random string
        Random random = new Random();
        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }

        return result.toString();
    }
}
