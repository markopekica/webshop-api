package webshop.database

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.config.DefaultDriverOption
import com.datastax.oss.driver.api.core.config.DriverConfigLoader
import com.datastax.oss.driver.api.querybuilder.schema.AlterKeyspace
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
    // working

    fun initializeKeyspace(keyspace: String) {
        session = CqlSession.builder()
            .addContactPoint(InetSocketAddress(host, port))
            .withLocalDatacenter(datacenter)
            .build()

        session.execute(
            """
                CREATE KEYSPACE IF NOT EXISTS $keyspace
                WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
            """.trimIndent()
        )
    }

    fun initializeTable(keyspace: String) {
        println("Creating table if it doesn't exist...")

        session.execute(
            """
                CREATE TABLE IF NOT EXISTS $keyspace.product (
                    id UUID PRIMARY KEY,
                    name text,
                    price double
                )
            """.trimIndent()
        )
    }
*/


    override fun close() {
        if (::session.isInitialized && !session.isClosed) {
            println("Closing Cassandra session...")
            session.close()
        }
    }
}