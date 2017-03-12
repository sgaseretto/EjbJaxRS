package py.pol.una.ii.pw.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.pol.una.ii.pw.data.CustomerRepository;
import py.pol.una.ii.pw.data.PagoRepository;
import py.pol.una.ii.pw.model.Customer;
import py.pol.una.ii.pw.model.Pago;
import py.pol.una.ii.pw.service.CustomerRegistration;
import py.pol.una.ii.pw.service.PagoRegistration;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the pagoes table.
 */
@Path("/pagos")
@RequestScoped
public class PagoResourceRESTService {
    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private PagoRepository repository;

    @Inject
    PagoRegistration registration;
    
    @Inject
    private CustomerRepository repoCliente;
    
    @Inject
    private CustomerRegistration regCliente;
    
    private Customer customer;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Pago> listAllVentas() {
        return repository.findAllOrderedById();
    }
    
    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Pago lookupPagoById(@PathParam("id") long id) {
        Pago pago = repository.findById(id);
        if (pago == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return pago;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPago(Pago pago) {

        Response.ResponseBuilder builder = null;

        try {
            // Validates pago using bean validation
            validatePago(pago);

            registration.register(pago);

            // Create an "ok" response
            builder = Response.ok();
            
            //Descontar el monto de la cuenta del cliente
            customer = pago.getCustomer();
            customer = repoCliente.findById(customer.getId());
            if (customer.getCuenta() > pago.getMonto()){
            	Integer saldo = customer.getCuenta()-pago.getMonto();
                customer.setCuenta(saldo);
                regCliente.update(customer);
            }
            
        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("email", "Email taken");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }
    
    private void validatePago(Pago pago) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Pago>> violations = validator.validate(pago);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
    }
    
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<String, String>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
}
}