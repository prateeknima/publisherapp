package com.merative.publisher.hibernate.entities;
import javax.persistence.*;
import com.merative.publisher.UserKeyGeneratorService;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import io.quarkus.security.jpa.PasswordType;

@Entity
@Table(name = "users")
@UserDefinition
public class UserEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Username
    public String username;
    public String firstname;
    public String lastname;
    @Column(unique = true)
    @GeneratorType( type = UserKeyGeneratorService.class, when = GenerationTime.INSERT)
    @Password(value = PasswordType.CLEAR)
    public String user_key;
    @Roles
    public String user_role;
    public String accountLocked;
}

