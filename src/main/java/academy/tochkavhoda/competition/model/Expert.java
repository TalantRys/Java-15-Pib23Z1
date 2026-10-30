package academy.tochkavhoda.competition.model;

import java.util.*;

public class Expert extends User {
    private Set<String> directions;

    public Expert() {
    }

    public Expert(String login, String password, String firstName, String lastName, Set<String> directions) {
        super(login, password, firstName, lastName);
        this.directions = directions;
    }

    public Set<String> getDirections() {
        return directions;
    }

}
