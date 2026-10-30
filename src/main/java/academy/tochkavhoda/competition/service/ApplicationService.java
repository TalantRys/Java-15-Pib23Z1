package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.*;
import academy.tochkavhoda.competition.dto.request.*;
import academy.tochkavhoda.competition.dto.response.*;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.*;
import academy.tochkavhoda.competition.server.ServerResponse;

import java.util.*;

public class ApplicationService extends BaseService {
    private final ApplicationDao applicationDao;
    private final RatingDao ratingDao;
    private final CompetitionDao competitionDao;

    public ApplicationService(UserDao userDao, ApplicationDao applicationDao, RatingDao ratingDao,
                              CompetitionDao competitionDao) {
        super(userDao);
        this.applicationDao = applicationDao;
        this.ratingDao = ratingDao;
        this.competitionDao = competitionDao;
    }

    public ServerResponse addApplication(String token, String requestJsonString) {
        try {
            Participant participant = requireParticipant(token);
            requireNotFinished();
            Application application = toModel(participant, parse(requestJsonString, AddApplicationDtoRequest.class));
            return ok(new AddApplicationDtoResponse(applicationDao.insert(application)));
        } catch (ServerException e) {
            return fail(e);
        }
    }

    /** Adds several applications at once; if any is invalid, none is added. */
    public ServerResponse addApplications(String token, String requestJsonString) {
        try {
            Participant participant = requireParticipant(token);
            requireNotFinished();
            AddApplicationsDtoRequest dto = parse(requestJsonString, AddApplicationsDtoRequest.class);
            if (dto.getApplications() == null || dto.getApplications().isEmpty()) {
                throw new ServerException(ServerErrorCode.WRONG_JSON);
            }
            List<Application> models = new ArrayList<>();
            for (AddApplicationDtoRequest item : dto.getApplications()) {
                if (item == null) {
                    throw new ServerException(ServerErrorCode.WRONG_JSON);
                }
                models.add(toModel(participant, item));
            }
            List<Integer> ids = new ArrayList<>();
            for (Application a : models) {
                ids.add(applicationDao.insert(a));
            }
            return ok(new AddApplicationsDtoResponse(ids));
        } catch (ServerException e) {
            return fail(e);
        }
    }

    public ServerResponse cancelApplication(String token, String requestJsonString) {
        try {
            Participant participant = requireParticipant(token);
            requireNotFinished();
            CancelApplicationDtoRequest dto = parse(requestJsonString, CancelApplicationDtoRequest.class);
            Application application = applicationDao.get(dto.getApplicationId());
            if (application == null) {
                throw new ServerException(ServerErrorCode.APPLICATION_NOT_FOUND);
            }
            if (application.getParticipant() != participant) {
                throw new ServerException(ServerErrorCode.NOT_APPLICATION_OWNER);
            }
            applicationDao.delete(application);
            return ok(null);
        } catch (ServerException e) {
            return fail(e);
        }
    }

    public ServerResponse getMyApplications(String token) {
        try {
            Participant participant = requireParticipant(token);
            List<ApplicationDtoResponse> result = new ArrayList<>();
            for (Application a : applicationDao.getByParticipant(participant)) {
                result.add(ApplicationMapper.toDto(a, ApplicationMapper.average(ratingDao, a), null));
            }
            return ok(new ApplicationsDtoResponse(result));
        } catch (ServerException e) {
            return fail(e);
        }
    }

    /** Expert: applications in the given directions (all of his directions if none are given). */
    public ServerResponse getApplicationsByDirections(String token, String requestJsonString) {
        try {
            Expert expert = requireExpert(token);
            Set<String> directions = expert.getDirections();
            if (requestJsonString != null && !requestJsonString.trim().isEmpty()) {
                GetApplicationsDtoRequest dto = parse(requestJsonString, GetApplicationsDtoRequest.class);
                if (dto.getDirections() != null && !dto.getDirections().isEmpty()) {
                    directions = normalizeDirections(dto.getDirections());
                    for (String d : directions) {
                        if (!expert.getDirections().contains(d)) {
                            throw new ServerException(ServerErrorCode.NOT_EXPERT_IN_DIRECTION, d);
                        }
                    }
                }
            }
            List<ApplicationDtoResponse> result = new ArrayList<>();
            for (Application a : applicationDao.getAll()) {
                if (intersects(a.getDirections(), directions)) {
                    result.add(ApplicationMapper.toDto(a, null, ratingDao.get(expert, a)));
                }
            }
            return ok(new ApplicationsDtoResponse(result));
        } catch (ServerException e) {
            return fail(e);
        }
    }

    private void requireNotFinished() throws ServerException {
        if (competitionDao.isFinished()) {
            throw new ServerException(ServerErrorCode.COMPETITION_FINISHED);
        }
    }

    private static Application toModel(Participant participant, AddApplicationDtoRequest dto) throws ServerException {
        if (isBlank(dto.getName())) {
            throw new ServerException(ServerErrorCode.WRONG_APPLICATION_NAME);
        }
        if (isBlank(dto.getDescription())) {
            throw new ServerException(ServerErrorCode.WRONG_APPLICATION_DESCRIPTION);
        }
        Set<String> directions = normalizeDirections(dto.getDirections());
        if (dto.getSum() <= 0) {
            throw new ServerException(ServerErrorCode.WRONG_SUM);
        }
        return new Application(0, participant, dto.getName().trim(), dto.getDescription().trim(),
                directions, dto.getSum());
    }
}
