package webshop.database

import com.datastax.oss.driver.api.core.CqlSession
import java.net.InetSocketAddress

class CassandraConnector(private val host: String, private val port: Int) {
    lateinit var session: CqlSession

    fun connect(keyspace: String? = null) {
        session = CqlSession.builder()
            .addContactPoint(InetSocketAddress(host, port))
            .withLocalDatacenter("datacenter1")
            .apply { if (keyspace != null) withKeyspace(keyspace) }
            .build()
    }

    fun close() {
        session.close()
    }
}