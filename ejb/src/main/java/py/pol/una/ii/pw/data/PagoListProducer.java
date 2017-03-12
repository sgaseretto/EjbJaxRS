package py.pol.una.ii.pw.data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.List;

import py.pol.una.ii.pw.model.Pago;

@RequestScoped
public class PagoListProducer {

    @Inject
    private PagoRepository pagoRepository;

    private List<Pago> pagos;

    // @Named provides access the return value via the EL variable name "pagos" in the UI (e.g.,
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Pago> getPagos() {
        return pagos;
    }

    public void onPagoListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Pago pago) {
        retrieveAllPagosOrderedById();
    }

    @PostConstruct
    public void retrieveAllPagosOrderedById() {
        pagos = pagoRepository.findAllOrderedById();
    }
}