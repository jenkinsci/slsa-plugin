/*
 * Copyright (c) 2023 Eclipse Foundation. All rights reserved.
 *
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package org.jenkinsci.plugins.slsa.generator;

import org.eclipsefdn.security.slsa.attestation.model.SignedAttestation;
import org.jenkinsci.plugins.slsa.model.BuildInfo;
import org.jenkinsci.plugins.slsa.model.SubjectInfo;

import java.util.Collection;

public interface ProvenanceAttestationGenerator {
    SignedAttestation generateAttestation(Collection<SubjectInfo> subjects, BuildInfo buildInfo);
}