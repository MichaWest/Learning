package ru.itmo.michawest.learning.collection;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ru.itmo.michawest.learning.data.Color;
import ru.itmo.michawest.learning.data.Country;
import ru.itmo.michawest.learning.data.Person;
import ru.itmo.michawest.learning.exception.ParameterException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PersonCollection implements Serializable {
    private Vector<Person> collection;
    private final LocalTime createTime;
    private HashSet<Integer> existId;
    private boolean order;

    private PersonCollection(Vector<Person> col){
        createTime = LocalTime.now();
        this.collection = col;
    }

    public PersonCollection(){
        createTime = LocalTime.now();
        order = true;
        if(collection == null){
            collection = new Vector<>();
        }
    }

    public String getInfo() {
        return "Vector объектов Person, количество элементов: " + collection.size() + ", время создания: " ;
    }

    public boolean deserializeFile(String xml){
        ArrayList<String[]> col;
        try{
            if(xml ==null || xml.isEmpty()){
                collection = new Vector<>();
            }else{
                col = XML.readFile(xml);
                for(String[] parameter: col){
                    Person man = new Person();
                    Person.ReadParameter getParameter = man.new ReadParameter();
                    if(!man.addName(parameter[0])) throw new ParameterException("Введена пустая строка");
                    if(!man.addId(getParameter.convertToId(parameter[1]))) throw new ParameterException("Указана пустая строка");
                    if(!man.addCreationDate(getParameter.convertToCreationTime(parameter[3])));
                    if(!man.addHeight(getParameter.convertToHeight(parameter[4]))) throw new ParameterException("Рост не может быть отрицательным");
                    if(!man.addWeight(getParameter.convertToWeight(parameter[5]))) throw new ParameterException("Вес не может быть отрицательным");
                    if(!man.addColor(getParameter.convertToColor(parameter[6]))) throw new ParameterException("Попробуйте еще раз");
                    if(!man.addNationality(getParameter.convertToCountry(parameter[7]))) throw new ParameterException("Попробуйте еще раз");
                    if(!man.addCoordinates(getParameter.convertToCX(parameter[9]), getParameter.convertToCY(parameter[10])))throw new ParameterException("Координаты должны быть больше -199");
                    if(!man.addLocation(getParameter.convertToLX(parameter[11]), getParameter.convertToLY(parameter[12]), getParameter.convertToLZ(parameter[13]))) throw new ParameterException("Возникла ошибка. Попробуйте еще раз");
                    //add(man, man.getId());
                }
            }
        }catch (ParameterException e){
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public void clear(){
        collection.clear();
    }

    public Vector<Person> getCollection() {
        return collection;
    }

    public String serializeCollection(){
        String text = "";
        for (Person man : collection) {
            text = text + XML.strToXml(man);
        }
        return text;
    }
    public boolean checkId(int i){
        return existId.stream().anyMatch(w -> w==i);
    }

    public int countByHairColor(Color color){
        return (int)collection.stream().filter(P -> P.getHairColor()==color).count();
    }

    public PersonCollection groupByNationality(Country nation){
        return new PersonCollection((Vector<Person>) collection.stream().filter(P -> P.getNationality()==nation).collect(Collectors.toList()));
    }

    public Person minByWeight(){
        return collection.stream().min(Person::compare).get();
    }

    public void removeById(int id){
        collection = (Vector<Person>)collection.stream().filter(w -> w.getId()!=id).collect(Collectors.toList());
    }

    public void remove(int n){
        collection.remove(n);
    }

    public void reorder(){
        order = false;
        collection = (Vector<Person>) collection.stream().sorted(new PersonComparator(order)).collect(Collectors.toList());
    }

    public void updateById(int ni, Person np){
        ((collection.stream().filter(p -> p.getId()==ni).findFirst().get())).update(np);
    }

    public void add(Person np){
        PersonComparator comparator = new PersonComparator(order);
        try {
            collection.add(collection.indexOf(collection.stream().filter(p -> comparator.compare(p, np) < 0).findFirst().get()), np);
        }catch(NoSuchElementException e){
            collection.add(np);
        }
    }

    private static class XML{
        public static ArrayList<String[]> readFile(String s){
            ArrayList<String[]> returnArray = new ArrayList<>();
            try{
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(s)));
                String[] tags = new String[]{"name", "id", "coordinates", "creationDate", "height", "weight", "hairColor", "nationality", "location"};
                int counter = document.getDocumentElement().getElementsByTagName("Person").getLength();
                for(int k = 0; k<counter; k++){
                    String[] values = new String[14];
                    for(int i=0; i<tags.length; i++){
                        try{
                            if((i!=2)&&(i!=8)){
                                NodeList personElements = document.getDocumentElement().getElementsByTagName(tags[i]);
                                String value = personElements.item(k).getFirstChild().toString();
                                values[i] = value.substring(8, value.length()-1);
                            }else{
                                if(i==2){
                                    for(int nc = 0; nc < 2; nc++){
                                        String[] tagsCoordinates = new String[]{"x", "y"};
                                        NodeList personElements = document.getDocumentElement().getElementsByTagName(tagsCoordinates[nc]);
                                        String value = personElements.item(k).getFirstChild().toString();
                                        values[9+nc] = value.substring(8, value.length()-1);
                                    }
                                }
                                if(i==8){
                                    for(int nl =0; nl<3; nl++){
                                        String[] tagsLocation = new String[]{"lx", "ly", "lz"};
                                        NodeList personElements = document.getDocumentElement().getElementsByTagName(tagsLocation[nl]);
                                        String value = personElements.item(k).getFirstChild().toString();
                                        values[11+nl] = value.substring(8, value.length()-1);
                                    }
                                }
                            }
                        }catch(NullPointerException e){
                            values[i] = "null";
                        }
                    }
                    returnArray.add(values);
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                System.out.println("Ошибка при дессериализации");
            }
            return returnArray;
        }

        public static String strToXml(Person man){
            return "  <Person>\n" +
                    "       <name>" + man.getName() + "</name>\n" +
                    "       <id>" + man.getId() + "</id>\n" +
                    "       <coordinates>\n" +
                    "           <x>" + man.getCoordinates().getX() + "</x>\n" +
                    "           <y>" + man.getCoordinates().getY() + "</y>\n" +
                    "       </coordinates>\n" +
                    "       <creationDate>" + DataConverter.dateToString(man.getCreationDate()) + "</creationDate>\n" +
                    "       <height>" + man.getHeight() + "</height>\n" +
                    "       <weight>" + man.getWeight() + "</weight>\n" +
                    "       <hairColor>" + man.getHairColor() + "</hairColor>\n" +
                    "       <nationality>" + man.getNationality() + "</nationality>\n" +
                    "       <location>\n" +
                    "           <lx>" + man.getLocation().getX() + "</lx>\n" +
                    "           <ly>" + man.getLocation().getY() + "</ly>\n" +
                    "           <lz>" + man.getLocation().getZ() + "</lz>\n" +
                    "       </location>\n" +
                    "   </Person>\n";
        }

    }
}
