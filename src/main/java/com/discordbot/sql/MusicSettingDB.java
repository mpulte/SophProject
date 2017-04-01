package com.discordbot.sql;

/**
 * A {@link SQLiteDatabase} for querying the Music Settings Database.
 *
 * @see SQLiteDatabase
 */
public class MusicSettingDB extends SettingDB {

    // table constants
    private final static String TABLE = "music_setting";

    // create table statement
    private final static String CREATE_TABLE_SETTING =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    SETTING_KEY +   " TEXT     NOT NULL  PRIMARY KEY, " +
                    SETTING_VALUE + " TEXT     NOT NULL);";

    // drop table statement
    private final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE;

    /**
     * Constructor sets table name
     */
    public MusicSettingDB() {
        super("music_setting");
    }

    /**
     * Called by {@link SQLiteDatabase} if the database needs to be created.
     */
    @Override
    protected void onCreate() {
        query(CREATE_TABLE_SETTING);
    }

    /**
     * Called by {@link SQLiteDatabase} if the database needs to be destroyed.
     */
    @Override
    protected void onDestroy() {
        query(DROP_TABLE);
    }

}
