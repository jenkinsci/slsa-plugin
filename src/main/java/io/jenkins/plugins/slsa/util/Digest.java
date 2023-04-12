/*
 * Copyright (c) 2023 Eclipse Foundation. All rights reserved.
 *
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package io.jenkins.plugins.slsa.util;

import hudson.remoting.VirtualChannel;
import jenkins.MasterToSlaveFileCallable;
import org.eclipsefdn.security.slsa.attestation.util.Digests;

import java.io.File;
import java.io.IOException;

/**
 * A {@link hudson.FilePath.FileCallable} implementation to generate
 * digests for workspace files.
 */
public class Digest extends MasterToSlaveFileCallable<String> {
    private final String algorithm;

    public static Digest ofSha256() {
        return new Digest("SHA-256");
    }

    public Digest(String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public String invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
        return Digests.getDigest(algorithm, f.toPath());
    }
}
