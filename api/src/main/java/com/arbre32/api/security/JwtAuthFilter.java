package com.arbre32.api.security;
import com.arbre32.api.user.PlayerAccountRepository;
import com.arbre32.api.user.PlayerAccount;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwt; private final PlayerAccountRepository repo;
  public JwtAuthFilter(JwtService j, PlayerAccountRepository r){ this.jwt=j; this.repo=r; }
  @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String h = req.getHeader("Authorization");
    if(h!=null && h.startsWith("Bearer ")){
      String token = h.substring(7);
      try{
        var jws = jwt.parse(token);
        UUID id = UUID.fromString(jws.getBody().getSubject());
        var user = repo.findById(id).orElse(null);
        if(user!=null && "ACTIVE".equals(user.getStatus())){
          var auth = new UsernamePasswordAuthenticationToken(user, null,
              List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole())));
          auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      }catch(Exception ignored){}
    }
    chain.doFilter(req, res);
  }
}
