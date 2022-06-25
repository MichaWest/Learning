package ru.itmo.michawest.learning.Input;

import ru.itmo.michawest.learning.commands.CommandWrapper;
import ru.itmo.michawest.learning.commands.DataConverter;
import ru.itmo.michawest.learning.data.*;
import ru.itmo.michawest.learning.exception.*;

import java.util.Scanner;

public class InputAll {
    private Scanner scanner;

    public InputAll(Scanner sc) {
        this.scanner = sc;
        scanner.useDelimiter("\n");
    }

    public void setScanner(Scanner news) {
        scanner = news;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public CommandWrapper readCommand() {
        String command = scannerNextLine();
        if (command.contains(" ")) {
            String[] arr = command.split(" ", 2);
            command = arr[0];
            String arg = arr[1];
            return new CommandWrapper(command, arg);
        } else {
            return new CommandWrapper(command);
        }
    }

    public Person readPerson() throws ParameterException {
        String name = readName();
        Coordinates coordinates = readCoordinates();
        java.time.LocalTime createTime = readCreateTime();
        double height = readHeight();
        long weight = readWeight();
        Color hairColor = readHairColor();
        Country nationality = readNationality();
        Location location = readLocation();
        return new Person(name, coordinates, createTime, height, weight, hairColor, nationality, location);
    }

    public String readName() throws EmptyStringException {
        String s = scannerNextLine();
        if (s.isEmpty()) {
            throw new EmptyStringException();
        }
        return s;
    }

    public Coordinates readCoordinates() throws InvalidNumberException {
        float x = readXCoordinate();
        float y = readYCoordinate();
        Coordinates c = new Coordinates();
        c.addCoordinates(x, y);
        return c;
    }

    public float readXCoordinate() throws InvalidNumberException {
        String s = scannerNextLine();
        float x;
        try {
            x = Float.parseFloat(s);
        } catch (NumberFormatException e) {
            throw new InvalidNumberException();
        }
        if (x > -119) {
            return x;
        } else {
            throw new InvalidNumberException("x должна быть больше -119");
        }
    }

    public float readYCoordinate() throws InvalidNumberException {
        String s = scannerNextLine();
        float y;
        try {
            y = Float.parseFloat(s);
        } catch (NumberFormatException e) {
            throw new InvalidNumberException();
        }
        if (y > -119) {
            return y;
        } else {
            throw new InvalidNumberException("y должна быть больше -119");
        }
    }

    public java.time.LocalTime readCreateTime() throws InvalidDateFormatException {
        String s = scannerNextLine();
        if (s.equals("")) {
            return null;
        } else {
            return DataConverter.parseLocalDate(s);
        }
    }

    public double readHeight() throws InvalidNumberException {
        double height;
        try {
            height = Double.parseDouble(scannerNextLine());
        } catch (NumberFormatException e) {
            throw new InvalidNumberException();
        }
        if (height > 0) {
            return height;
        } else {
            throw new InvalidNumberException("Рост должен быть больше 0");
        }
    }

    public long readWeight() throws InvalidNumberException {
        long weight;
        try {
            weight = Long.parseLong(scannerNextLine());
        } catch (NumberFormatException e) {
            throw new InvalidNumberException();
        }
        if (weight > 0) {
            return weight;
        } else {
            throw new InvalidNumberException("Вес должен быть больше 0");
        }
    }

    public Color readHairColor() throws InvalidEnumException {
        try {
            return Color.valueOf(scannerNextLine());
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumException();
        }
    }

    public Country readNationality() throws InvalidEnumException {
        try {
            return Country.valueOf(scannerNextLine());
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumException();
        }
    }

    public Location readLocation() throws InvalidNumberException {
        double x = readXLocation();
        long y = readYLocation();
        double z = readZLocation();
        Location location = new Location();
        location.addLocation(x, y, z);
        return location;
    }

    public double readXLocation() throws InvalidNumberException {
        try {
            return Double.parseDouble(scannerNextLine());
        } catch (NumberFormatException e) {
            throw new InvalidNumberException();
        }
    }

    public long readYLocation() throws InvalidNumberException {
        try {
            return Long.parseLong(scannerNextLine());
        } catch (NumberFormatException e) {
            throw new InvalidNumberException();
        }
    }

    public double readZLocation() throws InvalidNumberException {
        try {
            return Long.parseLong(scannerNextLine());
        } catch (NumberFormatException e) {
            throw new InvalidNumberException();
        }
    }

    protected String scannerNextLine() {
        return scanner.nextLine().trim();
    }
}
