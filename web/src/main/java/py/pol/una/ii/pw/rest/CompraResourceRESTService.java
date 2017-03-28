package py.pol.una.ii.pw.rest;

import java.io.*;
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
import javax.ws.rs.core.*;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import py.pol.una.ii.pw.data.CompraRepository;
import py.pol.una.ii.pw.model.Compra;
import py.pol.una.ii.pw.model.ProductoComprado;
import py.pol.una.ii.pw.service.CompraRegistration;


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
                     request.getSession().setAttribute("compra",  bean);
                     bean.compraFile(fileName);
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



    protected Response.ResponseBuilder getNoCacheResponseBuilder( Response.Status status ) {
        CacheControl cc = new CacheControl();
        cc.setNoCache( true );
        cc.setMaxAge( -1 );
        cc.setMustRevalidate( true );

        return Response.status( status ).cacheControl( cc );
    }


    @GET
    @Path( "/download" )
    @Produces( "application/json" )
    public Response streamGenerateCompras() {

        return getNoCacheResponseBuilder( Response.Status.OK ).entity( new StreamingOutput() {

            // Instruct how StreamingOutput's write method is to stream the data
            @Override
            public void write( OutputStream os ) throws IOException, WebApplicationException {
                int tamano = 10;                      // Number of records for every round trip to the database
                int inicio = 0;                             // Initial record position index
                int tamanoTotalLista = registration.getTamanoLista();   // Total records found for the query

                // Empezar el streaming de datos
                try ( PrintWriter writer = new PrintWriter( new BufferedWriter( new OutputStreamWriter( os ) ) ) ) {

                    writer.print( "[" );

                    while ( tamanoTotalLista > 0 ) {
                        // Conseguir los datos paginados de la BD
                        List<Compra> compras = registration.listar( inicio, tamano );
                        Gson gs = new Gson();
                        for ( Compra compra : compras ) {
                            if ( inicio > 0 ) {
                                writer.print( "," );
                            }

                            // Stream de los datos en json

                            writer.print(gs.toJson(compra));

                            // Aumentar la posicion de la pagina
                            inicio++;
                        }

                        // Actualizar el numero de datos restantes
                        tamanoTotalLista -= tamano;
                    }

                    // Se termina el json
                    writer.print( "]" );
                }
            }
        } ).build();
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