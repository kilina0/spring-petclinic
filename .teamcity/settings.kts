// TeamCity Kotlin DSL for Spring PetClinic
// This is a simplified version of the TeamCity configuration for building and testing the Spring PetClinic application

// Note: This file is meant to be used with TeamCity's Kotlin DSL feature.
// When imported into TeamCity, it will create a build configuration for the Spring PetClinic application.

// To use this file:
// 1. Create a new project in TeamCity
// 2. Go to "Versioned Settings"
// 3. Select "Kotlin" as the format
// 4. Point to the repository containing this file
// 5. TeamCity will automatically import the configuration

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
 * Spring PetClinic TeamCity Build Configuration
 * 
 * This configuration sets up a build pipeline for the Spring PetClinic application
 * using Maven for building and testing.
 */

version = "2023.11"

project {
    // Project details
    uuid = "SpringPetClinic"
    id("SpringPetClinic")
    name = "Spring PetClinic"
    description = "Spring PetClinic Sample Application"

    // VCS Root configuration
    val vcsRoot = GitVcsRoot {
        id("SpringPetClinicVcs")
        name = "spring-petclinic git repository"
        url = "https://github.com/spring-projects/spring-petclinic.git"
        branch = "refs/heads/main"
        branchSpec = "+:refs/heads/*"
    }
    vcsRoot(vcsRoot)

    // Main build configuration
    buildType {
        id("SpringPetClinicBuild")
        name = "Build and Test"

        vcs {
            root(vcsRoot)
        }

        // Build steps
        steps {
            // Step 1: Compile CSS
            maven {
                name = "Compile CSS"
                goals = "package"
                runnerArgs = "-P css -DskipTests"
                mavenVersion = auto()
                jdkHome = "%env.JDK_17_HOME%"
            }

            // Step 2: Compile and run tests
            maven {
                name = "Compile and Test"
                goals = "clean verify"
                runnerArgs = "-Dmaven.test.failure.ignore=true"
                mavenVersion = auto()
                jdkHome = "%env.JDK_17_HOME%"
            }

            // Step 3: Package application
            maven {
                name = "Package Application"
                goals = "package"
                runnerArgs = "-DskipTests"
                mavenVersion = auto()
                jdkHome = "%env.JDK_17_HOME%"
            }
        }

        // Triggers
        triggers {
            vcs {
                branchFilter = "+:*"
            }
        }

        // Features
        features {
            perfmon {}
        }

        // Artifact rules
        artifactRules = """
            target/*.jar => build
            target/site/jacoco => coverage-report
        """.trimIndent()

        // Requirements
        requirements {
            exists("env.JDK_17_HOME")
        }
    }
}
