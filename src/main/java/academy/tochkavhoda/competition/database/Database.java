package academy.tochkavhoda.competition.database;

import academy.tochkavhoda.competition.exception.ServerErrorCode;
import academy.tochkavhoda.competition.exception.ServerException;
import academy.tochkavhoda.competition.model.*;

import java.util.*;

/** In-memory "database" (singleton). Knows only about model classes. */
public final class Database {
    private static final Database INSTANCE = new Database();

    private final Map<String, User> users = new HashMap<>();
    private final Map<String, User> tokens = new HashMap<>();
    private final Map<Integer, Application> applications = new LinkedHashMap<>();
    /** applicationId -> (expert login -> rating) */
    private final Map<Integer, Map<String, Integer>> ratings = new HashMap<>();
    private List<Grant> grants;
    private int nextApplicationId = 1;

    private Database() {
    }

    public static Database getInstance() {
        return INSTANCE;
    }

    public synchronized void clear() {
        users.clear();
        tokens.clear();
        applications.clear();
        ratings.clear();
        grants = null;
        nextApplicationId = 1;
    }

    // ---------- users ----------
    public synchronized void insertUser(User user) throws ServerException {
        if (users.containsKey(user.getLogin())) {
            throw new ServerException(ServerErrorCode.LOGIN_ALREADY_USED, user.getLogin());
        }
        users.put(user.getLogin(), user);
    }

    public synchronized User getUserByLogin(String login) {
        return users.get(login);
    }

    public synchronized User getUserByToken(String token) {
        return token == null ? null : tokens.get(token);
    }

    public synchronized String login(User user) {
        tokens.values().removeIf(u -> u == user);
        String token = UUID.randomUUID().toString();
        tokens.put(token, user);
        return token;
    }

    public synchronized void logout(String token) {
        tokens.remove(token);
    }

    public synchronized void deleteUser(User user) {
        tokens.values().removeIf(u -> u == user);
        users.remove(user.getLogin());
        if (user instanceof Participant) {
            applications.values().removeIf(a -> {
                boolean own = a.getParticipant() == user;
                if (own) {
                    ratings.remove(a.getId());
                }
                return own;
            });
        } else {
            ratings.values().forEach(m -> m.remove(user.getLogin()));
        }
    }

    // ---------- applications ----------
    public synchronized int insertApplication(Application application) {
        application.setId(nextApplicationId++);
        applications.put(application.getId(), application);
        return application.getId();
    }

    public synchronized Application getApplication(int id) {
        return applications.get(id);
    }

    public synchronized void deleteApplication(Application application) {
        applications.remove(application.getId());
        ratings.remove(application.getId());
    }

    public synchronized List<Application> getApplicationsByParticipant(Participant participant) {
        List<Application> result = new ArrayList<>();
        for (Application a : applications.values()) {
            if (a.getParticipant() == participant) {
                result.add(a);
            }
        }
        return result;
    }

    public synchronized List<Application> getAllApplications() {
        return new ArrayList<>(applications.values());
    }

    // ---------- ratings ----------
    public synchronized void setRating(Expert expert, Application application, int rating) {
        ratings.computeIfAbsent(application.getId(), k -> new HashMap<>()).put(expert.getLogin(), rating);
    }

    public synchronized boolean removeRating(Expert expert, Application application) {
        Map<String, Integer> m = ratings.get(application.getId());
        return m != null && m.remove(expert.getLogin()) != null;
    }

    public synchronized Integer getRating(Expert expert, Application application) {
        Map<String, Integer> m = ratings.get(application.getId());
        return m == null ? null : m.get(expert.getLogin());
    }

    public synchronized List<Integer> getRatings(Application application) {
        Map<String, Integer> m = ratings.get(application.getId());
        return m == null ? new ArrayList<>() : new ArrayList<>(m.values());
    }

    public synchronized List<Application> getApplicationsRatedBy(Expert expert) {
        List<Application> result = new ArrayList<>();
        for (Application a : applications.values()) {
            if (getRating(expert, a) != null) {
                result.add(a);
            }
        }
        return result;
    }

    // ---------- competition ----------
    public synchronized boolean isFinished() {
        return grants != null;
    }

    public synchronized void finish(List<Grant> result) {
        grants = new ArrayList<>(result);
    }

    public synchronized List<Grant> getGrants() {
        return grants == null ? new ArrayList<>() : new ArrayList<>(grants);
    }
}
