package net.projecttl.lobby.type

enum class DatabaseType(val driver: String) {
	SQLITE("org.sqlite.JDBC"),
	MARIADB("org.mariadb.jdbc.Driver");
}