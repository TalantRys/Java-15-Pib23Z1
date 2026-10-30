package academy.tochkavhoda.competition.dto.response;

import java.util.*;

public class LoginDtoResponse {
    private String token;

    public LoginDtoResponse() {
    }

    public LoginDtoResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}
