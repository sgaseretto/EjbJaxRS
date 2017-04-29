package py.pol.una.ii.pw.mappers;

import py.pol.una.ii.pw.model.Product;
import py.pol.una.ii.pw.model.ProductoComprado;

import java.util.List;
import java.util.Map;

/**
 * Created by cristhianjbd on 28/04/17.
 */
public interface ProductoCompradoMapper {

    public void register(ProductoComprado pc);
    public void delete(Long id);
    public Product findById(Long Id);
    public List<ProductoComprado> findAllOrderedByName();


}
