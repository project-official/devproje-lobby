package net.projecttl.lobby.service

import net.projecttl.lobby.type.ServerDataType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

data class ServerObj(
	val name: String,
	val description: String = "",
	val head: String = "DEV_IO",
	val server: String
)

data class Server(
	val id: UUID,
	val obj: ServerObj,
)

class ServerListService(database: Database) {
	object ServerListTable : Table("server_list") {
		val id = uuid("id")
		val name = varchar("name", 50)
		val description = text("description").nullable()
		val head = varchar("head", 50).default("DEV_IO")
		val server = varchar("server", 30).uniqueIndex()

		override val primaryKey = PrimaryKey(id, name = "PK_ServerList_ID")
	}

	init {
		transaction(database) {
			SchemaUtils.create(ServerListTable)
		}
	}

	suspend fun <T> dbQuery(block: suspend () -> T) = newSuspendedTransaction { block() }

	suspend fun find(id: UUID) = dbQuery {
		ServerListTable.selectAll().where(ServerListTable.id eq id).map {
			Server(
				id = it[ServerListTable.id],
				obj = ServerObj(
					name = it[ServerListTable.name],
					description = it[ServerListTable.description] ?: "",
					head = it[ServerListTable.head],
					server = it[ServerListTable.server]
				)
			)
		}.singleOrNull()
	}

	suspend fun findAll() = dbQuery {
		ServerListTable.selectAll().map {
			Server(
				id = it[ServerListTable.id],
				obj = ServerObj(
					name = it[ServerListTable.name],
					description = it[ServerListTable.description] ?: "",
					head = it[ServerListTable.head],
					server = it[ServerListTable.server]
				)
			)
		}.toList()
	}

	suspend fun register(data: ServerObj) = dbQuery {
		val uuid = UUID.randomUUID()
		ServerListTable.insert {
			it[id] = uuid
			it[name] = data.name
			it[description] = data.description
			it[head] = data.head
			it[server] = data.server
		}[ServerListTable.id]
	}

	suspend fun unregister(id: UUID) = dbQuery {
		ServerListTable.deleteWhere { ServerListTable.id eq id }
	}

	suspend fun update(id: UUID, type: ServerDataType, content: () -> String) = dbQuery {
		ServerListTable.update({ ServerListTable.id eq id }) {
			when (type) {
				ServerDataType.NAME        -> it[name] = content()
				ServerDataType.DESCRIPTION -> it[description] = if (content() == "") null else content()
				ServerDataType.HEAD_DATA   -> it[head] = if (content() == "") "DEV_IO" else content()
				ServerDataType.SERVER      -> it[server] = content()
			}
		}
	}
}