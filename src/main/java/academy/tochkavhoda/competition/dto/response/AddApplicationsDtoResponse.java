package academy.tochkavhoda.competition.dto.response;

import java.util.*;

public class AddApplicationsDtoResponse {
    private List<Integer> ids;

    public AddApplicationsDtoResponse() {
    }

    public AddApplicationsDtoResponse(List<Integer> ids) {
        this.ids = ids;
    }

    public List<Integer> getIds() {
        return ids;
    }

}
