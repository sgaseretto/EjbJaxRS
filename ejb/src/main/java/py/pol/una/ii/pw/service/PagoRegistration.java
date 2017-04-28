package py.pol.una.ii.pw.service;

import org.apache.ibatis.session.SqlSession;
import py.pol.una.ii.pw.data.CustomerRepository;
import py.pol.una.ii.pw.mappers.PagoMapper;
import py.pol.una.ii.pw.mappers.ProviderMapper;
import py.pol.una.ii.pw.model.Customer;
import py.pol.una.ii.pw.model.Pago;
import py.pol.una.ii.pw.model.Provider;
import py.pol.una.ii.pw.util.SqlSessionFactoryMyBatis;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class PagoRegistration {

    @Inject
    private Logger log;

    @Inject
    private CustomerRepository repoCliente;
    
    @Inject
    private CustomerRegistration regCliente;
    
    private Customer customer;

    public void register(Pago pago) throws Exception {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            PagoMapper pagoMapper = sqlSession.getMapper(PagoMapper.class);
            pagoMapper.insert(pago);
            customer = pago.getCustomer();
            customer = repoCliente.findById(customer.getId());
            if (customer.getCuenta() > pago.getMonto()){
                Float saldo = customer.getCuenta()-pago.getMonto();
                customer.setCuenta(saldo);
                regCliente.update(customer);
            }else{
                customer.setCuenta((float) 0);
                regCliente.update(customer);
            }
        }catch(Exception e){
            log.info("No se pude insertar correctamente" + e.getMessage());
        }
    }
}