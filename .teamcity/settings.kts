import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.failureConditions.failOnMetricChange
import jetbrains.buildServer.configs.kotlin.triggers.finishBuildTrigger
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
    description = "Spring PetClinic Application"

    vcsRoot(HttpsGithubComKilina0springPetclinicGitRefsHeadsMain)

    buildType(Build)
    buildType(Test)
    buildType(CodeQuality)
    buildType(Deploy)

    buildTypesOrder = arrayListOf(Build, Test, CodeQuality, Deploy)
}

object Build : BuildType({
    name = "Build"
    description = "Build Spring PetClinic Application"

    artifactRules = """
        target/*.jar => build
        target/classes => classes
    """.trimIndent()

    vcs {
        root(HttpsGithubComKilina0springPetclinicGitRefsHeadsMain)
    }

    steps {
        maven {
            name = "Clean & Package"
            goals = "clean package"
            runnerArgs = "-Dmaven.test.failure.ignore=true -Dmaven.test.skip=true"
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
    description = "Run tests for Spring PetClinic Application"

    artifactRules = """
        target/surefire-reports => reports.zip
        target/site/jacoco => jacoco-report.zip
    """.trimIndent()

    vcs {
        root(HttpsGithubComKilina0springPetclinicGitRefsHeadsMain)
    }

    steps {
        maven {
            name = "Run Tests with JaCoCo"
            goals = "clean test jacoco:report"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
            branchFilter = "+:*"
            enabled = false
        }
        finishBuildTrigger {
            buildType = "${Build.id}"
            successfulOnly = true
        }
    }

    dependencies {
        snapshot(Build) {
            onDependencyFailure = FailureAction.FAIL_TO_START
        }
    }

    features {
        perfmon {
        }
    }

    failureConditions {
        failOnMetricChange {
            metric = BuildFailureOnMetric.MetricType.TEST_COUNT
            threshold = 10
            units = BuildFailureOnMetric.MetricUnit.PERCENTS
            comparison = BuildFailureOnMetric.MetricComparison.LESS
            compareTo = build {
                buildRule = lastSuccessful()
            }
        }
    }
})

object CodeQuality : BuildType({
    name = "Code Quality"
    description = "Run code quality checks"

    artifactRules = """
        target/checkstyle-result.xml => checkstyle.zip
    """.trimIndent()

    vcs {
        root(HttpsGithubComKilina0springPetclinicGitRefsHeadsMain)
    }

    steps {
        maven {
            name = "Run Checkstyle"
            goals = "checkstyle:checkstyle"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        finishBuildTrigger {
            buildType = "${Test.id}"
            successfulOnly = true
        }
    }

    dependencies {
        snapshot(Test) {
            onDependencyFailure = FailureAction.FAIL_TO_START
        }
    }

    features {
        perfmon {
        }
    }
})

object Deploy : BuildType({
    name = "Deploy"
    description = "Deploy Spring PetClinic Application"

    type = BuildTypeSettings.Type.DEPLOYMENT
    enablePersonalBuilds = false
    maxRunningBuilds = 1

    vcs {
        root(HttpsGithubComKilina0springPetclinicGitRefsHeadsMain)
    }

    steps {
        script {
            name = "Prepare Deployment"
            scriptContent = """
                echo "Preparing deployment environment..."
                mkdir -p deploy
            """.trimIndent()
        }

        script {
            name = "Deploy Application"
            scriptContent = """
                echo "Deploying Spring PetClinic application..."
                cp %teamcity.build.checkoutDir%/target/*.jar deploy/
                echo "Application deployed successfully!"
            """.trimIndent()
        }
    }

    dependencies {
        snapshot(CodeQuality) {
            onDependencyFailure = FailureAction.FAIL_TO_START
        }
        artifacts(Build) {
            buildRule = lastSuccessful()
            artifactRules = "build/*.jar => target/"
        }
    }
})

object HttpsGithubComKilina0springPetclinicGitRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/kilina0/spring-petclinic.git#refs/heads/main"
    url = "https://github.com/kilina0/spring-petclinic.git"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
})
