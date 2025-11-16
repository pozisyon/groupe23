package com.arbre32.api.user;
import com.arbre32.api.security.JwtService;
import com.arbre32.api.user.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/auth")
@CrossOrigin(origins="http://localhost:5173", allowCredentials="true")
public class AuthController {
  private final PlayerAccountRepository repo;
  private final PasswordEncoder enc;
  private final JwtService jwt;
  public AuthController(PlayerAccountRepository r, PasswordEncoder e, JwtService j){ this.repo=r; this.enc=e; this.jwt=j; }
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest req){
    if(req.handle()==null || req.email()==null || req.password()==null) return ResponseEntity.badRequest().body(Map.of("error","Champs requis"));
    if(repo.findByEmail(req.email()).isPresent()) return ResponseEntity.badRequest().body(Map.of("error","Email déjà utilisé"));
    if(repo.findByHandle(req.handle()).isPresent()) return ResponseEntity.badRequest().body(Map.of("error","Handle déjà utilisé"));
    PlayerAccount p = new PlayerAccount();
    p.setId(UUID.randomUUID()); p.setHandle(req.handle()); p.setEmail(req.email()); p.setPasswordHash(enc.encode(req.password()));
    repo.save(p);
    return ResponseEntity.ok(new AuthResponse(jwt.generate(p.getId(), p.getHandle(), p.getRole())));
  }
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest req){
    var p = repo.findByEmail(req.email()).orElse(null);
    if(p==null || !enc.matches(req.password(), p.getPasswordHash())) return ResponseEntity.status(401).body(Map.of("error","Identifiants invalides"));
    return ResponseEntity.ok(new AuthResponse(jwt.generate(p.getId(), p.getHandle(), p.getRole())));
  }
    @GetMapping("/secure/test")
    public String secureTest() {
        return "OK Secure";
    }

}
