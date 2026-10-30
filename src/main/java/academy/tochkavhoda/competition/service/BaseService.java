package academy.tochkavhoda.competition.service;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.dto.response.ErrorDtoResponse;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;
import academy.tochkavhoda.competition.server.ServerResponse;

import java.util.*;

abstract class BaseService {
    protected static final Gson GSON = new Gson();
    private final UserDao userDao;

    protected BaseService(UserDao userDao) {
        this.userDao = userDao;
    }

    protected static <T> T parse(String json, Class<T> cls) throws ServerException {
        if (json == null || json.trim().isEmpty()) {
            throw new ServerException(ServerErrorCode.WRONG_JSON);
        }
        try {
            T result = GSON.fromJson(json, cls);
            if (result == null) {
                throw new ServerException(ServerErrorCode.WRONG_JSON);
            }
            return result;
        } catch (JsonParseException e) {
            throw new ServerException(ServerErrorCode.WRONG_JSON);
        }
    }

    protected static ServerResponse ok(Object dto) {
        return new ServerResponse(200, dto == null ? "{}" : GSON.toJson(dto));
    }

    protected static ServerResponse fail(ServerException e) {
        return new ServerResponse(400, GSON.toJson(new ErrorDtoResponse(e.getMessage())));
    }

    protected User requireUser(String token) throws ServerException {
        User user = userDao.getByToken(token);
        if (user == null) {
            throw new ServerException(ServerErrorCode.INVALID_TOKEN);
        }
        return user;
    }

    protected Participant requireParticipant(String token) throws ServerException {
        User user = requireUser(token);
        if (!(user instanceof Participant)) {
            throw new ServerException(ServerErrorCode.ACCESS_DENIED);
        }
        return (Participant) user;
    }

    protected Expert requireExpert(String token) throws ServerException {
        User user = requireUser(token);
        if (!(user instanceof Expert)) {
            throw new ServerException(ServerErrorCode.ACCESS_DENIED);
        }
        return (Expert) user;
    }

    protected static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** Directions are compared case-insensitively and without surrounding spaces. */
    protected static Set<String> normalizeDirections(List<String> directions) throws ServerException {
        if (directions == null || directions.isEmpty()) {
            throw new ServerException(ServerErrorCode.WRONG_DIRECTIONS);
        }
        Set<String> result = new TreeSet<>();
        for (String d : directions) {
            if (isBlank(d)) {
                throw new ServerException(ServerErrorCode.WRONG_DIRECTIONS);
            }
            result.add(d.trim().toLowerCase(Locale.ROOT));
        }
        return result;
    }

    protected static boolean intersects(Set<String> a, Set<String> b) {
        for (String s : a) {
            if (b.contains(s)) {
                return true;
            }
        }
        return false;
    }
}
