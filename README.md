# LaPasse for Java

CQRS and Redux inspired library for Java.

LaPasse requires at minimum Java 7.

[![Build Status](https://travis-ci.org/cookingfox/lapasse-java.svg?branch=master)](https://travis-ci.org/cookingfox/lapasse-java)
[![Javadocs](http://www.javadoc.io/badge/com.cookingfox/lapasse-java.svg)](http://www.javadoc.io/doc/com.cookingfox/lapasse-java)

### _Note: proper documentation is in the works!_

## Download

[![Download](https://api.bintray.com/packages/cookingfox/maven/lapasse-java/images/download.svg)](https://bintray.com/cookingfox/maven/lapasse-java/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.cookingfox/lapasse/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.cookingfox/lapasse)

The distribution is hosted on [Bintray](https://bintray.com/cookingfox/maven/lapasse-java/view).
To include the package in your projects, you can add the jCenter repository.

### Gradle

Add jCenter to your `repositories` block (not necessary for Android - jCenter is the default
repository):

```groovy
repositories {
    jcenter()
}
```

and add the project to the `dependencies` block in your `build.gradle`:

```groovy
dependencies {
    compile 'com.cookingfox:lapasse:0.3.1'
}
```

### Maven

Add jCenter to your repositories in `pom.xml` or `settings.xml`:

```xml
<repositories>
    <repository>
        <id>jcenter</id>
        <url>http://jcenter.bintray.com</url>
    </repository>
</repositories>
```

and add the project to the `dependencies` block in your `pom.xml`:

```xml
<dependency>
    <groupId>com.cookingfox</groupId>
    <artifactId>lapasse</artifactId>
    <version>0.3.1</version>
</dependency>
```

### Rx extension

There is also an [RxJava](https://github.com/ReactiveX/RxJava) extension for the library. To
download it, replace `lapasse` with `lapasse-rx` in the above dependency declarations.
