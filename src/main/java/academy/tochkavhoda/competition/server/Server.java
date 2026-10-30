package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.dao.*;
import academy.tochkavhoda.competition.daoimpl.*;
import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.service.*;

import java.util.Random;

public class Server {
    public static final long DEFAULT_FUND = 1_000_000;
    public static final double DEFAULT_THRESHOLD = 3.0;

    private final UserService userService;
    private final ApplicationService applicationService;
    private final RatingService ratingService;
    private final ResultService resultService;

    public Server() {
        this(DEFAULT_FUND, DEFAULT_THRESHOLD, new Random());
    }

    /**
     * @param fund      total grant fund
     * @param threshold applications with an average rating not greater than this value get nothing
     */
    public Server(long fund, double threshold) {
        this(fund, threshold, new Random());
    }

    public Server(long fund, double threshold, Random random) {
        // starting a server means starting with an empty "database"
        Database.getInstance().clear();
        UserDao userDao = new UserDaoImpl();
        ApplicationDao applicationDao = new ApplicationDaoImpl();
        RatingDao ratingDao = new RatingDaoImpl();
        CompetitionDao competitionDao = new CompetitionDaoImpl();
        userService = new UserService(userDao);
        applicationService = new ApplicationService(userDao, applicationDao, ratingDao, competitionDao);
        ratingService = new RatingService(userDao, applicationDao, ratingDao, competitionDao);
        resultService = new ResultService(userDao, applicationDao, ratingDao, competitionDao, fund, threshold, random);
    }

    // ----- registration, login -----
    public ServerResponse registerParticipant(String requestJsonString) {
        return userService.registerParticipant(requestJsonString);
    }

    public ServerResponse registerExpert(String requestJsonString) {
        return userService.registerExpert(requestJsonString);
    }

    public ServerResponse login(String requestJsonString) {
        return userService.login(requestJsonString);
    }

    // ----- common -----
    public ServerResponse logout(String token) {
        return userService.logout(token);
    }

    public ServerResponse leave(String token) {
        return userService.leave(token);
    }

    // ----- participant -----
    public ServerResponse addApplication(String token, String requestJsonString) {
        return applicationService.addApplication(token, requestJsonString);
    }

    public ServerResponse addApplications(String token, String requestJsonString) {
        return applicationService.addApplications(token, requestJsonString);
    }

    public ServerResponse cancelApplication(String token, String requestJsonString) {
        return applicationService.cancelApplication(token, requestJsonString);
    }

    public ServerResponse getMyApplications(String token) {
        return applicationService.getMyApplications(token);
    }

    // ----- expert -----
    public ServerResponse getApplicationsByDirections(String token, String requestJsonString) {
        return applicationService.getApplicationsByDirections(token, requestJsonString);
    }

    public ServerResponse rateApplication(String token, String requestJsonString) {
        return ratingService.rateApplication(token, requestJsonString);
    }

    public ServerResponse removeRating(String token, String requestJsonString) {
        return ratingService.removeRating(token, requestJsonString);
    }

    public ServerResponse getRatedApplications(String token) {
        return ratingService.getRatedApplications(token);
    }

    // ----- results -----
    public ServerResponse finishCompetition(String token) {
        return resultService.finishCompetition(token);
    }

    public ServerResponse getResults(String token) {
        return resultService.getResults(token);
    }
}
