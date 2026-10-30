package academy.tochkavhoda.competition.dto.request;

import java.util.*;

public class RemoveRatingDtoRequest {
    private int applicationId;

    public RemoveRatingDtoRequest() {
    }

    public RemoveRatingDtoRequest(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getApplicationId() {
        return applicationId;
    }

}
