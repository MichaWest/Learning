package ru.itmo.michawest.learning.data;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private float x;
    private float y;

    public boolean addCoordinates(float nx, float ny){
        if((nx > -199) && (ny > -199)){
            this.x = nx;
            this.y = ny;
            return true;
        }else{
            return false;
        }
    }

    public float getX(){
         return this.x;
    }

    public float getY(){
        return this.y;
    }
}
