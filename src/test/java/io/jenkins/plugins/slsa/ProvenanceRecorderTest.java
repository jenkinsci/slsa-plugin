/*
 * Copyright (c) 2023 Eclipse Foundation. All rights reserved.
 *
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package io.jenkins.plugins.slsa;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class ProvenanceRecorderTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void testConfigRoundtrip() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        project.getPublishersList().add(new ProvenanceRecorder("**.jar", "build/slsa"));
        project = jenkins.configRoundtrip(project);
        jenkins.assertEqualDataBoundBeans(new ProvenanceRecorder("**.jar", "build/slsa"), project.getPublishersList().get(0));
    }

    @Test
    public void testBuild() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        ProvenanceRecorder recorder = new ProvenanceRecorder("**.jar", "build/slsa");
        project.getPublishersList().add(recorder);

        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("[slsa]", build);
    }
}