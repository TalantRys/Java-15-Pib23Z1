package academy.tochkavhoda.competition.daoimpl;

import academy.tochkavhoda.competition.dao.CompetitionDao;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.model.Grant;

import java.util.List;

public class CompetitionDaoImpl implements CompetitionDao {
    private final Database database = Database.getInstance();

    @Override
    public boolean isFinished() {
        return database.isFinished();
    }

    @Override
    public void finish(List<Grant> grants) {
        database.finish(grants);
    }

    @Override
    public List<Grant> getGrants() {
        return database.getGrants();
    }
}
