[![GitHub Actions](https://github.com/netomi/slsa-jenkins-plugin/workflows/GitHub%20CI/badge.svg?branch=main)](https://github.com/netomi/slsa-jenkins-plugin/actions)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

# SLSA Jenkins Plugin

The SLSA Jenkins plugin generates [SLSA provenance attestations](https://slsa.dev/provenance/) for build artifacts.

## Job configuration

The plugin provides a ```Post-build action``` which will generate provenance attestations
(`<artifact-name>.intoto.jsonl` or `multiple.intoto.jsonl`) in SLSA format for artifacts that
match a given filter after a successful build.

![job configuration](docs/images/jenkins-job-configuration.png)

**Artifact Filter**: Specifies the artifacts to include.

**Target Directory**: Specifies the directory where generated provenance attestations should be created.

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE)

