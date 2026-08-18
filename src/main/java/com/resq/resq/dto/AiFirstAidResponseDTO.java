package com.resq.resq.dto;

import com.resq.resq.model.AiUrgencyLevel;

import java.util.List;

public class AiFirstAidResponseDTO {

    private AiUrgencyLevel urgencyLevel;

    // Main 3–5 prioritized actions shown to the Citizen
    private List<String> mainFirstAidActions;

    // Detailed AI information for Volunteer/Admin use
    private List<String> visibleConcerns;

    private List<String> immediateActions;

    private List<String> precautions;

    private List<String> doNotDo;

    private boolean requiresUrgentHelp;

    private String disclaimer;


    public AiUrgencyLevel getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(
            AiUrgencyLevel urgencyLevel
    ) {
        this.urgencyLevel = urgencyLevel;
    }


    public List<String> getMainFirstAidActions() {
        return mainFirstAidActions;
    }

    public void setMainFirstAidActions(
            List<String> mainFirstAidActions
    ) {
        this.mainFirstAidActions = mainFirstAidActions;
    }


    public List<String> getVisibleConcerns() {
        return visibleConcerns;
    }

    public void setVisibleConcerns(
            List<String> visibleConcerns
    ) {
        this.visibleConcerns = visibleConcerns;
    }


    public List<String> getImmediateActions() {
        return immediateActions;
    }

    public void setImmediateActions(
            List<String> immediateActions
    ) {
        this.immediateActions = immediateActions;
    }


    public List<String> getPrecautions() {
        return precautions;
    }

    public void setPrecautions(
            List<String> precautions
    ) {
        this.precautions = precautions;
    }


    public List<String> getDoNotDo() {
        return doNotDo;
    }

    public void setDoNotDo(
            List<String> doNotDo
    ) {
        this.doNotDo = doNotDo;
    }


    public boolean isRequiresUrgentHelp() {
        return requiresUrgentHelp;
    }

    public void setRequiresUrgentHelp(
            boolean requiresUrgentHelp
    ) {
        this.requiresUrgentHelp = requiresUrgentHelp;
    }


    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(
            String disclaimer
    ) {
        this.disclaimer = disclaimer;
    }
}