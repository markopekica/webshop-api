package webshop.database

import com.datastax.oss.driver.api.core.CqlSession
import java.net.InetSocketAddress

class CassandraConnector(private val host: String, private val port: Int) {
    lateinit var session: CqlSession

    fun connect(keyspace: String) {
        println("Connecting to Cassandra at $host:$port")
        session = CqlSession.builder()
            .addContactPoint(InetSocketAddress(host, port))
            .withLocalDatacenter("datacenter1")
            .withKeyspace(keyspace)
            .build()
        println("Connected to Cassandra!")
    }

    fun close() {
        session.close()
    }
}