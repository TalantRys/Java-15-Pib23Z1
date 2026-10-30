package academy.tochkavhoda.competition.model;

import java.util.*;

public class Application {
    private int id;
    private Participant participant;
    private String name;
    private String description;
    private Set<String> directions;
    private long sum;

    public Application() {
    }

    public Application(int id, Participant participant, String name, String description, Set<String> directions, long sum) {
        this.id = id;
        this.participant = participant;
        this.name = name;
        this.description = description;
        this.directions = directions;
        this.sum = sum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getDirections() {
        return directions;
    }

    public void setDirections(Set<String> directions) {
        this.directions = directions;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

}
