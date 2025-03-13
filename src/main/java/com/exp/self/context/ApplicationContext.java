package com.exp.self.context;


import com.exp.self.annotation.Component;
import com.exp.self.annotation.Inject;
import com.exp.self.annotation.Primary;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;


public class ApplicationContext {
    private final Map<Class<?>, List<Object>> beans = new HashMap<>(); //хранилище бинов <Название, Объект>
    private final Map<Class<?>, Object> primaryBeans = new HashMap<>();


    private static ApplicationContext instance;


    private ApplicationContext() throws Exception {
        Set<Class<?>> classes = ClassScanner.findClasses();

        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Component.class)) {
                createBean(clazz);
            }
        }
        injectDependencies();
    }

    public static ApplicationContext getInstance() throws Exception {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    private void injectDependencies() throws IllegalAccessException {

        for (Map.Entry<Class<?>, List<Object>> bean : beans.entrySet()) {
            if (bean.getKey().isAnnotationPresent(Component.class)) {
                Object instance = bean.getValue().get(0);
                for (Field field : bean.getKey().getDeclaredFields()) {
                    if (field.isAnnotationPresent(Inject.class)) {
                        System.out.println("Inject: " + field);
                        boolean accessible = field.canAccess(instance);
                        field.setAccessible(true);
                        Object dependency = getBean(field.getType());
                        field.set(instance, dependency);
                        field.setAccessible(accessible);
                    }
                }
            }
        }
    }

    private void createBean(Class<?> clazz) throws Exception {
        if (beans.containsKey(clazz)) {
            return;
        }

        System.out.println("Create bean for class: " + clazz);

        Constructor<?> constructor = null;
        for (Constructor<?> c : clazz.getDeclaredConstructors()) {
            if (c.isAnnotationPresent(Inject.class)) {
                constructor = c;
                break;
            }
        }

        if (constructor == null) {
            constructor = clazz.getDeclaredConstructor();
        }

        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = getBean(parameterTypes[i]);
        }

        Object instance = constructor.newInstance(parameters);

        List<Object> list = new ArrayList<>();
        list.add(instance);
        beans.put(clazz, list);


        if (clazz.isAnnotationPresent(Primary.class)) {
            for (AnnotatedType clazz2 : clazz.getAnnotatedInterfaces()) {
                Class<?> classOfInterface = (Class<?>) clazz2.getType();
                primaryBeans.put(classOfInterface, instance);
            }
        }

        for (AnnotatedType clazz2 : clazz.getAnnotatedInterfaces()) {
            Class<?> classOfInterface = (Class<?>) clazz2.getType();
            if (beans.containsKey(classOfInterface)) {
                beans.get(classOfInterface).add(instance);
            } else {
                List<Object> list2 = new ArrayList<>();
                list2.add(instance);
                beans.put(classOfInterface, list2);
            }
        }
    }


    public <T> T getBean(Class<T> clazz) {
        if (primaryBeans.containsKey(clazz)) {
            return (T) primaryBeans.get(clazz);
        }

        if (beans.containsKey(clazz) && beans.get(clazz).size() > 0) {
            return (T) beans.get(clazz).get(0);

        }
        throw new RuntimeException("No bean found for class: " + clazz);

    }
}

