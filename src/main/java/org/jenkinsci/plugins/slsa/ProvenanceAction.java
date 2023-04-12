/*
 * Copyright (c) 2023 Eclipse Foundation. All rights reserved.
 *
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package org.jenkinsci.plugins.slsa;

import hudson.model.Run;
import jenkins.model.RunAction2;
import org.jenkinsci.plugins.slsa.model.SubjectInfo;

import java.util.*;

public class ProvenanceAction implements RunAction2 {

    private static final long serialVersionUID = 20230412L;

    private transient Run<?, ?> run;

    private List<SubjectInfo>   subjects               = new ArrayList<>();
    private Map<String, String> provenanceAttestations = new TreeMap<>();

    public void addSubject(SubjectInfo subject) {
        subjects.add(subject);
    }

    public Collection<SubjectInfo> getSubjects() {
        return subjects;
    }

    public void addProvenanceAttestation(String path, String provenanceContent) {
        provenanceAttestations.put(path, provenanceContent);
    }

    public Map<String, String> getProvenanceAttestations() {
        return provenanceAttestations;
    }

    @Override
    public String getIconFileName() {
        return "/plugin/slsa/icons/slsa-logo.png";
    }

    @Override
    public String getDisplayName() {
        return Messages.ProvenanceAction_DisplayName();
    }

    @Override
    public String getUrlName() {
        return "slsa-provenance-attestations";
    }

    @Override
    public void onAttached(Run<?, ?> run) {
        this.run = run;
    }

    @Override
    public void onLoad(Run<?, ?> run) {
        this.run = run;
    }

    public Run<?, ?> getRun() {
        return run;
    }
}
