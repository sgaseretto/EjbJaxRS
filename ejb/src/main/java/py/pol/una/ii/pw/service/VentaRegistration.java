package py.pol.una.ii.pw.service;


import org.apache.ibatis.session.SqlSession;
import py.pol.una.ii.pw.mappers.VentaMasivaMapper;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.UserTransaction;

import com.google.gson.Gson;
import py.pol.una.ii.pw.model.ProductoComprado;
import py.pol.una.ii.pw.model.Venta;
import py.pol.una.ii.pw.util.SqlSessionFactoryMyBatis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateful
@TransactionManagement(TransactionManagementType.BEAN)
public class VentaRegistration{

    @Inject
    private Logger log;

    @Resource
    private UserTransaction transaccion;

    private Venta venta_en_proceso;

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
                    register(venta_en_proceso);
                    System.out.println("se ha registrado la compra:"+i+ "  " + venta_en_proceso);
                    i++;
                }catch(Exception e){
                    System.out.println("error al cargar las compras");

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

    public void register(Venta venta) throws Exception {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            VentaMasivaMapper ventaMapper = sqlSession.getMapper(VentaMasivaMapper.class);
            ventaMapper.insert(venta);
            for(ProductoComprado productoComprado: venta.getProductos()){
                ventaMapper.insertProductComprado(productoComprado);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id_venta", venta.getId());
                map.put("id_productocomprado", productoComprado.getId());
                ventaMapper.insertProduct(map);
            }
            sqlSession.commit();
        }catch(Exception e){
            log.info("No se pude insertar correctamente" + e.getMessage());
        } finally {
            sqlSession.close();
        }
    }

    public int getTamanoLista() {

        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            VentaMasivaMapper ventaMapper = sqlSession.getMapper(VentaMasivaMapper.class);
            return ventaMapper.getTamanoLista();
        }finally {
            sqlSession.close();
        }
    }

    public List<Venta> listar(int inicio, int cantidad ) {


        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            VentaMasivaMapper ventaMapper = sqlSession.getMapper(VentaMasivaMapper.class);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("inicio", inicio);
            map.put("cantidad", cantidad);
            return ventaMapper.listar(map);
        }finally {
            sqlSession.close();
        }
    }


    public void update(Venta venta) throws Exception {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            VentaMasivaMapper ventaMapper = sqlSession.getMapper(VentaMasivaMapper.class);
            ventaMapper.update(venta);
            sqlSession.commit();
        }catch(Exception e){
            log.info("No se pude actualizar correctamente" + e.getMessage());
        }finally {
            sqlSession.close();
        }
    }

    public void delete(Venta venta) throws Exception {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            VentaMasivaMapper ventaMapper = sqlSession.getMapper(VentaMasivaMapper.class);
            ventaMapper.delete(venta.getId());
            sqlSession.commit();
        }catch(Exception e){
            log.info("No se pude eliminar correctamente" + e.getMessage());
        }finally {
            sqlSession.close();
        }
    }

}