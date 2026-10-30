package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.dto.request.*;
import academy.tochkavhoda.competition.dto.response.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestApplications extends TestBase {

    @Test
    void addApplicationOk() {
        String token = participant("participant1");
        ServerResponse r = addApplicationResponse(token, "Заявка", 100, "Математика", "информатика");
        assertOk(r);
        int id1 = GSON.fromJson(r.getResponseData(), AddApplicationDtoResponse.class).getId();
        int id2 = addApplication(token, "Вторая", 200, "физика");
        assertNotEquals(id1, id2);

        List<ApplicationDtoResponse> mine = applications(server.getMyApplications(token));
        assertEquals(2, mine.size());
        ApplicationDtoResponse first = mine.get(0);
        assertEquals("Заявка", first.getName());
        assertEquals("Описание Заявка", first.getDescription());
        assertEquals(Arrays.asList("информатика", "математика"), first.getDirections());
        assertEquals(100, first.getSum());
        assertEquals("Фирма participant1", first.getCompanyName());
        assertNull(first.getAverageRating());
    }

    @Test
    void addApplicationValidation() {
        String token = participant("participant1");
        assertError(addApplicationResponse(token, " ", 100, "математика"), "Название заявки");
        assertError(server.addApplication(token, json(new AddApplicationDtoRequest(
                "Заявка", "", Collections.singletonList("математика"), 100))), "Описание заявки");
        assertError(addApplicationResponse(token, "Заявка", 100), "направлений");
        assertError(addApplicationResponse(token, "Заявка", 100, ""), "направлений");
        assertError(addApplicationResponse(token, "Заявка", 0, "математика"), "сумма");
        assertError(addApplicationResponse(token, "Заявка", -5, "математика"), "сумма");
        assertError(server.addApplication(token, "garbage"), "Некорректный запрос");
        assertError(server.addApplication(token, null), "Некорректный запрос");
        assertEquals(0, applications(server.getMyApplications(token)).size());
    }

    @Test
    void expertCannotAddApplication() {
        String expert = expert("expert1", "математика");
        assertError(addApplicationResponse(expert, "Заявка", 100, "математика"), "недоступна");
        assertError(server.getMyApplications(expert), "недоступна");
    }

    @Test
    void addSeveralApplicationsAtOnce() {
        String token = participant("participant1");
        List<AddApplicationDtoRequest> list = Arrays.asList(
                new AddApplicationDtoRequest("A", "описание", Collections.singletonList("математика"), 10),
                new AddApplicationDtoRequest("B", "описание", Collections.singletonList("физика"), 20));
        ServerResponse r = server.addApplications(token, json(new AddApplicationsDtoRequest(list)));
        assertOk(r);
        assertEquals(2, GSON.fromJson(r.getResponseData(), AddApplicationsDtoResponse.class).getIds().size());
        assertEquals(2, applications(server.getMyApplications(token)).size());
    }

    @Test
    void addSeveralApplicationsIsAtomic() {
        String token = participant("participant1");
        List<AddApplicationDtoRequest> list = Arrays.asList(
                new AddApplicationDtoRequest("A", "описание", Collections.singletonList("математика"), 10),
                new AddApplicationDtoRequest("B", "описание", Collections.singletonList("физика"), 0));
        assertError(server.addApplications(token, json(new AddApplicationsDtoRequest(list))), "сумма");
        assertEquals(0, applications(server.getMyApplications(token)).size());
        assertError(server.addApplications(token, json(new AddApplicationsDtoRequest(Collections.emptyList()))),
                "Некорректный запрос");
    }

    @Test
    void cancelApplication() {
        String token = participant("participant1");
        int id = addApplication(token, "Заявка", 100, "математика");
        int keep = addApplication(token, "Другая", 100, "математика");
        String expert = expert("expert1", "математика");
        assertOk(rate(expert, id, 4));

        assertOk(server.cancelApplication(token, json(new CancelApplicationDtoRequest(id))));

        List<ApplicationDtoResponse> mine = applications(server.getMyApplications(token));
        assertEquals(1, mine.size());
        assertEquals(keep, mine.get(0).getId());
        assertEquals(0, applications(server.getRatedApplications(expert)).size());
        assertError(server.cancelApplication(token, json(new CancelApplicationDtoRequest(id))), "Заявка не найдена");
    }

    @Test
    void cannotCancelForeignApplication() {
        String owner = participant("participant1");
        String other = participant("participant2");
        int id = addApplication(owner, "Заявка", 100, "математика");
        assertError(server.cancelApplication(other, json(new CancelApplicationDtoRequest(id))), "другому участнику");
        assertEquals(1, applications(server.getMyApplications(owner)).size());
    }

    @Test
    void myApplicationsContainAverageRating() {
        String token = participant("participant1");
        int id = addApplication(token, "Заявка", 100, "математика");
        String e1 = expert("expert1", "математика");
        String e2 = expert("expert2", "математика");
        rate(e1, id, 5);
        rate(e2, id, 2);
        assertEquals(3.5, applications(server.getMyApplications(token)).get(0).getAverageRating());
    }

    @Test
    void expertGetsApplicationsOfHisDirections() {
        String p = participant("participant1");
        int math = addApplication(p, "Математика", 100, "математика");
        int cs = addApplication(p, "Информатика", 100, "информатика");
        int phys = addApplication(p, "Физика", 100, "физика");
        int both = addApplication(p, "Смешанная", 100, "физика", "математика");
        String expert = expert("expert1", "Математика", "информатика");
        rate(expert, cs, 4);

        List<ApplicationDtoResponse> all = applications(server.getApplicationsByDirections(expert, null));
        assertEquals(Arrays.asList(math, cs, both), ids(all));
        assertEquals(4, all.get(1).getMyRating());
        assertNull(all.get(0).getMyRating());
        assertNull(all.get(1).getAverageRating());

        assertEquals(Arrays.asList(math, both), ids(applications(server.getApplicationsByDirections(expert,
                json(new GetApplicationsDtoRequest(Collections.singletonList(" МАТЕМАТИКА ")))))));
        assertEquals(Collections.singletonList(cs), ids(applications(server.getApplicationsByDirections(expert,
                json(new GetApplicationsDtoRequest(Collections.singletonList("информатика")))))));
        assertEquals(all.size(), applications(server.getApplicationsByDirections(expert, "{}")).size());
        assertFalse(ids(all).contains(phys));
    }

    @Test
    void expertCannotRequestForeignDirection() {
        String expert = expert("expert1", "математика");
        assertError(server.getApplicationsByDirections(expert, json(new GetApplicationsDtoRequest(
                Arrays.asList("математика", "физика")))), "не являетесь экспертом");
        assertError(server.getApplicationsByDirections(expert, "oops"), "Некорректный запрос");
    }

    @Test
    void participantCannotUseExpertList() {
        String p = participant("participant1");
        assertError(server.getApplicationsByDirections(p, null), "недоступна");
    }

    private static List<Integer> ids(List<ApplicationDtoResponse> list) {
        List<Integer> result = new java.util.ArrayList<>();
        for (ApplicationDtoResponse a : list) {
            result.add(a.getId());
        }
        return result;
    }
}
