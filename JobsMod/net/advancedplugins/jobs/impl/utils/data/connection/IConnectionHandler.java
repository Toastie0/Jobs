package net.advancedplugins.jobs.impl.utils.data.connection;

import com.j256.ormlite.support.BaseConnectionSource;
import java.io.IOException;
import java.sql.SQLException;
import org.bukkit.configuration.ConfigurationSection;

public interface IConnectionHandler {
   ConnectionType getConnectionType();

   void retrieveCredentials(ConfigurationSection var1);

   BaseConnectionSource connect() throws IOException, SQLException;

   BaseConnectionSource connectHikari() throws IOException, SQLException;

   void close();
}
