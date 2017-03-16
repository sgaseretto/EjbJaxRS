
package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.data.ProductRepository;
import py.pol.una.ii.pw.data.ProviderRepository;
import py.pol.una.ii.pw.model.Compra;
import py.pol.una.ii.pw.model.Product;
import py.pol.una.ii.pw.model.ProductoComprado;
import py.pol.una.ii.pw.model.Provider;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class CompraRegistration {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Compra> compraEventSrc;
    
    @Inject
    private ProductRepository repoProducto;
    
    @Inject
    private ProviderRepository repoProveedor;

    

    public void register(Compra compra) throws Exception {
    	log.info("Registrando " + compra.getId());
    	
        //Rellenar los datos necesarios
        Provider proveedor = repoProveedor.findById(compra.getProvider().getId());
        compra.setProvider(proveedor);
        int i=0;
        for(ProductoComprado pc: compra.getProductos()){
        	Product p= repoProducto.findById(pc.getProducto().getId());
        	pc.setProducto(p);
        	compra.getProductos().set(i, pc);
        	i++;
        }
        
    	em.persist(compra);

        compraEventSrc.fire(compra);
    }
    
    public void update(Compra compra) throws Exception {
    	log.info("Actualizando Compra, el nuevo nombre es: " + compra.getId());
    	em.merge(compra);
    	em.flush();
    	compraEventSrc.fire(compra);
    }
    
    public void remove(Compra compra) throws Exception {
    	compra = em.merge(compra);
    	em.remove(compra);
    	em.flush();
    }
}