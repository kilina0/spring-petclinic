import jetbrains.buildServer.configs.kotlin.v2023_2.*
import jetbrains.buildServer.configs.kotlin.v2023_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2023_2.ui.*

version = "2023.2"

// Project definition
project {
    description = "TeamCity Project for Maven Build and Testing with Spring Petclinic"

    // Define the build configuration
    buildType(BuildMavenApp)
}

// Build configuration for Maven
object BuildMavenApp : BuildType({
    name = "Build and Test Maven App"

    vcs {
        root(DslContext.settingsRoot) // Automatically uses the VCS Root of your repository
    }

    steps {
        // Step 1: Run Maven Build
        maven {
            goals = "clean package"
            runnerArgs = "-DskipTests=false" // Ensures tests are executed
            pomLocation = "pom.xml"
            jdkHome = "%env.JDK_21%" // Use Java 21 (replace with appropriate JDK environment variable)
        }

        // Optional: Step to build the Docker image with Spring Boot plugins
        maven {
            goals = "spring-boot:build-image"
            runnerArgs = "-DskipTests=true"
            pomLocation = "pom.xml"
            jdkHome = "%env.JDK_21%" // Replace with correct JDK environment variable if needed
            enabled = false // Disable by default
        }
    }

    // Define triggers
    triggers {
        vcs {
            branchFilter = "+:<default>" // Only trigger for the default branch
        }
    }

    // Define artifacts (e.g., jar from Maven's `target` directory)
    artifactRules = "target/*.jar => build-output"

    // Requirements for agent
    requirements {
        contains("teamcity.agent.jvm.os.name", "Windows") // Ensures it runs on Windows agents
        equals("teamcity.agent.work.dir", "%teamcity.build.workingDirs.root%") // Ensure predefined working directory
    }
})