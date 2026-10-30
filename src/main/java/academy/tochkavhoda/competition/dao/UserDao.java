package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.User;

public interface UserDao {
    void insert(User user) throws ServerException;

    User getByLogin(String login);

    User getByToken(String token);

    String login(User user);

    void logout(String token);

    void delete(User user);
}
