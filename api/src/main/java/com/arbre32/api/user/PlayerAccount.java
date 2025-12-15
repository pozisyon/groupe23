package com.arbre32.api.user;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="player_account")
public class PlayerAccount {
  @Id private UUID id;
  @Column(nullable=false, unique=true, length=40) private String handle;
  @Column(nullable=false, unique=true, length=190) private String email;
  @Column(nullable=false) private String passwordHash;
  @Column(nullable=false, length=30) private String role="USER";
  @Column(nullable=false, length=30) private String status="ACTIVE";
  @Column(nullable=false) private Instant createdAt=Instant.now();
  public boolean isAdmin() {
    return "ADMIN".equalsIgnoreCase(role);
  }
  public UUID getId(){return id;} public void setId(UUID id){this.id=id;}
  public String getHandle(){return handle;} public void setHandle(String h){this.handle=h;}
  public String getEmail(){return email;} public void setEmail(String e){this.email=e;}
  public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String p){this.passwordHash=p;}
  public String getRole(){return role;} public void setRole(String r){this.role=r;}
  public String getStatus(){return status;} public void setStatus(String s){this.status=s;}
  public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant t){this.createdAt=t;}
}
