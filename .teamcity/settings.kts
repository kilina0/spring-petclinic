import jetbrains.buildServer.configs.kotlin.v2023_2.* // Ensure correct version import
import jetbrains.buildServer.configs.kotlin.v2023_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2023_2.Project
import jetbrains.buildServer.configs.kotlin.v2023_2.buildSteps.maven

version = "2023.2"

project {
    description = "TeamCity Project for Spring PetClinic: Build & Test"

    vcsRoot(DslContext.settingsRoot)

    buildType(BuildMavenApp) // Add a build configuration to the project
}

// Define the Maven build type
object BuildMavenApp : BuildType({
    name = "Build and Test Maven App"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            name = "Maven Build and Test"
            goals = "clean package"
            runnerArgs = "-DskipTests=false"
            jdkHome = "%env.JDK_21%" // Environmental variable for JDK 21 installation path
        }
    }

    triggers {
        vcs {
            branchFilter = "+:<default>" // Only trigger builds for the default branch
        }
    }

    artifactRules = "target/*.jar => build-output"

    requirements {
        exists("env.JDK_21") // Verify that JDK 21 is installed on the agent
    }
})
