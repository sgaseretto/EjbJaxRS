package py.pol.una.ii.pw.rest;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;

import py.pol.una.ii.pw.data.VentaRepository;
import py.pol.una.ii.pw.model.Venta;
import py.pol.una.ii.pw.service.VentaRegistration;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the ventas table.
 */
@Path("/ventas")
@RequestScoped
public class VentaNormalResourceRESTService {
    @Inject
    private Logger log;

    @Inject
    private VentaRepository repository;

    @Inject
    VentaRegistration registration;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Venta> listAllVentas() {
        return repository.findAllOrderedById();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Venta lookupVentasById(@PathParam("id") long id) {
        Venta venta = repository.findById(id);
        if (venta == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return venta;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createVenta(Venta venta) throws IOException {

        Response.ResponseBuilder builder = null;
        try {

            registration.registerVenta(venta);

            builder = Response.ok();
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return builder.build();
    }

    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Venta deleteVentaById(@PathParam("id") long id) throws Exception {
        Venta venta = repository.findById(id);

            if (venta == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
        registration.delete(venta);

        return venta;
    }
}