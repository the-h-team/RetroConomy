import org.gradle.api.Project

val Project.properName: String
    get() = findProperty("properName") as String