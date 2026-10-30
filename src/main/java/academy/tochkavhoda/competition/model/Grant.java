package academy.tochkavhoda.competition.model;

import java.util.*;

public class Grant {
    private Application application;
    private double averageRating;
    private long amount;

    public Grant() {
    }

    public Grant(Application application, double averageRating, long amount) {
        this.application = application;
        this.averageRating = averageRating;
        this.amount = amount;
    }

    public Application getApplication() {
        return application;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public long getAmount() {
        return amount;
    }

}
