package py.pol.una.ii.pw.mappers;

import py.pol.una.ii.pw.model.Pago;

import java.util.List;

/**
 * Created by cristhianjbd on 16/04/17.
 */
public interface PagoMapper {
    public void insert(Pago pago);
    public Pago findById(Long Id);
    public List<Pago> findAllOrderedById();
}
