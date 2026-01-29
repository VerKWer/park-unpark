plugins {
	application
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	testImplementation(libs.junit.jupiter)
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
	mainClass = "ParkUnpark"
}

tasks.withType<Test> {
	useJUnitPlatform()
	outputs.upToDateWhen { false }
	testLogging {
		showStandardStreams = true
	}
}
