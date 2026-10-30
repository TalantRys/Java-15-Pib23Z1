package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Participant;

import java.util.List;

public interface ApplicationDao {
    int insert(Application application);

    Application get(int id);

    void delete(Application application);

    List<Application> getByParticipant(Participant participant);

    List<Application> getAll();
}
