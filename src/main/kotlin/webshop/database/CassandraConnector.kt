package webshop.database

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.config.DefaultDriverOption
import com.datastax.oss.driver.api.core.config.DriverConfigLoader
import java.net.InetSocketAddress
import java.time.Duration

class CassandraConnector(
    private val host: String,
    private val port: Int,
    private val datacenter: String
) : AutoCloseable {
    lateinit var session: CqlSession

    fun connect(keyspace: String) {
        println("Connecting to Cassandra at $host:$port")
        session = CqlSession.builder()
            .addContactPoint(InetSocketAddress(host, port))
            .withLocalDatacenter(datacenter)
            .withKeyspace(keyspace)
            .build()
        println("Connected to Cassandra!")
    }

    /*
    fun initializeKeyspace(keyspace: String) {
        session = CqlSession.builder()
            .addContactPoint(InetSocketAddress(host, port))
            .withLocalDatacenter(datacenter)
            .withKeyspace("system")
            .build()

        try {
            println("Creating keyspace '$keyspace' if not exists...")
            session.execute(
                """
                    CREATE KEYSPACE IF NOT EXISTS $keyspace
                    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
                """.trimIndent()
            )
            println("Keyspace '$keyspace' ensured.")
        } catch (e: Exception) {
            println("Error creating keyspace: ${e.message}")
            e.printStackTrace()
        } finally {
            session.close()
        }

    }

    fun initializeTable(keyspace: String) {
        session = CqlSession.builder()
            .addContactPoint(InetSocketAddress(host, port))
            .withLocalDatacenter(datacenter)
            .withKeyspace(keyspace)
            .withConfigLoader(
                DriverConfigLoader.programmaticBuilder()
                    .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(5))
                    .build()
            )
            .build()

        try {
            println("Creating table 'product' in keyspace '$keyspace' if not exists...")
            session.execute(
                """
                    CREATE TABLE IF NOT EXISTS product (
                        id UUID PRIMARY KEY,
                        name text,
                        price double
                    );
                """.trimIndent()
            )
            println("Table 'product' initialized in keyspace '$keyspace'.")
        } catch (e: Exception) {
            println("Error creating table: ${e.message}")
            e.printStackTrace()
        } finally {
            session.close()
        }
    }

     */

    override fun close() {
        if (::session.isInitialized && !session.isClosed) {
            println("Closing Cassandra session...")
            session.close()
        }
    }
}