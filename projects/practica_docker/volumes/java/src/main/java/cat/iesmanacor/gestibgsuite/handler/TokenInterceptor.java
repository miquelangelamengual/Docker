package cat.iesmanacor.gestibgsuite.handler;

import cat.iesmanacor.gestibgsuite.manager.GMailService;
import cat.iesmanacor.gestibgsuite.manager.TokenManager;
import cat.iesmanacor.gestibgsuite.manager.TokenResponse;
import cat.iesmanacor.gestibgsuite.manager.UsuariService;
import cat.iesmanacor.gestibgsuite.model.Rol;
import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private GMailService gMailService;

    @Value("${gc.adminUser}")
    private String administrador;

    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        /*
         * Detecta si la petición es un OPTIONS en tal caso devuelve true.
         * */
        if (request.getMethod().equals("OPTIONS")) return true;

        /*
         * Si no es un OPTIONS comprueba si la petición contiene el Token
         * y comprueba si es válido o si ha expirado.
         * */
        String auth = request.getHeader("Authorization");
        //System.out.println("Request url: " + request.getRequestURL());
        if (auth != null && !auth.isEmpty()) {
            String token = auth.replace("Bearer ", "");
            TokenResponse validate = tokenManager.validateToken(token);

            if (validate.equals(TokenResponse.ERROR)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No valid Token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;

            } else if (validate.equals(TokenResponse.EXPIRED)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired Token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            Claims claims = tokenManager.getClaims(request);

            String email = (String) claims.get("email");
            Usuari usuari = usuariService.findByEmail(email);
            //System.out.println("Email Token Interceptor: " + email);
            /*if (email.equals(this.administrador)) {
                response.setStatus(HttpServletResponse.SC_OK);
                return true;
            }*/

            if (usuari == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not valid");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            //Rols
            String URI = request.getRequestURI();
            Rol[] rolsURL = getRolsFromURL(URI);
            boolean hasAuthorization = this.hasAuthorization(usuari, rolsURL);

            if (rolsURL == null) {
                this.gMailService.sendMessage("Error a l'autorització d'usuaris","La URL " + URI + " no té accés.",this.administrador);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "La URL " + URI + " no té accés.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            if (!hasAuthorization) {
                StringBuilder rolsString = new StringBuilder();
                for (Rol rol : rolsURL) {
                    rolsString.append(rol.name()).append(" ");
                }
                this.gMailService.sendMessage("Error a l'autorització d'usuaris","Usuari: "+usuari.getGsuiteEmail()+" Permisos insuficients. Es necessita un d'aquests permisos: " + rolsString,this.administrador);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Permisos insuficients. Es necessita un d'aquests permisos: " + rolsString);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            return true;

        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token not received");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    private Rol[] getRolsFromURL(String url) {
        Multimap<String, Rol[]> securedURLs = LinkedHashMultimap.create();

        //Genral
        securedURLs.put("/error", Rol.values()); //Tots els rols presents i futurs


        //Calendari
        securedURLs.put("/calendari/llistat", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR});

        //Centre
        securedURLs.put("/centre/", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR});

        //Grups de correu
        securedURLs.put("/grupcorreu/desar", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/grupcorreu/grupambusuaris/", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/grupcorreu/llistat", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/grupcorreu/llistatambusuaris", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/grupcorreu/autoemplenar", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/grupcorreu/grupsusuari/", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/grupcorreu/addmember", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/grupcorreu/removemember", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});

        //Sincronització
        securedURLs.put("/sync/reassignarGrups", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/sync/reassignarGrupsProfessors", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/sync/reassignarGrupsAlumnes", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/sync/reassignarGrupsCorreuGSuiteToDatabase", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/sync/sincronitza", new Rol[]{Rol.ADMINISTRADOR});
        securedURLs.put("/sync/uploadfile", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR});

        //Usuaris
        securedURLs.put("/usuaris/llistat/actius", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/usuaris/llistat/eliminats", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/usuaris/llistat/pendentssuspendre", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/usuaris/llistat/suspesos", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/usuaris/profile/rol", Rol.values()); //Tots els rols presents i futurs
        securedURLs.put("/usuaris/suspendre", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/usuaris/profile/", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/usuari/reset", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});

        //Cursos
        securedURLs.put("/curs/getByCodiGestib/", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});

        //Grups
        securedURLs.put("/grup/llistat", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});

        //Llistats
        securedURLs.put("/google/sheets/alumnatpergrup", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/google/sheets/usuarisgrupcorreu", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/google/sheets/usuarisdispositiu", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/google/sheets/usuariscustom", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});

        //APP Grups Cooperatius
        securedURLs.put("/apps/grupscooperatius/aleatori", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/grupscooperatius/genetica", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/grupscooperatius/items", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/grupscooperatius/item/", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/grupscooperatius/item/valors/", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/grupscooperatius/item/desar", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});

        //APP Sheet Parser
        securedURLs.put("/apps/sheetparser/send", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/sheetparser/draft", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});

        //APP Convalidacions
        securedURLs.put("/apps/convalidacions/categories", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/convalidacions/categoria/", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/convalidacions/categoria/desar", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/convalidacions/categoria/esborrar", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/convalidacions/titulacions", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/convalidacions/items", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/convalidacions/titulacio/", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/convalidacions/titulacio/desar", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/convalidacions/titulacio/esborrar", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/convalidacions/convalidacio/llistat", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/convalidacions/convalidacio/desar", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/convalidacions/convalidacio/esborrar", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/convalidacions/solicitud/llistat", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/convalidacions/solicitud/desar", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});
        securedURLs.put("/apps/convalidacions/solicitud/esborrar", new Rol[]{Rol.ADMINISTRADOR, Rol.DIRECTOR, Rol.CAP_ESTUDIS});


        Map.Entry<String, Rol[]> securedURL = securedURLs.entries().stream()
                .filter(urlsecured -> url.contains(urlsecured.getKey()))
                .findFirst().orElse(null);

        if (securedURL != null) {
            return securedURL.getValue();
        } else {
            System.out.println("Error Secured URL: "+url);
            return null;
        }
    }

    private boolean hasAuthorization(Usuari u, Rol... allowedRols) throws MessagingException, GeneralSecurityException, IOException {
        if (allowedRols == null) {
            return false;
        }

        if (u == null || !u.getActiu()) {
            return false;
        }

        for (Rol rol : allowedRols) {
            if (u.getRols() != null && u.getRols().contains(rol)) {
                return true;
            }
            if (u.getGestibProfessor() != null && u.getGestibProfessor() && rol.equals(Rol.PROFESSOR)) {
                return true;
            }
            if (u.getGestibAlumne() != null && u.getGestibAlumne() && rol.equals(Rol.ALUMNE)) {
                return true;
            }
        }
        return false;
    }
}
