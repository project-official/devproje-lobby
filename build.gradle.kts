import org.gradle.toolchains.foojay.match
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "2.0.0"
}

group = "net.projecttl"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib"))
	implementation(kotlin("reflect"))
	implementation("org.jetbrains.exposed:exposed-core:0.50.1")
	implementation("org.jetbrains.exposed:exposed-jdbc:0.50.1")
	implementation("net.minestom:minestom-snapshots:8ea7760e6a")
	implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.23.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

tasks {
	withType<KotlinCompile> {}

	processResources {
		filesMatching("config.properties") {
			expand(project.properties)
		}
	}

	test {
		useJUnitPlatform()
	}
}

kotlin {
	jvmToolchain(21)
}