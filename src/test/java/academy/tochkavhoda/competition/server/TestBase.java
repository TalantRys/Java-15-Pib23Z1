package academy.tochkavhoda.competition.server;

import com.google.gson.Gson;
import academy.tochkavhoda.competition.dto.request.*;
import academy.tochkavhoda.competition.dto.response.*;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/** The test is a client: it only calls Server and works with json / DTO. */
abstract class TestBase {
    protected static final Gson GSON = new Gson();
    protected static final String PASSWORD = "Passw0rd!";
    protected static final long FUND = 1000;
    protected Server server;

    @BeforeEach
    void setUp() {
        server = new Server(FUND, 3.0, new Random(1));
    }

    protected static String json(Object dto) {
        return GSON.toJson(dto);
    }

    protected static void assertOk(ServerResponse r) {
        assertEquals(200, r.getResponseCode(), r.getResponseData());
    }

    protected static void assertError(ServerResponse r, String messagePart) {
        assertEquals(400, r.getResponseCode(), r.getResponseData());
        String error = GSON.fromJson(r.getResponseData(), ErrorDtoResponse.class).getError();
        assertNotNull(error);
        assertTrue(error.contains(messagePart), "Expected error containing '" + messagePart + "' but was '" + error + "'");
    }

    protected ServerResponse registerParticipant(String login, String company) {
        return server.registerParticipant(json(
                new RegisterParticipantDtoRequest("Иван", "Иванов", company, login, PASSWORD)));
    }

    protected ServerResponse registerExpert(String login, String... directions) {
        return server.registerExpert(json(
                new RegisterExpertDtoRequest("Пётр", "Петров", Arrays.asList(directions), login, PASSWORD)));
    }

    protected String login(String login) {
        ServerResponse r = server.login(json(new LoginDtoRequest(login, PASSWORD)));
        assertOk(r);
        return GSON.fromJson(r.getResponseData(), LoginDtoResponse.class).getToken();
    }

    protected String participant(String login) {
        assertOk(registerParticipant(login, "Фирма " + login));
        return login(login);
    }

    protected String expert(String login, String... directions) {
        assertOk(registerExpert(login, directions));
        return login(login);
    }

    protected ServerResponse addApplicationResponse(String token, String name, long sum, String... directions) {
        return server.addApplication(token, json(new AddApplicationDtoRequest(name, "Описание " + name,
                Arrays.asList(directions), sum)));
    }

    protected int addApplication(String token, String name, long sum, String... directions) {
        ServerResponse r = addApplicationResponse(token, name, sum, directions);
        assertOk(r);
        return GSON.fromJson(r.getResponseData(), AddApplicationDtoResponse.class).getId();
    }

    protected ServerResponse rate(String token, int applicationId, int rating) {
        return server.rateApplication(token, json(new RateApplicationDtoRequest(applicationId, rating)));
    }

    protected List<ApplicationDtoResponse> applications(ServerResponse r) {
        assertOk(r);
        return GSON.fromJson(r.getResponseData(), ApplicationsDtoResponse.class).getApplications();
    }

    protected ResultsDtoResponse results(ServerResponse r) {
        assertOk(r);
        return GSON.fromJson(r.getResponseData(), ResultsDtoResponse.class);
    }
}
