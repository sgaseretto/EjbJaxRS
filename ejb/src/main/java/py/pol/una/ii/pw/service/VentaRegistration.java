package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.data.CustomerRepository;
import py.pol.una.ii.pw.data.ProductRepository;
import py.pol.una.ii.pw.model.Customer;
import py.pol.una.ii.pw.model.Product;
import py.pol.una.ii.pw.model.ProductoComprado;
import py.pol.una.ii.pw.model.Venta;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.StatefulTimeout;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateful
@StatefulTimeout(unit = TimeUnit.MINUTES, value = 30)
@TransactionManagement(TransactionManagementType.BEAN)
public class VentaRegistration{

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Venta> ventaEventSrc;
    
    @Inject
    private CustomerRegistration regCliente;

    @Inject
    private CustomerRepository repoCustomer;

    @Inject
    private ProductRepository repoProduct;
    
    @Resource
    private EJBContext context;

    private UserTransaction transaccion;

    private Venta venta_en_proceso;
    
    private Customer customer;
    
    
    @PostConstruct
    private void init(){
    	venta_en_proceso = new Venta();
    }
    
    @Remove
    public void confirmar() throws Exception {
        try {
            transaccion.commit();
            customer = repoCustomer.findById(venta_en_proceso.getCustomer().getId());
            //Agregar cuenta de cliente
            Float cuenta = customer.getCuenta();
            for (ProductoComprado pc : venta_en_proceso.getProductos()) {
                Product p = repoProduct.findById(pc.getProducto().getId());
                cuenta = (float) (cuenta + (p.getPrice() * pc.getCantidad()));
            }
            customer.setCuenta(cuenta);
            regCliente.update(customer);

        } catch (Exception e){
            System.out.println("Fallo el commit");
        }

    }
    
    @Remove
    public void cancelar() throws Exception {
        transaccion.rollback();
    }
    

    public void iniciar(Venta venta) throws Exception{
    	venta_en_proceso = venta;
        transaccion = context.getUserTransaction();
        transaccion.begin();
        em.persist(venta_en_proceso);    	
    }

    public void addProductos(ProductoComprado producto_agregado) {	
    	venta_en_proceso.getProductos().add(producto_agregado);
        em.persist(venta_en_proceso);
    }

    public void removeProductos(ProductoComprado producto_a_eliminar) {
        int cont = 0;
        int aux = 0;
    	for(ProductoComprado pc: venta_en_proceso.getProductos()){
        	if(pc.getProducto().getId().equals(producto_a_eliminar.getProducto().getId())){
        	aux = cont;
        	}
        	cont++;
        }
    	venta_en_proceso.getProductos().remove(aux);
        em.persist(venta_en_proceso);
    }
    
    public void update(Venta venta) throws Exception {
    	log.info("Actualizando Compra, el nuevo nombre es: " + venta.getId());
    	em.merge(venta);
    	em.flush();
    	ventaEventSrc.fire(venta);
    }
    
    public void remove(Venta venta) throws Exception {
    	venta = em.merge(venta);
    	em.remove(venta);
    	em.flush();
    }
    
}
    
