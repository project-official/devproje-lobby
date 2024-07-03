import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "2.0.0"
	kotlin("plugin.serialization") version "2.0.0"
	id("com.github.johnrengelman.shadow") version "8.1.1"
	application
}

group = "net.projecttl"
version = "1.0-SNAPSHOT"

kotlin {
	compilerOptions {
		apiVersion.set(KotlinVersion.KOTLIN_2_0)
		languageVersion.set(KotlinVersion.KOTLIN_2_0)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib"))
	implementation(kotlin("reflect"))
	implementation("org.xerial:sqlite-jdbc:3.46.0.0")
	implementation("org.postgresql:postgresql:42.7.3")
	implementation("com.mysql:mysql-connector-j:9.0.0")
	implementation("org.jetbrains.exposed:exposed-core:0.50.1")
	implementation("org.jetbrains.exposed:exposed-jdbc:0.50.1")
	implementation("net.minestom:minestom-snapshots:8ea7760e6a")
	implementation("org.mariadb.jdbc:mariadb-java-client:3.4.0")
	implementation("net.kyori:adventure-text-minimessage:4.17.0")
	implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

application {
	mainClass.set("net.projecttl.lobby.MainKt")
}

tasks {
	withType<KotlinCompile> {
		compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
	}

	processResources {
		filesMatching("config.properties") {
			expand(project.properties)
		}
	}

	shadowJar {
		archiveBaseName.set(project.name)
		archiveClassifier.set("")
		archiveVersion.set("")

		manifest.attributes(Pair("Main-Class", "net.projecttl.lobby.MainKt"))
	}

	test {
		useJUnitPlatform()
	}
}
