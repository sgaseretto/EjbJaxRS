
package py.pol.una.ii.pw.data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import py.pol.una.ii.pw.model.Compra;

@RequestScoped
public class CompraListProducer {

    @Inject
    private CompraRepository compraRepository;

    private List<Compra> compras;

    // @Named provides access the return value via the EL variable name "compras" in the UI (e.g.,
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Compra> getCompras() {
        return compras;
    }

    public void onCompraListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Compra compra) {
        retrieveAllComprasOrderedByName();
    }

    @PostConstruct
    public void retrieveAllComprasOrderedByName() {
        compras = compraRepository.findAllOrderedByName();
    }
}