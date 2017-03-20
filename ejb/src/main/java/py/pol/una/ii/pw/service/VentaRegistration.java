package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.data.CustomerRepository;
import py.pol.una.ii.pw.data.ProductRepository;
import py.pol.una.ii.pw.model.Customer;
import py.pol.una.ii.pw.model.Product;
import py.pol.una.ii.pw.model.ProductoComprado;
import py.pol.una.ii.pw.model.Venta;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Remove;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import javax.ejb.EJBContext;
import java.util.logging.Logger;


// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class VentaRegistration {
	
    @Inject
    private Logger log;

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

    @Resource
    private EJBContext context;

    private UserTransaction tx;

    private Venta venta_actual;

    @PostConstruct
    public void initializateBean(){
        venta_actual = new Venta();
    }

    public void register(Venta venta) throws Exception {
    	 //Anhadir datos de los productos
//        customer = venta.getCustomer();
//        customer = repoCliente.findById(customer.getId());
//        venta.setCustomer(customer);
//        int i = 0;
//        for(ProductoComprado pc: venta.getProductos()){
//        	Product p = repoProducto.findById(pc.getProducto().getId());
//        	pc.setProducto(p);
//        	venta.getProductos().set(i, pc);
//        	i++;
//        }

//        em.merge(venta);
//        em.flush();
        //em.persist(venta);
//        ventaEventSrc.fire(venta);
      
        
        //Agregar cuenta de cliente 
//        Integer cuenta = customer.getCuenta();
//        for(ProductoComprado pc: venta.getProductos()){
//        	Product p = repoProducto.findById(pc.getId());
//        	cuenta = (int) (cuenta + p.getPrice()*pc.getCantidad());
//        }
//        customer.setCuenta(cuenta);
//        regCliente.update(customer);
        log.info("Registrando venta de:" + venta.getCustomer());
        tx=context.getUserTransaction();
        venta_actual=venta;
        tx.begin();
        em.persist(venta_actual);
        ventaEventSrc.fire(venta);

    }

    public void agregarCarrito (ProductoComprado pc) throws Exception{
        venta_actual.getProductos().add(pc);
        em.persist(venta_actual);
    }

    public void removeItem(Product p) throws Exception{
        boolean flag = false;
        int n = venta_actual.getProductos().size();
        for (int i=0; i<n; i++){
            ProductoComprado pc = venta_actual.getProductos().get(i);
            if(p.getId().equals(pc.getProducto().getId())){
                venta_actual.getProductos().remove(i);
                em.persist(venta_actual);
                flag = true;
                n--;
            }
        }
        if (!flag){
            log.info("El item a eliminar no existe");
        }
    }

    @Remove
    public void completarVenta(){
        try{
            tx.commit();
            customer = repoCliente.findById(venta_actual.getCustomer().getId());
            //Agregar cuenta del cliente
            double cuenta = customer.getCuenta();
            Product p;
            for(ProductoComprado pc : venta_actual.getProductos()){
                p = repoProducto.findById(pc.getProducto().getId());
                cuenta = cuenta + (p.getPrice() * pc.getCantidad());
            }
            customer.setCuenta(cuenta);
            regCliente.update(customer);
        } catch (Exception e){
            System.out.println("Fallo el commit");
        }
    }

    @Remove
    public void cancelarVenta() throws Exception{
        tx.rollback();
    }


}
