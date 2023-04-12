/*
 * Copyright (c) 2023 Eclipse Foundation. All rights reserved.
 *
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package io.jenkins.plugins.slsa.generator;

import org.eclipsefdn.security.slsa.attestation.model.SignedAttestation;
import io.jenkins.plugins.slsa.model.BuildInfo;
import io.jenkins.plugins.slsa.model.SubjectInfo;

import java.util.Collection;

public interface ProvenanceAttestationGenerator {
    SignedAttestation generateAttestation(Collection<SubjectInfo> subjects, BuildInfo buildInfo);
}