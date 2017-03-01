/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package py.pol.una.ii.pw.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.pol.una.ii.pw.data.ProductRepository;
import py.pol.una.ii.pw.model.Customer;
import py.pol.una.ii.pw.model.Product;
import py.pol.una.ii.pw.service.ProductRegistration;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the members table.
 */
@Path("/products")
@RequestScoped
public class ProductResourceRESTService {
    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private ProductRepository repository;

    @Inject
    ProductRegistration registration;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> listAllProduct() {
        return repository.findAllOrderedByName();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Product lookupProductById(@PathParam("id") long id) {
        Product product = repository.findById(id);
        if (product == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return product;
    }
    

    /**
     * Creates a new product from the values provided. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProduct(Product product) {

        Response.ResponseBuilder builder = null;

        try {
            // Validates member using bean validation
            validateProduct(product);

            registration.register(product);

            // Create an "ok" response
            builder = Response.ok();
        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("name", "Name taken");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }
    
    
    @PUT
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Product UpdateProduct(@PathParam("id") long id,Product producto) throws Exception{
        	validateProduct(producto);
       
        	producto.setId(id);
            registration.update(producto);
            
            return producto;
    	
    }
    
    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Product DeleteProduct(@PathParam("id") long id) throws Exception{
    	Product producto = repository.findById(id);
    	try{
    	
    	  if (producto == null) {
              throw new WebApplicationException(Response.Status.NOT_FOUND);
          }
    	
    	
    	registration.delete(producto);
    	log.info("Updating " + producto.getName());
    	}
    	catch(Exception e){
    	}
    	
    	
    	return producto;
    	
    }
    
    @GET
    @Path("/data")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> searchMember(@QueryParam("name") String name, @QueryParam("descripcion") String descripcion ){
    	log.info("name " + name);
    	log.info("descripcion " + descripcion);
    	List<Product> productos = repository.findByNameAndDescription(name,descripcion);
    	log.info("lista" + productos);
    	
    	
    	return productos;
    }

    /**
     * <p>
     * Validates the given Member variable and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.
     * </p>
     * <p>
     * If the error is caused because an existing member with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.
     * </p>
     * 
     * @param member Member to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If member with the same email already exists
     */
    private void validateProduct(Product product) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the email address
        if (nameAlreadyExists(product.getName())) {
            throw new ValidationException("Unique Name Violation");
        }
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

    /**
     * Checks if a member with the same name is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "name")" constraint from the Member class.
     * 
     * @param name The name to check
     * @return True if the name already exists, and false otherwise
     */
    public boolean nameAlreadyExists(String name) {
        Product product = null;
        try {
            product = repository.findByName(name);
        } catch (NoResultException e) {
            // ignore
        }
        return product != null;
    }
}