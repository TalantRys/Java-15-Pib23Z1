package academy.tochkavhoda.competition.dto.request;

import java.util.*;

public class LoginDtoRequest {
    private String login;
    private String password;

    public LoginDtoRequest() {
    }

    public LoginDtoRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

}
