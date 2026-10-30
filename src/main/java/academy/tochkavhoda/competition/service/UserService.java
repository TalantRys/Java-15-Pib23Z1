package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.UserDao;
import academy.tochkavhoda.competition.dto.request.*;
import academy.tochkavhoda.competition.dto.response.LoginDtoResponse;
import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.Expert;
import academy.tochkavhoda.competition.model.Participant;
import academy.tochkavhoda.competition.model.User;
import academy.tochkavhoda.competition.server.ServerResponse;

public class UserService extends BaseService {
    private static final int MIN_LOGIN_LENGTH = 5;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        super(userDao);
        this.userDao = userDao;
    }

    public ServerResponse registerParticipant(String requestJsonString) {
        try {
            RegisterParticipantDtoRequest dto = parse(requestJsonString, RegisterParticipantDtoRequest.class);
            validateNames(dto.getFirstName(), dto.getLastName());
            if (isBlank(dto.getCompanyName())) {
                throw new ServerException(ServerErrorCode.WRONG_COMPANY_NAME);
            }
            validateCredentials(dto.getLogin(), dto.getPassword());
            userDao.insert(new Participant(dto.getLogin(), dto.getPassword(),
                    dto.getFirstName().trim(), dto.getLastName().trim(), dto.getCompanyName().trim()));
            return ok(null);
        } catch (ServerException e) {
            return fail(e);
        }
    }

    public ServerResponse registerExpert(String requestJsonString) {
        try {
            RegisterExpertDtoRequest dto = parse(requestJsonString, RegisterExpertDtoRequest.class);
            validateNames(dto.getFirstName(), dto.getLastName());
            validateCredentials(dto.getLogin(), dto.getPassword());
            userDao.insert(new Expert(dto.getLogin(), dto.getPassword(),
                    dto.getFirstName().trim(), dto.getLastName().trim(), normalizeDirections(dto.getDirections())));
            return ok(null);
        } catch (ServerException e) {
            return fail(e);
        }
    }

    public ServerResponse login(String requestJsonString) {
        try {
            LoginDtoRequest dto = parse(requestJsonString, LoginDtoRequest.class);
            User user = dto.getLogin() == null ? null : userDao.getByLogin(dto.getLogin());
            if (user == null || dto.getPassword() == null || !user.getPassword().equals(dto.getPassword())) {
                throw new ServerException(ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
            }
            return ok(new LoginDtoResponse(userDao.login(user)));
        } catch (ServerException e) {
            return fail(e);
        }
    }

    public ServerResponse logout(String token) {
        try {
            requireUser(token);
            userDao.logout(token);
            return ok(null);
        } catch (ServerException e) {
            return fail(e);
        }
    }

    /** The user leaves the server: all of his data is deleted. */
    public ServerResponse leave(String token) {
        try {
            userDao.delete(requireUser(token));
            return ok(null);
        } catch (ServerException e) {
            return fail(e);
        }
    }

    private static void validateNames(String firstName, String lastName) throws ServerException {
        if (isBlank(firstName)) {
            throw new ServerException(ServerErrorCode.WRONG_FIRST_NAME);
        }
        if (isBlank(lastName)) {
            throw new ServerException(ServerErrorCode.WRONG_LAST_NAME);
        }
    }

    private static void validateCredentials(String login, String password) throws ServerException {
        if (login == null || login.length() < MIN_LOGIN_LENGTH || login.chars().anyMatch(Character::isWhitespace)) {
            throw new ServerException(ServerErrorCode.WRONG_LOGIN);
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH
                || password.chars().noneMatch(Character::isLetter)
                || password.chars().noneMatch(Character::isDigit)
                || password.chars().allMatch(c -> Character.isLetterOrDigit(c))) {
            throw new ServerException(ServerErrorCode.WRONG_PASSWORD);
        }
    }
}
