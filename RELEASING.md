Take the following steps to release a new version of the library to Bintray (jCenter):

1. Run all unit tests.
2. Make sure all changes are committed and pushed to the origin repository.
3. Make sure the master branch is up-to-date and selected.
4. From the terminal, run the Maven Release plugin, as follows:
    a. `mvn release:prepare` (note: you will be asked which version you are releasing)
    b. `mvn release:perform`
5. Publish the uploaded artifacts on Bintray:
    a. Visit the project on [Bintray](https://bintray.com/cookingfox/maven/lapasse-java).
    b. Click the "Publish" link for the notification that says "You have X unpublished items".
    c. Publish to Maven Central by clicking the "Maven Central" link.
6. Describe the changes of this release in the [CHANGELOG](../CHANGELOG.md).
7. Update the version number in the [README](../README.md).
8. Add the release notes on GitHub (usually the same as CHANGELOG description).
