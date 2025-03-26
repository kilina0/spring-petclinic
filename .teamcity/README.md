# TeamCity Configuration for Spring PetClinic

This document provides instructions for setting up a TeamCity build configuration for the Spring PetClinic application using Maven.

## Prerequisites

1. TeamCity server (version 2023.11 or later recommended)
2. JDK 17 installed on the build agent
3. Maven installed on the build agent (or use TeamCity's bundled Maven)

## Manual Configuration

### 1. Create a New Project

1. In TeamCity, go to **Projects** and click **Create project**
2. Select **From a repository URL**
3. Enter the repository URL: `https://github.com/spring-projects/spring-petclinic.git` (or your fork)
4. Click **Proceed**

### 2. Configure Build Steps

Create the following build steps in your TeamCity project:

#### Step 1: Compile CSS

- **Runner type**: Maven
- **Step name**: Compile CSS
- **Goals**: `package`
- **Maven command line parameters**: `-P css -DskipTests`
- **Java Parameters**: Set JDK to Java 17

#### Step 2: Compile and Run Tests

- **Runner type**: Maven
- **Step name**: Compile and Test
- **Goals**: `clean verify`
- **Maven command line parameters**: `-Dmaven.test.failure.ignore=true`
- **Java Parameters**: Set JDK to Java 17

#### Step 3: Package Application

- **Runner type**: Maven
- **Step name**: Package Application
- **Goals**: `package`
- **Maven command line parameters**: `-DskipTests`
- **Java Parameters**: Set JDK to Java 17

### 3. Configure Artifacts

Add the following artifact paths:

```
target/*.jar => build
target/site/jacoco => coverage-report
```

### 4. Configure VCS Trigger

1. Go to **Triggers** and add a **VCS Trigger**
2. Configure it to trigger a build on each check-in

### 5. Configure Requirements

Add a requirement that the build agent must have JDK 17 installed:

- **Parameter name**: `env.JDK_17_HOME`
- **Condition**: exists

## Kotlin DSL Configuration

If you prefer to use TeamCity's Kotlin DSL, you can use the `settings.kts` file in this directory as a reference. To use it:

1. In TeamCity, go to your project settings
2. Select **Versioned Settings**
3. Choose **Kotlin** format
4. Point to the repository containing this file
5. TeamCity will automatically import the configuration

Note: The `settings.kts` file needs to be processed by TeamCity to resolve the DSL references.

## Build Steps Explanation

1. **Compile CSS**: This step compiles the SCSS files to CSS using the "css" Maven profile.
2. **Compile and Test**: This step compiles the application and runs all tests.
3. **Package Application**: This step packages the application into a JAR file.

## Additional Configuration Options

### Database Integration Tests

To run integration tests with different databases, you can add additional build configurations:

#### MySQL Integration Tests

- **Runner type**: Maven
- **Goals**: `verify`
- **Maven command line parameters**: `-P mysql`

#### PostgreSQL Integration Tests

- **Runner type**: Maven
- **Goals**: `verify`
- **Maven command line parameters**: `-P postgres`

### Docker Image Build

To build a Docker image, add a build step:

- **Runner type**: Maven
- **Goals**: `spring-boot:build-image`
- **Maven command line parameters**: `-DskipTests`
