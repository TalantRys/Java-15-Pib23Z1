package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.dao.RatingDao;
import academy.tochkavhoda.competition.dto.response.ApplicationDtoResponse;
import academy.tochkavhoda.competition.model.Application;

import java.util.ArrayList;

final class ApplicationMapper {
    private ApplicationMapper() {
    }

    static ApplicationDtoResponse toDto(Application a, Double averageRating, Integer myRating) {
        return new ApplicationDtoResponse(a.getId(), a.getName(), a.getDescription(),
                new ArrayList<>(a.getDirections()), a.getSum(), a.getParticipant().getCompanyName(),
                averageRating, myRating);
    }

    static Double average(RatingDao ratingDao, Application a) {
        return average(ratingDao.getRatings(a));
    }

    static Double average(java.util.List<Integer> ratings) {
        if (ratings.isEmpty()) {
            return null;
        }
        long sum = 0;
        for (int r : ratings) {
            sum += r;
        }
        return (double) sum / ratings.size();
    }
}
