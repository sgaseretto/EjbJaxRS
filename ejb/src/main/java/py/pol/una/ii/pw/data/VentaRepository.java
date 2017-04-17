package py.pol.una.ii.pw.data;

import javax.enterprise.context.ApplicationScoped;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import py.pol.una.ii.pw.mappers.VentaMasivaMapper;
import py.pol.una.ii.pw.model.Venta;
import py.pol.una.ii.pw.util.SqlSessionFactoryMyBatis;

@ApplicationScoped
public class VentaRepository {

    public Venta findById(Long id){
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            VentaMasivaMapper ventaMapper = sqlSession.getMapper(VentaMasivaMapper.class);
            return ventaMapper.findById(id);
        }finally {
            sqlSession.close();
        }
    }

    public List<Venta> findAllOrderedById() {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            VentaMasivaMapper ventaMapper = sqlSession.getMapper(VentaMasivaMapper.class);
            return ventaMapper.findAll();
        } finally {
            sqlSession.close();
        }
    }
}