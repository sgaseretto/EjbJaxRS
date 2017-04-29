package py.pol.una.ii.pw.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.ibatis.session.SqlSession;
import py.pol.una.ii.pw.mappers.ProductMapper;
import py.pol.una.ii.pw.mappers.ProductoCompradoMapper;
import py.pol.una.ii.pw.model.Product;
import py.pol.una.ii.pw.model.ProductoComprado;
import py.pol.una.ii.pw.util.SqlSessionFactoryMyBatis;


@ApplicationScoped
public class ProductoCompradoRepository {

    @Inject
    private Logger log;


    public Product findById(Long id) {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            ProductoCompradoMapper productMapper = sqlSession.getMapper(ProductoCompradoMapper.class);
            return productMapper.findById(id);
        }finally {
            sqlSession.close();
        }
    }


    public List<ProductoComprado> findAllOrderedByName() {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            ProductoCompradoMapper productMapper = sqlSession.getMapper(ProductoCompradoMapper.class);
            return productMapper.findAllOrderedByName();
        } finally {
            sqlSession.close();
        }
    }
    
    
}