package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;

import java.util.List;

public interface RatingDao {
    void set(Expert expert, Application application, int rating);

    boolean remove(Expert expert, Application application);

    Integer get(Expert expert, Application application);

    List<Integer> getRatings(Application application);

    List<Application> getRatedBy(Expert expert);
}
