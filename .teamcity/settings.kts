import jetbrains.buildServer.configs.kotlin.v2023_2.* // Update this if you're using a different version
import jetbrains.buildServer.configs.kotlin.v2023_2.buildSteps.maven

version = "2023.2" // Ensure the version matches your TeamCity installation

project {
    description = "TeamCity Project for Spring PetClinic Maven Build and Test"

    // Add the VCS Root (ensure the repository URL is correctly set in TeamCity UI)
    vcsRoot(DslContext.settingsRoot)

    buildType(BuildMavenApp)
}

// Build configuration for Maven build and testing
object BuildMavenApp : BuildType({
    name = "Build and Test Maven App"

    vcs {
        root(DslContext.settingsRoot) // Links VCS Root
    }

    steps {
        // Step 1: Maven Clean and Package
        maven {
            name = "Maven Build and Test"
            goals = "clean package"
            runnerArgs = "-DskipTests=false" // Ensure tests are run
            jdkHome = "%env.JDK_21%" // Ensure this JDK environment parameter is set in TeamCity
        }
    }

    triggers {
        // VCS trigger: automatic builds on commits to the default branch
        vcs {
            branchFilter = "+:<default>" // Ensures tracking the VCS default branch
        }
    }

    artifactRules = """
        target/*.jar => build-output
    """

    requirements {
        // Ensures the build runs on an agent with the correct JDK version
        exists("env.JDK_21")
    }
})
