package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.dto.request.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestUsers extends TestBase {

    @Test
    void registerParticipantOk() {
        ServerResponse r = registerParticipant("participant1", "Фирма");
        assertOk(r);
        assertEquals("{}", r.getResponseData());
    }

    @Test
    void registerExpertOk() {
        assertOk(registerExpert("expert1", "математика", "информатика"));
    }

    @Test
    void duplicateLoginIsRejected() {
        assertOk(registerParticipant("sameLogin", "Фирма"));
        assertError(registerParticipant("sameLogin", "Другая фирма"), "Логин уже используется");
        assertError(registerExpert("sameLogin", "математика"), "Логин уже используется");
    }

    @Test
    void participantValidation() {
        assertError(server.registerParticipant(json(new RegisterParticipantDtoRequest(
                "", "Иванов", "Фирма", "participant1", PASSWORD))), "Имя");
        assertError(server.registerParticipant(json(new RegisterParticipantDtoRequest(
                "Иван", null, "Фирма", "participant1", PASSWORD))), "Фамилия");
        assertError(server.registerParticipant(json(new RegisterParticipantDtoRequest(
                "Иван", "Иванов", "  ", "participant1", PASSWORD))), "Название фирмы");
    }

    @Test
    void loginValidation() {
        assertError(registerParticipant("abc", "Фирма"), "Логин должен содержать");
        assertError(registerParticipant("abc def", "Фирма"), "Логин должен содержать");
        assertError(registerParticipant(null, "Фирма"), "Логин должен содержать");
    }

    @Test
    void passwordValidation() {
        for (String bad : new String[]{null, "", "Ab1!", "password!", "12345678!", "Password12"}) {
            assertError(server.registerParticipant(json(new RegisterParticipantDtoRequest(
                    "Иван", "Иванов", "Фирма", "participant1", bad))), "Пароль должен содержать");
        }
    }

    @Test
    void expertDirectionsValidation() {
        assertError(server.registerExpert(json(new RegisterExpertDtoRequest(
                "Пётр", "Петров", null, "expert1", PASSWORD))), "направлений");
        assertError(registerExpert("expert1"), "направлений");
        assertError(registerExpert("expert1", "математика", " "), "направлений");
    }

    @Test
    void wrongJson() {
        assertError(server.registerParticipant(null), "Некорректный запрос");
        assertError(server.registerParticipant(""), "Некорректный запрос");
        assertError(server.registerParticipant("{not a json"), "Некорректный запрос");
        assertError(server.registerExpert("[1,2]"), "Некорректный запрос");
        assertError(server.login("null"), "Некорректный запрос");
    }

    @Test
    void loginReturnsUniqueTokens() {
        registerParticipant("participant1", "Фирма");
        String t1 = login("participant1");
        assertNotNull(t1);
        assertFalse(t1.isEmpty());
        assertOk(server.logout(t1));
        assertNotEquals(t1, login("participant1"));
    }

    @Test
    void loginWithWrongCredentials() {
        registerParticipant("participant1", "Фирма");
        assertError(server.login(json(new LoginDtoRequest("participant1", "wrong"))), "Неверный логин или пароль");
        assertError(server.login(json(new LoginDtoRequest("unknown1", PASSWORD))), "Неверный логин или пароль");
        assertError(server.login(json(new LoginDtoRequest("participant1", null))), "Неверный логин или пароль");
        assertError(server.login(json(new LoginDtoRequest(null, null))), "Неверный логин или пароль");
    }

    @Test
    void logoutInvalidatesToken() {
        String token = participant("participant1");
        assertOk(server.logout(token));
        assertError(server.logout(token), "Недействительный токен");
        assertError(addApplicationResponse(token, "Заявка", 10, "математика"), "Недействительный токен");
        assertError(server.getMyApplications(token), "Недействительный токен");
    }

    @Test
    void secondLoginInvalidatesPreviousToken() {
        String first = participant("participant1");
        String second = login("participant1");
        assertNotEquals(first, second);
        assertError(server.getMyApplications(first), "Недействительный токен");
        assertOk(server.getMyApplications(second));
    }

    @Test
    void invalidTokens() {
        assertError(server.logout("no-such-token"), "Недействительный токен");
        assertError(server.leave(null), "Недействительный токен");
        assertError(server.getMyApplications(null), "Недействительный токен");
    }

    @Test
    void leaveDeletesUserAndHisData() {
        String token = participant("participant1");
        String other = participant("participant2");
        int id = addApplication(token, "Заявка", 100, "математика");
        addApplication(other, "Чужая заявка", 100, "математика");
        String expert = expert("expert1", "математика");
        assertOk(rate(expert, id, 5));

        assertOk(server.leave(token));

        assertError(server.getMyApplications(token), "Недействительный токен");
        assertError(server.login(json(new LoginDtoRequest("participant1", PASSWORD))), "Неверный логин или пароль");
        assertEquals(1, applications(server.getApplicationsByDirections(expert, null)).size());
        assertEquals(0, applications(server.getRatedApplications(expert)).size());
        // login is free again
        assertOk(registerParticipant("participant1", "Новая фирма"));
    }

    @Test
    void expertLeaveDeletesHisRatings() {
        String participant = participant("participant1");
        int id = addApplication(participant, "Заявка", 100, "математика");
        String e1 = expert("expert1", "математика");
        String e2 = expert("expert2", "математика");
        assertOk(rate(e1, id, 5));
        assertOk(rate(e2, id, 3));
        assertEquals(4.0, applications(server.getMyApplications(participant)).get(0).getAverageRating());

        assertOk(server.leave(e1));

        assertEquals(3.0, applications(server.getMyApplications(participant)).get(0).getAverageRating());
    }
}
