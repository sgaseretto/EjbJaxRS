
package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.model.Compra;
import py.pol.una.ii.pw.model.ProductoComprado;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.StatefulTimeout;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.spi.Context;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateful
@TransactionManagement(TransactionManagementType.BEAN)
public class CompraRegistration{

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Compra> compraEventSrc;
    
   // @Resource
   // private EJBContext context;
    @Resource
    private UserTransaction transaccion;

    private Compra compra_en_proceso;
    
    
    
    @PostConstruct
    private void init(){
    	compra_en_proceso = new Compra();
    }
  

    public void compraFile(String fileName) throws Exception{
    	//transaccion = context.getUserTransaction();
    	transaccion.begin();
    	Gson gson = new Gson();
    	System.out.println("el directorio"+fileName);
    	try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

			String sCurrentLine;
			int i = 1;
			while ((sCurrentLine = br.readLine()) != null) {
				compra_en_proceso= gson.fromJson(sCurrentLine, Compra.class);
				try{
					System.out.println("se ha registrado la compra:"+compra_en_proceso);
					em.persist(compra_en_proceso);
				}catch(Exception e){
					System.out.println("error al cargar las compras");
					 transaccion.rollback();
					 
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

    	transaccion.commit();
    }
    
    
    
    
    
  /*  
     @Remove
    public void confirmar() throws Exception {
    	transaccion.commit();
    }
    @Remove
    public void cancelar() throws Exception {
        transaccion.rollback();
    }
    

    public void iniciar(Compra compra) throws Exception{
    	compra_en_proceso = compra;
        transaccion = context.getUserTransaction();
        transaccion.begin();
        em.persist(compra_en_proceso);
          	
    }

    public void addProductos(ProductoComprado producto_agregado) {	
       compra_en_proceso.getProductos().add(producto_agregado);
        em.persist(compra_en_proceso);
    }

    public void removeProductos(ProductoComprado producto_a_eliminar) {
        int cont = 0;
        int aux = 0;
    	for(ProductoComprado pc: compra_en_proceso.getProductos()){
        	if(pc.getProducto().getId().equals(producto_a_eliminar.getProducto().getId())){
        	aux = cont;
        	}
        	cont++;
        }
    	compra_en_proceso.getProductos().remove(aux);
        em.persist(compra_en_proceso);
    }
    
    
    */
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