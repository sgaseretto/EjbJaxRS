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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.ibatis.session.SqlSession;
import py.pol.una.ii.pw.mappers.ProductMapper;
import py.pol.una.ii.pw.model.Product;
import py.pol.una.ii.pw.util.SqlSessionFactoryMyBatis;

@ApplicationScoped
public class ProductRepository {
    @Inject
    private Logger log;


    public Product findById(Long id) {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            ProductMapper productMapper = sqlSession.getMapper(ProductMapper.class);
            return productMapper.findById(id);
        }finally {
            sqlSession.close();
        }
    }

    public Product findByName(String name) {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            ProductMapper productMapper = sqlSession.getMapper(ProductMapper.class);
            return productMapper.findByName(name);
        } finally {
            sqlSession.close();
        }
    }

    public List<Product> findByNameAndDescription(String name,String descripcion) {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            ProductMapper productMapper = sqlSession.getMapper(ProductMapper.class);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", name);
            map.put("descripcion", descripcion);
            return productMapper.findByNameAndDescription(map);
        } finally {
            sqlSession.close();
        }
    }


    public List<Product> findAllOrderedByName() {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            ProductMapper productMapper = sqlSession.getMapper(ProductMapper.class);
            return productMapper.findAllOrderedByName();
        } finally {
            sqlSession.close();
        }
    }
}
