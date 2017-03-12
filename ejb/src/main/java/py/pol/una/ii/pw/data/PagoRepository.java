package py.pol.una.ii.pw.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import py.pol.una.ii.pw.model.Pago;


@ApplicationScoped
public class PagoRepository {

    @Inject
    private EntityManager em;

    public Pago findById(Long id) {
        return em.find(Pago.class, id);
    }
    
    public List<Pago> findAllOrderedById() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pago> criteria = cb.createQuery(Pago.class);
        Root<Pago> pago = criteria.from(Pago.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(pago).orderBy(cb.asc(pago.get(Pago_.id)));
        criteria.select(pago).orderBy(cb.asc(pago.get("id")));
        return em.createQuery(criteria).getResultList();
}
}