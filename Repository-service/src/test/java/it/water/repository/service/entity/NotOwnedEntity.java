package it.water.repository.service.entity;

import it.water.core.api.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class NotOwnedEntity implements BaseEntity {
    @Id
    private long id;
    private String entityField;
    private Date entityCreateDate;
    private Date entityModifyDate;
    private int entityVersion;

    public String getEntityField() {
        return entityField;
    }

    public void setEntityField(String entityField) {
        this.entityField = entityField;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Date getEntityCreateDate() {
        return entityCreateDate;
    }

    public void setEntityCreateDate(Date entityCreateDate) {
        this.entityCreateDate = entityCreateDate;
    }

    @Override
    public Date getEntityModifyDate() {
        return entityModifyDate;
    }

    public void setEntityModifyDate(Date entityModifyDate) {
        this.entityModifyDate = entityModifyDate;
    }

    @Override
    public Integer getEntityVersion() {
        return entityVersion;
    }

    @Override
    public void setEntityVersion(Integer integer) {
        //just for test purpose
    }
    public void setEntityVersion(int entityVersion) {
        this.entityVersion = entityVersion;
    }

}
