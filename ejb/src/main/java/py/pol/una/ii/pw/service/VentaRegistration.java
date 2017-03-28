package py.pol.una.ii.pw.service;

import com.google.gson.Gson;
import py.pol.una.ii.pw.data.CustomerRepository;
import py.pol.una.ii.pw.data.ProductRepository;
import py.pol.una.ii.pw.model.*;

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateful
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
    private UserTransaction transaccion;

    private Venta venta_en_proceso;
    
    private Customer customer;
    
    
    @PostConstruct
    private void init(){
        venta_en_proceso = new Venta();
    }

    public void ventaFile(String fileName) throws Exception{
        boolean fallo = false;
        transaccion.begin();
        Gson gson = new Gson();
        System.out.println("el directorio"+fileName);
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String sCurrentLine;
            int i =1;
            while ((sCurrentLine = br.readLine()) != null && fallo != true) {

                try{
                    venta_en_proceso= gson.fromJson(sCurrentLine, Venta.class);
                    System.out.println("se ha registrado la venta:"+i+ "  " +venta_en_proceso);
                    em.persist(venta_en_proceso);
                    i++;
                }catch(Exception e){
                    System.out.println("error al cargar las ventas");

                    transaccion.rollback();
                    fallo=true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(fallo==false) {
            try {
                transaccion.commit();
            }catch (Exception e){
                System.out.println("Error al hacer commit");
            }
        }
    }


    public int getTamanoLista() {
        return em.createNamedQuery( "Venta.tamano", Long.class )
                .getSingleResult().intValue();
    }

    public List<Venta> listar(int inicio, int cantidad ) {
        return em.createNamedQuery( "Venta.listar" )
                .setFirstResult( inicio )
                .setMaxResults( cantidad )
                .getResultList();
    }





    public void update(Venta venta) throws Exception {
    	log.info("Actualizando Venta, el nuevo nombre es: " + venta.getId());
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
    
