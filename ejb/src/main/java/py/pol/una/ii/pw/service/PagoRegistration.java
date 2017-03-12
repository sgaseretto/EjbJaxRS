package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.model.Pago;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class PagoRegistration {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Pago> pagoEventSrc;

    public void register(Pago pago) throws Exception {
        log.info("Registering " + pago.getId());
        em.persist(pago);
        pagoEventSrc.fire(pago);
    }
}