package com.arbre32.api.user;
/*
import com.arbre32.api.security.JwtService;
import com.arbre32.api.user.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/auth")
public class AuthController {
  private final PlayerAccountRepository repo;
  private final PasswordEncoder enc;
  private final JwtService jwt;
  public AuthController(PlayerAccountRepository r, PasswordEncoder e, JwtService j){ this.repo=r; this.enc=e; this.jwt=j; }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req){
        if(req.email() == null || req.password() == null || req.handle()==null){
            return ResponseEntity.badRequest().body(Map.of("error","Champs requis"));
        }

        if(repo.findByEmail(req.email()).isPresent()){
            return ResponseEntity.badRequest().body(Map.of("error","Email déjà utilisé"));
        }
        if(repo.findByHandle(req.handle()).isPresent()){
            return ResponseEntity.badRequest().body(Map.of("error","Handle déjà utilisé"));
        }

        PlayerAccount p = new PlayerAccount();
        p.setId(UUID.randomUUID());
        p.setHandle(req.handle());
        p.setEmail(req.email());
        p.setPasswordHash(enc.encode(req.password()));
        repo.save(p);

        String token = jwt.generate(p.getId(), p.getHandle(), p.getRole());
        UserDto user = new UserDto(p.getId(), p.getHandle(), p.getEmail());

        return ResponseEntity.ok(new AuthResponse(token, user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req){
        var p = repo.findByEmail(req.email()).orElse(null);
        if(p == null || !enc.matches(req.password(), p.getPasswordHash())){
            return ResponseEntity.status(401).body(Map.of("error","Identifiants invalides"));
        }

        String token = jwt.generate(p.getId(), p.getHandle(), p.getRole());
        UserDto user = new UserDto(p.getId(), p.getHandle(), p.getEmail());

        return ResponseEntity.ok(new AuthResponse(token, user));
    }

    @GetMapping("/secure/test")
    public String secureTest() {
        return "OK Secure";
    }

}
*/


import com.arbre32.api.security.JwtService;
import com.arbre32.api.user.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PlayerAccountRepository repo;
    private final PasswordEncoder enc;
    private final JwtService jwt;

    public AuthController(PlayerAccountRepository r, PasswordEncoder e, JwtService j) {
        this.repo = r;
        this.enc = e;
        this.jwt = j;
    }

    // ------------------------
    //  REGISTER
    // ------------------------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        if (req.email() == null || req.password() == null || req.handle() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Champs requis"));
        }

        if (repo.findByEmail(req.email()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email déjà utilisé"));
        }

        if (repo.findByHandle(req.handle()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Handle déjà utilisé"));
        }

        PlayerAccount p = new PlayerAccount();
        p.setId(UUID.randomUUID());
        p.setHandle(req.handle());
        p.setEmail(req.email());
        p.setPasswordHash(enc.encode(req.password()));

        // 👇 Par défaut, un utilisateur normal a le rôle USER.
        p.setRole("USER");

        repo.save(p);

        // Génération du token incluant ROLE
        String token = jwt.generate(p.getId(), p.getHandle(), p.getRole());

        // 🔥 Nouveau : inclure le role dans UserDto
        UserDto user = new UserDto(p.getId(), p.getHandle(), p.getEmail(), p.getRole());

        return ResponseEntity.ok(new AuthResponse(token, user));
    }

    // ------------------------
    //  LOGIN
    // ------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        var p = repo.findByEmail(req.email()).orElse(null);

        if (p == null || !enc.matches(req.password(), p.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Identifiants invalides"));
        }

        String token = jwt.generate(p.getId(), p.getHandle(), p.getRole());

        // 🔥 Nouveau : incluons le rôle
        UserDto user = new UserDto(p.getId(), p.getHandle(), p.getEmail(), p.getRole());

        return ResponseEntity.ok(new AuthResponse(token, user));
    }

    @GetMapping("/secure/test")
    public String secureTest() {
        return "OK Secure";
    }
}
