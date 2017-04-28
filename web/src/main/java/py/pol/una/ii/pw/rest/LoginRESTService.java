package py.pol.una.ii.pw.rest;

import org.apache.commons.codec.binary.Base64;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Created by cristhianjbd on 27/04/17.
 */

@Path("/login")
@RequestScoped
public class LoginRESTService {

    @Inject
    private Logger log;


    public boolean authenticate(String authCredentials) {

        if (null == authCredentials)
            return false;
        // header value format will be "Basic encodedstring" for Basic
        // authentication. Example "Basic YWRtaW46YWRtaW4="
        final String encodedUserPassword = authCredentials.replaceFirst("Basic"
                + " ", "");
        String usernameAndPassword = null;
        try {
            byte[] decodedBytes = Base64.decodeBase64(encodedUserPassword);
            usernameAndPassword = new String(decodedBytes, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final StringTokenizer tokenizer = new StringTokenizer(
                usernameAndPassword, ":");

        // we have fixed the userid and password as admin
        // call some UserService/LDAP here
        boolean authenticationStatus = "adminadmin".equals(usernameAndPassword);
        return authenticationStatus;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("user") String user, @FormParam("password") String pass) throws IOException {
        String concat = null;

            if(user.equals(pass)) {
                concat = user + pass;
                byte[] encodedBytes = Base64.encodeBase64(concat.getBytes());
                String session_id = new String(encodedBytes);
                return Response.status(200).entity("Su session_id es:  " + "Basic "+ session_id)
                        .build();
            }else{
                return Response.status(200).entity("Usuario y/o password incorrectos")
                        .build();
            }


    }










}
