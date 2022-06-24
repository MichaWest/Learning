package ru.itmo.michawest.learning.collection;

import com.sun.media.sound.InvalidDataException;
import ru.itmo.michawest.learning.exception.InvalidDateFormatException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DataConverter {
    static final DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static String dateToString(LocalTime date){
        return date.format(localDateFormatter);
    }

    public static LocalTime parseLocalDate(String s) throws InvalidDateFormatException {
        try{
            return LocalTime.parse(s, localDateFormatter);
        } catch(java.time.format.DateTimeParseException e){
            throw new InvalidDateFormatException();
        }
    }
}
