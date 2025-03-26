import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.failureConditions.failOnMetricChange
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
    name = "Spring Pet Clinic"
    description = "Spring Pet Clinic Sample Application"

    vcsRoot(HttpsGithubComKilina0springPetclinicGitRefsHeadsMain)

    buildType(Build)
}

object Build : BuildType({
    name = "Build"
    description = "Build and test Spring Pet Clinic application"

    vcs {
        root(HttpsGithubComKilina0springPetclinicGitRefsHeadsMain)
    }

    steps {
        maven {
            name = "Clean"
            goals = "clean"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }

        maven {
            name = "Compile"
            goals = "compile"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }

        maven {
            name = "Test"
            goals = "test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }

        maven {
            name = "Package"
            goals = "package"
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

    params {
        param("env.MAVEN_OPTS", "-Xmx1024m")
        param("teamcity.tool.maven", "maven3_6")
    }

    requirements {
        noLessThan("teamcity.agent.jvm.version", "17")
    }

    artifactRules = """
        target/*.jar
        target/*.war
    """.trimIndent()
})

object HttpsGithubComKilina0springPetclinicGitRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/kilina0/spring-petclinic.git#refs/heads/main"
    url = "https://github.com/kilina0/spring-petclinic.git"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
})
