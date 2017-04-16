package py.pol.una.ii.pw.mappers;

import py.pol.una.ii.pw.model.Provider;

import java.util.List;
import java.util.Map;

/**
 * Created by cristhianjbd on 15/04/17.
 */
public interface ProviderMapper {

    public void insert(Provider provider);
    public void update(Provider provider);
    public void delete(Long id);
    public Provider findById(Long Id);
    public Provider findByEmail(String name);
    public List<Provider> findByNameAndEmail(Map<String, Object> param);
    public List<Provider> findAllOrderedByName();

}
