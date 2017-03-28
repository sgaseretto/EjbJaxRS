package py.pol.una.ii.pw.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import py.pol.una.ii.pw.data.CompraRepository;
import py.pol.una.ii.pw.model.Compra;
import py.pol.una.ii.pw.model.ProductoComprado;
import py.pol.una.ii.pw.service.CompraRegistration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;



/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the compras table.
 */
@Path("/compras")
@RequestScoped
public class CompraResourceRESTService {
    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private CompraRepository repository;

    @Inject
    CompraRegistration registration;
    

     @Context
     private HttpServletRequest request;
   
   
     @POST
     @Path("/upload")
     @Consumes(MediaType.MULTIPART_FORM_DATA)
     public Response uploadFile(MultipartFormDataInput input) throws IOException {
          
         Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

         // Get file data to save
         List<InputPart> inputParts = uploadForm.get("file");

         for (InputPart inputPart : inputParts) {
             try {

                 MultivaluedMap<String, String> header = inputPart.getHeaders();
                 String fileName = getFileName(header);
   
                 // convert the uploaded file to inputstream
                 InputStream inputStream = inputPart.getBody(InputStream.class,
                         null);

                 byte[] bytes = IOUtils.toByteArray(inputStream);
                 // constructs upload file path
                 fileName = "/home/cristhianjbd/Escritorio/file/" + fileName;
   
                 writeFile(bytes, fileName);
                 
                 CompraRegistration bean = (CompraRegistration) request.getSession().getAttribute("compra");
                 
                 if(bean == null){
                     // EJB is not present in the HTTP session
                     // so let's fetch a new one from the container
                     try {
                       InitialContext ic = new InitialContext();
                       bean = (CompraRegistration) ic.lookup("java:global/EjbJaxRS-ear/EjbJaxRS-ejb/CompraRegistration");

                       // put EJB in HTTP session for future servlet calls
                       request.getSession().setAttribute("compra",  bean);
                      
                       bean.compraFile(fileName);

                     } catch (NamingException e) {
                       throw new ServletException(e);
                     }
               }else{
                   Map<String, String> response = new HashMap<String, String>();
                   response.put("error","error");
               }

                 return Response.status(200).entity("Uploaded file name : " + fileName)
                         .build();

             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
         return null;
     }

     private String getFileName(MultivaluedMap<String, String> header) {

         String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

         for (String filename : contentDisposition) {
             if ((filename.trim().startsWith("filename"))) {

                 String[] name = filename.split("=");

                 String finalFileName = name[1].trim().replaceAll("\"", "");
                 return finalFileName;
             }
         }
         return "unknown";
     }

     // Utility method
     private void writeFile(byte[] content, String filename) throws IOException {
         File file = new File(filename);
         if (!file.exists()) {
             System.out.println("not exist> " + file.getAbsolutePath());
             file.createNewFile();
         }
         FileOutputStream fop = new FileOutputStream(file);
         fop.write(content);
         fop.flush();
         fop.close();
     }
 
     
     
     
     

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Compra> listAllCompras() {
        return repository.findAllOrderedByName();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Compra lookupCompraById(@PathParam("id") long id) {
        Compra compra = repository.findById(id);
        if (compra == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return compra;
    }

    /**
     * Creates a new compra from the values provided. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.
     */
    /*
    @POST
    @Path("/iniciar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompra(Compra compra) {

        Response.ResponseBuilder builder = null;

        try {
            // Validates compra using bean validation
            validateCompra(compra);
            
           CompraRegistration bean = (CompraRegistration) request.getSession().getAttribute("compra");
            
           if(bean == null){
               // EJB is not present in the HTTP session
               // so let's fetch a new one from the container
               try {
                 InitialContext ic = new InitialContext();
                 bean = (CompraRegistration) 
                  ic.lookup("java:global/EjbJaxRS-ear/EjbJaxRS-ejb/CompraRegistration");

                 // put EJB in HTTP session for future servlet calls
                 request.getSession().setAttribute("compra",  bean);
                
              	   bean.iniciar(compra);


               } catch (NamingException e) {
                 throw new ServletException(e);
               }
         }else{
             Map<String, String> response = new HashMap<String, String>();
             response.put("error", "ya existe la compra");
         }
           
            // Create an "ok" response
           builder = Response.ok();
        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }
    
    
    @POST
    @Path("/comprar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompra(@QueryParam("opcion") String opcion,ProductoComprado p) {

        Response.ResponseBuilder builder = null;

        try {

               CompraRegistration bean = (CompraRegistration) request.getSession().getAttribute("compra");
			   
			   if(bean != null){
            
			     if(opcion != null && opcion.equalsIgnoreCase("agregar")){
			         bean.addProductos(p);
			       }
    
			     if(opcion != null && opcion.equalsIgnoreCase("eliminar")){
			    	 bean.removeProductos(p);
			       }
         
			   }
       
            // Create an "ok" response
            builder = Response.ok();
        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }
    
    @POST
    @Path("/terminar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmar(@QueryParam("opcion") String opcion) {

        Response.ResponseBuilder builder = null;

        try {
            // Validates compra using bean validation
            CompraRegistration bean = (CompraRegistration) request.getSession().getAttribute("compra");
          
            if(bean != null){
            	 if(opcion != null && opcion.equalsIgnoreCase("confirmar")){
                	   bean.confirmar();
                       request.getSession().setAttribute("compra", null);
                   }   
                   
                 if(opcion != null && opcion.equalsIgnoreCase("cancelar")){
                  	   bean.cancelar();
                       request.getSession().setAttribute( "compra", null);
                     } 

                builder = Response.ok();
            }
            // Create an "ok" response
        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
}*/

    /**
     * <p>
     * Validates the given Compra variable and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.
     * </p>
     * <p>
     * If the error is caused because an existing compra with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.
     * </p>
     * 
     * @param compra Compra to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If compra with the same email already exists
     */
    private void validateCompra(Compra compra) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Compra>> violations = validator.validate(compra);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the email address
        
    }

    /**
     * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can then be used
     * by clients to show violations.
     * 
     * @param violations A set of violations that needs to be reported
     * @return JAX-RS response containing all violations
     */
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<String, String>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }

    
    
    
    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Compra deleteCompraById(@PathParam("id") long id) {
        Compra compra = null;
    	try {
        	compra = repository.findById(id);
        	registration.remove(compra);
            if (compra == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
        } catch (Exception e){
        	log.info(e.toString());
        	compra = null;
        }
        return compra;
    }
}