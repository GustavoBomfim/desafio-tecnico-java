package br.com.shipay.ms_bkd_user.domain.model;

import lombok.Getter;

@Getter
public class RoleDomain {

    private Integer id;

    private String description;

    private RoleDomain(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    public static RoleDomain restore(Integer id, String description){
      return new RoleDomain(id, description);
    }

}
