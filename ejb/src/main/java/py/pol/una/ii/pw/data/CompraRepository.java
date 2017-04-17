
package py.pol.una.ii.pw.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import py.pol.una.ii.pw.mappers.CompraMasivaMapper;
import py.pol.una.ii.pw.mappers.CustomerMapper;
import py.pol.una.ii.pw.model.Compra;
import py.pol.una.ii.pw.util.SqlSessionFactoryMyBatis;

@ApplicationScoped
public class CompraRepository {

    public Compra findById(Long id){
    SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
    try {
        CompraMasivaMapper compraMapper = sqlSession.getMapper(CompraMasivaMapper.class);
        return compraMapper.findById(id);
    }finally {
        sqlSession.close();
    }
}

    public List<Compra> findAllOrderedByName() {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            CompraMasivaMapper compraMapper = sqlSession.getMapper(CompraMasivaMapper.class);
            return compraMapper.findAll();
        } finally {
            sqlSession.close();
        }
    }
}