package org.powermock.examples.spring.mockito;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ClassUtils;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class PowermockClassLoadTest {

    @Autowired
    private ConfigurableApplicationContext context;

    @Test
    public void testRest() throws Exception {
        BeanDefinition definition = context.getBeanFactory().getBeanDefinition("powermockClassLoadTest.Config");
        String className = definition.getBeanClassName();
        Class aClass = ClassUtils.forName(className, null);
        assertThat(aClass).isSameAs(((AbstractBeanDefinition)definition).getBeanClass());
    }

    @Configuration
    public static class Config {
    }

}