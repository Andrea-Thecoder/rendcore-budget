package it.thecoder.rendcore.budget.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import io.ebean.annotation.WhoCreated;
import io.ebean.annotation.WhoModified;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.OffsetDateTime;

@MappedSuperclass
@JsonInclude(value = Include.NON_NULL)
@Getter
@Setter
@SuppressWarnings("java:S116")
public abstract class AbstractAuditable extends Model {

    @Version
    @Schema(hidden = true)
    protected Long _version;

    @WhenCreated
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected OffsetDateTime _createdAt;

    @WhoCreated
    @Schema(hidden = true)
    @Column(updatable = false)
    protected String _createdBy;

    @WhenModified
    @Temporal(TemporalType.TIMESTAMP)
    protected OffsetDateTime _updatedAt;

    @WhoModified
    @Schema(hidden = true)
    protected String _updatedBy;
}