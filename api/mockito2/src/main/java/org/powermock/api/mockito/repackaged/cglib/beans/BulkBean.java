/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.beans;

import org.powermock.api.mockito.repackaged.asm.ClassVisitor;
import org.powermock.api.mockito.repackaged.cglib.core.AbstractClassGenerator;
import org.powermock.api.mockito.repackaged.cglib.core.KeyFactory;
import org.powermock.api.mockito.repackaged.cglib.core.ReflectUtils;

/**
 * @author Juozas Baliuka
 */
abstract public class BulkBean
{
    private static final BulkBeanKey KEY_FACTORY =
      (BulkBeanKey) KeyFactory.create(BulkBeanKey.class);
    protected Class target;
    protected String[] getters, setters;
    protected Class[] types;
    protected BulkBean() { }
    
    public static BulkBean create(Class target, String[] getters, String[] setters, Class[] types) {
        Generator gen = new Generator();
        gen.setTarget(target);
        gen.setGetters(getters);
        gen.setSetters(setters);
        gen.setTypes(types);
        return gen.create();
    }
    
    abstract public void getPropertyValues(Object bean, Object[] values);
    abstract public void setPropertyValues(Object bean, Object[] values);

    public Object[] getPropertyValues(Object bean) {
        Object[] values = new Object[getters.length];
        getPropertyValues(bean, values);
        return values;
    }
    
    public Class[] getPropertyTypes() {
        return (Class[])types.clone();
    }
    
    public String[] getGetters() {
        return (String[])getters.clone();
    }
    
    public String[] getSetters() {
        return (String[])setters.clone();
    }

    interface BulkBeanKey {
        public Object newInstance(String target, String[] getters, String[] setters, String[] types);
    }

    public static class Generator extends AbstractClassGenerator {
        private static final Source SOURCE = new Source(BulkBean.class.getName());
        private Class target;
        private String[] getters;
        private String[] setters;
        private Class[] types;

        public Generator() {
            super(SOURCE);
        }

        public void setTarget(Class target) {
            this.target = target;
        }

        public void setGetters(String[] getters) {
            this.getters = getters;
        }

        public void setSetters(String[] setters) {
            this.setters = setters;
        }

        public void setTypes(Class[] types) {
            this.types = types;
        }

        protected ClassLoader getDefaultClassLoader() {
            return target.getClassLoader();
        }

        public BulkBean create() {
            setNamePrefix(target.getName());
            String targetClassName = target.getName();
            String[] typeClassNames = ReflectUtils.getNames(types);
            Object key = KEY_FACTORY.newInstance(targetClassName, getters, setters, typeClassNames);
            return (BulkBean)super.create(key);
        }

        public void generateClass(ClassVisitor v) throws Exception {
            new BulkBeanEmitter(v, getClassName(), target, getters, setters, types);
        }

        protected Object firstInstance(Class type) {
            BulkBean instance = (BulkBean)ReflectUtils.newInstance(type);
            instance.target = target;
                    
            int length = getters.length;
            instance.getters = new String[length];
            System.arraycopy(getters, 0, instance.getters, 0, length);
                    
            instance.setters = new String[length];
            System.arraycopy(setters, 0, instance.setters, 0, length);
                    
            instance.types = new Class[types.length];
            System.arraycopy(types, 0, instance.types, 0, types.length);

            return instance;
        }

        protected Object nextInstance(Object instance) {
            return instance;
        }
    }
}
