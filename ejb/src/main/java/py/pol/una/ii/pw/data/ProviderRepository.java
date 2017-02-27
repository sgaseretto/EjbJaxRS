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

import py.pol.una.ii.pw.model.Provider;

@ApplicationScoped
public class ProviderRepository {

    @Inject
    private EntityManager em;

    public Provider findById(Long id) {
        return em.find(Provider.class, id);
    }

    public Provider findByEmail(String email) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Provider> criteria = cb.createQuery(Provider.class);
        Root<Provider> provider = criteria.from(Provider.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).where(cb.equal(member.get(Member_.email), email));
        criteria.select(provider).where(cb.equal(provider.get("email"), email));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<Provider> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Provider> criteria = cb.createQuery(Provider.class);
        Root<Provider> provider = criteria.from(Provider.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(provider).orderBy(cb.asc(provider.get(Provider_.name)));
        criteria.select(provider).orderBy(cb.asc(provider.get("name")));
        return em.createQuery(criteria).getResultList();
    }
}
