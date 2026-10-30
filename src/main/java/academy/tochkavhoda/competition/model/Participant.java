package academy.tochkavhoda.competition.model;

import java.util.*;

public class Participant extends User {
    private String companyName;

    public Participant() {
    }

    public Participant(String login, String password, String firstName, String lastName, String companyName) {
        super(login, password, firstName, lastName);
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

}
