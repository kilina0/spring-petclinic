import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.failureConditions.failOnMetricChange
import jetbrains.buildServer.configs.kotlin.requirements.exists
import jetbrains.buildServer.configs.kotlin.requirements.minRequiredVersion
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

    vcsRoot(HttpsGithubComKilina0springPetclinicGitRefsHeadsMain)

    buildType(Build)
}

object Build : BuildType({
    name = "Build"

    params {
        param("env.JAVA_HOME", "%env.JDK_17%")
        param("java.required.version", "17")
    }

    vcs {
        root(HttpsGithubComKilina0springPetclinicGitRefsHeadsMain)
    }

    requirements {
        exists("env.JAVA_HOME")
        minRequiredVersion("java.required.version", "17")
    }

    steps {
        maven {
            name = "Generate CSS Resources"
            goals = "generate-resources"
            runnerArgs = "-P css"
            jdkHome = "%env.JAVA_HOME%"
        }

        maven {
            name = "Run Tests"
            goals = "test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            jdkHome = "%env.JAVA_HOME%"
        }

        maven {
            name = "Clean and Package"
            goals = "clean package"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            jdkHome = "%env.JAVA_HOME%"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }

    failureConditions {
        failOnMetricChange {
            metric = BuildFailureOnMetric.MetricType.TEST_FAILED_COUNT
            threshold = 0
            units = BuildFailureOnMetric.MetricUnit.DEFAULT_UNIT
            comparison = BuildFailureOnMetric.MetricComparison.MORE
            compareTo = build {
                buildRule = lastSuccessful()
            }
        }
    }

    artifactRules = "target/*.jar"
})

object HttpsGithubComKilina0springPetclinicGitRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/kilina0/spring-petclinic.git#refs/heads/main"
    url = "https://github.com/kilina0/spring-petclinic.git"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
})
