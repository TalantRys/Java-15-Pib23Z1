package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.dto.request.*;
import academy.tochkavhoda.competition.dto.response.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestResults extends TestBase {

    @Test
    void rankingAndDistribution() {
        String p = participant("participant1");
        int a = addApplication(p, "A", 600, "математика");
        int b = addApplication(p, "B", 500, "математика");
        int c = addApplication(p, "C", 300, "математика");
        int d = addApplication(p, "D", 400, "математика");
        int e = addApplication(p, "E", 100, "математика");
        addApplication(p, "F без оценок", 50, "математика");
        String e1 = expert("expert1", "математика");
        String e2 = expert("expert2", "математика");
        rate(e1, a, 5); rate(e2, a, 5);   // 5.0
        rate(e1, b, 5); rate(e2, b, 4);   // 4.5, sum 500
        rate(e1, c, 4); rate(e2, c, 5);   // 4.5, sum 300 -> ranked before B
        rate(e1, d, 4); rate(e2, d, 4);   // 4.0
        rate(e1, e, 3); rate(e2, e, 3);   // 3.0 -> not greater than threshold

        ResultsDtoResponse r = results(server.finishCompetition(p));

        // A (600) -> 400 left; C (300) -> 100 left; B (500) and D (400) do not fit; E is below threshold
        assertEquals(2, r.getGrants().size());
        assertEquals(a, r.getGrants().get(0).getApplicationId());
        assertEquals(600, r.getGrants().get(0).getAmount());
        assertEquals(5.0, r.getGrants().get(0).getAverageRating());
        assertEquals(c, r.getGrants().get(1).getApplicationId());
        assertEquals(300, r.getGrants().get(1).getAmount());
        assertEquals(FUND, r.getFund());
        assertEquals(100, r.getRemaining());
        assertEquals("Фирма participant1", r.getGrants().get(0).getCompanyName());
    }

    @Test
    void tieIsBrokenBySmallerSum() {
        String p = participant("participant1");
        int big = addApplication(p, "Big", 900, "математика");
        int small = addApplication(p, "Small", 800, "математика");
        String e = expert("expert1", "математика");
        rate(e, big, 4);
        rate(e, small, 4);
        ResultsDtoResponse r = results(server.finishCompetition(e));
        assertEquals(1, r.getGrants().size());
        assertEquals(small, r.getGrants().get(0).getApplicationId());
        assertEquals(200, r.getRemaining());
    }

    @Test
    void fullTieIsResolvedRandomlyButExactlyOneWins() {
        String p = participant("participant1");
        int x = addApplication(p, "X", 700, "математика");
        int y = addApplication(p, "Y", 700, "математика");
        String e = expert("expert1", "математика");
        rate(e, x, 5);
        rate(e, y, 5);
        ResultsDtoResponse r = results(server.finishCompetition(p));
        assertEquals(1, r.getGrants().size());
        assertTrue(r.getGrants().get(0).getApplicationId() == x || r.getGrants().get(0).getApplicationId() == y);
        assertEquals(300, r.getRemaining());
    }

    @Test
    void requestExceedingFundIsSkippedButNextIsConsidered() {
        String p = participant("participant1");
        int huge = addApplication(p, "Huge", FUND + 1, "математика");
        int ok = addApplication(p, "Ok", 10, "математика");
        String e = expert("expert1", "математика");
        rate(e, huge, 5);
        rate(e, ok, 4);
        ResultsDtoResponse r = results(server.finishCompetition(p));
        assertEquals(1, r.getGrants().size());
        assertEquals(ok, r.getGrants().get(0).getApplicationId());
    }

    @Test
    void noRatingsNoGrants() {
        String p = participant("participant1");
        addApplication(p, "A", 10, "математика");
        ResultsDtoResponse r = results(server.finishCompetition(p));
        assertTrue(r.getGrants().isEmpty());
        assertEquals(FUND, r.getRemaining());
    }

    @Test
    void getResultsBeforeAndAfterFinish() {
        String p = participant("participant1");
        int id = addApplication(p, "A", 10, "математика");
        String e = expert("expert1", "математика");
        rate(e, id, 5);
        assertError(server.getResults(p), "ещё не завершён");

        ResultsDtoResponse first = results(server.finishCompetition(e));
        ResultsDtoResponse again = results(server.getResults(p));
        assertEquals(1, again.getGrants().size());
        assertEquals(first.getRemaining(), again.getRemaining());
        assertError(server.finishCompetition(e), "Конкурс завершён");
        assertError(server.getResults("bad"), "Недействительный токен");
    }

    @Test
    void nothingCanChangeAfterFinish() {
        String p = participant("participant1");
        int id = addApplication(p, "A", 10, "математика");
        String e = expert("expert1", "математика");
        rate(e, id, 5);
        assertOk(server.finishCompetition(p));

        assertError(addApplicationResponse(p, "B", 10, "математика"), "Конкурс завершён");
        assertError(server.addApplications(p, "{}"), "Конкурс завершён");
        assertError(server.cancelApplication(p, json(new CancelApplicationDtoRequest(id))), "Конкурс завершён");
        assertError(rate(e, id, 1), "Конкурс завершён");
        assertError(server.removeRating(e, json(new RemoveRatingDtoRequest(id))), "Конкурс завершён");
        // reading is still possible
        assertEquals(1, applications(server.getMyApplications(p)).size());
        assertEquals(1, results(server.getResults(e)).getGrants().size());
    }

    @Test
    void leavingExpertChangesOutcome() {
        String p = participant("participant1");
        int id = addApplication(p, "A", 10, "математика");
        String e1 = expert("expert1", "математика");
        String e2 = expert("expert2", "математика");
        rate(e1, id, 5);
        rate(e2, id, 1);              // average 3.0 -> not funded
        assertOk(server.leave(e2));   // ratings of a leaving expert are deleted -> average 5.0
        assertEquals(1, results(server.finishCompetition(p)).getGrants().size());
    }

    @Test
    void integrationScenario() {
        String company1 = participant("company1");
        String company2 = participant("company2");
        String math = expert("mathExpert", "математика");
        String cs = expert("csExpert", "информатика", "математика");

        int m = addApplication(company1, "Матмодель", 400, "математика");
        int i = addApplication(company2, "Приложение", 500, "информатика");
        int both = addApplication(company2, "Алгоритмы", 300, "информатика", "математика");

        assertEquals(2, applications(server.getApplicationsByDirections(math, null)).size());
        assertEquals(3, applications(server.getApplicationsByDirections(cs, null)).size());

        assertOk(rate(math, m, 5));
        assertOk(rate(cs, m, 4));
        assertOk(rate(cs, i, 5));
        assertOk(rate(math, both, 4));
        assertOk(rate(cs, both, 4));
        assertOk(rate(math, both, 5));          // changed his mind: 4.5
        assertOk(server.cancelApplication(company2, json(new CancelApplicationDtoRequest(i))));
        assertOk(server.logout(math));
        String math2 = login("mathExpert");
        assertEquals(2, applications(server.getRatedApplications(math2)).size());

        ResultsDtoResponse r = results(server.finishCompetition(math2));
        // m: 4.5 / 400, both: 4.5 / 300 -> both first (smaller sum), then m: 300 + 400 = 700 <= 1000
        assertEquals(2, r.getGrants().size());
        assertEquals(both, r.getGrants().get(0).getApplicationId());
        assertEquals(m, r.getGrants().get(1).getApplicationId());
        assertEquals(300, r.getRemaining());
    }
}
