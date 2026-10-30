package academy.tochkavhoda.competition.dto.request;

import java.util.*;

public class CancelApplicationDtoRequest {
    private int applicationId;

    public CancelApplicationDtoRequest() {
    }

    public CancelApplicationDtoRequest(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getApplicationId() {
        return applicationId;
    }

}
