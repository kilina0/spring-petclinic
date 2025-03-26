# TeamCity Configuration for Spring PetClinic - Summary

## Overview

This directory contains configuration files and instructions for setting up TeamCity to build and test the Spring PetClinic application using Maven. The following files are provided:

1. `README.md` - Detailed instructions for manual TeamCity configuration
2. `settings.kts` - TeamCity Kotlin DSL configuration file (needs to be processed by TeamCity)
3. `settings.kts.template` - Template file with additional examples and comments

## Quick Start

### Option 1: Manual Configuration

Follow the step-by-step instructions in `README.md` to manually configure your TeamCity project through the web interface.

### Option 2: Kotlin DSL Configuration

1. In TeamCity, create a new project
2. Go to **Versioned Settings**
3. Select **Kotlin** format
4. Point to the repository containing this `.teamcity` directory
5. TeamCity will automatically import the configuration

## Build Pipeline

The configuration sets up a build pipeline with the following steps:

1. **Compile CSS** - Compiles SCSS files to CSS
2. **Compile and Test** - Builds the application and runs tests
3. **Package Application** - Creates the final JAR file

## Additional Features

The configuration also includes:

- Artifact publishing for JAR files and test coverage reports
- VCS trigger for automatic builds on code changes
- Requirements for JDK 17
- Optional configurations for database integration tests and Docker image builds

## Customization

You can customize the configuration by:

1. Modifying the build steps in the TeamCity web interface
2. Editing the `settings.kts` file if using the Kotlin DSL approach
3. Adding additional build configurations for specific needs (e.g., deployment)

## Troubleshooting

If you encounter issues with the Kotlin DSL configuration:

1. Make sure TeamCity has the proper version to support the DSL syntax
2. Check that the build agent has JDK 17 installed and configured
3. Verify that Maven is properly installed on the build agent

For more detailed information, refer to the [TeamCity documentation](https://www.jetbrains.com/help/teamcity/teamcity-documentation.html).
