package az.zefflix.common.constants;

/**
 * Bütün Zefflix Kafka topic adları.
 *
 * <p>Hər topic adı servis prefiksi + hadisə adı şəklindədir.
 * Nümunə: {@code auth.user.registered}
 *
 * <p><b>Qayda:</b> Topic adını dəyişdirirsinizsə,
 * həm producer həm də consumer servisini yeniləyin.
 */
@SuppressWarnings("HideUtilityClassConstructor")
public final class KafkaTopics {

    // ── Auth Service → ────────────────────────────────────────────────────────
    public static final String AUTH_USER_REGISTERED = "auth.user.registered";
    public static final String AUTH_USER_VERIFIED = "auth.user.verified";
    public static final String AUTH_PASSWORD_RESET = "auth.password.reset";

    // ── Content Service → ─────────────────────────────────────────────────────
    public static final String CONTENT_CREATED = "content.created";
    public static final String CONTENT_UPDATED = "content.updated";
    public static final String CONTENT_DELETED = "content.deleted";

    // ── Person Service → ──────────────────────────────────────────────────────
    public static final String PERSON_CREATED = "person.created";
    public static final String PERSON_UPDATED = "person.updated";

    // ── User Service → ────────────────────────────────────────────────────────
    public static final String USER_WATCHLIST_UPDATED = "user.watchlist.updated";
    public static final String USER_PROFILE_UPDATED = "user.profile.updated";

    // ── Utility class – instantiate edilə bilməz ──────────────────────────────
    private KafkaTopics() {
        throw new UnsupportedOperationException("Bu sinif instansiya yaradıla bilməz.");
    }

}
