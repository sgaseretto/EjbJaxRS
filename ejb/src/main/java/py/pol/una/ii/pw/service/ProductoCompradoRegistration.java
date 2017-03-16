package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.model.ProductoComprado;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ProductoCompradoRegistration {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<ProductoComprado> productoEventSrc;

    public void register(ProductoComprado producto) throws Exception {
        log.info("Registrando " + producto.getId());
        em.persist(producto);
        productoEventSrc.fire(producto);
    }
    
    public void update(ProductoComprado producto) throws Exception {
    	log.info("Actualizando ProductoComprado, el nuevo nombre es: " + producto.getId());
    	em.merge(producto);
    	em.flush();
    	productoEventSrc.fire(producto);
    }
    
    public void remove(ProductoComprado producto) throws Exception {
    	producto = em.merge(producto);
    	em.remove(producto);
    	em.flush();
    }
}