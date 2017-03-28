
package py.pol.una.ii.pw.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

import py.pol.una.ii.pw.model.Compra;

@ApplicationScoped
public class CompraRepository {

    @Inject
    private EntityManager em;

    public Compra findById(Long id) {
        return em.find(Compra.class, id);
    }

    
    public List<Compra> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Compra> criteria = cb.createQuery(Compra.class);
        Root<Compra> compra = criteria.from(Compra.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(compra).orderBy(cb.asc(compra.get(Compra_.name)));
        criteria.select(compra).orderBy(cb.asc(compra.get("id")));
        return em.createQuery(criteria).getResultList();

    }
}