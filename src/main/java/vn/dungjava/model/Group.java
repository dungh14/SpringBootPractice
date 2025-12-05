package vn.dungjava.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_group")
public class Group extends AbstractEntity<Integer> implements Serializable {
    @Column(name = "name")
    private String name;

    private String description;

    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;

}
