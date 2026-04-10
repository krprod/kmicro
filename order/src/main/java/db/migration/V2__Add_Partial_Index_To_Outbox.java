package db.migration;

/*

public class V2__Add_Partial_Index_To_Outbox extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        try (Statement statement = context.getConnection().createStatement()) {
            // Complex logic: Create partial index only if on PostgreSQL
            String dbName = context.getConnection().getMetaData().getDatabaseProductName();
            if ("PostgreSQL".equalsIgnoreCase(dbName)) {
                statement.execute(
                        "CREATE INDEX IF NOT EXISTS idx_pending_outbox " +
                                "ON outbox_events (created_at) WHERE status = 'PENDING'"
                );
            }
        }
    }

}//EC
*/
