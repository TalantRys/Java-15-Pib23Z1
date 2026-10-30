package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.*;
import academy.tochkavhoda.competition.dto.request.*;
import academy.tochkavhoda.competition.dto.response.*;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.*;
import academy.tochkavhoda.competition.server.ServerResponse;

import java.util.*;

public class RatingService extends BaseService {
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;
    private final ApplicationDao applicationDao;
    private final RatingDao ratingDao;
    private final CompetitionDao competitionDao;

    public RatingService(UserDao userDao, ApplicationDao applicationDao, RatingDao ratingDao,
                         CompetitionDao competitionDao) {
        super(userDao);
        this.applicationDao = applicationDao;
        this.ratingDao = ratingDao;
        this.competitionDao = competitionDao;
    }

    /** Sets the rating, or changes it if the expert has already rated this application. */
    public ServerResponse rateApplication(String token, String requestJsonString) {
        try {
            Expert expert = requireExpert(token);
            requireNotFinished();
            RateApplicationDtoRequest dto = parse(requestJsonString, RateApplicationDtoRequest.class);
            if (dto.getRating() < MIN_RATING || dto.getRating() > MAX_RATING) {
                throw new ServerException(ServerErrorCode.WRONG_RATING);
            }
            Application application = findApplication(dto.getApplicationId());
            if (!intersects(application.getDirections(), expert.getDirections())) {
                throw new ServerException(ServerErrorCode.NOT_EXPERT_IN_DIRECTION);
            }
            ratingDao.set(expert, application, dto.getRating());
            return ok(null);
        } catch (ServerException e) {
            return fail(e);
        }
    }

    public ServerResponse removeRating(String token, String requestJsonString) {
        try {
            Expert expert = requireExpert(token);
            requireNotFinished();
            RemoveRatingDtoRequest dto = parse(requestJsonString, RemoveRatingDtoRequest.class);
            Application application = findApplication(dto.getApplicationId());
            if (!ratingDao.remove(expert, application)) {
                throw new ServerException(ServerErrorCode.RATING_NOT_FOUND);
            }
            return ok(null);
        } catch (ServerException e) {
            return fail(e);
        }
    }

    public ServerResponse getRatedApplications(String token) {
        try {
            Expert expert = requireExpert(token);
            List<ApplicationDtoResponse> result = new ArrayList<>();
            for (Application a : ratingDao.getRatedBy(expert)) {
                result.add(ApplicationMapper.toDto(a, null, ratingDao.get(expert, a)));
            }
            return ok(new ApplicationsDtoResponse(result));
        } catch (ServerException e) {
            return fail(e);
        }
    }

    private Application findApplication(int id) throws ServerException {
        Application application = applicationDao.get(id);
        if (application == null) {
            throw new ServerException(ServerErrorCode.APPLICATION_NOT_FOUND);
        }
        return application;
    }

    private void requireNotFinished() throws ServerException {
        if (competitionDao.isFinished()) {
            throw new ServerException(ServerErrorCode.COMPETITION_FINISHED);
        }
    }
}
