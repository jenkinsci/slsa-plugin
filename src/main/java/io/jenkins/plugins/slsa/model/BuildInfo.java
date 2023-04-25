/*
 * Copyright (c) 2023 Eclipse Foundation. All rights reserved.
 *
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package io.jenkins.plugins.slsa.model;

import hudson.EnvVars;
import hudson.model.Action;
import hudson.model.Run;
import hudson.plugins.git.Revision;
import hudson.plugins.git.util.BuildData;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

public class BuildInfo {

    private final String         buildNumber;
    private final OffsetDateTime buildStartedOn;
    private final OffsetDateTime buildFinishedOn;

    private final String sourceUrl;
    private final String sourceDigest;
    private final String sourceDigestAlgorithm;

    private final String jobName;
    private final String jobUrl;
    private final String buildUrl;
    private final String nodeName;

    public static BuildInfo of(Run<?, ?> run, EnvVars env) {
        OffsetDateTime startTime =
                OffsetDateTime.ofInstant(Instant.ofEpochMilli(run.getStartTimeInMillis()),
                        ZoneId.systemDefault());

        OffsetDateTime endTime = OffsetDateTime.now();

        String sourceUrl             = null;
        String sourceDigest          = null;
        String sourceDigestAlgorithm = null;

        String gitUrl = env.get("GIT_URL");
        if (gitUrl != null) {
            sourceUrl = "git+" + gitUrl + "@" + env.get("GIT_BRANCH");
            sourceDigestAlgorithm = "sha1";
            sourceDigest = env.get("GIT_COMMIT");
        } else {
            List<? extends Action> actionList = run.getAllActions();
            for (Action action : actionList) {
                if (action.getClass().getName().equals("hudson.plugins.git.util.BuildData")) {
                    String url = ((BuildData) action).getRemoteUrls().stream().findFirst().orElseThrow();

                    Revision revision = ((BuildData) action).getLastBuiltRevision();
                    if (revision != null) {
                        String branch = revision.getBranches().stream().findFirst().orElseThrow().getName();

                        sourceUrl = "git+" + url + "@" + branch;

                        sourceDigestAlgorithm = "sha1";
                        sourceDigest = revision.getSha1String();
                    }
                }
            }
        }

        String jobName  = env.get("JOB_NAME");
        String jobUrl   = env.get("JOB_URL");
        String buildUrl = env.get("BUILD_URL");

        String nodeName = env.get("NODE_NAME");

        return new BuildInfo(run.getId(),
                startTime,
                endTime,
                sourceUrl,
                sourceDigest,
                sourceDigestAlgorithm,
                jobName,
                jobUrl,
                buildUrl,
                nodeName);
    }

    private BuildInfo(String         buildNumber,
                      OffsetDateTime buildStartedOn,
                      OffsetDateTime buildFinishedOn,
                      String         sourceUrl,
                      String         sourceDigest,
                      String         sourceDigestAlgorithm,
                      String         jobName,
                      String         jobUrl,
                      String         buildUrl,
                      String         nodeName) {
        this.buildNumber           = buildNumber;
        this.buildStartedOn        = buildStartedOn;
        this.buildFinishedOn       = buildFinishedOn;
        this.sourceUrl             = sourceUrl;
        this.sourceDigest          = sourceDigest;
        this.sourceDigestAlgorithm = sourceDigestAlgorithm;
        this.jobName               = jobName;
        this.jobUrl                = jobUrl;
        this.buildUrl              = buildUrl;
        this.nodeName              = nodeName;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public OffsetDateTime getBuildStartedOn() {
        return buildStartedOn;
    }

    public OffsetDateTime getBuildFinishedOn() {
        return buildFinishedOn;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getSourceDigest() {
        return sourceDigest;
    }

    public String getSourceDigestAlgorithm() {
        return sourceDigestAlgorithm;
    }

    public String getJobName() {
        return jobName;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public String getBuildUrl() {
        return buildUrl;
    }

    public String getNodeName() {
        return nodeName;
    }
}
