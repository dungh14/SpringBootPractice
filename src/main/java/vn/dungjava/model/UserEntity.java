package vn.dungjava.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.dungjava.common.Gender;
import vn.dungjava.common.UserStatus;
import vn.dungjava.common.UserType;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Table(name = "tbl_user")
public class UserEntity extends AbstractEntity<Long> implements UserDetails, Serializable {

    @Column(name = "first_name", length = 255)
    private String firstName;

    @Column(name = "last_name", length = 255)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "gender", length = 255)
    private Gender gender;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 255)
    private String phone;

    @Column(name = "username", unique = true, nullable = false, length = 255)
    private String username;

    @Column(name = "password", length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", length = 255)
    private UserType type;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", length = 255)
    private UserStatus status;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<GroupHasUser> groups =  new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserHasRole> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // get role
        List<Role> roleList = roles.stream().map(UserHasRole::getRole).toList();

        //get role name
        List<String> roleNames = roleList.stream().map(Role::getName).toList();

        //add role name to authority
        //c1 neu su dung hasAnyAuthority
        return roleNames.stream().map(SimpleGrantedAuthority::new).toList();

        //c2 neu muon su dung hasAnyRole
//        return roleNames.stream().map(s -> new SimpleGrantedAuthority("ROLE_"+ s.toUpperCase())).toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return UserStatus.ACTIVE.equals(status);
    }
}
