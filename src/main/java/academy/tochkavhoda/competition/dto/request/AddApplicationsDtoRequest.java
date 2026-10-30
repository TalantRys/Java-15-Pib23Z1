package academy.tochkavhoda.competition.dto.request;

import java.util.*;

public class AddApplicationsDtoRequest {
    private List<AddApplicationDtoRequest> applications;

    public AddApplicationsDtoRequest() {
    }

    public AddApplicationsDtoRequest(List<AddApplicationDtoRequest> applications) {
        this.applications = applications;
    }

    public List<AddApplicationDtoRequest> getApplications() {
        return applications;
    }

}
