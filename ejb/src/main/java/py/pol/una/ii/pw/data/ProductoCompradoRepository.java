package py.pol.una.ii.pw.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

import py.pol.una.ii.pw.model.ProductoComprado;


@ApplicationScoped
public class ProductoCompradoRepository {

    @Inject
    private EntityManager em;

    public ProductoComprado findById(Long id) {
        return em.find(ProductoComprado.class, id);
    }

    public List<ProductoComprado> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductoComprado> criteria = cb.createQuery(ProductoComprado.class);
        Root<ProductoComprado> producto = criteria.from(ProductoComprado.class);
        criteria.select(producto).orderBy(cb.asc(producto.get("producto")));
        
        return em.createQuery(criteria).getResultList();
    }
    
    
}