package academy.tochkavhoda.competition.dto.request;

import java.util.*;

public class AddApplicationDtoRequest {
    private String name;
    private String description;
    private List<String> directions;
    private long sum;

    public AddApplicationDtoRequest() {
    }

    public AddApplicationDtoRequest(String name, String description, List<String> directions, long sum) {
        this.name = name;
        this.description = description;
        this.directions = directions;
        this.sum = sum;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getDirections() {
        return directions;
    }

    public long getSum() {
        return sum;
    }

}
