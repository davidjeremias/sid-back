package br.com.caixa.sidce.util.infraestructure.service;

import static org.springframework.data.domain.ExampleMatcher.matching;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.annotation.Transactional;

import br.com.caixa.sidce.util.infraestructure.domain.model.*;
import br.com.caixa.sidce.util.infraestructure.exception.InternalException;
import br.com.caixa.sidce.util.infraestructure.jpa.RepositoryBase;
import br.com.caixa.sidce.util.infraestructure.log.Log;

public class RestFullService<E extends EntidadeBase<PK>, PK extends Serializable> {

    private static final String PAGE_KEY = "page";
    private static final String PAGELIMIT_KEY = "limit";
    private static final String SORT_KEY = "sort";

    private static final int PAGELIMIT_DEFAULT = 10;
    private static final String DESC_KEY = "desc";
    private static Map<String,String> ignoreKeys = new HashMap<>();

    private Class<E> modelClass;
    protected final RepositoryBase<E, PK> repository;

    @SuppressWarnings("unchecked")
    private void loadTypes() {
        if(modelClass == null) {
            final ParameterizedType type = (ParameterizedType)getClass().getGenericSuperclass();
            modelClass = (Class<E>)type.getActualTypeArguments()[0];
        }
    }

    protected RestFullService(RepositoryBase<E, PK> repository) {
        this.repository = repository;
        loadTypes();
    }

    protected RepositoryBase<E, PK> getRepository() {
        return repository;
    }

    public List<E> findAll() {
        return repository.findAll();
    }

    public List<E> findAll(Example<E> example) {
        return repository.findAll(example);
    }

    public List<E> findAll(Example<E> example, Sort sort) {
        return repository.findAll(example, sort);
    }

    public List<E> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    public List<E> findAll(Map<String, String[]> params) {
        return repository.findAll(getExample(params), getSort(params));
    }

    public Page<E> findAllPageable(Map<String, String[]> params) {
        return repository.findAll(getExample(params), getPageRequest(params));
    }

    public Optional<E> findOne(PK id) {
        return repository.findById(id);
    }

    @Transactional
    public void delete(PK id) { //NOSONAR

        // MARCADO COM A INTERFACE "ExclusaoLogica", UPDATE
        for(Class<?> iface : modelClass.getInterfaces()) {
            if(iface == ExclusaoLogica.class) {
                Optional<E> e = repository.findById(id);
                if(e.isPresent()) {
                	((ExclusaoLogica)e.get()).setExcluido(SimNao.S);
                	repository.save(e.get());
                }
                return;
            }
        }

        // SEM MARCAÇÃO, APAGA!
        repository.deleteById(id);
    }

    @Transactional
    public E update(E e) {
        return save(e);
    }

    @Transactional
    public E add(E e) {
        if(e instanceof ExclusaoLogica && ((ExclusaoLogica)e).getExcluido() == null) {
            ((ExclusaoLogica)e).setExcluido(SimNao.N);
        }
        return save(e);
    }

    @Transactional
    public E addOrUpdate(E e) {
        if(e.getId() == null) {
            return add(e);
        } else {
            return update(e);
        }
    }

    @Transactional
    public E save(E e) {
        return repository.save(e);
    }

    protected Pageable getPageRequest(Map<String, String[]> params) {

        int page = 0;
        int limit = 10;

        String[] pageParam = params.get(PAGE_KEY);
        if(pageParam != null) {
            page = Integer.parseInt(pageParam[0]);
            page = page < 0 ? 0 : page;
        }

        String[] limitParam = params.get(PAGELIMIT_KEY);
        if(limitParam != null) {
            limit = Integer.parseInt(limitParam[0]);
            limit = limit < 1 ? PAGELIMIT_DEFAULT : limit;
        }

        return PageRequest.of(page, limit, getSort(params));

    }

    protected Sort getSort(Map<String, String[]> params) {

        String[] sortParams = params.get(SORT_KEY);
        if(sortParams != null && sortParams.length > 0 ) {

            List<Order> orders = new ArrayList<>();
            for(String sort : sortParams) {

                Sort.Direction direction = Sort.Direction.ASC;
                if(params.get(sort + ".dir").length > 0 && params.get(sort + ".dir")[0].equalsIgnoreCase(DESC_KEY)) {
                    direction = Sort.Direction.DESC;
                }
                orders.add(new Order(direction, sort));
            }

            return Sort.by(orders);
        }

        return Sort.by(new Order(Sort.Direction.ASC, "id"));
    }

    protected ExampleMatcher getExampleMatcher(E obj, Map<String, String[]> params) {

        if(ignoreKeys.isEmpty()) {
            ignoreKeys.put(SORT_KEY,SORT_KEY);
            ignoreKeys.put(PAGE_KEY,PAGE_KEY);
            ignoreKeys.put(PAGELIMIT_KEY,PAGELIMIT_KEY);
        }

        try {

            ExampleMatcher em = matching();

            if(obj instanceof ExclusaoLogica) {
                ((ExclusaoLogica) obj).setExcluido(SimNao.N);
            }

            for(String key : params.keySet()) {

              	String[] values = params.get(key);
                
            	if(ignoreKeys.containsKey(key) || values.length != 1) {
                    continue;
                }

                Method m = getMethodFromProperty(key);
                if(m != null) {
                    Class<?> paramType = m.getParameterTypes()[0];

                    // TRATAMENTOS DE PARAMETROS DO TIPO ENUM
                    if(paramType.isEnum()) {
                        invokeEnum(obj, m, values[0]);

                    // TRATAMENTOS DE PARAMETROS DO TIPO LONG
                    } else if(paramType.isAssignableFrom(Long.class)) {
                        invokeLong(obj, m, values[0]);

                    // TRATAMENTOS DE PARAMETROS DO TIPO STRING
                    } else if(paramType.isAssignableFrom(String.class)) {
                        em = invokeString(obj, m, values[0], key);

                    }
                }
            }

            return em;

        } catch(IllegalAccessException | InvocationTargetException ex) {
            throw new InternalException("problemas-classe-findall", ex);
        }

    }

    protected Example<E> getExample(Map<String, String[]> params) {
        try {
            E obj = modelClass.newInstance();
            return Example.of(obj, getExampleMatcher(obj, params));
        } catch(IllegalAccessException | InstantiationException ex) {
            throw new InternalException("problemas-classe-findall", ex);
        }
    }

    protected Method getMethodFromProperty(String property) {
        String mName = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
        for(Method m : modelClass.getMethods()) {
            if(m.getName().equals(mName) && m.getParameterTypes().length == 1) {
                return m;
            }
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void invokeEnum(Object obj, Method m, String value)
        throws IllegalAccessException, InvocationTargetException { //NOSONAR
        try {
            Class<?> paramType = m.getParameterTypes()[0];
            Class<Enum> ecl = (Class<Enum>)paramType;
            Enum<?> v = Enum.valueOf(ecl, value);
            if(v != null) {
                m.invoke(obj, v);
            }
        } catch(IllegalArgumentException ex) {
            Log.warn(this.getClass(), "Valor não válido para o enum, desconsiderando.",ex);
        }
    }

    private void invokeLong(Object obj, Method m, String value)
        throws IllegalAccessException, InvocationTargetException { //NOSONAR
        try {
            m.invoke(obj, new Long(value));
        } catch(IllegalArgumentException ex) {
            Log.warn(this.getClass(), "Valor numérico não válido, desconsiderando.",ex);
        }
    }

    private ExampleMatcher invokeString(Object obj, Method m, String value, String key)
        throws IllegalAccessException, InvocationTargetException { //NOSONAR
        ExampleMatcher em = matching();
        try {

            if(value.endsWith("*") && value.startsWith("*")) {
                em = matching().withMatcher(key, matcher -> matcher.contains().ignoreCase());

            } else if(value.startsWith("*")) {
                em = matching().withMatcher(key, matcher -> matcher.endsWith().ignoreCase());

            } else if(value.endsWith("*")) {
                em = matching().withMatcher(key, matcher -> matcher.startsWith().ignoreCase());

            } else {
                em = matching().withMatcher(key, matcher -> matcher.ignoreCase());
            }

            m.invoke(obj, value.replaceAll("\\*", ""));

        } catch(IllegalArgumentException ex) {
            Log.warn(this.getClass(), "Valor textual não válido, desconsiderando.",ex);
        }
        return em;
    }
}
