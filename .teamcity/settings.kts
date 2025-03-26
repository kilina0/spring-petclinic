import jetbrains.buildServer.configs.kotlin.v2019_2.*

version = "2019.2"

project {
    buildType(Build)
    buildType(Test)
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "Build with Maven"
            scriptContent = "./mvnw clean package"
        }
    }

    triggers {
        vcs {
            branchFilter = "+:<default>"
        }
    }
})

object Test : BuildType({
    name = "Test"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "Run Tests with Maven"
            scriptContent = "./mvnw test"
        }
    }

    dependencies {
        snapshot(Build) {
            onDependencyFailure = FailureAction.FAIL_TO_START
        }
    }
})
