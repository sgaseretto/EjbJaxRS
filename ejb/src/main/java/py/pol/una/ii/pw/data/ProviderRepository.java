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
import py.pol.una.ii.pw.mappers.ProviderMapper;
import py.pol.una.ii.pw.model.Provider;
import py.pol.una.ii.pw.util.SqlSessionFactoryMyBatis;

@ApplicationScoped
public class ProviderRepository {
    @Inject
    private Logger log;


    public Provider findById(Long id) {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            ProviderMapper providerMapper = sqlSession.getMapper(ProviderMapper.class);
            return providerMapper.findById(id);
        }finally {
            sqlSession.close();
        }
    }

    public Provider findByEmail(String email) {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            ProviderMapper providerMapper = sqlSession.getMapper(ProviderMapper.class);
            return providerMapper.findByEmail(email);
        } finally {
            sqlSession.close();
        }
    }

    public List<Provider> findByNameAndEmail(String name,String email) {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            ProviderMapper providerMapper = sqlSession.getMapper(ProviderMapper.class);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", name);
            map.put("email", email);
            return providerMapper.findByNameAndEmail(map);
        } finally {
            sqlSession.close();
        }
    }


    public List<Provider> findAllOrderedByName() {
        SqlSession sqlSession = SqlSessionFactoryMyBatis.getSqlSessionFactory().openSession();
        try {
            ProviderMapper providerMapper = sqlSession.getMapper(ProviderMapper.class);
            return providerMapper.findAllOrderedByName();
        } finally {
            sqlSession.close();
        }
    }
}
