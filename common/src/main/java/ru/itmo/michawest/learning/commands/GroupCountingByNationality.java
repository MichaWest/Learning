package ru.itmo.michawest.learning.commands;

import ru.itmo.michawest.learning.collection.PersonCollection;
import ru.itmo.michawest.learning.data.Country;
import ru.itmo.michawest.learning.data.Person;

public class GroupCountingByNationality extends Command{
    protected String nameOfCommand = "group_counting_by_nationality";
    private PersonCollection group;
    private Country nationality;

    public GroupCountingByNationality() {
        super("group_counting_by_nationality");
    }


    @Override
    public void getResult() {
        if(group == null || group.getCollection().isEmpty()){
            System.out.println("Группа пуста");
        } else{
            System.out.println("Группа с национальностью "+nationality+": ");
            for(Person p: group.getCollection()){
                System.out.print(p.getName()+", ");
            }
        }
    }

    public void setGroup(PersonCollection group) {
        this.group = group;
    }

    public void setNationality(Country nation) {
        this.nationality = nation;
    }

    public Country getNationality() {
        return nationality;
    }
}
