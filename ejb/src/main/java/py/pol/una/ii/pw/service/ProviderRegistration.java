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
package py.pol.una.ii.pw.service;


import org.apache.ibatis.session.SqlSession;
import py.pol.una.ii.pw.mappers.ProductMapper;
import py.pol.una.ii.pw.mappers.ProviderMapper;
import py.pol.una.ii.pw.model.Product;
import py.pol.una.ii.pw.model.Provider;
import py.pol.una.ii.pw.util.SqlSessionFactoryMyBatis;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ProviderRegistration {

    @Inject
    private Logger log;

    public void register(Provider provider) throws Exception {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            ProviderMapper providerMapper = sqlSession.getMapper(ProviderMapper.class);
            providerMapper.insert(provider);
            sqlSession.commit();
        }catch(Exception e){
            log.info("No se pude insertar correctamente" + e.getMessage());
        } finally {
            sqlSession.close();
        }
    }

    public void update(Provider provider) throws Exception {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            ProviderMapper providerMapper = sqlSession.getMapper(ProviderMapper.class);
            providerMapper.update(provider);
            sqlSession.commit();
        }catch(Exception e){
            log.info("No se pude actualizar correctamente" + e.getMessage());
        }finally {
            sqlSession.close();
        }
    }

    public void delete(Provider provider) throws Exception {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            ProviderMapper providerMapper = sqlSession.getMapper(ProviderMapper.class);
            providerMapper.delete(provider.getId());
            sqlSession.commit();
        }catch(Exception e){
            log.info("No se pude eliminar correctamente" + e.getMessage());
        }finally {
            sqlSession.close();
        }
    }
}

