package top.mrys.auth.ds;

import static org.springframework.util.StringUtils.isEmpty;

import cn.hutool.json.JSONUtil;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.cloud.client.discovery.event.ParentHeartbeatEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author mrys
 */
@Slf4j
@Component
//@ConditionalOnCloudPlatform(CloudPlatform.CLOUD_FOUNDRY)
public class NacosInstanceProvider implements SmartInitializingSingleton {

  private static final String KEY_MANAGEMENT_PORT = "management.port";

  private static final String KEY_MANAGEMENT_PATH = "management.context-path";

  private static final String KEY_MANAGEMENT_ADDRESS = "management.address";
  private final DefaultListableBeanFactory beanFactory;
  private final DiscoveryClient discoveryClient;

  private final RestTemplate restTemplate;

  public NacosInstanceProvider(DefaultListableBeanFactory beanFactory,
    DiscoveryClient discoveryClient, RestTemplate restTemplate) {
    this.beanFactory = beanFactory;
    this.discoveryClient = discoveryClient;
    this.restTemplate = restTemplate;
  }

  @EventListener
  public void onApplicationReady(ApplicationReadyEvent event) {
    log.info("onApplicationReady");
    discover();
  }

  @EventListener
  public void onInstanceRegistered(InstanceRegisteredEvent<?> event) {
    log.info("onInstanceRegistered");
    discover();
  }

  @EventListener
  public void onParentHeartbeat(ParentHeartbeatEvent event) {
    log.info("onParentHeartbeat");
    discoverIfNeeded(event.getValue());
  }

  @EventListener
  public void onApplicationEvent(HeartbeatEvent event) {
    log.info("onApplicationEvent");
    discoverIfNeeded(event.getValue());
  }


  @Override
  public void afterSingletonsInstantiated() {
    discover();
  }


  protected void discover() {
    log.info("discover");
    discoveryClient.getServices().stream().filter(this::filterRegisterInstance)
      .map(discoveryClient::getInstances)
      //自取第一个
      .filter(instances -> !instances.isEmpty())
      .map(instances -> instances.get(0))
      .map(this::convert)
      .reduce((a, b) -> {
        a.putAll(b);
        return a;
      }).ifPresent(this::register);
  }

  private void discoverIfNeeded(Object value) {
    discover();
  }

  private void register(Map<String, URI> map) {

    map.forEach((k, v) -> {
      System.out.println(k + "=" + v);
      try{
        ResponseEntity<String> resp = restTemplate.getForEntity(v, String.class);
        log.info("resp:{}", resp);
      }catch (Exception e){
        log.error("error",e);
      }
    });
  }


  private boolean filterRegisterInstance(String serviceId) {
    return true;
  }


  private Map<String, URI> convert(ServiceInstance instance) {
    log.info("{}", JSONUtil.toJsonStr(instance));
    return new HashMap<String, URI>() {{
      put(instance.getServiceId(),
//        UriComponentsBuilder.fromUri(getManagementUrl(instance))
//        UriComponentsBuilder.fromUri(getServiceUrl(instance))
        UriComponentsBuilder.fromHttpUrl("http://user")
          .path("/actuator/auth")
          .build()
          .toUri());
    }};
  }

  protected URI getServiceUrl(ServiceInstance instance) {
    return instance.getUri();
  }

  protected URI getManagementUrl(ServiceInstance instance) {
    URI serviceUrl = this.getServiceUrl(instance);
    String managementScheme = this.getManagementScheme(instance);
    String managementHost = this.getManagementHost(instance);
    int managementPort = this.getManagementPort(instance);

    UriComponentsBuilder builder;
    if (serviceUrl.getHost().equals(managementHost) && serviceUrl.getScheme()
      .equals(managementScheme)
      && serviceUrl.getPort() == managementPort) {
      builder = UriComponentsBuilder.fromUri(serviceUrl);
    } else {
      builder = UriComponentsBuilder.newInstance().scheme(managementScheme).host(managementHost);
      if (managementPort != -1) {
        builder.port(managementPort);
      }
    }

    return builder.path("/").build().toUri();
  }

  private String getManagementScheme(ServiceInstance instance) {
    return this.getServiceUrl(instance).getScheme();
  }

  protected String getManagementHost(ServiceInstance instance) {
    String managementServerHost = instance.getMetadata().get(KEY_MANAGEMENT_ADDRESS);
    if (!isEmpty(managementServerHost)) {
      return managementServerHost;
    }
    return getServiceUrl(instance).getHost();
  }

  protected int getManagementPort(ServiceInstance instance) {
    String managementPort = instance.getMetadata().get(KEY_MANAGEMENT_PORT);
    if (!isEmpty(managementPort)) {
      return Integer.parseInt(managementPort);
    }
    return getServiceUrl(instance).getPort();
  }

}
