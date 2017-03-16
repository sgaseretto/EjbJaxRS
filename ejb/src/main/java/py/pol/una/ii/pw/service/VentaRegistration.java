package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.data.CustomerRepository;
import py.pol.una.ii.pw.data.ProductRepository;
import py.pol.una.ii.pw.model.Customer;
import py.pol.una.ii.pw.model.Product;
import py.pol.una.ii.pw.model.ProductoComprado;
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
    
    @Inject
    private CustomerRepository repoCliente;
    
    @Inject
    private ProductRepository repoProducto;
    
    @Inject
    private CustomerRegistration regCliente;
    
    
    private Customer customer;

    public void register(Venta venta) throws Exception {
    	 //Anhadir datos de los productos
        customer = venta.getCustomer();
        customer = repoCliente.findById(customer.getId());
        venta.setCustomer(customer);
        int i = 0;
        for(ProductoComprado pc: venta.getProductos()){
        	Product p = repoProducto.findById(pc.getId());
        	pc.setProducto(p);
        	venta.getProductos().set(i, pc);
        	i++;
        }

        em.merge(venta);
        em.flush();
        //em.persist(venta);
        ventaEventSrc.fire(venta);
      
        
        //Agregar cuenta de cliente 
        Integer cuenta = customer.getCuenta();
        for(ProductoComprado pc: venta.getProductos()){
        	Product p = repoProducto.findById(pc.getId());
        	cuenta = (int) (cuenta + p.getPrice()*pc.getCantidad());
        }
        customer.setCuenta(cuenta);
        regCliente.update(customer);

    }
}