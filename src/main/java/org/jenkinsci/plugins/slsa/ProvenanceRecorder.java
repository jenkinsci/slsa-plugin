/*
 * Copyright (c) 2023 Eclipse Foundation. All rights reserved.
 *
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package org.jenkinsci.plugins.slsa;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.*;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import org.eclipsefdn.security.slsa.attestation.io.AttestationWriter;
import org.eclipsefdn.security.slsa.attestation.model.SignedAttestation;
import org.eclipsefdn.security.slsa.attestation.util.Json;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.slsa.generator.ProvenanceAttestationGenerator;
import org.jenkinsci.plugins.slsa.generator.ProvenanceV0_2Generator;
import org.jenkinsci.plugins.slsa.model.BuildInfo;
import org.jenkinsci.plugins.slsa.model.SubjectInfo;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;

public class ProvenanceRecorder extends Recorder implements SimpleBuildStep {

    private String artifactFilter;
    private String targetDirectory;

    @DataBoundConstructor
    public ProvenanceRecorder(String artifactFilter, String targetDirectory) {
        setArtifactFilter(artifactFilter);
        setTargetDirectory(targetDirectory);
    }

    public String getArtifactFilter() {
        return artifactFilter;
    }

    @DataBoundSetter
    public void setArtifactFilter(String artifactFilter) {
        this.artifactFilter = artifactFilter;
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    @DataBoundSetter
    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public void perform(@NonNull Run<?, ?> run, @NonNull FilePath workspace, @NonNull EnvVars env, @NonNull Launcher launcher, @NonNull TaskListener listener) throws InterruptedException, IOException {
        PrintStream console = listener.getLogger();

        if (run.getResult() != Result.SUCCESS) {
            console.println("[slsa] - build not successful, not generating provenance attestations");
            return;
        }

        ProvenanceAction provenanceData = new ProvenanceAction();
        run.addAction(provenanceData);

        String expandedFilter = env.expand(artifactFilter);
        FilePath[] artifacts  = workspace.list(expandedFilter);

        console.println("[slsa] collecting artifacts");

        for (FilePath artifact : artifacts) {
            console.println(" > " + artifact.getName());
            provenanceData.addSubject(SubjectInfo.of(artifact, workspace));
        }

        Collection<SubjectInfo> subjects = provenanceData.getSubjects();
        if (subjects.isEmpty()) {
            console.println(" > found no artifacts, not generating any provenance attestation");
            return;
        } else if (!areAllSubjectsUnique(provenanceData.getSubjects())) {
            console.println(" > found artifacts with duplicate names:");

            for (SubjectInfo subject : subjects) {
                console.println("   > " + subject.getArtifactName() + " -> " + subject.getWorkspacePath());
            }

            console.println(" > not generating attestation");
            return;
        }

        console.println("[slsa] generating attestation");

        BuildInfo buildInfo = BuildInfo.of(run, env);

        ProvenanceAttestationGenerator generator = new ProvenanceV0_2Generator();
        SignedAttestation attestation = generator.generateAttestation(subjects, buildInfo);

        FilePath targetPath = new FilePath(workspace, env.expand(targetDirectory));
        targetPath.mkdirs();

        String attestationFileName = subjects.size() > 1 ?
            "multiple.intoto.jsonl" :
            subjects.stream().findFirst().get().getArtifactName() + ".intoto.jsonl";

        FilePath outputFile = new FilePath(targetPath, attestationFileName);
        try (OutputStream os = outputFile.write()) {
            AttestationWriter attestationWriter = new AttestationWriter(os);
            attestationWriter.writeAttestation(attestation);
        }

        provenanceData.addProvenanceAttestation(outputFile.getName(), Json.dumpWithPrettyPrinting(attestation.getStatement()));
        console.println(" > written attestation to " + outputFile.getRemote());
    }

    public static boolean areAllSubjectsUnique(Collection<SubjectInfo> subjects) {
        return subjects.stream().map(SubjectInfo::getArtifactName).allMatch(new HashSet<>()::add);
    }

    @Extension
    @Symbol("provenanceRecorder")
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public FormValidation doCheckArtifactFilter(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.ProvenanceRecorder_DescriptorImpl_errors_missingArtifactFilter());
            return FormValidation.ok();
        }

        public FormValidation doCheckTargetDirectory(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.ProvenanceRecorder_DescriptorImpl_errors_missingTargetDirectory());
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.ProvenanceRecorder_DescriptorImpl_DisplayName();
        }
    }

}
