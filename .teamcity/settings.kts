import jetbrains.buildServer.configs.kotlin.v2023_2.* // Make sure this matches your TeamCity version
import jetbrains.buildServer.configs.kotlin.v2023_2.buildSteps.maven

version = "2023.2" // Matches the TeamCity version, update if needed

project {
    description = "TeamCity Project for Building and Testing Maven Application"

    // VCS Root definition
    vcsRoot(DslContext.settingsRoot)

    // Attach the build configuration
    buildType(BuildAndTestMavenApp)
}

// Build and Test configuration
object BuildAndTestMavenApp : BuildType({
    name = "Build and Test"

    // Link to VCS Root
    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        // Step 1: Maven Clean and Build with Tests
        maven {
            name = "Maven Build with Tests"
            goals = "clean package"
            runnerArgs = "-DskipTests=false" // Ensure tests are executed
            jdkHome = "%env.JDK_21%" // Replace this with the appropriate JDK path
        }
    }

    triggers {
        // Trigger builds on VCS changes
        vcs {
            branchFilter = "+:<default>" // Default branch trigger
        }
    }

    artifactRules = "target/*.jar => build-output" // Save JAR files from the `target` folder

    requirements {
        // Ensure the build agent has the required JDK installed
        exists("env.JDK_21")
    }
})