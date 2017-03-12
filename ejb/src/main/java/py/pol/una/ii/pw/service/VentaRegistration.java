package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.model.Venta;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class VentaRegistration {
	
    @Inject
    private EntityManager em;

    @Inject
    private Event<Venta> ventaEventSrc;


    public void register(Venta venta) throws Exception {
        em.merge(venta);
        em.flush();
        //em.persist(venta);
        ventaEventSrc.fire(venta);
    }
}