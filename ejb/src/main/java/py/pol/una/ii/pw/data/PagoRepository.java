package py.pol.una.ii.pw.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.apache.ibatis.session.SqlSession;
import py.pol.una.ii.pw.mappers.PagoMapper;
import py.pol.una.ii.pw.model.Pago;
import py.pol.una.ii.pw.util.SqlSessionFactoryMyBatis;


@ApplicationScoped
public class PagoRepository {

    public Pago findById(Long id) {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            PagoMapper pagoMapper = sqlSession.getMapper(PagoMapper.class);
            return pagoMapper.findById(id);
        }finally {
            sqlSession.close();
        }
    }

    public List<Pago> findAllOrderedById() {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            PagoMapper pagoMapper = sqlSession.getMapper(PagoMapper.class);
            return pagoMapper.findAllOrderedById();
        } finally {
            sqlSession.close();
        }
    }
}