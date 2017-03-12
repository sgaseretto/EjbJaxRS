package py.pol.una.ii.pw.data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.List;

import py.pol.una.ii.pw.model.Venta;

@RequestScoped
public class VentaListProducer {

    @Inject
    private VentaRepository ventaRepository;

    private List<Venta> ventas;

    // @Named provides access the return value via the EL variable name "ventas" in the UI (e.g.,
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Venta> getVentas() {
        return ventas;
    }

    public void onVentaListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Venta venta) {
        retrieveAllVentasOrderedById();
    }

    @PostConstruct
    public void retrieveAllVentasOrderedById() {
        ventas = ventaRepository.findAllOrderedById();
    }
}
