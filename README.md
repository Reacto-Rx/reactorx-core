<img src="/assets/logo.png" width="100">

# reactor-android

![Build Status](https://circleci.com/gh/filipproch/reactor-android/tree/master.svg?style=shield)
![Latest Version](https://api.bintray.com/packages/filipproch/maven/reactor-android/images/download.svg)
[![codecov](https://codecov.io/gh/filipproch/reactor-android/branch/master/graph/badge.svg)](https://codecov.io/gh/filipproch/reactor-android)

Library for writing Android apps in a reactive way.

### [Documentation](http://reactor-android.site/)

## Add library to your project

Latest Version : ![Latest Version](https://api.bintray.com/packages/filipproch/maven/reactor-android/images/download.svg)

```groovy
dependencies {
    // Reactor Core
    compile 'cz.filipproch.lib:reactor-android:<VERSION>'
    // Reactor Extras
    compile 'cz.filipproch.lib:reactor-android-extras:<VERSION>'
}
```

### Snapshot builds

You can use Jitpack.io to obtain latest SNAPSHOT builds

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

**dev** - the latest commit on `dev` branch

```groovy
dependencies {
    // Reactor Core
    compile 'com.github.filipproch.reactor-android:reactor-android:dev-SNAPSHOT'
    // Reactor Extras
    compile 'com.github.filipproch.reactor-android:reactor-android-extras:dev-SNAPSHOT'
}
```

## Contribute

TBD