package py.pol.una.ii.pw.mappers;

import py.pol.una.ii.pw.model.Product;

import java.util.List;
import java.util.Map;

/**
 * Created by cristhianjbd on 15/04/17.
 */
public interface ProductMapper {

    public void insert(Product product);
    public void update(Product product);
    public void delete(Long id);
    public Product findById(Long Id);
    public Product findByName(String name);
    public List<Product> findByNameAndDescription(Map<String, Object> param);
    public List<Product> findAllOrderedByName();


}
