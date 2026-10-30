package academy.tochkavhoda.competition.dto.response;

import java.util.*;

public class ResultsDtoResponse {
    private List<GrantDtoResponse> grants;
    private long fund;
    private long remaining;

    public ResultsDtoResponse() {
    }

    public ResultsDtoResponse(List<GrantDtoResponse> grants, long fund, long remaining) {
        this.grants = grants;
        this.fund = fund;
        this.remaining = remaining;
    }

    public List<GrantDtoResponse> getGrants() {
        return grants;
    }

    public long getFund() {
        return fund;
    }

    public long getRemaining() {
        return remaining;
    }

}
