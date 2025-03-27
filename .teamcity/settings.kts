import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2024.12"

project {
    description = "Spring PetClinic Sample Application"

    vcsRoot(HttpsGithubComKilina0springPetclinicGitRefsHeadsMain)

    buildType(Build)
    buildType(Test)
    buildType(BuildWithCSS)
    
    // Define build chain
    buildType(BuildChain)
}

object Build : BuildType({
    name = "Build"
    description = "Builds the Spring PetClinic application"

    artifactRules = "target/*.jar"

    vcs {
        root(HttpsGithubComKilina0springPetclinicGitRefsHeadsMain)
    }

    steps {
        maven {
            name = "Maven Build"
            goals = "clean package"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
            branchFilter = "+:*"
        }
    }

    features {
        perfmon {
        }
    }
})

object Test : BuildType({
    name = "Test"
    description = "Runs tests for the Spring PetClinic application"

    artifactRules = """
        target/surefire-reports => reports.zip
        target/site/jacoco => jacoco-report.zip
    """.trimIndent()

    vcs {
        root(HttpsGithubComKilina0springPetclinicGitRefsHeadsMain)
    }

    steps {
        maven {
            name = "Run Tests"
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
            branchFilter = "+:*"
        }
    }

    features {
        perfmon {
        }
    }
    
    dependencies {
        snapshot(Build) {
            reuseBuilds = ReuseBuilds.SUCCESSFUL
        }
    }
})

object BuildWithCSS : BuildType({
    name = "Build with CSS"
    description = "Builds the Spring PetClinic application with CSS compilation"

    artifactRules = "target/*.jar"

    vcs {
        root(HttpsGithubComKilina0springPetclinicGitRefsHeadsMain)
    }

    steps {
        maven {
            name = "Maven Build with CSS"
            goals = "clean package"
            runnerArgs = "-P css -Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
            branchFilter = "+:*"
        }
    }

    features {
        perfmon {
        }
    }
    
    dependencies {
        snapshot(Test) {
            reuseBuilds = ReuseBuilds.SUCCESSFUL
        }
    }
})

object BuildChain : BuildType({
    name = "Build Chain"
    description = "Runs the complete build chain: Build -> Test -> Build with CSS"
    
    type = BuildTypeSettings.Type.COMPOSITE
    
    vcs {
        root(HttpsGithubComKilina0springPetclinicGitRefsHeadsMain)
    }
    
    dependencies {
        snapshot(Build) {
            reuseBuilds = ReuseBuilds.SUCCESSFUL
        }
        snapshot(Test) {
            reuseBuilds = ReuseBuilds.SUCCESSFUL
        }
        snapshot(BuildWithCSS) {
            reuseBuilds = ReuseBuilds.SUCCESSFUL
        }
    }
})

object HttpsGithubComKilina0springPetclinicGitRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/kilina0/spring-petclinic.git#refs/heads/main"
    url = "https://github.com/kilina0/spring-petclinic.git"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
})
