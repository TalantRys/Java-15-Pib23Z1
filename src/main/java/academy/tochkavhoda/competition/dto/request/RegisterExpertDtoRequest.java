package academy.tochkavhoda.competition.dto.request;

import java.util.*;

public class RegisterExpertDtoRequest {
    private String firstName;
    private String lastName;
    private List<String> directions;
    private String login;
    private String password;

    public RegisterExpertDtoRequest() {
    }

    public RegisterExpertDtoRequest(String firstName, String lastName, List<String> directions, String login, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.directions = directions;
        this.login = login;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<String> getDirections() {
        return directions;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

}
