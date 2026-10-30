package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.dto.request.*;
import academy.tochkavhoda.competition.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestRatings extends TestBase {
    private String participant;
    private String expert;
    private int appId;

    @BeforeEach
    void init() {
        participant = participant("participant1");
        expert = expert("expert1", "математика");
        appId = addApplication(participant, "Заявка", 100, "математика");
    }

    private ApplicationDtoResponse mine() {
        return applications(server.getMyApplications(participant)).get(0);
    }

    @Test
    void rateChangeAndRemove() {
        assertOk(rate(expert, appId, 4));
        assertEquals(4.0, mine().getAverageRating());

        assertOk(rate(expert, appId, 2));
        assertEquals(2.0, mine().getAverageRating());
        List<ApplicationDtoResponse> rated = applications(server.getRatedApplications(expert));
        assertEquals(1, rated.size());
        assertEquals(2, rated.get(0).getMyRating());

        assertOk(server.removeRating(expert, json(new RemoveRatingDtoRequest(appId))));
        assertNull(mine().getAverageRating());
        assertEquals(0, applications(server.getRatedApplications(expert)).size());
        assertError(server.removeRating(expert, json(new RemoveRatingDtoRequest(appId))), "Оценка");
    }

    @Test
    void ratingRange() {
        assertError(rate(expert, appId, 0), "от 1 до 5");
        assertError(rate(expert, appId, 6), "от 1 до 5");
        assertError(rate(expert, appId, -1), "от 1 до 5");
        assertOk(rate(expert, appId, 1));
        assertOk(rate(expert, appId, 5));
    }

    @Test
    void unknownApplication() {
        assertError(rate(expert, 999, 3), "Заявка не найдена");
        assertError(server.removeRating(expert, json(new RemoveRatingDtoRequest(999))), "Заявка не найдена");
    }

    @Test
    void expertRatesOnlyHisDirections() {
        int other = addApplication(participant, "Физика", 100, "физика");
        assertError(rate(expert, other, 3), "не являетесь экспертом");
        int mixed = addApplication(participant, "Смесь", 100, "физика", "математика");
        assertOk(rate(expert, mixed, 3));
    }

    @Test
    void participantCannotRate() {
        assertError(rate(participant, appId, 3), "недоступна");
        assertError(server.getRatedApplications(participant), "недоступна");
        assertError(rate("bad-token", appId, 3), "Недействительный токен");
    }

    @Test
    void badRequests() {
        assertError(server.rateApplication(expert, null), "Некорректный запрос");
        assertError(server.rateApplication(expert, "{\"rating\":\"abc\"}"), "Некорректный запрос");
        assertError(server.removeRating(expert, ""), "Некорректный запрос");
    }

    @Test
    void severalExpertsAverage() {
        String e2 = expert("expert2", "математика");
        rate(expert, appId, 5);
        rate(e2, appId, 4);
        assertEquals(4.5, mine().getAverageRating());
    }
}
