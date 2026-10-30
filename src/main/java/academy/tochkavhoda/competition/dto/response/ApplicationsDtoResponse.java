package academy.tochkavhoda.competition.dto.response;

import java.util.*;

public class ApplicationsDtoResponse {
    private List<ApplicationDtoResponse> applications;

    public ApplicationsDtoResponse() {
    }

    public ApplicationsDtoResponse(List<ApplicationDtoResponse> applications) {
        this.applications = applications;
    }

    public List<ApplicationDtoResponse> getApplications() {
        return applications;
    }

}
