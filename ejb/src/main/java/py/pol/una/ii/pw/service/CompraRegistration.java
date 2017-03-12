
package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.model.Compra;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class CompraRegistration {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Compra> compraEventSrc;
    

    public void register(Compra compra) throws Exception {
    	log.info("Registrando " + compra.getId());
    	em.persist(compra);
        //em.flush();
        //log.info("Registrando " + compra.getId());

        //em.merge(compra);
        //em.flush();
        compraEventSrc.fire(compra);
    }
    
    public void update(Compra compra) throws Exception {
    	log.info("Actualizando Compra, el nuevo nombre es: " + compra.getId());
    	em.merge(compra);
    	em.flush();
    	compraEventSrc.fire(compra);
    }
    
    public void remove(Compra compra) throws Exception {
    	compra = em.merge(compra);
    	em.remove(compra);
    	em.flush();
    }
}