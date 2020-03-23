package dbobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@MappedSuperclass
public class DBOject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    public Long id;
    public Long getId() {
        return id;
    }
    @JsonIgnore
    public String getIdString() {
        return String.valueOf(id);
    }
    public void setId(Long id) {
        this.id = id;
    }

    @UpdateTimestamp
    @Column
    @JsonIgnore
    private Timestamp modifiedDate;

    @CreationTimestamp
    @Column
    private Timestamp createdDate;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @JsonIgnore
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}