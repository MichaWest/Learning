package ru.itmo.michawest.learning.Input;

import ru.itmo.michawest.learning.data.*;
import ru.itmo.michawest.learning.exception.*;

import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleInput extends InputAll {
    public ConsoleInput() {
        super(new Scanner(System.in));
        getScanner().useDelimiter("\n");
    }

    @Override
    public Person readPerson() throws ParameterException {
        String name = readName();
        Coordinates coordinates = readCoordinates();
        LocalTime createTime = readCreateTime();
        double height = readHeight();
        long weight = readWeight();
        Color hairColor = readHairColor();
        Country nationality = readNationality();
        Location location = readLocation();
        return new Person(name, coordinates, createTime, height, weight, hairColor, nationality, location);
    }

    public String readName() throws EmptyStringException {
        System.out.print("Введите имя: ");
        return super.readName();
    }

    @Override
    public Coordinates readCoordinates() throws InvalidNumberException {
        float x = readXCoordinate();
        float y = readYCoordinate();
        Coordinates c = new Coordinates();
        c.addCoordinates(x, y);
        return c;
    }

    @Override
    public float readXCoordinate() throws InvalidNumberException {
        System.out.print("Введите координату x(значение должно быть больше -119): ");
        return super.readXCoordinate();
    }

    @Override
    public float readYCoordinate() throws InvalidNumberException {
        System.out.print("Введите координату y(значение должно быть больше -119): ");
        return super.readYCoordinate();
    }

    @Override
    public LocalTime readCreateTime() {
        return LocalTime.now();
    }

    @Override
    public double readHeight() throws InvalidNumberException {
        System.out.print("Введите рост(значение должно быть больше нуля): ");
        return super.readHeight();
    }

    @Override
    public long readWeight() throws InvalidNumberException {
        System.out.print("Введите вес(значение должно быть больше нуля): ");
        return super.readWeight();
    }

    @Override
    public Color readHairColor() throws InvalidEnumException {
        System.out.print("Введите цвет волос(Доступные цвета RED, BROWN, YELLOW): ");
        return super.readHairColor();
    }

    @Override
    public Country readNationality() throws InvalidEnumException {
        System.out.print("Введите национальность(Доступные национальности USA, CHINA, VATICAN, NORTH_KOREA, JAPAN): ");
        return super.readNationality();
    }

    @Override
    public Location readLocation() throws InvalidNumberException {
        double x = readXLocation();
        long y = readYLocation();
        double z = readZLocation();
        Location location = new Location();
        location.addLocation(x, y, z);
        return location;
    }

    @Override
    public double readXLocation() throws InvalidNumberException {
        System.out.print("Введите координату x локации: ");
        return super.readXLocation();
    }

    @Override
    public long readYLocation() throws InvalidNumberException {
        System.out.print("Введите координату y локации: ");
        return super.readYLocation();
    }

    @Override
    public double readZLocation() throws InvalidNumberException {
        System.out.print("Введите координату z локации: ");
        return super.readZLocation();
    }

    @Override
    protected String scannerNextLine() {
        String s;
        try {
            s = getScanner().nextLine().trim();
        } catch (NoSuchElementException e) {
            System.out.println();
            Scanner sc = new Scanner(System.in);
            setScanner(sc);
            s = getScanner().nextLine().trim();
        }
        return s;
    }
}

