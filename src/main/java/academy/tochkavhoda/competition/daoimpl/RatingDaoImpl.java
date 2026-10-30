package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.RatingDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;

import java.util.List;

public class RatingDaoImpl implements RatingDao {
    private final Database database = Database.getInstance();

    @Override
    public void set(Expert expert, Application application, int rating) {
        database.setRating(expert, application, rating);
    }

    @Override
    public boolean remove(Expert expert, Application application) {
        return database.removeRating(expert, application);
    }

    @Override
    public Integer get(Expert expert, Application application) {
        return database.getRating(expert, application);
    }

    @Override
    public List<Integer> getRatings(Application application) {
        return database.getRatings(application);
    }

    @Override
    public List<Application> getRatedBy(Expert expert) {
        return database.getApplicationsRatedBy(expert);
    }
}
