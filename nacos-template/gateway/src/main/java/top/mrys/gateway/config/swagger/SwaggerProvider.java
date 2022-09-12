/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */

package top.mrys.gateway.config.swagger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

@Component
public class SwaggerProvider {

  private DiscoveryClient discoveryClient;

  private static final List<String> excludeProviders = Collections.singletonList("gateway");

  public SwaggerProvider(DiscoveryClient discoveryClient) {
    this.discoveryClient = discoveryClient;
  }

  public List<Object> get() {
    return discoveryClient.getServices().stream()
      .filter(this::excludeServer)
      .map(service -> swaggerResource(service, "/" + service + "/v3/api-docs"))
      .collect(Collectors.toList());
  }

  private Boolean excludeServer(String serviceId) {
    return !excludeProviders.contains(serviceId);
  }

  private static Object swaggerResource(String name, String location) {
    HashMap<Object, Object> map = new HashMap<>();
    map.put("name", name);
    map.put("location", location);
    map.put("url", location);
    map.put("swaggerVersion", "3.0");
    return map;
  }

}
