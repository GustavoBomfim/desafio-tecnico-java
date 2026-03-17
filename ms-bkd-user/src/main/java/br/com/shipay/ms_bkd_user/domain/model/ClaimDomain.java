package br.com.shipay.ms_bkd_user.domain.model;

import lombok.Getter;

@Getter
public class ClaimDomain {

    private Long id;

    private String description;

    private boolean active;

    public ClaimDomain(Long id, String description, boolean active) {

    }

}
