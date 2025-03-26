import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the
'inspect' command in 'TeamCity' tool window.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2023.11"

project {
    description = "Spring PetClinic Sample Application"

    buildType {
        id("SpringPetclinicBuild")
        name = "Build and Test"

        vcs {
            root(DslContext.settingsRoot)
        }

        steps {
            maven {
                name = "Clean, Build and Test"
                goals = "clean package"
                runnerArgs = "-Dmaven.test.failure.ignore=true"
                mavenVersion = defaultProvidedVersion()
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
        }

        requirements {
            exists("env.JDK_17")
        }

        failureConditions {
            executionTimeoutMin = 15
        }

        params {
            param("teamcity.ui.settings.readOnly", "true")
        }
    }
}
