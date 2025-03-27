import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.buildFeatures.notifications

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
    description = "Build and test Spring Petclinic application"

    artifactRules = """
        target/*.jar => artifacts.zip
        target/*.war => artifacts.zip
    """.trimIndent()

    vcs {
        root(HttpsGithubComKilina0springPetclinicGitRefsHeadsMain)
    }

    steps {
        maven {
            name = "Compile and Test"
            goals = "clean package"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            jdkHome = "%env.JDK_17%"
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
        notifications {
            notifierSettings = slackNotifier {
                connection = "tc-gathering-group1"
            }
            branchFilter = "+:*"
            buildFailed = true
            buildFailedToStart = true
        }
    }
})

object HttpsGithubComKilina0springPetclinicGitRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/kilina0/spring-petclinic.git#refs/heads/main"
    url = "https://github.com/kilina0/spring-petclinic.git"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
})
