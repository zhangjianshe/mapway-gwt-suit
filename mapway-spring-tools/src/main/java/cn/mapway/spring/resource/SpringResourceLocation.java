package cn.mapway.spring.resource;

import org.nutz.resource.NutResource;
import org.nutz.resource.impl.ResourceLocation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 用法 Scans.me().add(new SpringResourceLocation(ctx));
 *
 * @author wendal
 */
public class SpringResourceLocation extends ResourceLocation implements ApplicationContextAware {

    protected ApplicationContext applicationContext;

    public String id() {
        return "spring";
    }

    @Override
    public void scan(String base, Pattern pattern, List<NutResource> list) {
        try {
            Resource[] tmp = applicationContext.getResources("classpath*:" + base + "*");
            for (Resource resource : tmp) {
                if (resource.getFilename() == null)
                    continue;
                if (pattern != null && !pattern.matcher(resource.getFilename()).find()) {
                    continue;
                }
                SpringResource sr = new SpringResource();
                sr.resource = resource;
                sr.setName(resource.getFilename());
                sr.setSource("spring");
                list.add(sr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SpringResourceLocation(ApplicationContext context) {
        setApplicationContext(context);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public class SpringResource extends NutResource {

        protected Resource resource;

        public InputStream getInputStream() throws IOException {
            return resource.getInputStream();
        }

    }
}