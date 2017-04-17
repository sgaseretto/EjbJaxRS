package py.pol.una.ii.pw.mappers;

import py.pol.una.ii.pw.model.ProductoComprado;
import py.pol.una.ii.pw.model.Venta;

import java.util.List;
import java.util.Map;

/**
 * Created by cristhianjbd on 16/04/17.
 */
public interface VentaMasivaMapper {

    public void insert(Venta venta);
    public void insertProductComprado(ProductoComprado productoComprado);
    public void insertProduct(Map<String, Object> param);
    public Venta findById(Long Id);
    public void update(Venta venta);
    public void delete(Long id);
    public List<Venta> findAll();
    public int getTamanoLista();
    public List<Venta> listar(Map<String, Object> param);

}
