[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/slsa.svg)](https://plugins.jenkins.io/slsa)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/slsa-plugin.svg?label=release)](https://github.com/jenkinsci/slsa-plugin/releases/latest)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/slsa.svg?color=blue)](https://plugins.jenkins.io/slsa)
[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins%2Fslsa-plugin%2Fmain)](https://ci.jenkins.io/job/Plugins/job/slsa-plugin/job/main/)
[![GitHub license](https://img.shields.io/github/license/jenkinsci/slsa-plugin.svg)](https://github.com/jenkinsci/slsa-plugin/blob/master/LICENSE)

# SLSA Jenkins Plugin

The SLSA Jenkins plugin generates [SLSA provenance attestations](https://slsa.dev/provenance/) for build artifacts.

## Job configuration - Freestyle project

The plugin provides a ```Post-build action``` which will generate provenance attestations
(`<artifact-name>.intoto.jsonl` or `multiple.intoto.jsonl`) in SLSA format for artifacts that
match a given filter after a successful build.

![job configuration](docs/images/jenkins-job-configuration.png)

**Artifact Filter**: Specifies the artifacts to include.

**Target Directory**: Specifies the directory where generated provenance attestations should be created.

## Job configuration - Pipeline project

In order to use the plugin with the descriptive pipeline syntax, the following snippet
can be added:

```
...
post {
    success {
        provenanceRecorder artifactFilter: 'build/libs/**.jar', targetDirectory: 'build/slsa'
    }
}
...
```

**Artifact Filter**: Specifies the artifacts to include.

**Target Directory**: Specifies the directory where generated provenance attestations should be created.

## Current limitations

* currently only GIT SCM provider is supported
* executed build steps are not yet recorded
* signing of attestations is not yet supported

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE).

