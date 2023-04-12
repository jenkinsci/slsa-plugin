/*
 * Copyright (c) 2023 Eclipse Foundation. All rights reserved.
 *
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package org.jenkinsci.plugins.slsa.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.FilePath;
import org.jenkinsci.plugins.slsa.util.Digest;

import java.io.IOException;

/**
 * Represents a build artifact which should be included in
 * the SLSA provenance attestation.
 */
public class SubjectInfo {
    private final String artifactName;
    private final String workspacePath;
    private final String sha256Digest;

    public static SubjectInfo of(@NonNull FilePath artifact, @NonNull FilePath workspace) throws IOException, InterruptedException {
        String workspacePath = artifact.getRemote().substring(workspace.getRemote().length());
        if (workspacePath.startsWith("/")) {
            workspacePath = "." + workspacePath;
        }

        String digest = artifact.act(Digest.ofSha256());
        return new SubjectInfo(artifact.getName(), workspacePath, digest);
    }

    private SubjectInfo(@NonNull String artifactName, @NonNull String workspacePath, @NonNull String sha256Digest) {
        this.artifactName  = artifactName;
        this.workspacePath = workspacePath;
        this.sha256Digest  = sha256Digest;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public String getSha256Digest() {
        return sha256Digest;
    }
}
