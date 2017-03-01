/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package py.pol.una.ii.pw.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

import py.pol.una.ii.pw.model.Customer;
import py.pol.una.ii.pw.model.Product;

@ApplicationScoped
public class ProductRepository {

    @Inject
    private EntityManager em;

    public Product findById(Long id) {
        return em.find(Product.class, id);
    }

    public Product findByName(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> criteria = cb.createQuery(Product.class);
        Root<Product> product = criteria.from(Product.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).where(cb.equal(member.get(Member_.email), email));
        criteria.select(product).where(cb.equal(product.get("name"), name));
        return em.createQuery(criteria).getSingleResult();
    }
    
    public List<Product> findByNameAndDescription(String name,String descripcion) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> criteria = cb.createQuery(Product.class);
        Root<Product> producto = criteria.from(Product.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(producto).where(cb.equal                m (producto.get(Product.descripcion), descripcion));
        if(name==null){
        	 criteria.select(producto).where(cb.equal(producto.get("descripcion"), descripcion));
        }
        else if(descripcion==null){
        	criteria.select(producto).where(cb.equal(producto.get("name"), name));
        }
        else if(name != null && descripcion!=null){
        criteria.select(producto).where(cb.equal(producto.get("name"), name),cb.equal(producto.get("descripcion"), descripcion));
        }
        return em.createQuery(criteria).getResultList();
    }

    public List<Product> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> criteria = cb.createQuery(Product.class);
        Root<Product> product = criteria.from(Product.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(product).orderBy(cb.asc(product.get(Product_.name)));
        criteria.select(product).orderBy(cb.asc(product.get("name")));
        return em.createQuery(criteria).getResultList();
    }
}
