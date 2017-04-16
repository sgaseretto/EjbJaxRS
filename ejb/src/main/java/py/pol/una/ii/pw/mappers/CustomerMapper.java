package py.pol.una.ii.pw.mappers;

import org.apache.ibatis.annotations.Param;
import py.pol.una.ii.pw.model.Customer;

import java.util.List;
import java.util.Map;

/**
 * Created by cristhianjbd on 14/04/17.
 */
public interface CustomerMapper {

    public void insert(Customer customer);
    public void update(Customer customer);
    public void delete(Long id);
    public Customer findById(Long Id);
    public Customer findByEmail(String email);
    public List<Customer> findByNameAndEmail(Map<String, Object> param);
    public List<Customer> findAllOrderedByName();


}
