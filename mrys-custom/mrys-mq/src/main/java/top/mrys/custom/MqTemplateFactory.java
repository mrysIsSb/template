package top.mrys.custom;

/**
 * mqTemplate factory
 *
 * @author mrys
 */
public interface MqTemplateFactory<P> {

  MqTemplate getMqTemplate(P param);
}
