
package py.pol.una.ii.pw.service;

import org.apache.ibatis.session.SqlSession;
import py.pol.una.ii.pw.mappers.CompraMasivaMapper;
import py.pol.una.ii.pw.model.Compra;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.UserTransaction;

import com.google.gson.Gson;
import py.pol.una.ii.pw.model.ProductoComprado;
import py.pol.una.ii.pw.util.SqlSessionFactoryMyBatis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class CompraRegistration{

    @Inject
    private Logger log;

    @Resource
    private UserTransaction transaccion;

    private Compra compra_en_proceso;

    @PostConstruct
    private void init(){
    	compra_en_proceso = new Compra();
    }
  

    public void compraFile(String fileName) throws Exception{

		boolean fallo = false;
    	transaccion.begin();
    	Gson gson = new Gson();
    	System.out.println("el directorio"+fileName);
		SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
    	try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String sCurrentLine;
			int i =1;
			while ((sCurrentLine = br.readLine()) != null && fallo != true) {

				try{
					compra_en_proceso= gson.fromJson(sCurrentLine, Compra.class);
					register(compra_en_proceso,sqlSession);
					System.out.println("se ha registrado la compra:"+i+ "  " + compra_en_proceso);
					i++;
				}catch(Exception e){
					System.out.println("error al cargar las compras");

					 transaccion.rollback();
					 fallo=true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			sqlSession.close();
		}
		if(fallo==false) {
			try {
				transaccion.commit();
			}catch (Exception e){
				System.out.println("Error al hacer commit");
			}
		}
    }

	public void registerCompra(Compra compra) throws Exception{
		SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
		try {
			register(compra,sqlSession);
		}finally {
			sqlSession.close();
		}
	}

	public void register(Compra compra,SqlSession sqlSession) throws Exception {

		try {
			CompraMasivaMapper compraMapper = sqlSession.getMapper(CompraMasivaMapper.class);
			compraMapper.insert(compra);
			for(ProductoComprado productoComprado: compra.getProductos()){
				compraMapper.insertProductComprado(productoComprado);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id_compra", compra.getId());
				map.put("id_productocomprado", productoComprado.getId());
				compraMapper.insertProduct(map);
			}
		}catch(Exception e){
			log.info("No se pude insertar correctamente" + e.getMessage());
		}
	}

	public int getTamanoLista() {

		SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
		try{
		CompraMasivaMapper compraMapper = sqlSession.getMapper(CompraMasivaMapper.class);
			return compraMapper.getTamanoLista();
		}finally {
			sqlSession.close();
		}
		/*return em.createNamedQuery( "Compra.tamano", Long.class )
				.getSingleResult().intValue();*/
	}

	public List<Compra> listar(int inicio, int cantidad ) {

		SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
		try{
		CompraMasivaMapper compraMapper = sqlSession.getMapper(CompraMasivaMapper.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("inicio", inicio);
		map.put("cantidad", cantidad);
		return compraMapper.listar(map);
		}finally {
			sqlSession.close();
		}

		/* return em.createNamedQuery( "Compra.listar" )
				.setFirstResult( inicio )
				.setMaxResults( cantidad )
				.getResultList();*/
	}


	public void update(Compra compra) throws Exception {
		SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
		try {
			CompraMasivaMapper compraMapper = sqlSession.getMapper(CompraMasivaMapper.class);
			compraMapper.update(compra);
		}catch(Exception e){
			log.info("No se pude actualizar correctamente" + e.getMessage());
		}finally {
			sqlSession.close();
		}
	}

	public void delete(Compra compra) throws Exception {
		SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
		try {
			CompraMasivaMapper compraMapper = sqlSession.getMapper(CompraMasivaMapper.class);
			compraMapper.delete(compra.getId());
		}catch(Exception e){
			log.info("No se pude eliminar correctamente" + e.getMessage());
		}finally {
			sqlSession.close();
		}
	}
    
}