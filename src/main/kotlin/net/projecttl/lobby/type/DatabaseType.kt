package net.projecttl.lobby.type

enum class DatabaseType(val driver: String) {
	SQLITE("org.sqlite.JDBC"),
	MYSQL("org.mysql.cj.Driver"),
	MARIADB("org.mariadb.jdbc.Driver"),
	POSTGRESQL("org.postgresql.Driver");
}