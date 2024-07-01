package net.projecttl.lobby.service

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class TabListService {
	object TabListTable : Table("tab_list") {
		val index = integer("index")
		val content = text("content")
		val header = bool("header").default(true)
	}

	init {
		transaction {
			SchemaUtils.create(TabListTable)
		}
	}

	suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }

	private suspend fun index(header: Boolean = true): Int = dbQuery {
		TabListTable.select(TabListTable.index).where { TabListTable.header eq header }.last()[TabListTable.index]
	}

	private suspend fun refresh(start: Int, n: Int) = dbQuery {
		for (i in start..n) {
			TabListTable.update({ TabListTable.index eq i + 1 }) { it[index] = i }
		}
	}

	private suspend fun exist(index: Int, header: Boolean = true): Boolean = dbQuery {
		val res = TabListTable.select(TabListTable.index).where { TabListTable.index eq index and(TabListTable.header eq header) }.singleOrNull()
		return@dbQuery res != null
	}

	suspend fun getHeader(): String = dbQuery {
		val content = StringBuilder()
		TabListTable.select(TabListTable.content).where { TabListTable.header eq true }.orderBy(TabListTable.index).map {
			content.append(String.format("%s\n", it[TabListTable.content]))
		}

		return@dbQuery content.toString()
	}

	suspend fun getFooter(): String = dbQuery {
		val content = StringBuilder()
		TabListTable.select(TabListTable.content).where { TabListTable.header eq false }.orderBy(TabListTable.index).map {
			content.append(String.format("%s\n", it[TabListTable.content]))
		}

		return@dbQuery content.toString()
	}

	suspend fun setHeader(index: Int, content: String): Unit = dbQuery {
		TabListTable.update({ TabListTable.index eq index and(TabListTable.header eq true) }) {
			it[TabListTable.content] = content
		}
	}

	suspend fun setFooter(index: Int, content: String): Unit = dbQuery {
		TabListTable.update({ TabListTable.index eq index and(TabListTable.header eq false) }) {
			it[TabListTable.content] = content
		}
	}

	suspend fun addHeader(content: String): Unit = dbQuery {
		val index = index()
		if (exist(index)) {
			throw IllegalStateException("$index line current exist")
		}

		TabListTable.insert {
			it[this.content] = content
			it[this.index] = index
		}
	}

	suspend fun addFooter(content: String): Unit = dbQuery {
		val index = index()
		if (exist(index, false)) {
			throw IllegalStateException("$index line current exist")
		}

		TabListTable.insert {
			it[this.content] = content
			it[this.header] = false
			it[this.index] = index
		}
	}

	suspend fun delHeader(index: Int, empty: Boolean = false): Unit = dbQuery {
		if (!exist(index)) {
			throw NullPointerException("$index line current not exist")
		}

		if (empty) {
			TabListTable.update({ TabListTable.index eq index and (TabListTable.header eq true) }) {
				it[content] = "\n"
			}

			return@dbQuery
		}

		TabListTable.deleteWhere { TabListTable.index eq index and(header eq true) }
		refresh(index, index())
	}

	suspend fun delFooter(index: Int, empty: Boolean = false): Unit = dbQuery {
		if (!exist(index)) {
			throw NullPointerException("$index line current not exist")
		}

		if (empty) {
			TabListTable.update({ TabListTable.index eq index and (TabListTable.header eq false) }) {
				it[content] = "\n"
			}

			return@dbQuery
		}

		TabListTable.deleteWhere { TabListTable.index eq index and(header eq false) }
	}
}