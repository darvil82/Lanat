plugins {
	java
}

group = "darvil"
version = "INDEV"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains:annotations:23.0.0")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

java {
	withJavadocJar()
	withSourcesJar()
}

tasks.named<Test>("test") {
	useJUnitPlatform()
	filter {
		includeTestsMatching("Test*")
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.encoding = "UTF-8"
}

tasks.withType<Javadoc>().configureEach {
	options.encoding = "UTF-8"
}

tasks.configureEach {
	if (name != "clean") {
		dependsOn("clean")
	}
}