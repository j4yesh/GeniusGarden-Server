package com.geniusgarden.server.GameplayModel;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@NoArgsConstructor
@Component
public class ratContainer {

    private Map<String,vector3> rats = new ConcurrentHashMap<>();

    public void disappearRat(String key) {
        rats.remove(key);
    }

    public Map<String,vector3> getRats(){
        return rats;
    }

    public void addRat(String key,vector3 rat){
        rats.put(key,rat);
    }

    public void disappearAllRat(){
        rats.clear();
    }
}
