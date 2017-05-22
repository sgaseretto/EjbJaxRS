package py.pol.una.ii.pw.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Produces;
import java.io.IOException;

/**
 * Created by cristhianjbd on 17/05/17.
 */
@Path("/injection")
@RequestScoped
public class InjectionRestService {

    @POST
    @Path("/command")
    public void command(String directorio) throws IOException {
        Runtime r = Runtime.getRuntime();

        String[] commando = new String[]{"/bin/sh","-c","touch"+" /home/cristhianjbd/Escritorio/" + directorio};

        r.exec(commando);

    }

// "nombredirectorio ; rm /home/cristhianjbd/Escritorio/directorioAborrar
}
