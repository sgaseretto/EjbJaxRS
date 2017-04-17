package py.pol.una.ii.pw.mappers;

import py.pol.una.ii.pw.model.Compra;
import py.pol.una.ii.pw.model.ProductoComprado;

import java.util.List;
import java.util.Map;

/**
 * Created by cristhianjbd on 16/04/17.
 */
public interface CompraMasivaMapper {
    public void insert(Compra compra);
    public void insertProductComprado(ProductoComprado productoComprado);
    public void insertProduct(Map<String, Object> param);
    public Compra findById(Long Id);
    public void update(Compra compra);
    public void delete(Long id);
    public List<Compra> findAll();
    public int getTamanoLista();
    public List<Compra> listar(Map<String, Object> param);


}
