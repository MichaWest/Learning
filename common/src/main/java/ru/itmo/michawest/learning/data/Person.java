package ru.itmo.michawest.learning.data;

import ru.itmo.michawest.learning.collection.DataConverter;
import ru.itmo.michawest.learning.exception.InvalidDateFormatException;
import ru.itmo.michawest.learning.exception.ParameterException;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Locale;

public class Person implements Serializable {
    private Integer id;
    private String name;
    private Coordinates coordinates;
    private LocalTime creationDate;
    private Double height;
    private Long weight;
    private Color hairColor;
    private Country nationality;
    private Location location;

    public Person(){}

    public Person(String name, Coordinates coordinates, LocalTime createTime, Double height, Long weight, Color hairColor, Country nationality, Location location) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = createTime;
        this.height = height;
        this.weight = weight;
        this.hairColor = hairColor;
        this.nationality = nationality;
        this.location = location;
    }


    public boolean addName(String name){
        if((name!=null)&&(!name.isEmpty())){
            this.name = name;
            return true;
        }else{
            return false;
        }
    }

    public boolean addCreationDate(LocalTime cd){
        if(cd != null){
            this.creationDate = cd;
            return true;
        }else{
            return false;
        }
    }

    public boolean addId(int newId){
        this.id = newId;
        return true;
    }

    public boolean addHeight(double newHeight){
        if(newHeight > 0){
            this.height = newHeight;
            return true;
        }else{
            return false;
        }
    }

    public boolean addWeight(long newWeight){
        if(newWeight > 0){
            this.weight = newWeight;
            return true;
        } else{
            return false;
        }
    }

    public boolean addColor(Color newColor){
        Color color = newColor;
        this.hairColor = color;
        return true;
    }

    public boolean addNationality(Country newNationality){
        this.nationality = newNationality;
        return true;
    }

    public boolean addCoordinates(float newX, float newY){
        coordinates = new Coordinates();
        return coordinates.addCoordinates(newX, newY);
    }

    public boolean addCoordinates(Coordinates cor){
        this.coordinates = cor;
        return true;
    }

    public boolean addLocation(double newX, long newY, double newZ){
        location = new Location();
        return location.addLocation(newX, newY, newZ);
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public Coordinates getCoordinates(){
        return this.coordinates;
    }

    public LocalTime getCreationDate(){
        return  this.creationDate;
    }

    public double getHeight(){
        return this.height;
    }

    public long getWeight(){
        return this.weight;
    }

    public Color getHairColor(){
        return this.hairColor;
    }

    public Country getNationality(){
        return this.nationality;
    }

    public Location getLocation(){
        return this.location;
    }

    public int compare(Person person) {
        return (int)(this.weight - person.getWeight());
    }

    public void update(Person np){
        name = np.getName();
        coordinates = np.getCoordinates();
        creationDate = np.getCreationDate();
        height = np.getHeight();
        weight = np.getWeight();
        hairColor = np.getHairColor();
        nationality = np.getNationality();
        location = np.getLocation();
    }

    public class ReadParameter{
        public int convertToId(String s){
            int i=0;
            try{
                i = Integer.parseInt(s);
            }catch(NumberFormatException e){
                System.out.println(e.getMessage());
            }
            return i;
        }

        public java.time.LocalTime convertToCreationTime(String s) throws InvalidDateFormatException {
            return DataConverter.parseLocalDate(s);
        }

        public float convertToCX(String s){
            float x = -200;
            try{
                x = Float.parseFloat(s);
            } catch(NumberFormatException e){
                System.out.println(e.getMessage());
            }
            return x;
        }

        public float convertToCY(String s){
            float y = -200;
            try{
                y = Float.parseFloat(s);
            }catch(NumberFormatException e){
                System.out.println(e.getMessage());
            }
            return y;
        }

        public double convertToHeight(String s){
            float y = -1;
            try{
                height = Double.parseDouble(s);
            }catch(NumberFormatException e){
                System.out.println(e.getMessage());
            }
            return height;
        }

        public long convertToWeight(String s){
            long weight = -1;
            try{
                weight = Long.parseLong(s);
            }catch(NumberFormatException e){
                System.out.println(e.getMessage());
            }
            return weight;
        }

        public double convertToLX(String s){
            double x = 0;
            try{
                x = Double.parseDouble(s);
            }catch(NumberFormatException | NullPointerException e){
                System.out.println(e.getMessage());
            }
            return x;
        }

        public long convertToLY(String s){
            long y = 0;
            try{
                y = Long.parseLong(s);
            }catch(NumberFormatException e){
                System.out.println(e.getMessage());
            }
            return y;
        }

        public double convertToLZ(String s){
            double z = 0;
            try{
                z = Double.parseDouble(s);
            }catch (NumberFormatException | NullPointerException e){
                System.out.println(e.getMessage());
            }
            return z;
        }

        public Color convertToColor(String s) throws ParameterException {
            if(s==null || s.isEmpty()) throw new ParameterException("Была введена пустая строка");
            s = s.toLowerCase().trim();
            switch(s){
                case("red"):
                    return Color.RED;
                case("yellow"):
                    return Color.YELLOW;
                case("brown"):
                    return  Color.BROWN;
                default:
                    throw new ParameterException("Такого цвета нет");
            }
        }

        public Country convertToCountry(String s) throws ParameterException {
            if(s==null || s.isEmpty()) throw new ParameterException("Была введена пустая строка");
            s = s.toLowerCase().trim();
            switch(s){
                case("usa"):
                    return Country.USA;
                case("china"):
                    return Country.CHINA;
                case("vatican"):
                    return Country.VATICAN;
                case("north_korea"):
                    return Country.NORTH_KOREA;
                case("japan"):
                    return Country.JAPAN;
                default:
                    throw new ParameterException("Такой национальности нет");
            }
        }
    }
}
