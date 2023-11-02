plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

publishing {
    publications.create<MavenPublication>("maven") {
        pom {
            artifactId = properName // FIXME remove asap; here for parity with maven build
            name.set(properName)
            description = project.description!!
        }
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}