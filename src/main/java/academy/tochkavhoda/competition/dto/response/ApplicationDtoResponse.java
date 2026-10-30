package academy.tochkavhoda.competition.dto.response;

import java.util.*;

public class ApplicationDtoResponse {
    private int id;
    private String name;
    private String description;
    private List<String> directions;
    private long sum;
    private String companyName;
    private Double averageRating;
    private Integer myRating;

    public ApplicationDtoResponse() {
    }

    public ApplicationDtoResponse(int id, String name, String description, List<String> directions, long sum, String companyName, Double averageRating, Integer myRating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.directions = directions;
        this.sum = sum;
        this.companyName = companyName;
        this.averageRating = averageRating;
        this.myRating = myRating;
    }

    public int getId() {
        return id;
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

    public String getCompanyName() {
        return companyName;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public Integer getMyRating() {
        return myRating;
    }

}
