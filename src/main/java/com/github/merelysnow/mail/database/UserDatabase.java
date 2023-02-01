package com.github.merelysnow.mail.database;

import com.github.merelysnow.mail.data.Mail;
import com.github.merelysnow.mail.data.User;
import com.github.merelysnow.mail.database.connection.RepositoryProvider;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public class UserDatabase extends RepositoryProvider {

    private static final String TABLE_NAME = "user_table";
    private static final Gson GSON = new GsonBuilder().create();
    private SQLExecutor sqlExecutor;

    public UserDatabase(Plugin plugin) {
        super(plugin);
        this.prepare();

        this.handleTable();
    }

    @Override
    public SQLConnector prepare() {
        final SQLConnector connector = super.prepare();
        this.sqlExecutor = new SQLExecutor(connector);

        return connector;
    }

    public void handleTable() {
        this.sqlExecutor.updateQuery("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                "name VARCHAR(33) NOT NULL PRIMARY KEY," +
                "emails TEXT NOT NULL" +
                ");");
    }

    public Set<User> selectMany() {
        return this.sqlExecutor.resultManyQuery(
                "SELECT * FROM " + TABLE_NAME, simpleStatement -> {}, UserDatabaseAdapter.class);
    }

    public void insert(User user) {
        this.sqlExecutor.updateQuery("REPLACE INTO " + TABLE_NAME + " VALUES(?,?)", simpleStatement -> {
            simpleStatement.set(1, user.getName());
            simpleStatement.set(2, GSON.toJson(user.getEmails()));
        });
    }

    public void deleteOne(User user) {
        this.sqlExecutor.updateQuery(
                "DELETE FROM " + TABLE_NAME + " WHERE name = ?",
                simpleStatement -> simpleStatement.set(1, user.getName()));
    }

    public static class UserDatabaseAdapter implements SQLResultAdapter<User> {

        @Override
        public User adaptResult(SimpleResultSet resultSet) {

            Type type = new TypeToken<List<Mail>>() {}.getType();

            return new User(
                    resultSet.get("name"),
                    GSON.fromJson((String) resultSet.get("emails"), type)
            );
        }
    }
}
