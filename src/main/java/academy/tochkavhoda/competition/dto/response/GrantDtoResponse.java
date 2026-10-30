package academy.tochkavhoda.competition.dto.response;

import java.util.*;

public class GrantDtoResponse {
    private int applicationId;
    private String name;
    private String companyName;
    private double averageRating;
    private long amount;

    public GrantDtoResponse() {
    }

    public GrantDtoResponse(int applicationId, String name, String companyName, double averageRating, long amount) {
        this.applicationId = applicationId;
        this.name = name;
        this.companyName = companyName;
        this.averageRating = averageRating;
        this.amount = amount;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public String getName() {
        return name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public long getAmount() {
        return amount;
    }

}
