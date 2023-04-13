/*
 * Copyright (c) 2023 Eclipse Foundation. All rights reserved.
 *
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package io.jenkins.plugins.slsa.generator;

import io.jenkins.plugins.slsa.PluginVersion;
import org.eclipsefdn.security.slsa.attestation.model.SignedAttestation;
import org.eclipsefdn.security.slsa.attestation.model.slsa.ProvenanceStatement;
import org.eclipsefdn.security.slsa.attestation.model.slsa.Subject;
import org.eclipsefdn.security.slsa.attestation.model.slsa.common.DigestAlgorithm;
import org.eclipsefdn.security.slsa.attestation.model.slsa.v0_2.*;
import io.jenkins.plugins.slsa.model.BuildInfo;
import io.jenkins.plugins.slsa.model.SubjectInfo;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProvenanceV0_2Generator implements ProvenanceAttestationGenerator {

    public static final String BUILD_TYPE = "https://gitlab.eclipse.org/eclipsefdn/security/slsa-jenkins-plugin@v" + PluginVersion.VERSION;
    public static final String BUILDER_ID = "https://gitlab.eclipse.org/eclipsefdn/security/slsa-jenkins-plugin@v" + PluginVersion.VERSION;

    public SignedAttestation generateAttestation(Collection<SubjectInfo> subjects, BuildInfo buildInfo) {

        ProvenanceStatement.Builder provenanceBuilder = ProvenanceStatement.builder();

        for (SubjectInfo subjectInfo : subjects) {
            Subject subject =
                Subject.builder()
                       .withName(subjectInfo.getArtifactName())
                       .withDigest(DigestAlgorithm.SHA256, subjectInfo.getSha256Digest())
                       .build();

            provenanceBuilder.withSubject(subject);
        }

        ConfigSource configSource =
            ConfigSource.builder()
                        .withUri(buildInfo.getSourceUrl())
                        .withDigest(DigestAlgorithm.fromString(buildInfo.getSourceDigestAlgorithm()),
                                buildInfo.getSourceDigest())
                        .withEntryPoint(buildInfo.getJobName())
                        .build();

        Map<String, Object> environment = new LinkedHashMap<>();
        environment.put("job_url", buildInfo.getJobUrl());
        environment.put("build_url", buildInfo.getBuildUrl());
        environment.put("node_name", buildInfo.getNodeName());

        Invocation invocation =
            Invocation.builder()
                      .withConfigSource(configSource)
                      .withEnvironment(environment)
                      .build();

        Material sourceMaterial =
            Material.builder()
                    .withUri(buildInfo.getSourceUrl())
                    .withDigest(DigestAlgorithm.fromString(buildInfo.getSourceDigestAlgorithm()),
                            buildInfo.getSourceDigest())
                    .build();

        Completeness completeness =
            Completeness.builder()
                        .withCompleteParameters()
                        .withCompleteEnvironment()
                        .build();

        Metadata metadata =
            Metadata.builder()
                    .withBuildInvocationId(buildInfo.getBuildNumber())
                    .withBuildStartedOn(buildInfo.getBuildStartedOn())
                    .withBuildFinishedOn(buildInfo.getBuildFinishedOn())
                    .withCompleteness(completeness)
                    .withReproducible()
                    .build();

        ProvenanceV0_2 provenanceV02 =
            ProvenanceV0_2.builder()
                          .withBuildType(BUILD_TYPE)
                          .withBuilder(ProvenanceBuilder.of(BUILDER_ID))
                          .withInvocation(invocation)
                          .withMetadata(metadata)
                          .withMaterial(sourceMaterial)
                          .build();

        provenanceBuilder.withProvenance(provenanceV02);
        return SignedAttestation.of(provenanceBuilder.build());
    }
}
