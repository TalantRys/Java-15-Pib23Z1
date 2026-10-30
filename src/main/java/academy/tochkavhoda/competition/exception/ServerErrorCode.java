package academy.tochkavhoda.competition.exception;

public enum ServerErrorCode {
    WRONG_JSON("Некорректный запрос: неверный формат json или отсутствуют данные"),
    WRONG_FIRST_NAME("Имя не должно быть пустым"),
    WRONG_LAST_NAME("Фамилия не должна быть пустой"),
    WRONG_COMPANY_NAME("Название фирмы не должно быть пустым"),
    WRONG_LOGIN("Логин должен содержать не менее 5 символов и не содержать пробелов"),
    WRONG_PASSWORD("Пароль должен содержать не менее 8 символов, буквы, цифры и хотя бы один спецсимвол"),
    WRONG_DIRECTIONS("Список направлений не должен быть пустым и содержать пустые направления"),
    WRONG_APPLICATION_NAME("Название заявки не должно быть пустым"),
    WRONG_APPLICATION_DESCRIPTION("Описание заявки не должно быть пустым"),
    WRONG_SUM("Запрашиваемая сумма должна быть положительной"),
    WRONG_RATING("Оценка должна быть в диапазоне от 1 до 5"),
    LOGIN_ALREADY_USED("Логин уже используется"),
    WRONG_LOGIN_OR_PASSWORD("Неверный логин или пароль"),
    INVALID_TOKEN("Недействительный токен"),
    ACCESS_DENIED("Операция недоступна для данного типа пользователя"),
    APPLICATION_NOT_FOUND("Заявка не найдена"),
    NOT_APPLICATION_OWNER("Заявка принадлежит другому участнику"),
    NOT_EXPERT_IN_DIRECTION("Вы не являетесь экспертом по данному направлению"),
    RATING_NOT_FOUND("Оценка для этой заявки не найдена"),
    COMPETITION_FINISHED("Конкурс завершён, операция недоступна"),
    COMPETITION_NOT_FINISHED("Конкурс ещё не завершён");

    private final String message;

    ServerErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
