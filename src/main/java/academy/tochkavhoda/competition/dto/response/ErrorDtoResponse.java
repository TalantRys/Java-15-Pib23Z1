package academy.tochkavhoda.competition.dto.response;

import java.util.*;

public class ErrorDtoResponse {
    private String error;

    public ErrorDtoResponse() {
    }

    public ErrorDtoResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

}
