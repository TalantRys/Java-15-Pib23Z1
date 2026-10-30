package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.model.Grant;

import java.util.List;

public interface CompetitionDao {
    boolean isFinished();

    void finish(List<Grant> grants);

    List<Grant> getGrants();
}
