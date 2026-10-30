package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.*;
import academy.tochkavhoda.competition.dto.response.*;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.*;
import academy.tochkavhoda.competition.server.ServerResponse;

import java.util.*;

public class ResultService extends BaseService {
    private final ApplicationDao applicationDao;
    private final RatingDao ratingDao;
    private final CompetitionDao competitionDao;
    private final long fund;
    private final double threshold;
    private final Random random;

    public ResultService(UserDao userDao, ApplicationDao applicationDao, RatingDao ratingDao,
                         CompetitionDao competitionDao, long fund, double threshold, Random random) {
        super(userDao);
        this.applicationDao = applicationDao;
        this.ratingDao = ratingDao;
        this.competitionDao = competitionDao;
        this.fund = fund;
        this.threshold = threshold;
        this.random = random;
    }

    /** Moment X: stops applications and ratings, computes and stores the list of grants. */
    public ServerResponse finishCompetition(String token) {
        try {
            requireUser(token);
            if (competitionDao.isFinished()) {
                throw new ServerException(ServerErrorCode.COMPETITION_FINISHED);
            }
            competitionDao.finish(distribute());
            return ok(buildResults());
        } catch (ServerException e) {
            return fail(e);
        }
    }

    public ServerResponse getResults(String token) {
        try {
            requireUser(token);
            if (!competitionDao.isFinished()) {
                throw new ServerException(ServerErrorCode.COMPETITION_NOT_FINISHED);
            }
            return ok(buildResults());
        } catch (ServerException e) {
            return fail(e);
        }
    }

    private List<Grant> distribute() {
        List<Grant> candidates = new ArrayList<>();
        for (Application a : applicationDao.getAll()) {
            Double avg = ApplicationMapper.average(ratingDao, a);
            if (avg != null) {
                candidates.add(new Grant(a, avg, 0));
            }
        }
        // shuffle first: the sort is stable, so equal average and sum are ranked randomly
        Collections.shuffle(candidates, random);
        candidates.sort(Comparator.comparingDouble(Grant::getAverageRating).reversed()
                .thenComparingLong(g -> g.getApplication().getSum()));
        List<Grant> grants = new ArrayList<>();
        long remaining = fund;
        for (Grant c : candidates) {
            if (c.getAverageRating() <= threshold) {
                break;
            }
            long sum = c.getApplication().getSum();
            if (sum <= remaining) {
                grants.add(new Grant(c.getApplication(), c.getAverageRating(), sum));
                remaining -= sum;
            }
        }
        return grants;
    }

    private ResultsDtoResponse buildResults() {
        List<GrantDtoResponse> list = new ArrayList<>();
        long remaining = fund;
        for (Grant g : competitionDao.getGrants()) {
            Application a = g.getApplication();
            list.add(new GrantDtoResponse(a.getId(), a.getName(), a.getParticipant().getCompanyName(),
                    g.getAverageRating(), g.getAmount()));
            remaining -= g.getAmount();
        }
        return new ResultsDtoResponse(list, fund, remaining);
    }
}
