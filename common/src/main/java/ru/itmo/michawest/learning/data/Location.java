package ru.itmo.michawest.learning.data;

import java.io.Serializable;

public class Location implements Serializable {
    private Double x;
    private Long y;
    private Double z;

    public boolean addLocation(double nx, long ny, double nz){
        this.x = nx;
        this.y = ny;
        this.z = nz;
        return true;
    }

    public double getX(){
        return this.x;
    }

    public long getY(){
        return this.y;
    }

    public double getZ(){
        return  this.z;
    }

}
