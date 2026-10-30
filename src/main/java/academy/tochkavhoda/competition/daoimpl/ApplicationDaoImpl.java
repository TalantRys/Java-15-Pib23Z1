package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.ApplicationDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Participant;

import java.util.List;

public class ApplicationDaoImpl implements ApplicationDao {
    private final Database database = Database.getInstance();

    @Override
    public int insert(Application application) {
        return database.insertApplication(application);
    }

    @Override
    public Application get(int id) {
        return database.getApplication(id);
    }

    @Override
    public void delete(Application application) {
        database.deleteApplication(application);
    }

    @Override
    public List<Application> getByParticipant(Participant participant) {
        return database.getApplicationsByParticipant(participant);
    }

    @Override
    public List<Application> getAll() {
        return database.getAllApplications();
    }
}
