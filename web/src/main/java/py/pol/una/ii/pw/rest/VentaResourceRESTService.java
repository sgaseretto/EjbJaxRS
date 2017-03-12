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
import py.pol.una.ii.pw.data.ProductRepository;
import py.pol.una.ii.pw.data.VentaRepository;
import py.pol.una.ii.pw.model.Customer;
import py.pol.una.ii.pw.model.Product;
import py.pol.una.ii.pw.model.Venta;
import py.pol.una.ii.pw.service.CustomerRegistration;
import py.pol.una.ii.pw.service.VentaRegistration;

@Path("/ventas")
@RequestScoped
public class VentaResourceRESTService {
	@Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private VentaRepository repository;

    @Inject
    VentaRegistration registration;
    
    @Inject
    private ProductRepository repoProducto;
    
    @Inject
    private CustomerRepository repoCliente;
    
    
    @Inject
    private CustomerRegistration regCliente;
    
    

    
    private Customer customer;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Venta> listAllVentas() {
        return repository.findAllOrderedById();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Venta lookupVentaById(@PathParam("id") long id) {
        Venta venta = repository.findById(id);
        if (venta == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return venta;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createVenta(Venta venta) {

        Response.ResponseBuilder builder = null;

        try {
            // Validates venta using bean validation
            validateVenta(venta);
            
            //Anhadir datos de los productos
            customer = venta.getCustomer();
            customer = repoCliente.findById(customer.getId());
            venta.setCustomer(customer);
            int i = 0;
            for(Product pc: venta.getProductos()){
            	Product p = repoProducto.findById(pc.getId());
            	venta.getProductos().set(i, p);
            	i++;
            }

            registration.register(venta);
            
            //Agregar cuenta de cliente 
            customer = venta.getCustomer();
            Integer cuenta = customer.getCuenta();
            for(Product pc: venta.getProductos()){
            	Product p = repoProducto.findById(pc.getId());
            	cuenta = (int) (cuenta + p.getPrice());
            }
            customer.setCuenta(cuenta);
            regCliente.update(customer);

            // Create an "ok" response
            builder = Response.ok();
            
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
    
    private void validateVenta(Venta venta) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Venta>> violations = validator.validate(venta);

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