group = "net.tassia"
version = "1.0.0-BETA-B1"

plugins {
	java
}

repositories {
	mavenCentral()
	maven("https://oss.sonatype.org/content/repositories/snapshots")
	maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
	compileOnly("org.spigotmc:spigot-api:1.16.1-R0.1-SNAPSHOT")

	testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.getByName<Test>("test") {
	useJUnitPlatform()
}

tasks.getByName<ProcessResources>("processResources") {
	filter { it.replace("#::VERSION::#", project.version.toString()) }
}
