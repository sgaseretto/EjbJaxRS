package py.pol.una.ii.pw.data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.List;

import py.pol.una.ii.pw.model.ProductoComprado;

@RequestScoped
public class ProductoCompradoListProducer {

    @Inject
    private ProductoCompradoRepository productoRepository;

    private List<ProductoComprado> productos;

    // @Named provides access the return value via the EL variable name "members" in the UI (e.g.,
    // Facelets or JSP view)
    @Produces
    @Named
    public List<ProductoComprado> getProductosComprados() {
        return productos;
    }

    public void onProveedorListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final ProductoComprado producto) {
        retrieveAllProductosOrderedByName();
    }

    @PostConstruct
    public void retrieveAllProductosOrderedByName() {
        productos = productoRepository.findAllOrderedByName();
    }
}