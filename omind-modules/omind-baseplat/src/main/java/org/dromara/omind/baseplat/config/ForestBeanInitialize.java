package org.dromara.omind.baseplat.config;

import com.dtflys.forest.config.ForestConfiguration;
import org.dromara.common.core.exception.base.BaseException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

public class ForestBeanInitialize implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BaseException {
        ForestConfiguration configuration = ForestConfiguration.getDefaultConfiguration();
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            MetadataReaderFactory metaReader = new CachingMetadataReaderFactory();
            //根据类路径加载所有的类
            Resource[] resources = resolver.getResources("classpath*:org/dromara/omind/baseplat/client/*.class");
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            for (Resource resource : resources) {
                MetadataReader reader = metaReader.getMetadataReader(resource);
                String className = reader.getClassMetadata().getClassName();
                Class<?> clazz = loader.loadClass(className);
                //使用forest创建实例
                Object instance = configuration.createInstance(clazz);
                //注册到spring中
                configurableListableBeanFactory.registerSingleton(className, instance);
            }
        } catch (Exception e) {
            throw new BaseException("加载失败" + e.toString());
        }

    }
}
