package academy.tochkavhoda.competition.dto.request;

import java.util.*;

public class GetApplicationsDtoRequest {
    private List<String> directions;

    public GetApplicationsDtoRequest() {
    }

    public GetApplicationsDtoRequest(List<String> directions) {
        this.directions = directions;
    }

    public List<String> getDirections() {
        return directions;
    }

}
