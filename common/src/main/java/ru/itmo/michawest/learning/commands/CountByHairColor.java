package ru.itmo.michawest.learning.commands;

import ru.itmo.michawest.learning.data.Color;

public class CountByHairColor extends Command{
    protected String nameOfCommand = "count_by_hair_color";
    private int count;
    private Color color;

    public CountByHairColor() {
        super("count_by_hair_color");
    }

    @Override
    public void getResult() {
        System.out.println("Count person with color: " + color + " -" + count);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setCount(int i) {
        count = i;
    }

    public Color getColor() {
        return color;
    }
}
